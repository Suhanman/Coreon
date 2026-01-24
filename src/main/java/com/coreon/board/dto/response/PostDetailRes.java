package com.coreon.board.dto.response;

import java.time.LocalDateTime;

public class PostDetailRes {

    private Long boardId;
    private String category;
    private String title;
    private String content;
    private String dept;
    private Integer authorEmployeeNo;
    private String authorName;
    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PostDetailRes() {}

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public Integer getAuthorEmployeeNo() {
        return authorEmployeeNo;
    }

    public void setAuthorEmployeeNo(Integer authorEmployeeNo) {
        this.authorEmployeeNo = authorEmployeeNo;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
