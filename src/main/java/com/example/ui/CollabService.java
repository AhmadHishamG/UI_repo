package com.example.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.web.client.RestTemplate;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CollabService  {
    private static final String BASE_URL = "http://localhost:3000";
    private static final String WS_URL = "ws://localhost:3000/ws";

    private User user;

    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private String userId;
    private String docId;
    private String username;

    private MessageCallback callback;

    public interface MessageCallback {
        void onCreate(String sessionId, String editorCode, String viewerCode);

        void onJoin(Document doc, List<User> editors, List<User> viewers);

        void onUpdate(String content, List<String> characterIds);

        void onPresence(List<User> editors, List<User> viewers);

        void onCursor(String userId, int pos);

        void onError(String error);
    }

    public static User createUser(String username) {
        RestTemplate rest = new RestTemplate();
        String id = rest.postForObject(BASE_URL + "/users", username, String.class);
        return new User(id, username, 0);
    }

    public CollabService(MessageCallback callback, User user) {
        this.callback = callback;
        this.user = user;// UUID.randomUUID().toString().substring(0, 6);
    }

    public void sendInsertOperation(String sessionId, String parentId, String ch, String characterId, User user) {
        if (stompSession != null && sessionId != null) {
            ObjectNode msg = mapper.createObjectNode();
            ObjectNode sender = msg.putObject("sender");
            sender.put("id", user.getId());
            sender.put("username", user.getUsername());
            sender.put("cursorPosition", user.getCursorPosition());

            ObjectNode op = msg.putObject("operation");
            op.put("type", "INSERT");
            op.put("parentId", parentId);
            op.put("ch", ch);
            op.put("userId", user.getId());
            op.put("timestamp", System.currentTimeMillis());
            op.put("characterId", characterId);

            stompSession.send("/topic/session/" + sessionId + "/edit", msg.toString().getBytes());
        }
    }

    public void sendDeleteOperation(String sessionId, int index, String characterId, User user) {
        if (stompSession != null && sessionId != null) {
            ObjectNode msg = mapper.createObjectNode();
            ObjectNode sender = msg.putObject("sender");
            sender.put("id", user.getId());
            sender.put("username", user.getUsername());
            sender.put("cursorPosition", user.getCursorPosition());

            ObjectNode op = msg.putObject("operation");
            op.put("type", "DELETE");
            op.put("characterId", characterId);
            op.put("userId", user.getId());
            op.put("timestamp", System.currentTimeMillis());

            stompSession.send("/topic/session/" + sessionId + "/edit", msg.toString().getBytes());
        }
    }

    public Document createSession(String title, User user) {
        this.user = user;
        // ...create doc via REST if needed...
        connectStomp();
        stompSession.subscribe("/topic/user/" + user.getId(), createFrameHandler());
        // ...send create message...
        JsonNode userNode = mapper.createObjectNode()
                .put("id", user.getId())
                .put("username", user.getUsername())
                .put("cursorPosition", user.getCursorPosition());
        stompSession.send("/app/session/create", userNode.toString().getBytes());
        // ...return doc if needed...
        return null;
    }

    public Document joinSession(String code, User user) {
        this.user = user;
        connectStomp();
        stompSession.subscribe("/topic/user/" + user.getId(), createFrameHandler());
        ObjectNode msg = mapper.createObjectNode();
        ObjectNode sender = msg.putObject("sender");
        sender.put("id", user.getId());
        sender.put("username", user.getUsername());
        sender.put("cursorPosition", user.getCursorPosition());
        msg.put("code", code);
        stompSession.send("/app/session/join", msg.toString().getBytes());
        return null;
    }

    private void connectStomp() {
        if (stompClient != null && stompSession != null && stompSession.isConnected())
            return;
        System.err.println("=================================================/n");
        System.err.println("=============ws connecting not returned==========/n");
        System.err.println("=================================================/n");
        var transports = List.<Transport>of(new WebSocketTransport(new StandardWebSocketClient()));
        stompClient = new WebSocketStompClient(new SockJsClient(transports));
        ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler();
        ts.afterPropertiesSet();
        stompClient.setTaskScheduler(ts);
        // stompSession = stompClient.connect(WS_URL, new StompSessionHandlerAdapter() {
        // }).join();
        try {

            stompSession = stompClient.connect(WS_URL, new StompSessionHandlerAdapter() {
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to connect to WebSocket", (Throwable) e);
        }
    }

    private StompFrameHandler createFrameHandler() {
        return new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return byte[].class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    JsonNode n = mapper.readTree((byte[]) payload);
                    String type = n.get("type").asText();

                    switch (type) {
                        case "CREATE":
                            callback.onCreate(
                                    n.get("sessionId").asText(),
                                    n.get("editorCode").asText(),
                                    n.get("viewerCode").asText());
                            break;
                        case "JOIN":
                            Document doc = new Document();
                            doc.setId(n.get("sessionId").asText());
                            doc.setEditorCode(n.get("editorCode").asText());
                            doc.setViewerCode(n.get("viewerCode").asText());
                            doc.setContent(n.get("content").asText());

                            List<User> editors = mapper.convertValue(n.get("editors"),
                                    new com.fasterxml.jackson.core.type.TypeReference<List<User>>() {
                                    });
                            List<User> viewers = mapper.convertValue(n.get("viewers"),
                                    new com.fasterxml.jackson.core.type.TypeReference<List<User>>() {
                                    });

                            callback.onJoin(doc, editors, viewers);
                            stompSession.subscribe("/topic/session/" + doc.getId(),
                                    sessionFrameHandler());
                            break;

                        case "UPDATE":
                            List<String> backendCharacterIds = null;
                            if (n.has("characterIds")) {
                                backendCharacterIds = mapper.convertValue(n.get("characterIds"),new TypeReference<List<String>>() {});
                            }
                            callback.onUpdate(n.get("content").asText(), backendCharacterIds);
                            break;

                        case "PRESENCE":
                            List<User> editorsPresence = mapper.convertValue(n.get("editors"),
                                    new com.fasterxml.jackson.core.type.TypeReference<List<User>>() {
                                    });
                            List<User> viewersPresence = mapper.convertValue(n.get("viewers"),
                                    new com.fasterxml.jackson.core.type.TypeReference<List<User>>() {
                                    });
                            callback.onPresence(editorsPresence, viewersPresence);
                            break;

                        case "CURSOR":
                            JsonNode sender = n.get("sender");
                            callback.onCursor(sender.get("id").asText(),
                                    sender.get("cursorPosition").asInt());
                            break;

                        case "ERROR":
                            callback.onError(n.get("error").asText());
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private StompFrameHandler sessionFrameHandler() {
        return new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return byte[].class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {

                    JsonNode n = mapper.readTree((byte[]) payload);
                    Message msg = mapper.readValue((byte[]) payload,
                            new com.fasterxml.jackson.core.type.TypeReference<Message>() {
                            });
                    String type = msg.getType();
                    switch (type) {
                        case "JOIN":
                            Document doc = new Document();
                            doc.setId(n.get("sessionId").asText());
                            doc.setEditorCode(n.has("editorCode") ? n.get("editorCode").asText() : "");
                            doc.setViewerCode(n.has("viewerCode") ? n.get("viewerCode").asText() : "");
                            // Subscribe to session topic if not already
                            stompSession.subscribe("/topic/session/" + doc.getId(), sessionFrameHandler());

                            callback.onJoin(doc, msg.getEditors(), msg.getViewers());
                            break;
                            case "UPDATE":
                            List<String> backendCharacterIds = null;
                            if (n.has("characterIds")) {
                                backendCharacterIds = mapper.convertValue(n.get("characterIds"), new TypeReference<List<String>>() {});
                            }
                            callback.onUpdate(n.get("content").asText(), backendCharacterIds);
                            break;
                        case "PRESENCE":
                            callback.onPresence(msg.getEditors(), msg.getViewers());
                            break;
                        case "CURSOR":
                            callback.onCursor(
                                    n.get("sender").get("id").asText(),
                                    n.get("sender").get("cursorPosition").asInt());
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void sendInsertChar(String sessionId, int parentId, char ch, User user) {
        if (stompSession != null && sessionId != null) {
            ObjectNode msg = mapper.createObjectNode();
            ObjectNode sender = msg.putObject("sender");
            sender.put("id", user.getId());
            sender.put("username", user.getUsername());
            sender.put("cursorPosition", user.getCursorPosition());

            ObjectNode op = msg.putObject("operation");
            op.put("type", "INSERT");
            op.put("parentId", parentId);
            op.put("ch", String.valueOf(ch));
            op.put("userId", user.getId());
            op.put("timestamp", System.currentTimeMillis());

            stompSession.send("/app/session/" + sessionId + "/edit", msg.toString().getBytes());
        }
    }

    public void sendEditOperation(String sessionId, String opType, int caretPos, String ch, String content, User user) {
        if (stompSession != null && sessionId != null) {
            ObjectNode msg = mapper.createObjectNode();
            ObjectNode sender = msg.putObject("sender");
            sender.put("id", user.getId());
            sender.put("username", user.getUsername());
            sender.put("cursorPosition", caretPos);

            ObjectNode op = msg.putObject("operation");
            op.put("type", opType);
            op.put("parentId", caretPos > 0 ? caretPos - 1 : -1);
            op.put("ch", ch != null ? ch : "");
            op.put("userId", user.getId());
            op.put("timestamp", System.currentTimeMillis());

            msg.put("content", content);

            stompSession.send("/app/session/" + sessionId + "/edit", msg.toString().getBytes());
        }
    }

    public void sendCursorUpdate(String sessionId, int caretPos, User user) {
        if (stompSession != null && sessionId != null) {
            ObjectNode msg = mapper.createObjectNode();
            msg.put("type", "CURSOR");
            ObjectNode sender = msg.putObject("sender");
            sender.put("id", user.getId());
            sender.put("username", user.getUsername());
            sender.put("cursorPosition", caretPos);
            stompSession.send("/app/session/" + sessionId + "/update-cursor", msg.toString().getBytes());
        }
    }

    /** Trigger REST undo/redo */
    public void undo() {
        rest.postForLocation(BASE_URL + "/api/documents/{id}/undo?userId={u}",
                null, docId, userId);
    }

    public void redo() {
        rest.postForLocation(BASE_URL + "/api/documents/{id}/redo?userId={u}",
                null, docId, userId);
    }
}
// if ("CREATE".equals(type)) {
// callback.onCreate(n.get("editorCode").asText(),
// n.get("viewerCode").asText());
// } else if ("JOIN".equals(type)) {
// Document doc = new Document();
// doc.setId(n.get("sessionId").asText());
// doc.setEditorCode(n.has("editorCode") ? n.get("editorCode").asText() : "");
// doc.setViewerCode(n.has("viewerCode") ? n.get("viewerCode").asText() : "");
// doc.setContent(n.has("content") ? n.get("content").asText() : ""); // <-- set
// content
// callback.onJoin(doc);
// stompSession.subscribe("/topic/session/" + doc.getId(),
// sessionFrameHandler());
// }

/** Send an edit over WS */
// public void sendEdit(String content) {
// // if (stompSession != null && stompSession.isConnected()) {
// // // The backend expects a Message with sender and operation (for CRDT)
// // // JsonNode msg = mapper.createObjectNode()
// // // .putObject("sender")
// // // .put("id", userId)
// // // .put("username", username)
// // // .put("cursorPosition", 0)
// // // //.parent()
// // // .put("type", "INSERT")
// // // .put("content", content);
// // // stompSession.send("/app/session/" + docId + "/edit",
// // // msg.toString().getBytes());
// // ObjectNode msg = mapper.createObjectNode();
// // ObjectNode sender = msg.putObject("sender");
// // sender.put("id", user.getId());
// // sender.put("username", user.getUsername());
// // sender.put("cursorPosition", user.getCursorPosition());
// // ObjectNode op = msg.putObject("operation");
// // op.put("type", "INSERT"); // or "EDIT" if your backend expects that
// // op.put("parentId", -1); // or actual parentId logic if needed
// // op.put("ch", ""); // not needed for full content, but required by backend
// schema
// // op.put("userId", user.getId());
// // op.put("timestamp", System.currentTimeMillis());
// // msg.put("content", content); // send the full content
// // stompSession.send("/app/session/" + docId + "/edit",
// msg.toString().getBytes());

// // }

// if (stompSession != null && docId != null) {
// ObjectNode msg = mapper.createObjectNode();
// msg.put("content", content);
// ObjectNode sender = msg.putObject("sender");
// sender.put("id", user.getId());
// sender.put("username", user.getUsername());
// sender.put("cursorPosition", user.getCursorPosition());
// stompSession.send("/app/session/" + docId + "/edit",
// msg.toString().getBytes());
// }
// }

// private StompFrameHandler createFrameHandler() {
// return new StompFrameHandler() {
// @Override
// public Type getPayloadType(StompHeaders headers) {
// return byte[].class;
// }

// @Override
// public void handleFrame(StompHeaders headers, Object payload) {
// try {
// JsonNode n = mapper.readTree((byte[]) payload);
// // if ("CREATE".equals(n.get("type").asText())) {
// // callback.onCreate(n.get("editorCode").asText(),
// n.get("viewerCode").asText());
// // }
// String type = n.get("type").asText();
// if ("CREATE".equals(type)) {
// callback.onCreate(n.get("editorCode").asText(),
// n.get("viewerCode").asText());
// } else if ("JOIN".equals(type)) {
// Document doc = new Document();
// doc.setId(n.get("sessionId").asText());
// doc.setEditorCode(n.has("editorCode") ? n.get("editorCode").asText() : "");
// doc.setViewerCode(n.has("viewerCode") ? n.get("viewerCode").asText() : "");
// callback.onJoin(doc);
// // Now subscribe to session topic for further updates
// stompSession.subscribe("/topic/session/" + doc.getId(),
// sessionFrameHandler());
// }

// } catch (Exception e) {
// e.printStackTrace();
// }
// }
// };
// }

// private StompFrameHandler createFrameHandler() {
// return new StompFrameHandler() {
// @Override
// public Type getPayloadType(StompHeaders headers) {
// return byte[].class;
// }

// @Override
// public void handleFrame(StompHeaders headers, Object payload) {
// try {
// JsonNode n = mapper.readTree((byte[]) payload);
// String type = n.get("type").asText();

// switch (type) {
// case "CREATE":
// String sessionId = n.has("sessionId") ? n.get("sessionId").asText() : "";
// callback.onCreate(sessionId,n.get("editorCode").asText(),
// n.get("viewerCode").asText());
// break;
// case "JOIN":
// Document doc = new Document();
// doc.setId(n.get("sessionId").asText());
// doc.setEditorCode(n.has("editorCode") ? n.get("editorCode").asText() : "");
// doc.setViewerCode(n.has("viewerCode") ? n.get("viewerCode").asText() : "");
// doc.setContent(n.has("content") ? n.get("content").asText() : "");
// callback.onJoin(doc);
// stompSession.subscribe("/topic/session/" + doc.getId(),
// sessionFrameHandler());
// break;
// case "UPDATE":
// callback.onUpdate(n.get("content").asText());
// break;
// case "PRESENCE":
// callback.onPresence(mapper.convertValue(n.get("editors"),
// java.util.List.class));
// break;
// case "CURSOR":
// callback.onCursor(
// n.get("sender").get("id").asText(),
// n.get("sender").get("cursorPosition").asInt());
// break;
// case "ERROR":
// callback.onError(n.get("error").asText());
// break;
// }
// } catch (Exception e) {
// e.printStackTrace();
// }
// }
// };
// }

// public Document joinSession(String code, String username) {
// System.err.println("=================================================");
// System.err.println("=======joining sess with code: " + code +
// "============");
// System.err.println("=================================================");
// this.username = username;

// // 1. Connect WebSocket if not already connected
// connectStomp();

// // 2. Subscribe to user and session topics (if not already)
// stompSession.subscribe("/topic/user/" + userId, createFrameHandler());
// // Session topic will be known after join response

// // 3. Send join message via WebSocket
// ObjectNode msg = mapper.createObjectNode();
// ObjectNode sender = msg.putObject("sender");
// sender.put("id", userId);
// sender.put("username", username);
// sender.put("cursorPosition", 0);
// msg.put("code", code);
// stompSession.send("/app/session/join", msg.toString().getBytes());

// // 4. Return null or a placeholder Document; actual doc info will come via
// WebSocket callback
// return null;
// }

// public User createUser(String username) {
// String id = rest.postForObject(BASE_URL + "/users", username, String.class);
// return new User(id, username, 0);
// }

/** Create a new document via REST and open WS session */
// public Document createSession(String title, String username) {
// this.username = username;
// // 1) create document via REST
// System.err.println("=================================================/n");
// System.err.println("==================Getting doc====================/n");
// System.err.println("=================================================/n");
// Document doc = rest.postForObject(
// BASE_URL + "/api/documents?title={t}", null, Document.class, title);
// if (doc == null)
// throw new RuntimeException("Create document failed");
// this.docId = doc.getId();
// System.err.println("=================================================/n");
// System.err.println("====================got doc======================/n");
// System.err.println("=================================================/n");
// // 2) connect and subscribe
// System.err.println("=================================================/n");
// System.err.println("==================ws connecting==================/n");
// System.err.println("=================================================/n");
// connectStomp();
// System.err.println("=================================================/n");
// System.err.println("==================ws connected===================/n");
// System.err.println("=================================================/n");
// stompSession.subscribe("/topic/user/" + userId, createFrameHandler());
// stompSession.subscribe("/topic/session/" + docId, sessionFrameHandler());

// // 3) send User object (server expects User payload)
// JsonNode userNode = mapper.createObjectNode()
// .put("id", userId)
// .put("username", username)
// .put("cursorPosition", 0);
// stompSession.send("/app/session/create", userNode.toString().getBytes());

// return doc;
// } // public void handleFrame(StompHeaders headers, Object payload) {
// try {
// Message msg = mapper.readValue((byte[]) payload,
// new com.fasterxml.jackson.core.type.TypeReference<Message>() {
// });
// JsonNode n = mapper.readTree((byte[]) payload);
// String type = n.get("type").asText();
// switch (type) {
// case "CREATE":
// callback.onCreate(msg.getSessionId(), msg.getEditorCode(),
// msg.getViewerCode());
// break;
// case "JOIN":
// Document doc = new Document();
// doc.setId(msg.getSessionId());
// doc.setEditorCode(msg.getEditorCode());
// doc.setViewerCode(msg.getViewerCode());
// doc.setContent(msg.getContent());
// callback.onJoin(doc, msg.getEditors(), msg.getViewers());
// stompSession.subscribe("/topic/session/" + doc.getId(),
// sessionFrameHandler());
// break;
// case "UPDATE":
// callback.onUpdate(msg.getContent());
// break;
// case "PRESENCE":
// callback.onPresence(msg.getEditors(), msg.getViewers());
// break;
// case "CURSOR":
// callback.onCursor(msg.getSender().getId(),
// msg.getSender().getCursorPosition());
// break;
// case "ERROR":
// callback.onError(msg.getError());
// break;
// }
// } catch (Exception e) {
// e.printStackTrace();
// }
// }
// };
// } // private StompFrameHandler createFrameHandler() {
// return new StompFrameHandler() {
// @Override
// public Type getPayloadType(StompHeaders headers) { return byte[].class; }
// @Override
// public void handleFrame(StompHeaders headers, Object payload) {
// try {
// Message msg = mapper.readValue((byte[]) payload, new
// com.fasterxml.jackson.core.type.TypeReference<Message>() {}););
// JsonNode n = mapper.readTree((byte[]) payload);
// String type = n.get("type").asText();
// switch (type) {
// case "CREATE":
// callback.onCreate(msg.getSessionId(), msg.getEditorCode(),
// msg.getViewerCode());
// break;
// case "JOIN":
// Document doc = new Document();
// doc.setId(msg.getSessionId());
// doc.setEditorCode(msg.getEditorCode());
// doc.setViewerCode(msg.getViewerCode());
// doc.setContent(msg.getContent());
// callback.onJoin(doc,msg.getEditors(),msg.getViewers());
// stompSession.subscribe("/topic/session/" + doc.getId(),
// sessionFrameHandler());
// break;
// case "UPDATE":
// callback.onUpdate(msg.getContent());
// break;
// case "PRESENCE":
// callback.onPresence(msg.getEditors(),msg.getViewers());
// break;
// case "CURSOR":
// callback.onCursor(msg.getSender().getId(),
// msg.getSender().getCursorPosition());
// break;
// case "ERROR":
// callback.onError(msg.getError());
// break;
// }
// } catch (Exception e) {
// e.printStackTrace();
// }
// }
// };
// }