package com.coreon.board.dto.response;

import java.util.List;

public class AddAttachmentsRes {
    private String message;
    private Long boardId;
    private List<Long> attachmentIds;

    public AddAttachmentsRes(String message, Long boardId, List<Long> attachmentIds) {
        this.message = message;
        this.boardId = boardId;
        this.attachmentIds = attachmentIds;
    }

    public String getMessage() { return message; }
    public Long getBoardId() { return boardId; }
    public List<Long> getAttachmentIds() { return attachmentIds; }
}
