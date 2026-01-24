package com.coreon.board.dto.response;

import java.time.LocalDateTime;

public class BoardPostListItemResponse {
    private Long boardId;
    private String category;
    private String title;
    private String dept;
    private Long authorEmployeeNo;
    private String authorName;
    private Long viewCount;
    private LocalDateTime createdAt;

    public Long getBoardId() { return boardId; }
    public void setBoardId(Long boardId) { this.boardId = boardId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDept() { return dept; }
    public void setDept(String dept) { this.dept = dept; }

    public Long getAuthorEmployeeNo() { return authorEmployeeNo; }
    public void setAuthorEmployeeNo(Long authorEmployeeNo) { this.authorEmployeeNo = authorEmployeeNo; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public Long getViewCount() { return viewCount; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
