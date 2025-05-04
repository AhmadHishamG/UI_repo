// package com.example.ui;

// import com.fasterxml.jackson.databind.ObjectMapper;

// import javafx.geometry.Bounds;

// import com.fasterxml.jackson.databind.JsonNode;
// import org.springframework.web.client.RestTemplate;
// import org.springframework.messaging.simp.stomp.StompFrameHandler;
// import org.springframework.messaging.simp.stomp.StompHeaders;
// import org.springframework.messaging.simp.stomp.StompSession;
// import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
// import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
// import org.springframework.web.socket.client.standard.StandardWebSocketClient;
// import org.springframework.web.socket.messaging.WebSocketStompClient;
// import org.springframework.web.socket.sockjs.client.SockJsClient;
// import org.springframework.web.socket.sockjs.client.Transport;
// import org.springframework.web.socket.sockjs.client.WebSocketTransport;

// import java.lang.reflect.Type;
// import java.util.List;
// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;

// /**
//  * Handles REST calls and STOMP WebSocket interactions.
//  */
// public class CollabService {
//     // private static final String REST_URL = "http://192.168.1.5:3000/";
//     // private static final String WS_URL = "http://192.168.1.5:3000/ws";

//     private static final String BASE_URL = "http://localhost:8080";
//     private static final String WS_URL = "ws://localhost:8080/ws";

//     private final RestTemplate rest = new RestTemplate();
//     private final ObjectMapper mapper = new ObjectMapper();

//     private WebSocketStompClient stompClient;
//     private StompSession stompSession;
//     private final Map<String, LineCallback> lineHandlers = new ConcurrentHashMap<>();

//     public interface MessageCallback {
//         void onCreate(String editorCode, String viewerCode);
//         void onUpdate(String content);
//         void onPresence(List<String> users);
//         void onCursor(String userId, int pos);
//     }

//     private MessageCallback callback;

//     public CollabService(MessageCallback callback) {
//         this.callback = callback;
//     }

//     /** Create a new document via REST and open WS session */
//     public String createUser(String username) {
//         // POST to /users with plain text body
//         return rest.postForObject(BASE_URL + "/users", username, String.class);
//     }

//     // public String createDocumentAndConnect(String title) {
//     //     // call POST /api/documents?title={title}
//     //     Document doc = rest.postForObject(REST_URL + "?title={t}", null, Document.class, title);
//     //     if (doc == null) throw new RuntimeException("Failed to create document");
//     //     connectStomp(doc.getId());
//     //     return doc.getId();
//     // }

//     public Document createDocument(String title) {
//         return rest.postForObject(BASE_URL + "/api/documents?title=" + title, null, Document.class);
//     }

//     public Document getDocument(String id) {
//         return rest.getForObject(BASE_URL + "/api/documents/" + id, Document.class);
//     }

//     /** Join existing doc by code */
//     public String joinDocumentAndConnect(String code) {
//         // try editor then viewer
//         Document doc = rest.getForObject(BASE_URL + "/api/documents"+ "/editor/{c}", Document.class, code);
//         if (doc == null) doc = rest.getForObject(REST_URL + "/viewer/{c}", Document.class, code);
//         if (doc == null) throw new RuntimeException("Invalid code");
//         connectStomp(doc.getId());
//         return doc.getId();
//     }

//     // private void connectStomp(String docId) {
//     //     if (stompClient != null) return;
//     //     var transports = List.<Transport>of(new WebSocketTransport(new StandardWebSocketClient()));
//     //     stompClient = new WebSocketStompClient(new SockJsClient(transports));
//     //     ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler();
//     //     ts.afterPropertiesSet();
//     //     stompClient.setTaskScheduler(ts);
//     //     stompClient.connect(WS_URL, new StompSessionHandlerAdapter() {
//     //         @Override
//     //         public void afterConnected(StompSession session, StompHeaders headers) {
//     //             stompSession = session;
//     //             session.subscribe("/topic/session/"+docId, this::handleFrame);
//     //             // send JOIN
//     //             session.send("/app/session/join",
//     //                 ("{\"type\":\"JOIN\",\"code\":\""+docId+"\"}").getBytes());
//     //         }
//     //         private void handleFrame(StompHeaders h, byte[] payload) {
//     //             try {
//     //                 JsonNode n = mapper.readTree(payload);
//     //                 String type = n.get("type").asText();
//     //                 switch (type) {
//     //                     case "CREATE":
//     //                         callback.onCreate(n.get("editorCode").asText(), n.get("viewerCode").asText());
//     //                         break;
//     //                     case "UPDATE": callback.onUpdate(n.get("content").asText()); break;
//     //                     case "PRESENCE":
//     //                         List<String> users = mapper.convertValue(n.get("activeUsers"), List.class);
//     //                         callback.onPresence(users);
//     //                         break;
//     //                     case "CURSOR": callback.onCursor(n.get("userId").asText(), n.get("position").asInt()); break;
//     //                 }
//     //             } catch (Exception e) {e.printStackTrace();}
//     //         }
//     //         @Override public Type getPayloadType(StompHeaders headers) { return byte[].class; }
//     //     });
//     // }

// private void connectStomp(String docId) {
//     if (stompClient != null) return;
//     var transports = List.<Transport>of(new WebSocketTransport(new StandardWebSocketClient()));
//     stompClient = new WebSocketStompClient(new SockJsClient(transports));
//     ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler();
//     ts.afterPropertiesSet();
//     stompClient.setTaskScheduler(ts);
//     stompClient.connect(WS_URL, new StompSessionHandlerAdapter() {
//         @Override
//         public void afterConnected(StompSession session, StompHeaders headers) {
//             stompSession = session;
//             session.subscribe("/topic/session/" + docId, new StompFrameHandler() {
//                 @Override
//                 public Type getPayloadType(StompHeaders headers) {
//                     return byte[].class;
//                 }

//                 @Override
//                 public void handleFrame(StompHeaders headers, Object payload) {
//                     try {
//                         byte[] bytePayload = (byte[]) payload;
//                         JsonNode n = mapper.readTree(bytePayload);
//                         String type = n.get("type").asText();
//                         switch (type) {
//                             case "CREATE":
//                                 callback.onCreate(n.get("editorCode").asText(), n.get("viewerCode").asText());
//                                 break;
//                             case "UPDATE":
//                                 callback.onUpdate(n.get("content").asText());
//                                 break;
//                             case "PRESENCE":
//                                 List<String> users = mapper.convertValue(n.get("activeUsers"), List.class);
//                                 callback.onPresence(users);
//                                 break;
//                             case "CURSOR":
//                                 callback.onCursor(n.get("userId").asText(), n.get("position").asInt());
//                                 break;
//                         }
//                     } catch (Exception e) {
//                         e.printStackTrace();
//                     }
//                 }
//             });
//             // send JOIN
//             session.send("/app/session/join",
//                 ("{\"type\":\"JOIN\",\"code\":\""+docId+"\"}").getBytes());
//         }
//     });
// }

//     public void sendEdit(String docId, String userId, String content) {
//         if (stompSession!=null) {
//             String msg = String.format("{\"type\":\"EDIT\",\"userId\":\"%s\",\"content\":\"%s\"}", userId, content);
//             stompSession.send("/app/session/"+docId+"/edit", msg.getBytes());
//         }
//     }

//     public void undo(String docId, String userId) {
//         rest.postForLocation(REST_URL+"/"+docId+"/undo?userId={u}", null, userId);
//     }
//     public void redo(String docId, String userId) {
//         rest.postForLocation(REST_URL+"/"+docId+"/redo?userId={u}", null, userId);
//     }

//     public interface LineCallback { void drawLine(String userId, Bounds b); }
//     public void setLineCallback(LineCallback cb) { this.lineHandlers.put("default", cb); }
// }

// package com.example.ui;

// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import javafx.geometry.Bounds;
// import org.springframework.web.client.RestTemplate;
// import org.springframework.messaging.simp.stomp.StompHeaders;
// import org.springframework.messaging.simp.stomp.StompSession;
// import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
// import org.springframework.messaging.simp.stomp.StompFrameHandler;
// import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
// import org.springframework.web.socket.client.standard.StandardWebSocketClient;
// import org.springframework.web.socket.messaging.WebSocketStompClient;
// import org.springframework.web.socket.sockjs.client.SockJsClient;
// import org.springframework.web.socket.sockjs.client.Transport;
// import org.springframework.web.socket.sockjs.client.WebSocketTransport;

// import java.lang.reflect.Type;
// import java.util.List;
// import java.util.Map;
// import java.util.UUID;
// import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles REST calls and STOMP WebSocket interactions against the Spring backend.
 */
// public class CollabService {
//     private static final String BASE_URL = "http://localhost:8080";
//     private static final String WS_URL = "ws://localhost:8080/ws";

//     private final RestTemplate rest = new RestTemplate();
//     private final ObjectMapper mapper = new ObjectMapper();

//     private WebSocketStompClient stompClient;
//     private StompSession stompSession;
//     private String userId;
//     private String docId;

//     private final Map<String, LineCallback> lineHandlers = new ConcurrentHashMap<>();
//     private MessageCallback callback;

//     public interface MessageCallback {
//         void onCreate(String editorCode, String viewerCode);
//         void onUpdate(String content);
//         void onPresence(List<String> users);
//         void onCursor(String userId, int pos);
//     }

//     public interface LineCallback { void drawLine(String userId, Bounds b); }

//     public CollabService(MessageCallback callback) {
//         this.callback = callback;
//         this.userId = UUID.randomUUID().toString().substring(0,6);
//     }

//     /**
//      * Create a new user on the backend and return its generated id.
//      */
//     public String createUser(String username) {
//         // backend expects plain text username in body
//         return rest.postForObject(BASE_URL + "/users", username, String.class);
//     }

//     /**
//      * Create a new document (session) via REST, then connect WS and send CREATE.
//      */
//     public String createSession(String title, String username) {
//         // 1) create document
//         Document doc = rest.postForObject(
//                 BASE_URL + "/api/documents?title={t}", null, Document.class, title);
//         if (doc == null) throw new RuntimeException("Create document failed");
//         this.docId = doc.getId();

//         // 2) open WS/STOMP and subscribe
//         connectStomp();
//         stompSession.subscribe("/topic/user/" + userId, createFrameHandler());
//         stompSession.subscribe("/topic/session/" + docId, sessionFrameHandler());

//         // 3) send CREATE message
//         JsonNode msg = mapper.createObjectNode()
//                 .put("type", "CREATE")
//                 .putObject("sender")
//                     .put("id", userId).put("username", username).put("cursorPosition", 0)
//                 .put("code", "");
//         stompSession.send("/app/session/create", msg.toString().getBytes());

//         return docId;
//     }

//     /**
//      * Join an existing session by code, then connect WS and send JOIN.
//      */
//     public String joinSession(String code, String username) {
//         // try as editor
//         Document doc = rest.getForObject(
//                 BASE_URL + "/api/documents/editor/{c}", Document.class, code);
//         if (doc == null) {
//             // then as viewer
//             doc = rest.getForObject(
//                 BASE_URL + "/api/documents/viewer/{c}", Document.class, code);
//         }
//         if (doc == null) throw new RuntimeException("Invalid session code");
//         this.docId = doc.getId();

//         connectStomp();
//         stompSession.subscribe("/topic/user/" + userId, createFrameHandler());
//         stompSession.subscribe("/topic/session/" + docId, sessionFrameHandler());

//         // send JOIN message
//         JsonNode msg = mapper.createObjectNode()
//                 .put("type", "JOIN")
//                 .putObject("sender").put("id", userId).put("username", username).put("cursorPosition", 0)
//                 .put("code", code);
//         stompSession.send("/app/session/join", msg.toString().getBytes());

//         return docId;
//     }

//     private void connectStomp() {
//         if (stompClient != null) return;
//         var transports = List.<Transport>of(new WebSocketTransport(new StandardWebSocketClient()));
//         stompClient = new WebSocketStompClient(new SockJsClient(transports));
//         ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler(); ts.afterPropertiesSet();
//         stompClient.setTaskScheduler(ts);
//         try {
//             stompSession = stompClient.connect(WS_URL, new StompSessionHandlerAdapter() {}).get();
//         } catch (Exception e) {
//             e.printStackTrace();
//             throw new RuntimeException("Failed to connect to WebSocket", e);
//         }
//     }

//     private StompFrameHandler createFrameHandler() {
//         return new StompFrameHandler() {
//             @Override public Type getPayloadType(StompHeaders headers) { return byte[].class; }
//             @Override public void handleFrame(StompHeaders headers, Object payload) {
//                 try {
//                     JsonNode n = mapper.readTree((byte[])payload);
//                     if ("CREATE".equals(n.get("type").asText())) {
//                         callback.onCreate(n.get("editorCode").asText(), n.get("viewerCode").asText());
//                     }
//                 } catch (Exception e) { e.printStackTrace(); }
//             }
//         };
//     }

//     private StompFrameHandler sessionFrameHandler() {
//         return new StompFrameHandler() {
//             @Override public Type getPayloadType(StompHeaders headers) { return byte[].class; }
//             @Override public void handleFrame(StompHeaders headers, Object payload) {
//                 try {
//                     JsonNode n = mapper.readTree((byte[])payload);
//                     String type = n.get("type").asText();
//                     switch (type) {
//                         case "UPDATE": callback.onUpdate(n.get("content").asText()); break;
//                         case "PRESENCE": callback.onPresence(
//                             mapper.convertValue(n.get("activeUsers"), List.class)); break;
//                         case "CURSOR": callback.onCursor(n.get("userId").asText(), n.get("position").asInt()); break;
//                     }
//                 } catch (Exception e) { e.printStackTrace(); }
//             }
//         };
//     }

//     /** Send an edit operation over STOMP */
//     public void sendEdit(String content) {
//         if (stompSession != null) {
//             JsonNode msg = mapper.createObjectNode()
//                     .put("type","EDIT").put("userId",userId).put("content",content);
//             stompSession.send("/app/session/" + docId + "/edit", msg.toString().getBytes());
//         }
//     }

//     /** Call REST undo/redo */
//     public void undo() {
//         rest.postForLocation(BASE_URL + "/api/documents/{id}/undo?userId={u}",
//                 null, docId, userId);
//     }
//     public void redo() {
//         rest.postForLocation(BASE_URL + "/api/documents/{id}/redo?userId={u}",
//                 null, docId, userId);
//     }

//     public void setLineCallback(LineCallback cb) { lineHandlers.put("default", cb); }
// }

// public class CollabService {
//     private static final String BASE_URL = "http://192.168.1.5:3000";
//     private static final String WS_URL = "http://192.168.1.5:3000/ws";

//     private final RestTemplate rest = new RestTemplate();
//     private final ObjectMapper mapper = new ObjectMapper();

//     private WebSocketStompClient stompClient;
//     private StompSession stompSession;
//     private String userId;
//     private String docId;
//     private String username;
//     private MessageCallback callback;

//     public interface MessageCallback {
//         void onCreate(String editorCode, String viewerCode);
//         void onUpdate(String content);
//         void onPresence(java.util.List<String> users);
//         void onCursor(String userId, int pos);
//     }

//     public CollabService(MessageCallback callback) {
//         this.callback = callback;
//     }

//     public String createSession(String title, String username, String userId) {
//         this.username = username;
//         this.userId = userId;
//         // 1. Create document via REST
//         Document doc = rest.postForObject(BASE_URL + "/api/documents?title={t}", null, Document.class, title);
//         if (doc == null) throw new RuntimeException("Create document failed");
//         this.docId = doc.getId();

//         // 2. Connect WebSocket and subscribe
//         connectStomp();

//         // 3. Send CREATE message
//         var msg = mapper.createObjectNode()
//             .put("type", "CREATE")
//             .putObject("sender")
//                 .put("id", userId)
//                 .put("username", username)
//                 .put("cursorPosition", 0)
//             .put("code", "");
//         stompSession.send("/app/session/create", msg.toString().getBytes());

//         return docId;
//     }

//     public String joinSession(String code, String username, String userId) {
//         this.username = username;
//         this.userId = userId;
//         // Try as editor, then as viewer
//         Document doc = rest.getForObject(BASE_URL + "/api/documents/editor/{c}", Document.class, code);
//         if (doc == null) {
//             doc = rest.getForObject(BASE_URL + "/api/documents/viewer/{c}", Document.class, code);
//         }
//         if (doc == null) throw new RuntimeException("Invalid session code");
//         this.docId = doc.getId();

//         connectStomp();

//         // Send JOIN message
//         var msg = mapper.createObjectNode()
//             .put("type", "JOIN")
//             .putObject("sender")
//                 .put("id", userId)
//                 .put("username", username)
//                 .put("cursorPosition", 0)
//             .put("code", code);
//         stompSession.send("/app/session/join", msg.toString().getBytes());

//         return docId;
//     }

//     private void connectStomp() {
//         if (stompClient != null && stompSession != null && stompSession.isConnected()) return;
//         var transports = java.util.List.<Transport>of(new WebSocketTransport(new StandardWebSocketClient()));
//         stompClient = new WebSocketStompClient(new SockJsClient(transports));
//         ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler();
//         ts.afterPropertiesSet();
//         stompClient.setTaskScheduler(ts);
//         try {
//             stompSession = stompClient.connect(WS_URL, new StompSessionHandlerAdapter() {}).get();
//         } catch (Exception e) {
//             throw new RuntimeException("Failed to connect to WebSocket", e);
//         }
//         stompSession.subscribe("/topic/user/" + userId, createFrameHandler());
//         stompSession.subscribe("/topic/session/" + docId, sessionFrameHandler());
//     }

//     private StompFrameHandler createFrameHandler() {
//         return new StompFrameHandler() {
//             @Override public Type getPayloadType(StompHeaders headers) { return byte[].class; }
//             @Override public void handleFrame(StompHeaders headers, Object payload) {
//                 try {
//                     JsonNode n = mapper.readTree((byte[])payload);
//                     if ("CREATE".equals(n.get("type").asText())) {
//                         callback.onCreate(n.get("editorCode").asText(), n.get("viewerCode").asText());
//                     }
//                 } catch (Exception e) { e.printStackTrace(); }
//             }
//         };
//     }

//     private StompFrameHandler sessionFrameHandler() {
//         return new StompFrameHandler() {
//             @Override public Type getPayloadType(StompHeaders headers) { return byte[].class; }
//             @Override public void handleFrame(StompHeaders headers, Object payload) {
//                 try {
//                     JsonNode n = mapper.readTree((byte[])payload);
//                     String type = n.get("type").asText();
//                     switch (type) {
//                         case "UPDATE": callback.onUpdate(n.get("content").asText()); break;
//                         case "PRESENCE": callback.onPresence(
//                             mapper.convertValue(n.get("activeUsers"), java.util.List.class)); break;
//                         case "CURSOR": callback.onCursor(n.get("userId").asText(), n.get("position").asInt()); break;
//                     }
//                 } catch (Exception e) { e.printStackTrace(); }
//             }
//         };
//     }

//     public void sendEdit(String content) {
//         if (stompSession != null) {
//             var msg = mapper.createObjectNode()
//                 .put("type", "EDIT")
//                 .put("userId", userId)
//                 .put("content", content);
//             stompSession.send("/app/session/" + docId + "/edit", msg.toString().getBytes());
//         }
//     }
// }

// public class CollabService {
//     private static final String BASE_URL = "http://192.168.1.5:3000";
//     private static final String WS_URL = "ws://localhost:8080/ws";

//     private final RestTemplate rest = new RestTemplate();
//     private final ObjectMapper mapper = new ObjectMapper();

//     private WebSocketStompClient stompClient;
//     private StompSession stompSession;
//     private String userId;
//     private String docId;

//     private MessageCallback callback;

//     public interface MessageCallback {
//         void onCreate(String editorCode, String viewerCode);
//         void onUpdate(String content);
//         void onPresence(List<String> users);
//         void onCursor(String userId, int pos);
//     }

//     public CollabService(MessageCallback callback) {
//         this.callback = callback;
//         this.userId = UUID.randomUUID().toString().substring(0,6);
//     }

//     /** Create a new document via REST and open WS session */
//     public String createSession(String title, String username) {
//         // 1) create document via REST
//         Document doc = rest.postForObject(
//                 BASE_URL + "/api/documents?title={t}", null, Document.class, title);
//         if (doc == null) throw new RuntimeException("Create document failed");
//         this.docId = doc.getId();

//         // 2) connect and subscribe
//         connectStomp();
//         stompSession.subscribe("/topic/user/" + userId, createFrameHandler());
//         stompSession.subscribe("/topic/session/" + docId, sessionFrameHandler());

//         // 3) send just the User object (server expects User payload)
//         JsonNode userNode = mapper.createObjectNode()
//                 .put("id", userId)
//                 .put("username", username)
//                 .put("cursorPosition", 0);
//         stompSession.send("/app/session/create", userNode.toString().getBytes());

//         return docId;
//     }

//     /** Join existing session by code */
//     public String joinSession(String code, String username) {
//         // fetch by editor code or viewer code
//         Document doc = rest.getForObject(
//                 BASE_URL + "/api/documents/editor/{c}", Document.class, code);
//         if (doc == null) {
//             doc = rest.getForObject(
//                     BASE_URL + "/api/documents/viewer/{c}", Document.class, code);
//         }
//         if (doc == null) throw new RuntimeException("Invalid session code");
//         this.docId = doc.getId();

//         connectStomp();
//         stompSession.subscribe("/topic/user/" + userId, createFrameHandler());
//         stompSession.subscribe("/topic/session/" + docId, sessionFrameHandler());

//         // server expects Message payload with sender and code
//         JsonNode msg = mapper.createObjectNode()
//                 .putObject("sender")
//                     .put("id", userId)
//                     .put("username", username)
//                     .put("cursorPosition", 0)
//                 .parent()
//                 .put("code", code);
//         stompSession.send("/app/session/join", msg.toString().getBytes());

//         return docId;
//     }

//     private void connectStomp() {
//         if (stompClient != null && stompSession != null && stompSession.isConnected()) return;
//         var transports = List.<Transport>of(new WebSocketTransport(new StandardWebSocketClient()));
//         stompClient = new WebSocketStompClient(new SockJsClient(transports));
//         ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler(); ts.afterPropertiesSet();
//         stompClient.setTaskScheduler(ts);
//         stompSession = stompClient.connect(WS_URL, new StompSessionHandlerAdapter() {}).join();
//     }

//     private StompFrameHandler createFrameHandler() {
//         return new StompFrameHandler() {
//             @Override public Type getPayloadType(StompHeaders headers) { return byte[].class; }
//             @Override public void handleFrame(StompHeaders headers, Object payload) {
//                 try {
//                     JsonNode n = mapper.readTree((byte[])payload);
//                     if ("CREATE".equals(n.get("type").asText())) {
//                         callback.onCreate(n.get("editorCode").asText(), n.get("viewerCode").asText());
//                     }
//                 } catch (Exception e) { e.printStackTrace(); }
//             }
//         };
//     }

//     private StompFrameHandler sessionFrameHandler() {
//         return new StompFrameHandler() {
//             @Override public Type getPayloadType(StompHeaders headers) { return byte[].class; }
//             @Override public void handleFrame(StompHeaders headers, Object payload) {
//                 try {
//                     JsonNode n = mapper.readTree((byte[])payload);
//                     String type = n.get("type").asText();
//                     switch (type) {
//                         case "UPDATE": callback.onUpdate(n.get("content").asText()); break;
//                         case "PRESENCE": callback.onPresence(
//                             mapper.convertValue(n.get("activeUsers"), List.class)); break;
//                         case "CURSOR": callback.onCursor(n.get("userId").asText(), n.get("position").asInt()); break;
//                     }
//                 } catch (Exception e) { e.printStackTrace(); }
//             }
//         };
//     }

//     /** Send an edit over WS */
//     public void sendEdit(String content) {
//         if (stompSession != null && stompSession.isConnected()) {
//             JsonNode msg = mapper.createObjectNode()
//                     .put("type","EDIT")
//                     .put("userId",userId)
//                     .put("content",content);
//             stompSession.send("/app/session/" + docId + "/edit", msg.toString().getBytes());
//         }
//     }

//     /** Trigger REST undo/redo */
//     public void undo() {
//         rest.postForLocation(BASE_URL + "/api/documents/{id}/undo?userId={u}",
//                 null, docId, userId);
//     }
//     public void redo() {
//         rest.postForLocation(BASE_URL + "/api/documents/{id}/redo?userId={u}",
//                 null, docId, userId);
//     }

//     //public void setLineCallback(LineCallback cb) { /* ... */ }
// }

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

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CollabService {
    private static final String BASE_URL = "http://localhost:6969";
    private static final String WS_URL = "ws://localhost:6969/ws";

    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private String userId;
    private String docId;
    private String username;

    private MessageCallback callback;

    public interface MessageCallback {
        void onCreate(String editorCode, String viewerCode);
        void onJoin(Document doc);
        void onUpdate(String content);
        void onPresence(java.util.List<String> users);
        void onCursor(String userId, int pos);
    }

    public CollabService(MessageCallback callback) {
        this.callback = callback;
        this.userId = UUID.randomUUID().toString().substring(0, 6);
    }

    public User createUser(String username) {
        String id = rest.postForObject(BASE_URL + "/users", username, String.class);
        return new User(id, username, 0);
    }

    /** Create a new document via REST and open WS session */
    public Document createSession(String title, String username) {
        this.username = username;
        // 1) create document via REST
        System.err.println("=================================================/n");
        System.err.println("==================Getting doc====================/n");
        System.err.println("=================================================/n");
        Document doc = rest.postForObject(
                BASE_URL + "/api/documents?title={t}", null, Document.class, title);
        if (doc == null)
            throw new RuntimeException("Create document failed");
        this.docId = doc.getId();
        System.err.println("=================================================/n");
        System.err.println("====================got doc======================/n");
        System.err.println("=================================================/n");
        // 2) connect and subscribe
        System.err.println("=================================================/n");
        System.err.println("==================ws connecting==================/n");
        System.err.println("=================================================/n");
        connectStomp();
        System.err.println("=================================================/n");
        System.err.println("==================ws connected===================/n");
        System.err.println("=================================================/n");
        stompSession.subscribe("/topic/user/" + userId, createFrameHandler());
        stompSession.subscribe("/topic/session/" + docId, sessionFrameHandler());

        // 3) send User object (server expects User payload)
        JsonNode userNode = mapper.createObjectNode()
                .put("id", userId)
                .put("username", username)
                .put("cursorPosition", 0);
        stompSession.send("/app/session/create", userNode.toString().getBytes());

        return doc;
    }

    public Document joinSession(String code, String username) {
        System.err.println("=================================================");
        System.err.println("=======joining sess with code: " + code + "============");
        System.err.println("=================================================");
        this.username = username;
    
        // 1. Connect WebSocket if not already connected
        connectStomp();
    
        // 2. Subscribe to user and session topics (if not already)
        stompSession.subscribe("/topic/user/" + userId, createFrameHandler());
        // Session topic will be known after join response
    
        // 3. Send join message via WebSocket
        ObjectNode msg = mapper.createObjectNode();
        ObjectNode sender = msg.putObject("sender");
        sender.put("id", userId);
        sender.put("username", username);
        sender.put("cursorPosition", 0);
        msg.put("code", code);
        stompSession.send("/app/session/join", msg.toString().getBytes());
    
        // 4. Return null or a placeholder Document; actual doc info will come via WebSocket callback
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

            stompSession = stompClient.connect(WS_URL, new StompSessionHandlerAdapter() {}).get();
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
                    // if ("CREATE".equals(n.get("type").asText())) {
                    //     callback.onCreate(n.get("editorCode").asText(), n.get("viewerCode").asText());
                    // }
                    String type = n.get("type").asText();
                    if ("CREATE".equals(type)) {
                        callback.onCreate(n.get("editorCode").asText(), n.get("viewerCode").asText());
                    } else if ("JOIN".equals(type)) {
                        Document doc = new Document();
                        doc.setId(n.get("sessionId").asText());
                        doc.setEditorCode(n.has("editorCode") ? n.get("editorCode").asText() : "");
                        doc.setViewerCode(n.has("viewerCode") ? n.get("viewerCode").asText() : "");
                        callback.onJoin(doc);
                        // Now subscribe to session topic for further updates
                        stompSession.subscribe("/topic/session/" + doc.getId(), sessionFrameHandler());
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
                    String type = n.get("type").asText();
                    switch (type) {
                        case "JOIN":
                            // Build Document from JOIN message
                            // Document doc = new Document();
                            // doc.setId(n.get("sessionId").asText());
                            // doc.setEditorCode(n.has("editorCode") ? n.get("editorCode").asText() : "");
                            // doc.setViewerCode(n.has("viewerCode") ? n.get("viewerCode").asText() : "");
                            // callback.onJoin(doc);
                            Document doc = new Document();
                            doc.setId(n.get("sessionId").asText());
                            doc.setEditorCode(n.has("editorCode") ? n.get("editorCode").asText() : "");
                            doc.setViewerCode(n.has("viewerCode") ? n.get("viewerCode").asText() : "");
                            // Subscribe to session topic if not already
                            stompSession.subscribe("/topic/session/" + doc.getId(), sessionFrameHandler());
                            callback.onJoin(doc);
                            break;
                        case "UPDATE":
                            callback.onUpdate(n.get("content").asText());
                            break;
                        case "PRESENCE":
                            callback.onPresence(mapper.convertValue(n.get("editors"), List.class));
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

   

    /** Send an edit over WS */
    public void sendEdit(String content) {
        if (stompSession != null && stompSession.isConnected()) {
            // The backend expects a Message with sender and operation (for CRDT)
            JsonNode msg = mapper.createObjectNode()
                    .putObject("sender")
                    .put("id", userId)
                    .put("username", username)
                    .put("cursorPosition", 0)
                    //.parent()
                    .put("type", "EDIT")
                    .put("content", content);
            stompSession.send("/app/session/" + docId + "/edit", msg.toString().getBytes());
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
 // private StompFrameHandler sessionFrameHandler() {
    //     return new StompFrameHandler() {
    //         @Override
    //         public Type getPayloadType(StompHeaders headers) {
    //             return byte[].class;
    //         }

    //         @Override
    //         public void handleFrame(StompHeaders headers, Object payload) {
    //             try {
    //                 JsonNode n = mapper.readTree((byte[]) payload);
    //                 String type = n.get("type").asText();
    //                 switch (type) {
    //                     case "UPDATE":
    //                         callback.onUpdate(n.get("content").asText());
    //                         break;
    //                     case "PRESENCE":
    //                         callback.onPresence(
    //                                 mapper.convertValue(n.get("editors"), List.class));
    //                         break;
    //                     case "CURSOR":
    //                         callback.onCursor(
    //                                 n.get("sender").get("id").asText(),
    //                                 n.get("sender").get("cursorPosition").asInt());
    //                         break;
    //                 }
    //             } catch (Exception e) {
    //                 e.printStackTrace();
    //             }
    //         }
    //     };
    // } /** Join existing session by code */
    // public Document joinSession(String code, String username) {
    //     System.err.println("=================================================");
    //     System.err.println("=======joining sess with code:" +code+"============");
    //     System.err.println("=================================================");
    //     this.username = username;
    //     // fetch by editor code or viewer code
    //     Document doc = rest.getForObject(
    //             BASE_URL + "/api/documents/editor/{c}", Document.class, code);
    //     if (doc == null) {
    //         doc = rest.getForObject(
    //                 BASE_URL + "/api/documents/viewer/{c}", Document.class, code);
    //     }
    //     if (doc == null)
    //         throw new RuntimeException("Invalid session code");
    //     this.docId = doc.getId();

    //     connectStomp();
    //     stompSession.subscribe("/topic/user/" + userId, createFrameHandler());
    //     stompSession.subscribe("/topic/session/" + docId, sessionFrameHandler());

    //     // server expects Message payload with sender and code
    //     // ObjectNode msg = mapper.createObjectNode();
    //     // ObjectNode sender = msg.putObject("sender");
    //     // sender.put("id", userId);
    //     // sender.put("username", username);
    //     // sender.put("cursorPosition", 0);
    //     // msg.put("code", code);
    //     ObjectNode msg = mapper.createObjectNode();
    //     ObjectNode sender = msg.putObject("sender");
    //     sender.put("id", userId);
    //     sender.put("username", username);
    //     sender.put("cursorPosition", 0);
    //     msg.put("code", code);
    //     stompSession.send("/app/session/join", msg.toString().getBytes());

    //     // .putObject("sender")
    //     // .put("id", userId)
    //     // .put("username", username)
    //     // .put("cursorPosition", 0)
    //     // //.parent()
    //     // .put("code", code);
    //     stompSession.send("/app/session/join", msg.toString().getBytes());

    //     return doc;
    // }
