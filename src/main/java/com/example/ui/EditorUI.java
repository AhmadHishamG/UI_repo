// // package com.example.ui;
// // import javafx.application.Application;
// // import javafx.application.Platform;
// // import javafx.geometry.Bounds;
// // import javafx.geometry.Insets;
// // import javafx.scene.Node;
// // import javafx.scene.Scene;
// // import javafx.scene.control.Button;
// // import javafx.scene.control.Label;
// // import javafx.scene.control.TextArea;
// // import javafx.scene.control.TextField;
// // import javafx.scene.input.KeyEvent;
// // import javafx.scene.layout.HBox;
// // import javafx.scene.layout.Pane;
// // import javafx.scene.layout.VBox;
// // import javafx.scene.paint.Color;
// // import javafx.scene.shape.Line;
// // import javafx.stage.Stage;
// // import java.net.URI;
// // import java.net.http.HttpClient;
// // import java.net.http.WebSocket;
// // import java.util.HashMap;
// // import java.util.Map;
// // import java.util.UUID;
// // import java.util.concurrent.CompletionStage;

// /**
//  * EditorUI implements tasks 1-3:
//  * 1. User name input and JOIN message
//  * 2. TextArea editing with WebSocket send/receive
//  * 3. Display other users' cursors based on character position
//  */
// // public class EditorUI extends Application {
// //     private TextField nameField, joinField;
// //     private Button joinBtn;
// //     private TextArea textArea;

// //     private WebSocket webSocket;
// //     private final String userId = UUID.randomUUID().toString().substring(0, 6);

// //     // Task 3: cursor display state
// //     private final Map<String, Line> cursorLines = new HashMap<>();
// //     private final Color[] cursorColors = { Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE };

// //     @Override
// //     public void start(Stage stage) {
// //         // Name input (Task 1)
// //         nameField = new TextField();
// //         nameField.setPromptText("Enter your name");

// //         // Session code input (Tasks 4 stub)
// //         joinField = new TextField();
// //         joinField.setPromptText("Session code");

// //         joinBtn = new Button("Join");
// //         joinBtn.setOnAction(e -> joinSession(joinField.getText()));

// //         HBox topBar = new HBox(10,
// //                 new Label("Name:"), nameField,
// //                 new Label("Code:"), joinField, joinBtn
// //         );
// //         topBar.setPadding(new Insets(10));

// //         // Text area (Task 2)
// //         textArea = new TextArea();
// //         textArea.setWrapText(true);
// //         textArea.setDisable(true);
// //         textArea.addEventFilter(KeyEvent.KEY_TYPED, this::onLocalEdit);

// //         VBox layout = new VBox(topBar, textArea);
// //         Scene scene = new Scene(layout, 800, 600);

// //         stage.setScene(scene);
// //         stage.setTitle("Collaborative Editor (tasks 1-3)");
// //         stage.show();
// //     }

// //     /** Task 4 stub: open WebSocket and send JOIN */
// //     private void joinSession(String code) {
// //         textArea.setDisable(false);
// //         webSocket = HttpClient.newHttpClient()
// //                 .newWebSocketBuilder()
// //                 .buildAsync(URI.create("ws://localhost:8080/ws-endpoint"), new WebSocket.Listener() {
// //                     @Override
// //                     public void onOpen(WebSocket ws) {
// //                         String joinMsg = String.format(
// //                                 "{\"type\":\"JOIN\",\"userId\":\"%s\",\"name\":\"%s\",\"code\":\"%s\"}",
// //                                 userId, escapeJson(nameField.getText()), escapeJson(code)
// //                         );
// //                         ws.sendText(joinMsg, true);
// //                         WebSocket.Listener.super.onOpen(ws);
// //                     }

// //                     @Override
// //                     public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
// //                         String msg = data.toString();
// //                         if (msg.contains("\"type\":\"EDIT\"")) {
// //                             String content = msg.replaceAll(".*\\\"content\\\":\\\"([^\\\\]*)\\\".*", "$1");
// //                             Platform.runLater(() -> textArea.setText(unescapeJson(content)));
// //                         } else if (msg.contains("\"type\":\"CURSOR\"")) {
// //                             String uid = msg.replaceAll(".*\\\"userId\\\":\\\"([^\\\\]*)\\\".*", "$1");
// //                             int pos = Integer.parseInt(msg.replaceAll(".*\\\"pos\\\":(\\d+).*", "$1"));
// //                             Platform.runLater(() -> updateCursor(uid, pos));
// //                         }
// //                         return WebSocket.Listener.super.onText(ws, data, last);
// //                     }
// //                 }).join();
// //     }

// //     /** Task 2: send EDIT messages */
// //     private void onLocalEdit(KeyEvent ev) {
// //         if (webSocket != null) {
// //             String text = textArea.getText();
// //             String editMsg = String.format(
// //                     "{\"type\":\"EDIT\",\"userId\":\"%s\",\"content\":\"%s\"}",
// //                     userId, escapeJson(text)
// //             );
// //             webSocket.sendText(editMsg, true);
// //         }
// //     }

// //     /** Task 3: render other users' cursors based on caret node */
// //     private void updateCursor(String otherUser, int position) {
// //         Pane content = (Pane) textArea.lookup(".content");
// //         if (content == null) return;

// //         // remove existing cursor for user
// //         Line old = cursorLines.remove(otherUser);
// //         if (old != null) content.getChildren().remove(old);

// //         // preserve own caret
// //         int currentPos = textArea.getCaretPosition();

// //         // move caret to target position to locate .caret node
// //         textArea.positionCaret(position);
// //         Node caret = textArea.lookup(".caret");
// //         if (caret != null) {
// //             Bounds b = caret.getBoundsInParent();
// //             Line line = new Line(b.getMinX(), b.getMinY(), b.getMinX(), b.getMaxY());
// //             Color c = cursorColors[Math.abs(otherUser.hashCode()) % cursorColors.length];
// //             line.setStroke(c);
// //             line.setStrokeWidth(2);
// //             content.getChildren().add(line);
// //             cursorLines.put(otherUser, line);
// //         }

// //         // restore own caret position
// //         textArea.positionCaret(currentPos);
// //     }

// //     // Simple JSON escape/unescape
// //     private String escapeJson(String s) {
// //         return s.replace("\\", "\\\\").replace("\"", "\\\"");
// //     }
// //     private String unescapeJson(String s) {
// //         return s.replace("\\\"", "\"").replace("\\\\", "\\");
// //     }

// //     public static void main(String[] args) {
// //         launch(args);
// //     }
// // }

// // package com.example.ui;

// // import javafx.application.Application;
// // import javafx.application.Platform;
// // import javafx.geometry.Bounds;
// // import javafx.geometry.Insets;
// // import javafx.geometry.Pos;
// // import javafx.scene.Node;
// // import javafx.scene.Scene;
// // import javafx.scene.control.*;
// // import javafx.scene.input.Clipboard;
// // import javafx.scene.input.ClipboardContent;
// // import javafx.scene.input.KeyEvent;
// // import javafx.scene.layout.*;
// // import javafx.scene.paint.Color;
// // import javafx.scene.shape.Line;
// // import javafx.stage.FileChooser;
// // import javafx.stage.Stage;

// // import java.io.File;
// // import java.io.FileWriter;
// // import java.net.URI;
// // import java.net.http.HttpClient;
// // import java.net.http.WebSocket;
// // import java.util.*;
// // import java.util.concurrent.CompletionStage;

// // public class EditorUI extends Application {
// //     private TextArea textArea;

// //     // left‑panel controls
// //     private Button undoBtn, redoBtn, exportBtn;
// //     private TextField viewerCodeField, editorCodeField;
// //     private Button copyViewerBtn, copyEditorBtn;
// //     private ListView<String> usersList;

// //     private WebSocket webSocket;
// //     private final String userId = UUID.randomUUID().toString().substring(0, 6);

// //     // cursor lines for other users
// //     private final Map<String, Line> cursorLines = new HashMap<>();
// //     private final Color[] cursorColors = { Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE };

// //     @Override
// //     public void start(Stage stage) {
// //         // ── LEFT PANEL ─────────────────────────────────────────────────────────────

// //         // undo / redo / export
// //         undoBtn = new Button("↶");
// //         redoBtn = new Button("↷");
// //         exportBtn = new Button("Export");
// //         HBox topButtons = new HBox(5, undoBtn, redoBtn, exportBtn);
// //         topButtons.setAlignment(Pos.CENTER_LEFT);

// //         // viewer code
// //         Label viewerLbl = new Label("Viewer Code");
// //         viewerCodeField = new TextField("#yq1xrx"); // you will set actual code
// //         viewerCodeField.setEditable(false);
// //         copyViewerBtn = new Button("Copy");
// //         HBox viewerBox = new HBox(5, viewerCodeField, copyViewerBtn);
// //         viewerBox.setAlignment(Pos.CENTER_LEFT);

// //         // editor code
// //         Label editorLbl = new Label("Editor Code");
// //         editorCodeField = new TextField("#1jEo2K"); // you will set actual code
// //         editorCodeField.setEditable(false);
// //         copyEditorBtn = new Button("Copy");
// //         HBox editorBox = new HBox(5, editorCodeField, copyEditorBtn);
// //         editorBox.setAlignment(Pos.CENTER_LEFT);

// //         // active users
// //         Label usersLbl = new Label("Active Users");
// //         usersList = new ListView<>();
// //         usersList.setPrefHeight(200);

// //         VBox leftPane = new VBox(15,
// //                 topButtons,
// //                 viewerLbl, viewerBox,
// //                 editorLbl, editorBox,
// //                 usersLbl, usersList);
// //         leftPane.setPadding(new Insets(10));
// //         leftPane.setPrefWidth(250);
// //         leftPane.setStyle("-fx-background-color: #f4f4f4;");

// //         // ── TEXT AREA ────────────────────────────────────────────────────────────────

// //         textArea = new TextArea();
// //         textArea.setWrapText(true);
// //         textArea.setDisable(true);
// //         textArea.addEventFilter(KeyEvent.KEY_TYPED, this::onLocalEdit);

// //         // when Export clicked, save to file
// //         exportBtn.setOnAction(e -> {
// //             FileChooser fc = new FileChooser();
// //             fc.setInitialFileName("document.txt");
// //             File f = fc.showSaveDialog(stage);
// //             if (f != null) {
// //                 try (FileWriter w = new FileWriter(f)) {
// //                     w.write(textArea.getText());
// //                 } catch (Exception ex) {
// //                     ex.printStackTrace();
// //                 }
// //             }
// //         });

// //         // copy buttons
// //         copyViewerBtn.setOnAction(e -> copyToClipboard(viewerCodeField.getText()));
// //         copyEditorBtn.setOnAction(e -> copyToClipboard(editorCodeField.getText()));

// //         // ── ROOT LAYOUT ─────────────────────────────────────────────────────────────

// //         HBox root = new HBox(leftPane, textArea);
// //         HBox.setHgrow(textArea, Priority.ALWAYS);

// //         Scene scene = new Scene(root, 900, 600);
// //         stage.setScene(scene);
// //         stage.setTitle("Collaborative Editor");
// //         stage.show();

// //         // (after UI is shown you can join session, enable textArea, etc.)
// //     }

// //     private void copyToClipboard(String s) {
// //         ClipboardContent c = new ClipboardContent();
// //         c.putString(s);
// //         Clipboard.getSystemClipboard().setContent(c);
// //     }

// //     /** send EDIT messages when user types */
// //     private void onLocalEdit(KeyEvent ev) {
// //         if (webSocket != null) {
// //             String text = textArea.getText();
// //             String editMsg = String.format(
// //                     "{\"type\":\"EDIT\",\"userId\":\"%s\",\"content\":\"%s\"}",
// //                     userId, escapeJson(text));
// //             webSocket.sendText(editMsg, true);
// //         }
// //     }

// //     /** render other users' caret positions */
// //     private void updateCursor(String otherUser, int position) {
// //         Pane content = (Pane) textArea.lookup(".content");
// //         if (content == null)
// //             return;

// //         Line old = cursorLines.remove(otherUser);
// //         if (old != null)
// //             content.getChildren().remove(old);

// //         int myPos = textArea.getCaretPosition();
// //         textArea.positionCaret(position);
// //         Node caret = textArea.lookup(".caret");
// //         if (caret != null) {
// //             Bounds b = caret.getBoundsInParent();
// //             Line line = new Line(b.getMinX(), b.getMinY(), b.getMinX(), b.getMaxY());
// //             line.setStroke(cursorColors[Math.abs(otherUser.hashCode()) % cursorColors.length]);
// //             line.setStrokeWidth(2);
// //             content.getChildren().add(line);
// //             cursorLines.put(otherUser, line);
// //         }
// //         textArea.positionCaret(myPos);
// //     }

// //     private String escapeJson(String s) {
// //         return s.replace("\\", "\\\\").replace("\"", "\\\"");
// //     }

// //     public static void main(String[] args) {
// //         launch(args);
// //     }
// // }
// // package com.example.ui;

// // import javafx.application.Application;
// // import javafx.application.Platform;
// // import javafx.geometry.Bounds;
// // import javafx.geometry.Insets;
// // import javafx.geometry.Pos;
// // import javafx.scene.Node;
// // import javafx.scene.Scene;
// // import javafx.scene.control.*;
// // import javafx.scene.input.Clipboard;
// // import javafx.scene.input.ClipboardContent;
// // import javafx.scene.input.KeyEvent;
// // import javafx.scene.layout.*;
// // import javafx.scene.paint.Color;
// // import javafx.scene.shape.Line;
// // import javafx.stage.FileChooser;
// // import javafx.stage.Stage;

// // import java.io.File;
// // import java.io.FileWriter;
// // import java.net.URI;
// // import java.net.http.HttpClient;
// // import java.net.http.WebSocket;
// // import java.util.*;
// // import java.util.concurrent.CompletionStage;

// // public class EditorUI extends Application {
// //     private TextArea textArea;
// //     private Button undoBtn, redoBtn, exportBtn;
// //     private TextField viewerCodeField, editorCodeField;
// //     private Button copyViewerBtn, copyEditorBtn;
// //     private ListView<String> usersList;

// //     private WebSocket webSocket;
// //     private final String userId = UUID.randomUUID().toString().substring(0, 6);
// //     private final String serverUri = "ws://localhost:8080/ws";

// //     private final Map<String, Line> cursorLines = new HashMap<>();
// //     private final Color[] cursorColors = { Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE };

// //     @Override
// //     public void start(Stage stage) {
// //         // LEFT PANEL
// //         undoBtn = new Button("↶");
// //         redoBtn = new Button("↷");
// //         exportBtn = new Button("Export");
// //         HBox topButtons = new HBox(5, undoBtn, redoBtn, exportBtn);
// //         topButtons.setAlignment(Pos.CENTER_LEFT);

// //         Label viewerLbl = new Label("Viewer Code");
// //         viewerCodeField = new TextField(); viewerCodeField.setEditable(false);
// //         copyViewerBtn = new Button("Copy");
// //         HBox viewerBox = new HBox(5, viewerCodeField, copyViewerBtn);

// //         Label editorLbl = new Label("Editor Code");
// //         editorCodeField = new TextField(); editorCodeField.setEditable(false);
// //         copyEditorBtn = new Button("Copy");
// //         HBox editorBox = new HBox(5, editorCodeField, copyEditorBtn);

// //         Label usersLbl = new Label("Active Users");
// //         usersList = new ListView<>(); usersList.setPrefHeight(200);

// //         VBox leftPane = new VBox(15, topButtons, viewerLbl, viewerBox, editorLbl, editorBox, usersLbl, usersList);
// //         leftPane.setPadding(new Insets(10));
// //         leftPane.setPrefWidth(240);
// //         leftPane.setStyle("-fx-background-color: #f4f4f4;");

// //         // TEXT AREA
// //         textArea = new TextArea();
// //         textArea.setWrapText(true);
// //         textArea.setDisable(true);
// //         textArea.addEventFilter(KeyEvent.KEY_TYPED, this::onLocalEdit);

// //         exportBtn.setOnAction(e -> doExport(stage));
// //         copyViewerBtn.setOnAction(e -> copyToClipboard(viewerCodeField.getText()));
// //         copyEditorBtn.setOnAction(e -> copyToClipboard(editorCodeField.getText()));

// //         HBox root = new HBox(leftPane, textArea);
// //         HBox.setHgrow(textArea, Priority.ALWAYS);

// //         Scene scene = new Scene(root, 900, 600);
// //         stage.setScene(scene);
// //         stage.setTitle("Collaborative Editor");
// //         stage.show();

// //         // connect immediately and join using auto codes
// //         connectWebSocket();
// //     }

// //     private void connectWebSocket() {
// //         webSocket = HttpClient.newHttpClient()
// //             .newWebSocketBuilder()
// //             .buildAsync(URI.create(serverUri), new WebSocket.Listener() {
// //                 @Override public void onOpen(WebSocket ws) {
// //                     String join = String.format("{\"type\":\"CREATE\",\"userId\":\"%s\"}", userId);
// //                     ws.sendText(join, true);
// //                     WebSocket.Listener.super.onOpen(ws);
// //                 }
// //                 @Override public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
// //                     String msg = data.toString();
// //                     Platform.runLater(() -> handleMessage(msg));
// //                     return WebSocket.Listener.super.onText(ws, data, last);
// //                 }
// //             }).join();
// //     }

// //     private void handleMessage(String msg) {
// //         if (msg.contains("\"type\":\"CREATE\"")) {
// //             String viewer = extract(msg, "viewerCode");
// //             String editor = extract(msg, "editorCode");
// //             viewerCodeField.setText(viewer);
// //             editorCodeField.setText(editor);
// //             textArea.setDisable(false);
// //         }
// //         if (msg.contains("\"type\":\"UPDATE\"")) {
// //             String content = extract(msg, "content");
// //             textArea.setText(unescapeJson(content));
// //         }
// //         if (msg.contains("\"type\":\"PRESENCE\"")) {
// //             String list = msg.replaceAll(".*\"activeUsers\":\\[(.*)\\].*", "$1");
// //             String[] users = list.replaceAll("[\" ]", "").split(",");
// //             usersList.getItems().setAll(users);
// //         }
// //         if (msg.contains("\"type\":\"CURSOR\"")) {
// //             String uid = extract(msg, "userId");
// //             int pos = Integer.parseInt(extract(msg, "position"));
// //             updateCursor(uid, pos);
// //         }
// //     }

// //     private void onLocalEdit(KeyEvent ev) {
// //         if (webSocket!=null) {
// //             String txt = textArea.getText();
// //             String m = String.format("{\"type\":\"EDIT\",\"userId\":\"%s\",\"content\":\"%s\"}", userId, escapeJson(txt));
// //             webSocket.sendText(m, true);
// //         }
// //     }

// //     private void updateCursor(String other, int position) {
// //         Pane content = (Pane) textArea.lookup(".content"); if (content==null) return;
// //         Line old = cursorLines.remove(other); if (old!=null) content.getChildren().remove(old);
// //         int me = textArea.getCaretPosition(); textArea.positionCaret(position);
// //         Node caret = textArea.lookup(".caret");
// //         if (caret!=null) {
// //             Bounds b = caret.getBoundsInParent();
// //             Line line = new Line(b.getMinX(), b.getMinY(), b.getMinX(), b.getMaxY());
// //             line.setStroke(cursorColors[Math.abs(other.hashCode())%cursorColors.length]);
// //             line.setStrokeWidth(2);
// //             content.getChildren().add(line);
// //             cursorLines.put(other,line);
// //         }
// //         textArea.positionCaret(me);
// //     }

// //     private void doExport(Stage stage) {
// //         FileChooser fc = new FileChooser(); fc.setInitialFileName("doc.txt");
// //         File f = fc.showSaveDialog(stage); if (f==null) return;
// //         try (FileWriter w=new FileWriter(f)) { w.write(textArea.getText()); } catch(Exception e){e.printStackTrace();}
// //     }

// //     private void copyToClipboard(String s) {
// //         ClipboardContent cc = new ClipboardContent(); cc.putString(s);
// //         Clipboard.getSystemClipboard().setContent(cc);
// //     }

// //     private String extract(String json, String key) {
// //         return json.replaceAll(".*\""+key+"\":\"([^\\]*)\".*", "$1");
// //     }
// //     private String escapeJson(String s) { return s.replace("\\","\\\\").replace("\"","\\\""); }
// //     private String unescapeJson(String s) { return s.replace("\\\"","\"").replace("\\\\","\\"); }

// //     public static void main(String[] args) { launch(args); }
// // }

// // package com.example.ui;

// // import javafx.application.Application;
// // import javafx.application.Platform;
// // import javafx.geometry.Bounds;
// // import javafx.geometry.Insets;
// // import javafx.geometry.Pos;
// // import javafx.scene.Node;
// // import javafx.scene.Scene;
// // import javafx.scene.control.*;
// // import javafx.scene.input.Clipboard;
// // import javafx.scene.input.ClipboardContent;
// // import javafx.scene.input.KeyEvent;
// // import javafx.scene.layout.*;
// // import javafx.scene.paint.Color;
// // import javafx.scene.shape.Line;
// // import javafx.stage.FileChooser;
// // import javafx.stage.Stage;

// // import java.io.File;
// // import java.io.FileWriter;
// // import java.net.URI;
// // import java.net.http.HttpClient;
// // import java.net.http.WebSocket;
// // import java.util.*;
// // import java.util.concurrent.CompletionStage;
// // import java.util.concurrent.ExecutionException;

// // public class EditorUI extends Application {
// //     private Stage primaryStage;
// //     private Scene nameScene, lobbyScene, editorScene;

// //     // UI controls for name screen
// //     private TextField nameField;
// //     private Button nameNextBtn;

// //     // UI controls for lobby (create/join)
// //     private Button createBtn, joinBtn;
// //     private TextField joinCodeField;

// //     // UI controls for editor
// //     private TextArea textArea;
// //     private TextField viewerCodeField, editorCodeField;
// //     private Button copyViewerBtn, copyEditorBtn, exportBtn;
// //     private ListView<String> usersList;
// //     private Button undoBtn, redoBtn;

// //     private String userId = UUID.randomUUID().toString().substring(0,6);
// //     private String username;
// //     private WebSocket webSocket;
// //     private final Map<String,Line> cursorLines = new HashMap<>();
// //     private final Color[] cursorColors = {Color.RED,Color.BLUE,Color.GREEN,Color.ORANGE};
// //     private final String WS_URI = "ws://localhost:8080/ws";

// //     @Override
// //     public void start(Stage stage) {
// //         this.primaryStage = stage;
// //         buildNameScene();
// //         buildLobbyScene();
// //         buildEditorScene();
// //         primaryStage.setTitle("Collaborative Editor");
// //         primaryStage.setScene(nameScene);
// //         primaryStage.show();
// //     }

// //     private void buildNameScene() {
// //         nameField = new TextField();
// //         nameField.setPromptText("Enter your name");
// //         nameNextBtn = new Button("Next");
// //         nameNextBtn.setOnAction(e -> {
// //             username = nameField.getText().trim();
// //             if (!username.isEmpty()) {
// //                 primaryStage.setScene(lobbyScene);
// //             }
// //         });
// //         HBox row = new HBox(10, new Label("Name:"), nameField, nameNextBtn);
// //         row.setAlignment(Pos.CENTER);
// //         VBox root = new VBox(20, row);
// //         root.setAlignment(Pos.CENTER);
// //         nameScene = new Scene(root, 600, 400);
// //     }

// //     private void buildLobbyScene() {
// //         createBtn = new Button("Create Session");
// //         joinCodeField = new TextField();
// //         joinCodeField.setPromptText("Enter session code");
// //         joinBtn = new Button("Join Session");

// //         createBtn.setOnAction(e -> connectAndSend("CREATE", null));
// //         joinBtn.setOnAction(e -> connectAndSend("JOIN", joinCodeField.getText().trim()));

// //         HBox row1 = new HBox(10, createBtn);
// //         HBox row2 = new HBox(10, joinCodeField, joinBtn);
// //         row1.setAlignment(Pos.CENTER);
// //         row2.setAlignment(Pos.CENTER);
// //         VBox root = new VBox(20, row1, row2);
// //         root.setAlignment(Pos.CENTER);
// //         lobbyScene = new Scene(root, 600, 400);
// //     }

// //     private void buildEditorScene() {
// //         // left pane
// //         undoBtn = new Button("↶"); redoBtn = new Button("↷"); exportBtn = new Button("Export");
// //         HBox topButtons = new HBox(5, undoBtn, redoBtn, exportBtn);
// //         viewerCodeField = new TextField(); viewerCodeField.setEditable(false);
// //         copyViewerBtn = new Button("Copy");
// //         HBox viewBox = new HBox(5, new Label("Viewer Code"), viewerCodeField, copyViewerBtn);
// //         editorCodeField = new TextField(); editorCodeField.setEditable(false);
// //         copyEditorBtn = new Button("Copy");
// //         HBox editBox = new HBox(5, new Label("Editor Code"), editorCodeField, copyEditorBtn);
// //         usersList = new ListView<>(); usersList.setPrefHeight(200);
// //         VBox left = new VBox(10, topButtons, viewBox, editBox, new Label("Active Users"), usersList);
// //         left.setPadding(new Insets(10)); left.setPrefWidth(250); left.setStyle("-fx-background-color:#f4f4f4");
// //         // text area
// //         textArea = new TextArea(); textArea.setWrapText(true); textArea.setDisable(true);
// //         textArea.addEventFilter(KeyEvent.KEY_TYPED, this::onLocalEdit);
// //         exportBtn.setOnAction(e -> doExport());
// //         copyViewerBtn.setOnAction(e -> copyClipboard(viewerCodeField.getText()));
// //         copyEditorBtn.setOnAction(e -> copyClipboard(editorCodeField.getText()));
// //         HBox root = new HBox(left, textArea);
// //         HBox.setHgrow(textArea, Priority.ALWAYS);
// //         editorScene = new Scene(root, 900, 600);
// //     }

// //     private void connectAndSend(String type, String code) {
// //         primaryStage.setScene(editorScene);
// //         HttpClient.newHttpClient()
// //             .newWebSocketBuilder()
// //             .buildAsync(URI.create(WS_URI), new WebSocket.Listener() {
// //                 @Override public void onOpen(WebSocket ws) {
// //                     webSocket = ws;
// //                     String msg = String.format("{\"type\":\"%s\",\"userId\":\"%s\",\"name\":\"%s\"%s}",
// //                         type, userId, username,
// //                         code!=null?",\"code\":\""+code+"\"":"");
// //                     ws.sendText(msg, true);
// //                     WebSocket.Listener.super.onOpen(ws);
// //                 }
// //                 @Override public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
// //                     Platform.runLater(() -> handleMessage(data.toString()));
// //                     return WebSocket.Listener.super.onText(ws, data, last);
// //                 }
// //             }).exceptionally(ex-> { ex.printStackTrace(); return null; });
// //     }

// //     private void handleMessage(String msg) {
// //         if (msg.contains("\"type\":\"CREATE\"")) {
// //             viewerCodeField.setText(extract(msg, "viewerCode"));
// //             editorCodeField.setText(extract(msg, "editorCode"));
// //             textArea.setDisable(false);
// //         }
// //         if (msg.contains("\"type\":\"UPDATE\"")) {
// //             textArea.setText(unesc(extract(msg, "content")));
// //         }
// //         if (msg.contains("\"type\":\"PRESENCE\"")) {
// //             String list = msg.replaceAll(".*\"activeUsers\":\\[(.*)\\].*", "$1");
// //             usersList.getItems().setAll(list.replaceAll("[\" ]","").split(","));
// //         }
// //         if (msg.contains("\"type\":\"CURSOR\"")) {
// //             String uid = extract(msg, "userId");
// //             int pos = Integer.parseInt(extract(msg, "position"));
// //             updateCursor(uid, pos);
// //         }
// //     }

// //     private void onLocalEdit(KeyEvent ev) {
// //         if (webSocket!=null) {
// //             String m = String.format("{\"type\":\"EDIT\",\"userId\":\"%s\",\"content\":\"%s\"}",
// //                     userId, escape(textArea.getText()));
// //             webSocket.sendText(m,true);
// //         }
// //     }

// //     private void updateCursor(String other,int p){
// //         Pane pane=(Pane)textArea.lookup(".content"); if(pane==null)return;
// //         Line old=cursorLines.remove(other); if(old!=null)pane.getChildren().remove(old);
// //         int me=textArea.getCaretPosition(); textArea.positionCaret(p);
// //         Node caret=textArea.lookup(".caret");
// //         if(caret!=null){Bounds b=caret.getBoundsInParent();Line l=new Line(b.getMinX(),b.getMinY(),b.getMinX(),b.getMaxY());
// //             l.setStroke(cursorColors[Math.abs(other.hashCode())%cursorColors.length]);l.setStrokeWidth(2);
// //             pane.getChildren().add(l);cursorLines.put(other,l);}        
// //         textArea.positionCaret(me);
// //     }

// //     private void doExport() {
// //         FileChooser fc=new FileChooser();fc.setInitialFileName("doc.txt");
// //         File f=fc.showSaveDialog(primaryStage); if(f!=null) try(FileWriter w=new FileWriter(f)){w.write(textArea.getText());}catch(Exception e){}}

// //     private void copyClipboard(String s){ClipboardContent c=new ClipboardContent();c.putString(s);Clipboard.getSystemClipboard().setContent(c);}
// //     private String extract(String j,String k){return j.replaceAll(".*\""+k+"\":\"([^\\]*)\".*","$1");}
// //     private String escape(String s){return s.replace("\\","\\\\").replace("\"","\\\"");}
// //     private String unesc(String s){return s.replace("\\\"","\"").replace("\\\\","\\");}

// //     public static void main(String[] args){launch(args);}
// // }

// package com.example.ui;

// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;

// import javafx.application.Application;
// import javafx.application.Platform;
// import javafx.geometry.Bounds;
// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
// import javafx.scene.Node;
// import javafx.scene.Scene;
// import javafx.scene.control.*;
// import javafx.scene.input.Clipboard;
// import javafx.scene.input.ClipboardContent;
// import javafx.scene.input.KeyEvent;
// import javafx.scene.layout.*;
// import javafx.scene.paint.Color;
// import javafx.scene.shape.Line;
// import javafx.stage.FileChooser;
// import javafx.stage.Stage;

// import org.springframework.messaging.simp.stomp.StompHeaders;
// import org.springframework.messaging.simp.stomp.StompSession;
// import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
// import org.springframework.web.client.RestTemplate;
// import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
// import org.springframework.web.socket.client.standard.StandardWebSocketClient;
// import org.springframework.web.socket.messaging.WebSocketStompClient;
// import org.springframework.web.socket.sockjs.client.SockJsClient;
// import org.springframework.web.socket.sockjs.client.Transport;
// import org.springframework.web.socket.sockjs.client.WebSocketTransport;

// // import com.fasterxml.jackson.databind.ObjectMapper;

// import java.io.File;
// import java.io.FileWriter;
// import java.lang.reflect.Type;

// // public class EditorUI extends Application {
// //     private Stage primary;
// //     private Scene nameScene, homeScene, editorScene;
// //     private TextField nameField, codeField;
// //     private Button createBtn, joinBtn;

// //     private TextArea textArea;
// //     private TextField viewerCodeField, editorCodeField;
// //     private Button copyViewerBtn, copyEditorBtn, undoBtn, redoBtn, exportBtn;
// //     private ListView<String> usersList;

// //     private RestTemplate rest = new RestTemplate();
// //     private static final String REST_URL = "http://localhost:8080/api/documents";
// //     private static final String WS_URL = "ws://localhost:8080/ws";
// //     private WebSocketStompClient stompClient;
// //     private StompSession stompSession;
// //     private String docId;
// //     private String userId;
// //     private final ObjectMapper mapper = new ObjectMapper();

// //     private Map<String, Line> cursorLines = new HashMap<>();
// //     private Color[] cursorColors = { Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE };

// //     @Override
// //     public void start(Stage stage) {
// //         primary = stage;
// //         buildNameScene();
// //         buildHomeScene();
// //         buildEditorScene();
// //         primary.setScene(nameScene);
// //         primary.setTitle("Collaborative Editor");
// //         primary.show();
// //     }

// //     private void buildNameScene() {
// //         nameField = new TextField();
// //         nameField.setPromptText("Enter your name");
// //         Button next = new Button("Next");
// //         next.setOnAction(e -> {
// //             if (!nameField.getText().isBlank()) {
// //                 userId = UUID.randomUUID().toString().substring(0, 6);
// //                 primary.setScene(homeScene);
// //             }
// //         });
// //         VBox v = new VBox(10, new Label("Your name:"), nameField, next);
// //         v.setAlignment(Pos.CENTER);
// //         v.setPadding(new Insets(20));
// //         nameScene = new Scene(v, 400, 200);
// //     }

// //     private void buildHomeScene() {
// //         createBtn = new Button("Create Session");
// //         codeField = new TextField();
// //         codeField.setPromptText("Session code");
// //         joinBtn = new Button("Join Session");
// //         createBtn.setOnAction(e -> createSession());
// //         joinBtn.setOnAction(e -> joinSession(codeField.getText()));
// //         VBox v = new VBox(10, createBtn, new HBox(5, codeField, joinBtn));
// //         v.setAlignment(Pos.CENTER);
// //         v.setPadding(new Insets(20));
// //         homeScene = new Scene(v, 400, 200);
// //     }

// //     private void buildEditorScene() {
// //         undoBtn = new Button("↶");
// //         redoBtn = new Button("↷");
// //         exportBtn = new Button("Export");
// //         HBox topButtons = new HBox(5, undoBtn, redoBtn, exportBtn);
// //         viewerCodeField = new TextField();
// //         viewerCodeField.setEditable(false);
// //         copyViewerBtn = new Button("Copy");
// //         editorCodeField = new TextField();
// //         editorCodeField.setEditable(false);
// //         copyEditorBtn = new Button("Copy");
// //         usersList = new ListView<>();
// //         usersList.setPrefHeight(150);
// //         VBox left = new VBox(10, topButtons,
// //                 new Label("Viewer Code"), new HBox(5, viewerCodeField, copyViewerBtn),
// //                 new Label("Editor Code"), new HBox(5, editorCodeField, copyEditorBtn),
// //                 new Label("Active Users"), usersList);
// //         left.setPadding(new Insets(10));
// //         left.setPrefWidth(250);
// //         textArea = new TextArea();
// //         textArea.setDisable(true);
// //         textArea.setWrapText(true);
// //         textArea.addEventFilter(KeyEvent.KEY_TYPED, this::sendEdit);
// //         exportBtn.setOnAction(e -> doExport());
// //         copyViewerBtn.setOnAction(e -> copyToClipboard(viewerCodeField.getText()));
// //         copyEditorBtn.setOnAction(e -> copyToClipboard(editorCodeField.getText()));
// //         HBox root = new HBox(left, textArea);
// //         HBox.setHgrow(textArea, Priority.ALWAYS);
// //         editorScene = new Scene(root, 900, 600);
// //     }

// //     private void createSession() {
// //         // call REST to create document
// //         var doc = rest.getForObject(REST_URL + "?title=untitled", null, Document.class);
// //         docId = ((Document) doc).getId();
// //         setupStomp();
// //         primary.setScene(editorScene);
// //     }

// //     private void joinSession(String code) {
// //         // fetch by editor code or viewer
// //         Document doc = rest.getForObject(REST_URL + "/editor/" + code, Document.class);
// //         if (doc == null)
// //             doc = rest.getForObject(REST_URL + "/viewer/" + code, Document.class);
// //         if (doc != null) {
// //             docId = doc.getId();
// //             setupStomp();
// //             primary.setScene(editorScene);
// //             textArea.setText(rest.getForObject(REST_URL + "/" + docId + "/text", String.class));
// //         }
// //     }

// //     private void setupStomp() {
// //         if (stompClient != null)
// //             return;
// //         var transports = List.<Transport>of(new WebSocketTransport(new StandardWebSocketClient()));
// //         stompClient = new WebSocketStompClient(new SockJsClient(transports));
// //         ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler();
// //         ts.afterPropertiesSet();
// //         stompClient.setTaskScheduler(ts);
// //         stompClient.connect(WS_URL, new StompSessionHandlerAdapter() {
// //             @Override
// //             public void afterConnected(StompSession session, StompHeaders headers) {
// //                 stompSession = session;
// //                 // session.subscribe("/topic/session/" + docId, msg -> {
// //                 //     try {
// //                 //         JsonNode n = mapper.readTree(msg.getPayload());
// //                 //         Platform.runLater(() -> handle(n));
// //                 //     } catch (Exception ex) {
// //                 //         ex.printStackTrace();
// //                 //     }
// //                 // });

// //                 session.subscribe("/topic/session/" + docId, new StompSessionHandlerAdapter() {
// //                     @Override
// //                     public void handleFrame(StompHeaders headers, Object payload) {
// //                         try {
// //                             JsonNode n = mapper.readTree((byte[]) payload);
// //                             Platform.runLater(() -> handle(n));
// //                         } catch (Exception ex) {
// //                             ex.printStackTrace();
// //                         }
// //                     }

// //                     @Override
// //                     public Type getPayloadType(StompHeaders headers) {
// //                         return byte[].class;
// //                     }
// //                 });
// //                 // send JOIN
// //                 var join = mapper.createObjectNode()
// //                         .put("type", "JOIN").put("userId", userId).put("code", docId);
// //                 session.send("/app/session/join", join.toString().getBytes());
// //             }
// //         });
// //     }

// //     private void handle(JsonNode n) {
// //         switch (n.get("type").asText()) {
// //             case "CREATE":
// //                 viewerCodeField.setText(n.get("viewerCode").asText());
// //                 editorCodeField.setText(n.get("editorCode").asText());
// //                 textArea.setDisable(false);
// //                 break;
// //             case "UPDATE":
// //                 textArea.setText(n.get("content").asText());
// //                 break;
// //             case "PRESENCE":
// //                 List<String> users = new ArrayList<>();
// //                 n.get("activeUsers").forEach(u -> users.add(u.asText()));
// //                 usersList.getItems().setAll(users);
// //                 break;
// //             case "CURSOR":
// //                 updateCursor(n.get("userId").asText(), n.get("position").asInt());
// //                 break;
// //         }
// //     }

// //     private void sendEdit(KeyEvent ev) {
// //         if (stompSession != null) {
// //             var m = mapper.createObjectNode()
// //                     .put("type", "EDIT").put("userId", userId)
// //                     .put("content", textArea.getText());
// //             stompSession.send("/app/session/" + docId + "/edit", m.toString().getBytes());
// //         }
// //     }

// //     private void doExport() {
// //         FileChooser fc = new FileChooser();
// //         fc.setInitialFileName("doc.txt");
// //         File f = fc.showSaveDialog(primary);
// //         if (f == null)
// //             return;
// //         try (FileWriter w = new FileWriter(f)) {
// //             w.write(textArea.getText());
// //         } catch (Exception e) {
// //             e.printStackTrace();
// //         }
// //     }

// //     private void copyToClipboard(String s) {
// //         var c = new ClipboardContent();
// //         c.putString(s);
// //         Clipboard.getSystemClipboard().setContent(c);
// //     }

// //     private void updateCursor(String u, int pos) {
// //         Pane p = (Pane) textArea.lookup(".content");
// //         if (p == null)
// //             return;
// //         Line old = cursorLines.remove(u);
// //         if (old != null)
// //             p.getChildren().remove(old);
// //         int me = textArea.getCaretPosition();
// //         textArea.positionCaret(pos);
// //         Node caret = textArea.lookup(".caret");
// //         if (caret != null) {
// //             Bounds b = caret.getBoundsInParent();
// //             Line ln = new Line(b.getMinX(), b.getMinY(), b.getMinX(), b.getMaxY());
// //             ln.setStroke(cursorColors[Math.abs(u.hashCode()) % cursorColors.length]);
// //             ln.setStrokeWidth(2);
// //             p.getChildren().add(ln);
// //             cursorLines.put(u, ln);
// //         }
// //         textArea.positionCaret(me);
// //     }

// //     public static void main(String[] args) {
// //         launch(args);
// //     }
// // }

// // ...existing imports...
// import java.util.*;
// //import java.util.concurrent.ExecutionException;

// import javafx.scene.paint.Color;
// import javafx.scene.shape.Line;
// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;

// // public class EditorUI extends Application {
// //     private Stage primary;
// //     private Scene nameScene, homeScene, editorScene;
// //     private TextField nameField, codeField;
// //     private Button createBtn, joinBtn;
// //     private TextArea textArea;
// //     private TextField viewerCodeField, editorCodeField;
// //     private Button copyViewerBtn, copyEditorBtn, undoBtn, redoBtn, exportBtn;
// //     private ListView<String> usersList;

// //     private final Map<String, Line> cursorLines = new HashMap<>();
// // private final Color[] cursorColors = { Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE };
// //     private String userId;
// //     private String username;
// //     private String docId;

// //     private CollabService collabService;

// //     @Override
// //     public void start(Stage stage) {
// //         primary = stage;
// //         buildNameScene();
// //         buildHomeScene();
// //         buildEditorScene();
// //         primary.setScene(nameScene);
// //         primary.setTitle("Collaborative Editor");
// //         primary.show();
// //     }

// //     private void buildNameScene() {
// //         nameField = new TextField();
// //         nameField.setPromptText("Enter your name");
// //         Button next = new Button("Next");
// //         next.setOnAction(e -> {
// //             if (!nameField.getText().isBlank()) {
// //                 username = nameField.getText().trim();
// //                 userId = UUID.randomUUID().toString().substring(0, 6);
// //                 // Initialize CollabService with callbacks
// //                 collabService = new CollabService(new CollabService.MessageCallback() {
// //                     @Override
// //                     public void onCreate(String editorCode, String viewerCode) {
// //                         Platform.runLater(() -> {
// //                             editorCodeField.setText(editorCode);
// //                             viewerCodeField.setText(viewerCode);
// //                             textArea.setDisable(false);
// //                         });
// //                     }
// //                     @Override
// //                     public void onUpdate(String content) {
// //                         Platform.runLater(() -> textArea.setText(content));
// //                     }
// //                     @Override
// //                     public void onPresence(List<String> users) {
// //                         Platform.runLater(() -> usersList.getItems().setAll(users));
// //                     }
// //                     @Override
// //                     public void onCursor(String userId, int pos) {
// //                         Platform.runLater(() -> updateCursor(userId, pos));
// //                     }
// //                 });
// //                 primary.setScene(homeScene);
// //             }
// //         });
// //         VBox v = new VBox(10, new Label("Your name:"), nameField, next);
// //         v.setAlignment(Pos.CENTER);
// //         v.setPadding(new Insets(20));
// //         nameScene = new Scene(v, 400, 200);
// //     }

// //     private void buildHomeScene() {
// //         createBtn = new Button("Create Session");
// //         codeField = new TextField();
// //         codeField.setPromptText("Session code");
// //         joinBtn = new Button("Join Session");
// //         createBtn.setOnAction(e -> createSession());
// //         joinBtn.setOnAction(e -> joinSession(codeField.getText()));
// //         VBox v = new VBox(10, createBtn, new HBox(5, codeField, joinBtn));
// //         v.setAlignment(Pos.CENTER);
// //         v.setPadding(new Insets(20));
// //         homeScene = new Scene(v, 400, 200);
// //     }

// //     private void buildEditorScene() {
// //         undoBtn = new Button("↶");
// //         redoBtn = new Button("↷");
// //         exportBtn = new Button("Export");
// //         HBox topButtons = new HBox(5, undoBtn, redoBtn, exportBtn);
// //         viewerCodeField = new TextField();
// //         viewerCodeField.setEditable(false);
// //         copyViewerBtn = new Button("Copy");
// //         editorCodeField = new TextField();
// //         editorCodeField.setEditable(false);
// //         copyEditorBtn = new Button("Copy");
// //         usersList = new ListView<>();
// //         usersList.setPrefHeight(150);
// //         VBox left = new VBox(10, topButtons,
// //                 new Label("Viewer Code"), new HBox(5, viewerCodeField, copyViewerBtn),
// //                 new Label("Editor Code"), new HBox(5, editorCodeField, copyEditorBtn),
// //                 new Label("Active Users"), usersList);
// //         left.setPadding(new Insets(10));
// //         left.setPrefWidth(250);
// //         textArea = new TextArea();
// //         textArea.setDisable(true);
// //         textArea.setWrapText(true);
// //         textArea.addEventFilter(KeyEvent.KEY_TYPED, this::sendEdit);
// //         exportBtn.setOnAction(e -> doExport());
// //         copyViewerBtn.setOnAction(e -> copyToClipboard(viewerCodeField.getText()));
// //         copyEditorBtn.setOnAction(e -> copyToClipboard(editorCodeField.getText()));
// //         HBox root = new HBox(left, textArea);
// //         HBox.setHgrow(textArea, Priority.ALWAYS);
// //         editorScene = new Scene(root, 900, 600);
// //     }

// //     private void createSession() {
// //         docId = collabService.createSession("untitled", username);
// //         primary.setScene(editorScene);
// //     }
    
// //     private void joinSession(String code) {
// //         docId = collabService.joinSession(code, username);
// //         primary.setScene(editorScene);
// //     }
    
// //     private void sendEdit(KeyEvent ev) {
// //         if (textArea.isDisabled() || collabService == null) return;
// //         collabService.sendEdit(textArea.getText());
// //     }

// //     private void doExport() {
// //         FileChooser fc = new FileChooser();
// //         fc.setInitialFileName("doc.txt");
// //         File f = fc.showSaveDialog(primary);
// //         if (f == null)
// //             return;
// //         try (FileWriter w = new FileWriter(f)) {
// //             w.write(textArea.getText());
// //         } catch (Exception e) {
// //             e.printStackTrace();
// //         }
// //     }

// //     private void copyToClipboard(String s) {
// //         var c = new ClipboardContent();
// //         c.putString(s);
// //         Clipboard.getSystemClipboard().setContent(c);
// //     }

// //     private void updateCursor(String u, int pos) {
// //         Pane p = (Pane) textArea.lookup(".content");
// //         if (p == null)
// //             return;
// //         Line old = cursorLines.remove(u);
// //         if (old != null)
// //             p.getChildren().remove(old);
// //         int me = textArea.getCaretPosition();
// //         textArea.positionCaret(pos);
// //         Node caret = textArea.lookup(".caret");
// //         if (caret != null) {
// //             Bounds b = caret.getBoundsInParent();
// //             Line ln = new Line(b.getMinX(), b.getMinY(), b.getMinX(), b.getMaxY());
// //             ln.setStroke(cursorColors[Math.abs(u.hashCode()) % cursorColors.length]);
// //             ln.setStrokeWidth(2);
// //             p.getChildren().add(ln);
// //             cursorLines.put(u, ln);
// //         }
// //         textArea.positionCaret(me);
// //     }

// //     public static void main(String[] args) {
// //         launch(args);
// //     }
// // }
//     // private void createSession() {
//     //     // Use CollabService to create document and connect
//     //     docId = collabService.createDocumentAndConnect("untitled");
//     //     primary.setScene(editorScene);
//     // }

//     // private void joinSession(String code) {
//     //     // Use CollabService to join document and connect
//     //     docId = collabService.joinDocumentAndConnect(code);
//     //     primary.setScene(editorScene);
//     // }

//     // // private void sendEdit(KeyEvent ev) {
//     // //     if (textArea.isDisabled()) return;
//     // //     // Send edit through CollabService
//     // //     collabService.sendEdit(docId, userId, textArea.getText());
//     // // }

//     // private void sendEdit(KeyEvent ev) {
//     // if (textArea.isDisabled() || collabService == null) return;
//     // collabService.sendEdit(docId, userId, textArea.getText());
//     // }


//     // public class EditorUI extends Application {
//     //     private Stage primary;
//     //     private Scene editorScene;
//     //     private TextField nameField, codeField;
//     //     private Button createBtn, joinBtn;
//     //     private TextArea textArea;
//     //     private TextField viewerCodeField, editorCodeField;
//     //     private Button copyViewerBtn, copyEditorBtn, undoBtn, redoBtn, exportBtn;
//     //     private ListView<String> usersList;
//     //     private Label docNameLabel;
    
//     //     private String userId;
//     //     private String username;
//     //     private Document currentDoc;
//     //     private CollabService collabService;
    
//     //     private final Map<String, Line> cursorLines = new HashMap<>();
//     //     private final Color[] cursorColors = { Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE };
    
//     //     @Override
//     //     public void start(Stage stage) {
//     //         this.primary = stage;
//     //         userId = UUID.randomUUID().toString().substring(0,6);
//     //         buildEditorScene();
//     //         primary.setScene(editorScene);
//     //         primary.setTitle("Collaborative Editor");
//     //         primary.show();
//     //     }
    
//     //     private void buildEditorScene() {
//     //         // Sidebar: name, session controls
//     //         nameField = new TextField(); nameField.setPromptText("Your name");
//     //         createBtn = new Button("Create");
//     //         codeField = new TextField(); codeField.setPromptText("Session code");
//     //         joinBtn = new Button("Join");
//     //         createBtn.setOnAction(e -> createSession());
//     //         joinBtn.setOnAction(e -> joinSession());
//     //         VBox sessionBox = new VBox(8,
//     //             new Label("User & Session"),
//     //             new HBox(5, new Label("Name:"), nameField),
//     //             new HBox(5, createBtn, codeField, joinBtn)
//     //         );
//     //         sessionBox.setPadding(new Insets(10));
//     //         sessionBox.setStyle("-fx-background-color: #336699;");
//     //         sessionBox.getChildren().forEach(n -> n.setStyle("-fx-text-fill: white;"));
    
//     //         // Document name
//     //         docNameLabel = new Label("Untitled");
//     //         docNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    
//     //         // Top toolbar
//     //         undoBtn = new Button("↶"); redoBtn = new Button("↷"); exportBtn = new Button("Export");
//     //         undoBtn.setOnAction(e -> collabService.undo());
//     //         redoBtn.setOnAction(e -> collabService.redo());
//     //         exportBtn.setOnAction(e -> doExport());
//     //         HBox topButtons = new HBox(5, undoBtn, redoBtn, exportBtn);
//     //         topButtons.setAlignment(Pos.CENTER_LEFT);
//     //         topButtons.setPadding(new Insets(5));
//     //         topButtons.setStyle("-fx-background-color: #eeeeee;");
    
//     //         // Codes and users
//     //         viewerCodeField = new TextField(); viewerCodeField.setEditable(false);
//     //         copyViewerBtn = new Button("Copy"); copyViewerBtn.setOnAction(e -> copyToClipboard(viewerCodeField.getText()));
//     //         editorCodeField = new TextField(); editorCodeField.setEditable(false);
//     //         copyEditorBtn = new Button("Copy"); copyEditorBtn.setOnAction(e -> copyToClipboard(editorCodeField.getText()));
//     //         usersList = new ListView<>(); usersList.setPrefHeight(150);
//     //         VBox infoBox = new VBox(10,
//     //             new HBox(5, new Label("Viewer Code:"), viewerCodeField, copyViewerBtn),
//     //             new HBox(5, new Label("Editor Code:"), editorCodeField, copyEditorBtn),
//     //             new Label("Active Users:"), usersList
//     //         );
//     //         infoBox.setPadding(new Insets(10));
    
//     //         VBox leftPane = new VBox(sessionBox, infoBox);
//     //         leftPane.setPrefWidth(260);
//     //         leftPane.setStyle("-fx-border-color: #cccccc;");
    
//     //         // Text area section
//     //         textArea = new TextArea(); textArea.setDisable(true); textArea.setWrapText(true);
//     //         textArea.addEventFilter(KeyEvent.KEY_TYPED, this::sendEdit);
//     //         VBox editorBox = new VBox(5, docNameLabel, topButtons, textArea);
//     //         VBox.setVgrow(textArea, Priority.ALWAYS);
//     //         editorBox.setPadding(new Insets(10));
    
//     //         HBox root = new HBox(leftPane, editorBox);
//     //         HBox.setHgrow(editorBox, Priority.ALWAYS);
//     //         editorScene = new Scene(root, 1000, 600);
//     //     }
    
//     //     private void createSession() {
//     //         username = nameField.getText().trim();
//     //         currentDoc = collabService.createSession("untitled", username);
//     //         afterDocLoaded();
//     //     }
//     //     private void joinSession() {
//     //         username = nameField.getText().trim();
//     //         String code = codeField.getText().trim();
//     //         currentDoc = collabService.joinSession(code, username);
//     //         afterDocLoaded();
//     //     }
//     //     private void afterDocLoaded() {
//     //         docNameLabel.setText(currentDoc.getId());
//     //         viewerCodeField.setText(currentDoc.getViewerCode());
//     //         editorCodeField.setText(currentDoc.getEditorCode());
//     //         textArea.setDisable(false);
//     //     }
    
//     //     private void sendEdit(KeyEvent ev) {
//     //         if (collabService != null) collabService.sendEdit(textArea.getText());
//     //     }
//     //     private void doExport() {
//     //         FileChooser fc = new FileChooser(); fc.setInitialFileName(""+currentDoc.getId()+".txt");
//     //         File f = fc.showSaveDialog(primary);
//     //         if (f!=null) try(FileWriter w=new FileWriter(f)){ w.write(textArea.getText()); } catch(Exception ex){ex.printStackTrace();}
//     //     }
//     //     private void copyToClipboard(String s) {
//     //         ClipboardContent c = new ClipboardContent(); c.putString(s);
//     //         javafx.scene.input.Clipboard.getSystemClipboard().setContent(c);
//     //     }
    
//     //     public static void main(String[] args) { launch(args); }
//     // }


//     public class EditorUI extends Application {
//         private Stage primary;
//         private Scene homeScene, editorScene;
//         private TextField nameField, codeField;
//         private Button createBtn, joinBtn;
    
//         private TextArea textArea;
//         private TextField viewerCodeField, editorCodeField;
//         private Button copyViewerBtn, copyEditorBtn, undoBtn, redoBtn, exportBtn;
//         private ListView<String> usersList;
//         private Label docNameLabel;
    
//         private String userId;
//         private String username;
//         private Document currentDoc;
//         private CollabService collabService;
    
//         private final Map<String, Line> cursorLines = new HashMap<>();
//         private final Color[] cursorColors = { Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE };
    
//         @Override
//         public void start(Stage stage) {
//             this.primary = stage;
//             userId = UUID.randomUUID().toString().substring(0,6);
//             buildHomeScene();
//             buildEditorScene();
//             primary.setScene(homeScene);
//             primary.setTitle("Collaborative Editor");
//             primary.show();
//         }
    
//         // private void buildHomeScene() {
//         //     nameField = new TextField(); nameField.setPromptText("Your name");
//         //     createBtn = new Button("Create Session");
//         //     codeField = new TextField(); codeField.setPromptText("Session code");
//         //     joinBtn = new Button("Join Session");
//         //     createBtn.setOnAction(e -> createSession());
//         //     joinBtn.setOnAction(e -> joinSession());
    
//         //     VBox homeBox = new VBox(15,
//         //         new Label("Enter your name and either create a new session or join an existing one."),
//         //         new HBox(5, new Label("Name:"), nameField),
//         //         new HBox(5, createBtn, codeField, joinBtn)
//         //     );
//         //     homeBox.setAlignment(Pos.CENTER);
//         //     homeBox.setPadding(new Insets(20));
//         //     homeScene = new Scene(homeBox, 400, 200);
//         // }


//         private void buildHomeScene() {
//             nameField = new TextField(); nameField.setPromptText("Your name");
//             createBtn = new Button("Create Session");
//             codeField = new TextField(); codeField.setPromptText("Session code");
//             joinBtn = new Button("Join Session");
//             createBtn.setOnAction(e -> {
//                 if (collabService == null) {
//                     username = nameField.getText().trim();
//                     if (username.isEmpty()) return;
//                     collabService = new CollabService(new CollabService.MessageCallback() {
//                         @Override
//                         public void onCreate(String editorCode, String viewerCode) {
//                             Platform.runLater(() -> {
//                                 editorCodeField.setText(editorCode);
//                                 viewerCodeField.setText(viewerCode);
//                                 textArea.setDisable(false);
//                             });
//                         }
//                         @Override
//                         public void onUpdate(String content) {
//                             Platform.runLater(() -> textArea.setText(content));
//                         }
//                         @Override
//                         public void onPresence(java.util.List<String> users) {
//                             Platform.runLater(() -> usersList.getItems().setAll(users));
//                         }
//                         @Override
//                         public void onCursor(String userId, int pos) {
//                             Platform.runLater(() -> updateCursor(userId, pos));
//                         }
//                     });
//                 }
//                 createSession();
//             });
//             joinBtn.setOnAction(e -> {
//                 if (collabService == null) {
//                     username = nameField.getText().trim();
//                     if (username.isEmpty()) return;
//                     collabService = new CollabService(new CollabService.MessageCallback() {
//                         @Override
//                         public void onCreate(String editorCode, String viewerCode) {
//                             Platform.runLater(() -> {
//                                 editorCodeField.setText(editorCode);
//                                 viewerCodeField.setText(viewerCode);
//                                 textArea.setDisable(false);
//                             });
//                         }
//                         @Override
//                         public void onUpdate(String content) {
//                             Platform.runLater(() -> textArea.setText(content));
//                         }
//                         @Override
//                         public void onPresence(java.util.List<String> users) {
//                             Platform.runLater(() -> usersList.getItems().setAll(users));
//                         }
//                         @Override
//                         public void onCursor(String userId, int pos) {
//                             Platform.runLater(() -> updateCursor(userId, pos));
//                         }
//                     });
//                 }
//                 joinSession();
//             });
        
//             VBox homeBox = new VBox(15,
//                 new Label("Enter your name and either create a new session or join an existing one."),
//                 new HBox(5, new Label("Name:"), nameField),
//                 new HBox(5, createBtn, codeField, joinBtn)
//             );
//             homeBox.setAlignment(Pos.CENTER);
//             homeBox.setPadding(new Insets(20));
//             homeScene = new Scene(homeBox, 400, 200);
//         }
    
//         private void buildEditorScene() {
//             // Sidebar: codes and users
//             docNameLabel = new Label("Untitled");
//             docNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    
//             undoBtn = new Button("↶"); redoBtn = new Button("↷"); exportBtn = new Button("Export");
//             undoBtn.setOnAction(e -> collabService.undo());
//             redoBtn.setOnAction(e -> collabService.redo());
//             exportBtn.setOnAction(e -> doExport());
//             HBox topButtons = new HBox(5, undoBtn, redoBtn, exportBtn);
//             topButtons.setAlignment(Pos.CENTER_LEFT);
//             topButtons.setPadding(new Insets(5));
//             topButtons.setStyle("-fx-background-color: #eeeeee;");
    
//             viewerCodeField = new TextField(); viewerCodeField.setEditable(false);
//             copyViewerBtn = new Button("Copy"); copyViewerBtn.setOnAction(e -> copyToClipboard(viewerCodeField.getText()));
//             editorCodeField = new TextField(); editorCodeField.setEditable(false);
//             copyEditorBtn = new Button("Copy"); copyEditorBtn.setOnAction(e -> copyToClipboard(editorCodeField.getText()));
//             usersList = new ListView<>(); usersList.setPrefHeight(150);
//             VBox infoBox = new VBox(10,
//                 new HBox(5, new Label("Viewer Code:"), viewerCodeField, copyViewerBtn),
//                 new HBox(5, new Label("Editor Code:"), editorCodeField, copyEditorBtn),
//                 new Label("Active Users:"), usersList
//             );
//             infoBox.setPadding(new Insets(10));
    
//             VBox leftPane = new VBox(10, docNameLabel, topButtons, infoBox);
//             leftPane.setPrefWidth(260);
//             leftPane.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #cccccc;");
    
//             textArea = new TextArea(); textArea.setDisable(true); textArea.setWrapText(true);
//             textArea.addEventFilter(KeyEvent.KEY_TYPED, this::sendEdit);
    
//             VBox editorBox = new VBox(5, textArea);
//             VBox.setVgrow(textArea, Priority.ALWAYS);
//             editorBox.setPadding(new Insets(10));
    
//             HBox root = new HBox(leftPane, editorBox);
//             HBox.setHgrow(editorBox, Priority.ALWAYS);
//             editorScene = new Scene(root, 900, 600);
//         }
    
//         private void createSession() {
//             username = nameField.getText().trim();
//             if (username.isEmpty()) return;
//             currentDoc = collabService.createSession("untitled", username);
//             afterDocLoaded();
//             primary.setScene(editorScene);
//         }
//         private void joinSession() {
//             username = nameField.getText().trim();
//             String code = codeField.getText().trim();
//             if (username.isEmpty() || code.isEmpty()) return;
//             currentDoc = collabService.joinSession(code, username);
//             afterDocLoaded();
//             primary.setScene(editorScene);
//         }
//         private void afterDocLoaded() {
//             docNameLabel.setText(currentDoc.getId());
//             viewerCodeField.setText(currentDoc.getViewerCode());
//             editorCodeField.setText(currentDoc.getEditorCode());
//             textArea.setDisable(false);
//         }
    
//         private void sendEdit(KeyEvent ev) {
//             if (collabService != null) collabService.sendEdit(textArea.getText());
//         }
//         private void doExport() {
//             FileChooser fc = new FileChooser(); fc.setInitialFileName(currentDoc.getId()+".txt");
//             File f = fc.showSaveDialog(primary);
//             if (f!=null) try(FileWriter w=new FileWriter(f)){ w.write(textArea.getText()); } catch(Exception ex){ex.printStackTrace();}
//         }
//         private void copyToClipboard(String s) {
//             ClipboardContent c = new ClipboardContent(); c.putString(s);
//             javafx.scene.input.Clipboard.getSystemClipboard().setContent(c);
//         }
    
//         public static void main(String[] args) { launch(args); }
//     }

// package com.example.ui;

// import javafx.application.Application;
// import javafx.application.Platform;
// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
// import javafx.scene.Scene;
// import javafx.scene.control.*;
// import javafx.scene.input.Clipboard;
// import javafx.scene.input.ClipboardContent;
// import javafx.scene.Node;
// import javafx.scene.input.KeyEvent;
// import javafx.scene.layout.*;
// import javafx.scene.paint.Color;
// import javafx.scene.shape.Line;
// import javafx.stage.FileChooser;
// import javafx.stage.Stage;

// import java.io.File;
// import java.io.FileWriter;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.UUID;



// public class EditorUI extends Application {
//     private Stage primary;
//     private Scene homeScene, editorScene;
//     private TextField nameField, codeField;
//     private Button createBtn, joinBtn;
//     private TextArea textArea;
//     private TextField viewerCodeField, editorCodeField;
//     private Button copyViewerBtn, copyEditorBtn, undoBtn, redoBtn, exportBtn;
//     private ListView<String> usersList;
//     private Label docNameLabel;

//     private String userId;
//     private String username;
//     private Document currentDoc;
//     private CollabService collabService;

//     private final Map<String, Line> cursorLines = new HashMap<>();
//     private final Color[] cursorColors = { Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE };

//     @Override
//     public void start(Stage stage) {
//         this.primary = stage;
//         userId = UUID.randomUUID().toString().substring(0,6);
//         buildHomeScene();
//         buildEditorScene();
//         primary.setScene(homeScene);
//         primary.setTitle("Collaborative Editor");
//         primary.show();
//     }

//     private void buildHomeScene() {
//         nameField = new TextField(); nameField.setPromptText("Your name");
//         createBtn = new Button("Create Session");
//         codeField = new TextField(); codeField.setPromptText("Session code");
//         joinBtn = new Button("Join Session");

//         createBtn.setOnAction(e -> {
//             username = nameField.getText().trim();
//             if (username.isEmpty()) return;
//             ensureCollabService();
//             createSession();
//         });

//         joinBtn.setOnAction(e -> {
//             username = nameField.getText().trim();
//             if (username.isEmpty()) return;
//             ensureCollabService();
//             joinSession();
//         });

//         VBox homeBox = new VBox(15,
//             new Label("Enter your name and either create a new session or join an existing one."),
//             new HBox(5, new Label("Name:"), nameField),
//             new HBox(5, createBtn, codeField, joinBtn)
//         );
//         homeBox.setAlignment(Pos.CENTER);
//         homeBox.setPadding(new Insets(20));
//         homeScene = new Scene(homeBox, 400, 200);
//     }

//     private void ensureCollabService() {
//         if (collabService == null) {
     

//             collabService = new CollabService(new CollabService.MessageCallback() {
//                 @Override
//                 public void onCreate(String editorCode, String viewerCode) {
//                     Platform.runLater(() -> {
//                         editorCodeField.setText(editorCode);
//                         viewerCodeField.setText(viewerCode);
//                         textArea.setDisable(false);
//                     });
//                 }
//                 @Override
//                 public void onJoin(Document doc) {
//                     Platform.runLater(() -> {
//                         currentDoc = doc;
//                         afterDocLoaded();
//                         primary.setScene(editorScene);
//                     });
//                 }
//                 @Override
//                 public void onUpdate(String content) {
//                     Platform.runLater(() -> textArea.setText(content));
//                 }
//                 @Override
//                 public void onPresence(java.util.List<String> users) {
//                     Platform.runLater(() -> usersList.getItems().setAll(users));
//                 }
//                 @Override
//                 public void onCursor(String userId, int pos) {
//                     Platform.runLater(() -> updateCursor(userId, pos));
//                 }
//             });
//         }
//     }

//     private void buildEditorScene() {
//         docNameLabel = new Label("Untitled");
//         docNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

//         undoBtn = new Button("↶"); redoBtn = new Button("↷"); exportBtn = new Button("Export");
//         undoBtn.setOnAction(e -> { if (collabService != null) collabService.undo(); });
//         redoBtn.setOnAction(e -> { if (collabService != null) collabService.redo(); });
//         exportBtn.setOnAction(e -> doExport());
//         HBox topButtons = new HBox(5, undoBtn, redoBtn, exportBtn);
//         topButtons.setAlignment(Pos.CENTER_LEFT);
//         topButtons.setPadding(new Insets(5));
//         topButtons.setStyle("-fx-background-color: #eeeeee;");

//         viewerCodeField = new TextField(); viewerCodeField.setEditable(false);
//         copyViewerBtn = new Button("Copy"); copyViewerBtn.setOnAction(e -> copyToClipboard(viewerCodeField.getText()));
//         editorCodeField = new TextField(); editorCodeField.setEditable(false);
//         copyEditorBtn = new Button("Copy"); copyEditorBtn.setOnAction(e -> copyToClipboard(editorCodeField.getText()));
//         usersList = new ListView<>(); usersList.setPrefHeight(150);
//         VBox infoBox = new VBox(10,
//             new HBox(5, new Label("Viewer Code:"), viewerCodeField, copyViewerBtn),
//             new HBox(5, new Label("Editor Code:"), editorCodeField, copyEditorBtn),
//             new Label("Active Users:"), usersList
//         );
//         infoBox.setPadding(new Insets(10));

//         VBox leftPane = new VBox(10, docNameLabel, topButtons, infoBox);
//         leftPane.setPrefWidth(260);
//         leftPane.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #cccccc;");

//         textArea = new TextArea(); textArea.setDisable(true); textArea.setWrapText(true);
//         textArea.addEventFilter(KeyEvent.KEY_TYPED, this::sendEdit);

//         VBox editorBox = new VBox(5, textArea);
//         VBox.setVgrow(textArea, Priority.ALWAYS);
//         editorBox.setPadding(new Insets(10));

//         HBox root = new HBox(leftPane, editorBox);
//         HBox.setHgrow(editorBox, Priority.ALWAYS);
//         editorScene = new Scene(root, 900, 600);
//     }

//     private void createSession() {
//         currentDoc = collabService.createSession("untitled", username);
//         afterDocLoaded();
//         primary.setScene(editorScene);
//     }
//     // private void joinSession() {
//     //     String code = codeField.getText().trim();
//     //     if (code.isEmpty()) return;
//     //     currentDoc = collabService.joinSession(code, username);
//     //     afterDocLoaded();
//     //     primary.setScene(editorScene);
//     // }

//     private void joinSession() {
//         String code = codeField.getText().trim();
//         if (code.isEmpty()) return;
//         collabService.joinSession(code, username);
//         // Do not set currentDoc or call afterDocLoaded here
//     }
//     private void afterDocLoaded() {
//         if (currentDoc == null) return;
//         docNameLabel.setText(currentDoc.getId());
//         viewerCodeField.setText(currentDoc.getViewerCode());
//         editorCodeField.setText(currentDoc.getEditorCode());
//         textArea.setDisable(false);
//     }

//     private void sendEdit(KeyEvent ev) {
//         if (collabService != null) collabService.sendEdit(textArea.getText());
//     }
//     private void doExport() {
//         if (currentDoc == null) return;
//         FileChooser fc = new FileChooser(); fc.setInitialFileName(currentDoc.getId()+".txt");
//         File f = fc.showSaveDialog(primary);
//         if (f!=null) try(FileWriter w=new FileWriter(f)){ w.write(textArea.getText()); } catch(Exception ex){ex.printStackTrace();}
//     }
//     private void copyToClipboard(String s) {
//         ClipboardContent c = new ClipboardContent(); c.putString(s);
//         Clipboard.getSystemClipboard().setContent(c);
//     }

//     private void updateCursor(String u, int pos) {
//         Pane p = (Pane) textArea.lookup(".content");
//         if (p == null) return;
//         Line old = cursorLines.remove(u);
//         if (old != null) p.getChildren().remove(old);
//         int me = textArea.getCaretPosition();
//         textArea.positionCaret(pos);
//         Node caret = textArea.lookup(".caret");
//         if (caret != null) {
//             javafx.geometry.Bounds b = caret.getBoundsInParent();
//             Line ln = new Line(b.getMinX(), b.getMinY(), b.getMinX(), b.getMaxY());
//             ln.setStroke(cursorColors[Math.abs(u.hashCode()) % cursorColors.length]);
//             ln.setStrokeWidth(2);
//             p.getChildren().add(ln);
//             cursorLines.put(u, ln);
//         }
//         textArea.positionCaret(me);
//     }

//     public static void main(String[] args) { launch(args); }
// } 
      // collabService = new CollabService(new CollabService.MessageCallback() {
            //     @Override
            //     public void onCreate(String editorCode, String viewerCode) {
            //         Platform.runLater(() -> {
            //             editorCodeField.setText(editorCode);
            //             viewerCodeField.setText(viewerCode);
            //             textArea.setDisable(false);
            //         });
            //     }
            //     @Override
            //     public void onUpdate(String content) {
            //         Platform.runLater(() -> textArea.setText(content));
            //     }
            //     @Override
            //     public void onPresence(java.util.List<String> users) {
            //         Platform.runLater(() -> usersList.getItems().setAll(users));
            //     }
            //     @Override
            //     public void onCursor(String userId, int pos) {
            //         Platform.runLater(() -> updateCursor(userId, pos));
            //     }
            // });



            package com.example.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditorUI extends Application {
    private Stage primary;
    private Scene homeScene, editorScene;
    private TextField nameField, codeField;
    private Button createBtn, joinBtn;

    private TextArea textArea;
    private TextField viewerCodeField, editorCodeField;
    private Button copyViewerBtn, copyEditorBtn, undoBtn, redoBtn, exportBtn;
    private ListView<String> usersList;
    private Label docNameLabel;

    private String userId;
    private String username;
    private Document currentDoc;
    private CollabService collabService;

    private final Map<String, Line> cursorLines = new HashMap<>();
    private final Color[] cursorColors = { Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE };

    @Override
    public void start(Stage stage) {
        this.primary = stage;
        userId = UUID.randomUUID().toString().substring(0,6);
        buildHomeScene();
        buildEditorScene();
        primary.setScene(homeScene);
        primary.setTitle("Collaborative Editor");
        primary.show();
    }

    private void buildHomeScene() {
        nameField = new TextField(); nameField.setPromptText("Your name");
        createBtn = new Button("Create Session");
        codeField = new TextField(); codeField.setPromptText("Session code");
        joinBtn = new Button("Join Session");

        createBtn.setOnAction(e -> {
            username = nameField.getText().trim();
            if (username.isEmpty()) return;
            ensureUserCreated();
            ensureCollabService();
            currentDoc = collabService.createSession("untitled", username);
            afterDocLoaded();
            primary.setScene(editorScene);
        });

        joinBtn.setOnAction(e -> {
            username = nameField.getText().trim();
            String code = codeField.getText().trim();
            if (username.isEmpty() || code.isEmpty()) return;
            ensureUserCreated();
            ensureCollabService();
            collabService.joinSession(code, username);
            // scene and UI will update in onJoin callback
        });

        VBox homeBox = new VBox(15,
            new Label("Enter your name and either create a new session or join an existing one."),
            new HBox(5, new Label("Name:"), nameField),
            new HBox(5, createBtn, codeField, joinBtn)
        );
        homeBox.setAlignment(Pos.CENTER);
        homeBox.setPadding(new Insets(20));
        homeScene = new Scene(homeBox, 400, 200);
    }


    private void ensureUserCreated() {
        if (userId == null || userId.isEmpty()) {
            String username = nameField.getText().trim();
            if (username.isEmpty()) return;
            User user = collabService.createUser(username);
            userId = user.getId();
            this.username = user.getUsername();
        }
    }

    private void ensureCollabService() {
        if (collabService != null) return;
        collabService = new CollabService(new CollabService.MessageCallback() {
            @Override public void onCreate(String editorCode, String viewerCode) {
                // handled in createSession directly
            }
            @Override public void onJoin(Document doc) {
                Platform.runLater(() -> {
                    currentDoc = doc;
                    afterDocLoaded();
                    primary.setScene(editorScene);
                });
            }
            @Override public void onUpdate(String content) {
                Platform.runLater(() -> textArea.setText(content));
            }
            @Override public void onPresence(java.util.List<String> users) {
                Platform.runLater(() -> usersList.getItems().setAll(users));
            }
            @Override public void onCursor(String userId, int pos) {
                Platform.runLater(() -> updateCursor(userId, pos));
            }
        });
    }

    private void buildEditorScene() {
        docNameLabel = new Label("Untitled");
        docNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        undoBtn = new Button("↶"); redoBtn = new Button("↷"); exportBtn = new Button("Export");
        undoBtn.setOnAction(e -> collabService.undo());
        redoBtn.setOnAction(e -> collabService.redo());
        exportBtn.setOnAction(e -> doExport());
        HBox topButtons = new HBox(5, undoBtn, redoBtn, exportBtn);
        topButtons.setAlignment(Pos.CENTER_LEFT);
        topButtons.setPadding(new Insets(5));
        topButtons.setStyle("-fx-background-color: #eeeeee;");

        viewerCodeField = new TextField(); viewerCodeField.setEditable(false);
        copyViewerBtn = new Button("Copy"); copyViewerBtn.setOnAction(e -> copyToClipboard(viewerCodeField.getText()));
        editorCodeField = new TextField(); editorCodeField.setEditable(false);
        copyEditorBtn = new Button("Copy"); copyEditorBtn.setOnAction(e -> copyToClipboard(editorCodeField.getText()));
        usersList = new ListView<>(); usersList.setPrefHeight(150);
        VBox infoBox = new VBox(10,
            new HBox(5, new Label("Viewer Code:"), viewerCodeField, copyViewerBtn),
            new HBox(5, new Label("Editor Code:"), editorCodeField, copyEditorBtn),
            new Label("Active Users:"), usersList
        );
        infoBox.setPadding(new Insets(10));

        VBox leftPane = new VBox(10, docNameLabel, topButtons, infoBox);
        leftPane.setPrefWidth(260);
        leftPane.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #cccccc;");

        textArea = new TextArea(); textArea.setDisable(true); textArea.setWrapText(true);
        textArea.addEventFilter(KeyEvent.KEY_TYPED, this::sendEdit);

        VBox editorBox = new VBox(5, textArea);
        VBox.setVgrow(textArea, Priority.ALWAYS);
        editorBox.setPadding(new Insets(10));

        HBox root = new HBox(leftPane, editorBox);
        HBox.setHgrow(editorBox, Priority.ALWAYS);
        editorScene = new Scene(root, 900, 600);
    }

    // private void createSession() {
    //     // handled inline in buildHomeScene
    // }
    // private void joinSession() {
    //     // handled inline in buildHomeScene
    // }

    private void afterDocLoaded() {
        if (currentDoc == null) return;
        docNameLabel.setText(currentDoc.getId());
        viewerCodeField.setText(currentDoc.getViewerCode());
        editorCodeField.setText(currentDoc.getEditorCode());
        textArea.setDisable(false);
    }

    private void sendEdit(KeyEvent ev) {
        if (collabService != null) collabService.sendEdit(textArea.getText());
    }
    private void doExport() {
        if (currentDoc == null) return;
        FileChooser fc = new FileChooser(); fc.setInitialFileName(currentDoc.getId()+".txt");
        File f = fc.showSaveDialog(primary);
        if (f!=null) try(FileWriter w=new FileWriter(f)){ w.write(textArea.getText()); } catch(Exception ex){ex.printStackTrace();}
    }
    private void copyToClipboard(String s) {
        ClipboardContent c = new ClipboardContent(); c.putString(s);
        javafx.scene.input.Clipboard.getSystemClipboard().setContent(c);
    }

    private void updateCursor(String u, int pos) {
        Pane p = (Pane) textArea.lookup(".content"); if (p == null) return;
        Line old = cursorLines.remove(u); if (old != null) p.getChildren().remove(old);
        int me = textArea.getCaretPosition(); textArea.positionCaret(pos);
        Node caret = textArea.lookup(".caret");
        if (caret != null) {
            javafx.geometry.Bounds b = caret.getBoundsInParent();
            Line ln = new Line(b.getMinX(), b.getMinY(), b.getMinX(), b.getMaxY());
            ln.setStroke(cursorColors[Math.abs(u.hashCode()) % cursorColors.length]);
            ln.setStrokeWidth(2);
            p.getChildren().add(ln);
            cursorLines.put(u, ln);
        }
        textArea.positionCaret(me);
    }

    public static void main(String[] args) { launch(args); }
}