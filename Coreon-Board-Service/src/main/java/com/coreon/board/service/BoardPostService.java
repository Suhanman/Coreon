package com.coreon.board.service;

import com.coreon.board.domain.BoardAttachment;
import com.coreon.board.domain.BoardPost;
import com.coreon.board.mapper.BoardAttachmentMapper;
import com.coreon.board.mapper.BoardPostMapper;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.coreon.board.dto.response.BoardPostListItemResponse;
import com.coreon.board.dto.response.PageRes;
import com.coreon.board.dto.response.PostDetailRes;

@Service
public class BoardPostService {

    private final BoardPostMapper boardPostMapper;
    private final BoardAttachmentMapper boardAttachmentMapper;
    private final S3service storageService;

    public BoardPostService(BoardPostMapper boardPostMapper,
                            BoardAttachmentMapper boardAttachmentMapper,
                            S3service storageService) {
        this.boardPostMapper = boardPostMapper;
        this.boardAttachmentMapper = boardAttachmentMapper;
        this.storageService = storageService;
    }

    @Transactional
    public Long createPost(com.coreon.board.dto.request.CreatePostReq req,
                           List<MultipartFile> files,
                           String id, String username, String myDept) {

        BoardPost post = new BoardPost();
        post.setCategory(req.getCategory());
        post.setTitle(req.getTitle());
        post.setContent(req.getContent());

        // dept는 req가 비면 세션 dept 사용
        String dept = (req.getDept() == null || req.getDept().isBlank()) ? myDept : req.getDept();
        post.setDept(dept);

        post.setAuthorid(id);
        post.setAuthorname(username);

        boardPostMapper.insertPost(post);

        // ✅ 첨부 업로드 (S3service.uploadFile 사용 / key 저장)
        if (files != null && !files.isEmpty()) {
            for (MultipartFile f : files) {
                if (f == null || f.isEmpty()) continue;

                try {
                    // ✅ S3service는 URL이 아니라 "key" 리턴
                    String storedKey = storageService.uploadFile(f);

                    BoardAttachment att = new BoardAttachment();
                    att.setBoardId(post.getBoardId());
                    att.setOriginalName(f.getOriginalFilename());
                    att.setStoredUrl(storedKey);          // ✅ DB에는 URL 말고 key 저장
                    att.setContentType(f.getContentType());
                    att.setSizeBytes(f.getSize());

                    boardAttachmentMapper.insertAttachment(att);

                } catch (IOException e) {
                    // 트랜잭션 안에서 업로드 실패 시 롤백 유도
                    throw new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "첨부파일 업로드 실패",
                            e
                    );
                }
            }
        }

        return post.getBoardId();
    }

    @Transactional
    public void deletePost(Long boardId, String requesterId, boolean isAdmin) {
        String authorId = boardPostMapper.selectAuthorId(boardId);
        if (authorId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다.");
        }

        boolean isOwner = authorId.equals(requesterId);
        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");
        }

        // ✅ (기존대로) 첨부 DB만 삭제 (S3 삭제는 스킵)
        boardAttachmentMapper.deleteByBoardId(boardId);

        int deleted = boardPostMapper.deletePostById(boardId);
        if (deleted == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다.");
        }
    }

    @Transactional
    public void updatePost(Long boardId, com.coreon.board.dto.request.UpdatePostReq req,
                           String requesterId, boolean isAdmin) {

        String authorId = boardPostMapper.selectAuthorId(boardId);
        if (authorId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다.");
        }

        boolean isOwner = authorId.equals(requesterId);
        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
        }

        BoardPost post = new BoardPost();
        post.setBoardId(boardId);
        post.setCategory(req.getCategory());
        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        post.setDept(req.getDept());

        int updated = boardPostMapper.updatePost(post);
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다.");
        }
    }

    @Transactional(readOnly = true)
    public PageRes<BoardPostListItemResponse> getPosts(
            int page, int size,
            String category, String dept,
            String authorId,
            String q, String sort
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        int offset = safePage * safeSize;

        var list = boardPostMapper.selectPosts(offset, safeSize, category, dept, authorId, q, sort);
        long total = boardPostMapper.countPosts(category, dept, authorId, q);

        return new PageRes<>(list, safePage, safeSize, total);
    }

    @Transactional(readOnly = true)
    public PostDetailRes getPostDetail(Long boardId) {
        boardPostMapper.incrementViewCount(boardId);

        BoardPost post = boardPostMapper.selectPostById(boardId);
        if (post == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글 없음");

        PostDetailRes res = new PostDetailRes();
        res.setBoardId(post.getBoardId());
        res.setCategory(post.getCategory());
        res.setTitle(post.getTitle());
        res.setContent(post.getContent());
        res.setDept(post.getDept());

        res.setAuthorId(post.getAuthorid());
        res.setAuthorName(post.getAuthorname());

        res.setViewCount(post.getViewCount());
        res.setCreatedAt(post.getCreatedAt());
        res.setUpdatedAt(post.getUpdatedAt());

        return res;
    }
}
