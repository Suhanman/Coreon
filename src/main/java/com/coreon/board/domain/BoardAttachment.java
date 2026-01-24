package com.coreon.board.domain;

import java.time.LocalDateTime;

public class BoardAttachment {
    private Long attachmentId;
    private Long boardId;

    private String originalName;
    private String storedUrl;

    private String contentType;   // nullable
    private Long sizeBytes;       // nullable

    private LocalDateTime createdAt; // DB에서 자동

    public BoardAttachment() {}

    public Long getAttachmentId() { return attachmentId; }
    public void setAttachmentId(Long attachmentId) { this.attachmentId = attachmentId; }

    public Long getBoardId() { return boardId; }
    public void setBoardId(Long boardId) { this.boardId = boardId; }

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }

    public String getStoredUrl() { return storedUrl; }
    public void setStoredUrl(String storedUrl) { this.storedUrl = storedUrl; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
