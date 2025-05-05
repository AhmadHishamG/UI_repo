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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javafx.stage.FileChooser;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

// In your EditorUI class, add this field:

public class EditorUI extends Application {
    private Stage primary;
    private Scene homeScene, editorScene;
    private TextField nameField, codeField;
    private Button createBtn, joinBtn;
    private Button importBtn;

    private TextArea textArea;
    private TextField viewerCodeField, editorCodeField;
    private Button copyViewerBtn, copyEditorBtn, undoBtn, redoBtn, exportBtn;
    private ListView<String> usersList;
    private ListView<String> editorsList;
    private ListView<String> viewersList;
    private Label docNameLabel;

    // private String userId;
    // private String username;
    private Document currentDoc;
    private CollabService collabService;
    private User currentUser;

    private final Map<String, Line> cursorLines = new HashMap<>();
    private final Color[] cursorColors = { Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE };

    private final java.util.List<String> characterIds = new java.util.ArrayList<>();
    private int charIdCounter = 0;

    @Override
    public void start(Stage stage) {
        this.primary = stage;
        // userId = UUID.randomUUID().toString().substring(0,6);
        buildHomeScene();
        buildEditorScene();
        primary.setScene(homeScene);
        primary.setTitle("Collaborative Editor");
        primary.show();
    }



    private void buildHomeScene() {
        nameField = new TextField();
        nameField.setPromptText("Your name");
        Button createUserBtn = new Button("Create User");
        createBtn = new Button("Create Session");
        codeField = new TextField();
        codeField.setPromptText("Session code");
        joinBtn = new Button("Join Session");

        // Disable session buttons until user is created
        createBtn.setDisable(true);
        joinBtn.setDisable(true);

        createUserBtn.setOnAction(e -> {
            String username = nameField.getText().trim();
            if (username.isEmpty()) {
                showAlert("Please enter your name.");
                return;
            }
            currentUser = CollabService.createUser(username);
            if (currentUser != null) {
                createBtn.setDisable(false);
                joinBtn.setDisable(false);
                showAlert("User created! You can now create or join a session.");
            } else {
                showAlert("User creation failed.");
            }
        });

        createBtn.setOnAction(e -> {
            if (currentUser == null) {
                showAlert("Create a user first!");
                return;
            }
            ensureCollabService();
            currentDoc = collabService.createSession("untitled", currentUser);
            afterDocLoaded();
            primary.setScene(editorScene);
        });

        joinBtn.setOnAction(e -> {
            if (currentUser == null) {
                showAlert("Create a user first!");
                return;
            }
            String code = codeField.getText().trim();
            if (code.isEmpty()) {
                showAlert("Please enter a session code.");
                return;
            }
            ensureCollabService();
            collabService.joinSession(code, currentUser);
            // UI will update in onJoin callback
        });

        VBox homeBox = new VBox(15,
                new Label("Enter your name, create a user, then create or join a session."),
                new HBox(5, new Label("Name:"), nameField, createUserBtn),
                new HBox(5, createBtn, codeField, joinBtn));
        homeBox.setAlignment(Pos.CENTER);
        homeBox.setPadding(new Insets(20));
        homeScene = new Scene(homeBox, 500, 200);
    }

    // Utility method for alerts
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }



    private void ensureCollabService() {
        if (collabService != null)
            return;
        collabService = new CollabService(new CollabService.MessageCallback() {
            @Override
            public void onCreate(String sessionId, String editorCode,String viewerCode) {
                Platform.runLater(() -> {
                    docNameLabel.setText(sessionId);
                    editorCodeField.setText(editorCode);
                    viewerCodeField.setText(viewerCode);
                    textArea.setDisable(false);
                    // Optionally, switch to editor scene here if you want
                    // editorsList.getItems().clear();
                    // viewersList.getItems().clear();
                    primary.setScene(editorScene);
                });
            }

            @Override
            public void onJoin(Document doc , List<User> editors,List<User> viewers) {
                Platform.runLater(() -> {
                    currentDoc = doc;
                    editorsList.getItems().setAll(editors.stream().map(User::getUsername).toList());
                    viewersList.getItems().setAll(viewers.stream().map(User::getUsername).toList());            
                    afterDocLoaded();
                    textArea.setText(doc.getContent());
                    primary.setScene(editorScene);
                });
            }

            @Override
            public void onUpdate(String content, List<String> backendCharacterIds) {
                Platform.runLater(() -> {
                    textArea.setText(content);
                    characterIds.clear();
                    if (backendCharacterIds != null) {
                        characterIds.addAll(backendCharacterIds);
                    }
                });
            }

            @Override
            public void onPresence(List<User> editors, List<User> viewers) {
                //Platform.runLater(() -> usersList.getItems().setAll(users.stream().map(User::getUsername).toList()));
                Platform.runLater(() -> {
                    //usersList.getItems().setAll(users.stream().map(User::getUsername).toList());
                    editorsList.getItems().setAll(editors.stream().map(User::getUsername).toList());
                    viewersList.getItems().setAll(viewers.stream().map(User::getUsername).toList());
                    
                    // Optionally, clear editors/viewers if not present in this message
                });
            }

            @Override
            public void onCursor(String userId, int pos) {
                Platform.runLater(() -> updateCursor(userId, pos));
            }

            @Override
            public void onError(String errorMsg) {
                Platform.runLater(() -> showAlert("Error: " + errorMsg));
            }
        }, currentUser);
    }

    private void buildEditorScene() {


        docNameLabel = new Label("Untitled");
        docNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        undoBtn = new Button("↶");
        redoBtn = new Button("↷");
        exportBtn = new Button("Export");
        importBtn = new Button("Import"); // <-- Add this line

        undoBtn.setOnAction(e -> collabService.undo());
        redoBtn.setOnAction(e -> collabService.redo());
        exportBtn.setOnAction(e -> doExport());
        importBtn.setOnAction(e -> doImport()); // <-- Add this line

        HBox topButtons = new HBox(5, undoBtn, redoBtn, exportBtn, importBtn); // <-- Add importBtn here
        topButtons.setAlignment(Pos.CENTER_LEFT);
        topButtons.setPadding(new Insets(5));
        topButtons.setStyle("-fx-background-color: #eeeeee;");

        viewerCodeField = new TextField();
        viewerCodeField.setEditable(false);
        copyViewerBtn = new Button("Copy");
        copyViewerBtn.setOnAction(e -> copyToClipboard(viewerCodeField.getText()));
        editorCodeField = new TextField();
        editorCodeField.setEditable(false);
        copyEditorBtn = new Button("Copy");
        copyEditorBtn.setOnAction(e -> copyToClipboard(editorCodeField.getText()));
        
        usersList = new ListView<>();
        usersList.setPrefHeight(100);
    
        editorsList = new ListView<>();
        editorsList.setPrefHeight(60);
    
        viewersList = new ListView<>();
        viewersList.setPrefHeight(60);
    
        VBox infoBox = new VBox(10,
                new HBox(5, new Label("Viewer Code:"), viewerCodeField, copyViewerBtn),
                new HBox(5, new Label("Editor Code:"), editorCodeField, copyEditorBtn),
                new Label("Active Users:"), usersList,
                new Label("Editors:"), editorsList,
                new Label("Viewers:"), viewersList
        );
        infoBox.setPadding(new Insets(10));

        VBox leftPane = new VBox(10, docNameLabel, topButtons, infoBox);
        leftPane.setPrefWidth(260);
        leftPane.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #cccccc;");

        textArea = new TextArea();
        textArea.setDisable(true);
        textArea.setWrapText(true);
        textArea.addEventFilter(KeyEvent.KEY_TYPED, this::sendEdit);

        VBox editorBox = new VBox(5, textArea);
        VBox.setVgrow(textArea, Priority.ALWAYS);
        editorBox.setPadding(new Insets(10));

        HBox root = new HBox(leftPane, editorBox);
        HBox.setHgrow(editorBox, Priority.ALWAYS);
        editorScene = new Scene(root, 900, 600);
    }



    private void afterDocLoaded() {
        if (currentDoc == null)
            return;
        docNameLabel.setText(currentDoc.getId());
        viewerCodeField.setText(currentDoc.getViewerCode());
        editorCodeField.setText(currentDoc.getEditorCode());
        textArea.setText(currentDoc.getContent()); // <-- set content here too
        textArea.setDisable(false);
    }



    // private void sendEdit(KeyEvent ev) {
    //     if (collabService == null || currentDoc == null)
    //         return;

    //     int caretPos = textArea.getCaretPosition();
    //     String text = textArea.getText();
    //     String ch = ev.getCharacter();

    //     // Determine operation type
    //     String opType = "INSERT";
    //     if (ev.getEventType() == KeyEvent.KEY_TYPED && (ch == null || ch.isEmpty())) {
    //         opType = "DELETE";
    //     }

    //     collabService.sendEditOperation(currentDoc.getId(), opType, caretPos, ch, text, currentUser);
    //     // Also send cursor position update
    //     collabService.sendCursorUpdate(currentDoc.getId(), caretPos, currentUser);
    // }

    private void sendEdit(KeyEvent ev) {
        if (collabService == null || currentDoc == null) return;
    
        int caretPos = textArea.getCaretPosition();
        String ch = ev.getCharacter();
        String opType = "INSERT";
        String characterId = currentUser.getId() + ":" + (charIdCounter++);
    
        // Determine operation type
        if (ev.getEventType() == KeyEvent.KEY_TYPED && (ch == null || ch.isEmpty())) {
            opType = "DELETE";
            if (caretPos == 0 || characterIds.isEmpty()) return;
            String deletedCharId = characterIds.remove(caretPos - 1);
            collabService.sendDeleteOperation(currentDoc.getId(), caretPos - 1, deletedCharId, currentUser);
            return;
        }
    
        // INSERT operation
        String parentId = (caretPos == 0) ? "-1" : characterIds.get(caretPos - 1);
        characterIds.add(caretPos, characterId);
        collabService.sendInsertOperation(currentDoc.getId(), parentId, ch, characterId, currentUser);
    }



    private void doExport() {
        FileChooser fc = new FileChooser();
        String fileName = (currentDoc != null && currentDoc.getId() != null)
                ? currentDoc.getId() + ".txt"
                : "document.txt";
        fc.setInitialFileName(fileName);
        File f = fc.showSaveDialog(primary);
        if (f != null) {
            try (FileWriter w = new FileWriter(f)) {
                w.write(textArea.getText());
            } catch (Exception ex) {
                showAlert("Failed to export file: " + ex.getMessage());
            }
        }
    }

    private void doImport() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File f = fc.showOpenDialog(primary);
        if (f != null) {
            try {
                String content = Files.readString(f.toPath(), StandardCharsets.UTF_8);
                textArea.setText(content);

                int parentId = -1;
                for (int i = 0; i < content.length(); i++) {
                    char ch = content.charAt(i);
                    // Send each character as an insert operation
                    collabService.sendInsertChar(currentDoc.getId(), parentId, ch, currentUser);
                    // Optionally update parentId if your backend/CRDT expects it
                    parentId = -1; // or update to the new char's id if needed
                    textArea.appendText(String.valueOf(ch)); // Update local view
                }       
            } catch (Exception ex) {
                showAlert("Failed to import file: " + ex.getMessage());
            }
        }
    }

    private void copyToClipboard(String s) {
        ClipboardContent c = new ClipboardContent();
        c.putString(s);
        javafx.scene.input.Clipboard.getSystemClipboard().setContent(c);
    }

    private void updateCursor(String u, int pos) {
        Pane p = (Pane) textArea.lookup(".content");
        if (p == null)
            return;
        Line old = cursorLines.remove(u);
        if (old != null)
            p.getChildren().remove(old);
        int me = textArea.getCaretPosition();
        textArea.positionCaret(pos);
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

    public static void main(String[] args) {
        launch(args);
    }
}        // docNameLabel = new Label("Untitled");
        // docNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // undoBtn = new Button("↶");
        // redoBtn = new Button("↷");
        // exportBtn = new Button("Export");
        // undoBtn.setOnAction(e -> collabService.undo());
        // redoBtn.setOnAction(e -> collabService.redo());
        // exportBtn.setOnAction(e -> doExport());
        // HBox topButtons = new HBox(5, undoBtn, redoBtn, exportBtn);
        // topButtons.setAlignment(Pos.CENTER_LEFT);
        // topButtons.setPadding(new Insets(5));
        // topButtons.setStyle("-fx-background-color: #eeeeee;"); 
    // private void doExport() {
    // if (currentDoc == null)
    // return;
    // FileChooser fc = new FileChooser();
    // fc.setInitialFileName(currentDoc.getId() + ".txt");
    // File f = fc.showSaveDialog(primary);
    // if (f != null)
    // try (FileWriter w = new FileWriter(f)) {
    // w.write(textArea.getText());
    // } catch (Exception ex) {
    // ex.printStackTrace();
    // }
    // }  
    // private void ensureUserCreated() {
    // if (userId == null || userId.isEmpty()) {
    // String username = nameField.getText().trim();
    // if (username.isEmpty()) return;
    // User user = collabService.createUser(username);
    // userId = user.getId();
    // this.username = user.getUsername();
    // }
    // }
    // private void buildHomeScene() {
    // nameField = new TextField(); nameField.setPromptText("Your name");
    // createBtn = new Button("Create Session");
    // codeField = new TextField(); codeField.setPromptText("Session code");
    // joinBtn = new Button("Join Session");

    // createBtn.setOnAction(e -> {
    // String username = nameField.getText().trim();
    // if (username.isEmpty()) return;
    // if (currentUser == null || !username.equals(currentUser.getUsername())) {
    // currentUser = CollabService.createUser(username); // static method, see below
    // }
    // ensureCollabService();
    // currentDoc = collabService.createSession("untitled", currentUser);
    // afterDocLoaded();
    // primary.setScene(editorScene);
    // });

    // joinBtn.setOnAction(e -> {
    // String username = nameField.getText().trim();
    // String code = codeField.getText().trim();
    // if (username.isEmpty() || code.isEmpty()) return;
    // if (currentUser == null || !username.equals(currentUser.getUsername())) {
    // currentUser = CollabService.createUser(username);
    // }
    // ensureCollabService();
    // collabService.joinSession(code, currentUser);
    // // UI will update in onJoin callback
    // });

    // VBox homeBox = new VBox(15,
    // new Label("Enter your name and either create a new session or join an
    // existing one."),
    // new HBox(5, new Label("Name:"), nameField),
    // new HBox(5, createBtn, codeField, joinBtn)
    // );
    // homeBox.setAlignment(Pos.CENTER);
    // homeBox.setPadding(new Insets(20));
    // homeScene = new Scene(homeBox, 400, 200);
    // }
     // private void sendEdit(KeyEvent ev) {
    // if (collabService != null)
    // collabService.sendEdit(textArea.getText());
    // }         
    // Optionally, send the imported content to the server:
                // if (collabService != null) {
                //     collabService.sendEdit(content);
                // }    // private void createSession() {
    // // handled inline in buildHomeScene
    // }
    // private void joinSession() {
    // // handled inline in buildHomeScene
    // }