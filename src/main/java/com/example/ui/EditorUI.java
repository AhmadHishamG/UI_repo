package com.example.ui;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * EditorUI implements tasks 1-3:
 * 1. User name input and JOIN message
 * 2. TextArea editing with WebSocket send/receive
 * 3. Display other users' cursors based on character position
 */
public class EditorUI extends Application {
    private TextField nameField, joinField;
    private Button joinBtn;
    private TextArea textArea;

    private WebSocket webSocket;
    private final String userId = UUID.randomUUID().toString().substring(0, 6);

    // Task 3: cursor display state
    private final Map<String, Line> cursorLines = new HashMap<>();
    private final Color[] cursorColors = { Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE };

    @Override
    public void start(Stage stage) {
        // Name input (Task 1)
        nameField = new TextField();
        nameField.setPromptText("Enter your name");

        // Session code input (Tasks 4 stub)
        joinField = new TextField();
        joinField.setPromptText("Session code");

        joinBtn = new Button("Join");
        joinBtn.setOnAction(e -> joinSession(joinField.getText()));

        HBox topBar = new HBox(10,
                new Label("Name:"), nameField,
                new Label("Code:"), joinField, joinBtn
        );
        topBar.setPadding(new Insets(10));

        // Text area (Task 2)
        textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setDisable(true);
        textArea.addEventFilter(KeyEvent.KEY_TYPED, this::onLocalEdit);

        VBox layout = new VBox(topBar, textArea);
        Scene scene = new Scene(layout, 800, 600);

        stage.setScene(scene);
        stage.setTitle("Collaborative Editor (tasks 1-3)");
        stage.show();
    }

    /** Task 4 stub: open WebSocket and send JOIN */
    private void joinSession(String code) {
        textArea.setDisable(false);
        webSocket = HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create("ws://localhost:8080/ws-endpoint"), new WebSocket.Listener() {
                    @Override
                    public void onOpen(WebSocket ws) {
                        String joinMsg = String.format(
                                "{\"type\":\"JOIN\",\"userId\":\"%s\",\"name\":\"%s\",\"code\":\"%s\"}",
                                userId, escapeJson(nameField.getText()), escapeJson(code)
                        );
                        ws.sendText(joinMsg, true);
                        WebSocket.Listener.super.onOpen(ws);
                    }

                    @Override
                    public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
                        String msg = data.toString();
                        if (msg.contains("\"type\":\"EDIT\"")) {
                            String content = msg.replaceAll(".*\\\"content\\\":\\\"([^\\\\]*)\\\".*", "$1");
                            Platform.runLater(() -> textArea.setText(unescapeJson(content)));
                        } else if (msg.contains("\"type\":\"CURSOR\"")) {
                            String uid = msg.replaceAll(".*\\\"userId\\\":\\\"([^\\\\]*)\\\".*", "$1");
                            int pos = Integer.parseInt(msg.replaceAll(".*\\\"pos\\\":(\\d+).*", "$1"));
                            Platform.runLater(() -> updateCursor(uid, pos));
                        }
                        return WebSocket.Listener.super.onText(ws, data, last);
                    }
                }).join();
    }

    /** Task 2: send EDIT messages */
    private void onLocalEdit(KeyEvent ev) {
        if (webSocket != null) {
            String text = textArea.getText();
            String editMsg = String.format(
                    "{\"type\":\"EDIT\",\"userId\":\"%s\",\"content\":\"%s\"}",
                    userId, escapeJson(text)
            );
            webSocket.sendText(editMsg, true);
        }
    }

    /** Task 3: render other users' cursors based on caret node */
    private void updateCursor(String otherUser, int position) {
        Pane content = (Pane) textArea.lookup(".content");
        if (content == null) return;

        // remove existing cursor for user
        Line old = cursorLines.remove(otherUser);
        if (old != null) content.getChildren().remove(old);

        // preserve own caret
        int currentPos = textArea.getCaretPosition();

        // move caret to target position to locate .caret node
        textArea.positionCaret(position);
        Node caret = textArea.lookup(".caret");
        if (caret != null) {
            Bounds b = caret.getBoundsInParent();
            Line line = new Line(b.getMinX(), b.getMinY(), b.getMinX(), b.getMaxY());
            Color c = cursorColors[Math.abs(otherUser.hashCode()) % cursorColors.length];
            line.setStroke(c);
            line.setStrokeWidth(2);
            content.getChildren().add(line);
            cursorLines.put(otherUser, line);
        }

        // restore own caret position
        textArea.positionCaret(currentPos);
    }

    // Simple JSON escape/unescape
    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
    private String unescapeJson(String s) {
        return s.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
