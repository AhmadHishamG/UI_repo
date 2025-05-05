package com.example.ui;

// public class Document {
//     private String id;
//     private String viewerCode;
//     private String editorCode;

//     public String getId() { return id; }
//     public void setId(String id) { this.id = id; }
//     public String getViewerCode() { return viewerCode; }
//     public void setViewerCode(String viewerCode) { this.viewerCode = viewerCode; }
//     public String getEditorCode() { return editorCode; }
//     public void setEditorCode(String editorCode) { this.editorCode = editorCode; }
// }

public class Document {
    private String id;
    private String viewerCode;
    private String editorCode;
    private String content; // <-- Add this

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getViewerCode() { return viewerCode; }
    public void setViewerCode(String viewerCode) { this.viewerCode = viewerCode; }
    public String getEditorCode() { return editorCode; }
    public void setEditorCode(String editorCode) { this.editorCode = editorCode; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}