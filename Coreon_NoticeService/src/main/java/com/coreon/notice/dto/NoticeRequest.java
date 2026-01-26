package com.coreon.notice.dto;

public class NoticeRequest {
	private Long noticeId;
    private String category;      // INSPECTION / UPDATE / GUIDE
    private String title;
    private String content;
    private String publisherDept;
    
    
	public Long getNoticeId() {
		return noticeId;
	}
	public void setNoticeId(Long noticeId) {
		this.noticeId = noticeId;
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
	public String getPublisherDept() {
		return publisherDept;
	}
	public void setPublisherDept(String publisherDept) {
		this.publisherDept = publisherDept;
	}

    // getter/setter
}
