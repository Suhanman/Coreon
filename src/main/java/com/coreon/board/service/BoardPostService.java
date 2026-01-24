package com.coreon.board.service;

import com.coreon.board.domain.BoardPost;
import com.coreon.board.mapper.BoardAttachmentMapper;
import com.coreon.board.mapper.BoardPostMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BoardPostService {

    private final BoardPostMapper boardPostMapper;
    private final BoardAttachmentMapper boardAttachmentMapper;


    public BoardPostService(BoardPostMapper boardPostMapper,
                            BoardAttachmentMapper boardAttachmentMapper) {
        this.boardPostMapper = boardPostMapper;
        this.boardAttachmentMapper = boardAttachmentMapper;
    }

    @Transactional
    public void deletePost(Long boardId, Long requesterEmployeeNo, boolean isAdmin) {
        Long authorEmployeeNo = boardPostMapper.selectAuthorEmployeeNo(boardId);
        if (authorEmployeeNo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다.");
        }

        boolean isOwner = authorEmployeeNo.equals(requesterEmployeeNo);
        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");
        }

        // 첨부 DB 먼저 삭제 (S3는 스킵)
        boardAttachmentMapper.deleteByBoardId(boardId);

        // 게시글 삭제
        boardPostMapper.deletePostById(boardId);
    }
    
    @Transactional
    public void updatePost(Long boardId, BoardPost req, Long requesterEmployeeNo, boolean isAdmin) {
        Long authorEmployeeNo = boardPostMapper.selectAuthorEmployeeNo(boardId);
        if (authorEmployeeNo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다.");
        }

        boolean isOwner = authorEmployeeNo.equals(requesterEmployeeNo);
        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
        }

        // boardId는 path 기준으로 강제
        req.setBoardId(boardId);

        int updated = boardPostMapper.updatePost(req);
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다.");
        }
    }

}
