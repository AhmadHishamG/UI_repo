package com.example.ui;



import java.util.List;

// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;

public class Message {
    private String type;
    private User sender;
    private String sessionId;
    private String editorCode;
    private String viewerCode;
    private String content;
    private List<User> editors;
    private List<User> viewers;
    private List<String> characterIds;
    private String error;

    public Message() {}
    public Message(String type, User sender, String sessionId, String editorCode, String viewerCode, String content,
            List<User> editors, List<User> viewers, List<String> characterIds, String error) {
        this.type = type;
        this.sender = sender;
        this.sessionId = sessionId;
        this.editorCode = editorCode;
        this.viewerCode = viewerCode;
        this.content = content;
        this.editors = editors;
        this.viewers = viewers;
        this.characterIds = characterIds;
        this.error = error;
    }


    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public User getSender() {
        return sender;
    }
    public void setSender(User sender) {
        this.sender = sender;
    }
    public String getSessionId() {
        return sessionId;
    }
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    public String getEditorCode() {
        return editorCode;
    }
    public void setEditorCode(String editorCode) {
        this.editorCode = editorCode;
    }
    public String getViewerCode() {
        return viewerCode;
    }
    public void setViewerCode(String viewerCode) {
        this.viewerCode = viewerCode;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public List<User> getEditors() {
        return editors;
    }
    public void setEditors(List<User> editors) {
        this.editors = editors;
    }
    public List<User> getViewers() {
        return viewers;
    }
    public void setViewers(List<User> viewers) {
        this.viewers = viewers;
    }
    public List<String> getCharacterIds() {
        return characterIds;
    }
    public void setCharacterIds(List<String> characterIds) {
        this.characterIds = characterIds;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }



}
