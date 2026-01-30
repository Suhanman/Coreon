package com.coreon.board.dto.response;

import java.time.LocalDateTime;

public class AttachmentItemRes {
    private Long attachmentId;
    private Long boardId;
    private String originalName;
    private String storedUrl;
    private String contentType;
    private Long sizeBytes;
    private LocalDateTime createdAt;

    public AttachmentItemRes() {}

    public AttachmentItemRes(Long attachmentId, Long boardId, String originalName,
                             String storedUrl, String contentType, Long sizeBytes, LocalDateTime createdAt) {
        this.attachmentId = attachmentId;
        this.boardId = boardId;
        this.originalName = originalName;
        this.storedUrl = storedUrl;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.createdAt = createdAt;
    }

    public Long getAttachmentId() { return attachmentId; }
    public Long getBoardId() { return boardId; }
    public String getOriginalName() { return originalName; }
    public String getStoredUrl() { return storedUrl; }
    public String getContentType() { return contentType; }
    public Long getSizeBytes() { return sizeBytes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
