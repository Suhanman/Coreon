package com.coreon.board.dto.request;

public class UpdatePostReq {
    private String category;
    private String title;
    private String content;
    private String dept;

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getDept() { return dept; }
    public void setDept(String dept) { this.dept = dept; }
}
