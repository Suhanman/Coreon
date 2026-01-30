package com.coreon.board.dto.response;

public class UpdatePostRes {
    private String message;
    private Long boardId;

    public UpdatePostRes(String message, Long boardId) {
        this.message = message;
        this.boardId = boardId;
    }

    public String getMessage() { return message; }
    public Long getBoardId() { return boardId; }
}
