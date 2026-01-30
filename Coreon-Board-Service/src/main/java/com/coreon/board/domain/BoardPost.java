package com.coreon.board.domain;

import java.time.LocalDateTime;

public class BoardPost {
    private Long board_id;
    private String category;
    private String title;
    private String content;
    private String dept;
    private String authorid;
    private String authorname;
    private Long view_count;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    public Long getBoardId() { return board_id; }
    public void setBoardId(Long board_id) { this.board_id = board_id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getDept() { return dept; }
    public void setDept(String dept) { this.dept = dept; }

  



    public String getAuthorid() {
		return authorid;
	}
	public void setAuthorid(String authorid) {
		this.authorid = authorid;
	}
	public String getAuthorname() {
		return authorname;
	}
	public void setAuthorname(String authorname) {
		this.authorname = authorname;
	}
	public Long getViewCount() { return view_count; }
    public void setViewCount(Long view_count) { this.view_count = view_count; }

    public LocalDateTime getCreatedAt() { return created_at; }
    public void setCreatedAt(LocalDateTime created_at) { this.created_at = created_at; }

    public LocalDateTime getUpdatedAt() { return updated_at; }
    public void setUpdatedAt(LocalDateTime updated_at) { this.updated_at = updated_at; }
}
