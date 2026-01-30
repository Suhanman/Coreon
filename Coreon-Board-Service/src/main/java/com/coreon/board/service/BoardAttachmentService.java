package com.coreon.board.service;

import com.coreon.board.domain.BoardAttachment;
import com.coreon.board.domain.BoardPost;
import com.coreon.board.dto.response.AttachmentItemRes;
import com.coreon.board.mapper.BoardAttachmentMapper;
import com.coreon.board.mapper.BoardPostMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BoardAttachmentService {

    private final BoardAttachmentMapper attachmentMapper;
    private final BoardPostMapper postMapper;
    private final S3service s3service;

    public BoardAttachmentService(BoardAttachmentMapper attachmentMapper,
                                  BoardPostMapper postMapper,
                                  S3service s3service) {
        this.attachmentMapper = attachmentMapper;
        this.postMapper = postMapper;
        this.s3service = s3service;
    }

    @Transactional(readOnly = true)
    public List<AttachmentItemRes> list(Long boardId) {
        return attachmentMapper.selectByBoardId(boardId).stream()
                .map(a -> new AttachmentItemRes(
                        a.getAttachmentId(), a.getBoardId(), a.getOriginalName(),
                        a.getStoredUrl(), a.getContentType(), a.getSizeBytes(), a.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public List<Long> addAttachments(Long boardId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return List.of();

        BoardPost post = postMapper.selectPostById(boardId);
        if (post == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글 없음: " + boardId);
        }

        List<Long> ids = new ArrayList<>();

        for (MultipartFile f : files) {
            if (f == null || f.isEmpty()) continue;

            // 1) S3 업로드 (✅ URL이 아니라 key 반환)
            final String storedKey;
            try {
                storedKey = s3service.uploadFile(f);
            } catch (IOException e) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "첨부파일 업로드 실패",
                        e
                );
            }

            // 2) DB insert (✅ storedUrl 컬럼에 key 저장)
            BoardAttachment att = new BoardAttachment();
            att.setBoardId(boardId);
            att.setOriginalName(f.getOriginalFilename());
            att.setStoredUrl(storedKey); // ✅ DB에는 URL 말고 key 저장
            att.setContentType(f.getContentType());
            att.setSizeBytes(f.getSize());

            attachmentMapper.insertAttachment(att);
            ids.add(att.getAttachmentId());
        }

        return ids;
    }

    @Transactional
    public void deleteAttachment(Long attachmentId, String loginId, boolean isAdmin) {
        if (loginId == null || loginId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요");
        }

        BoardAttachment att = attachmentMapper.selectById(attachmentId);
        if (att == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "첨부 없음: " + attachmentId);
        }

        BoardPost post = postMapper.selectPostById(att.getBoardId());
        if (post == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글 없음: " + att.getBoardId());
        }

        // ✅ 작성자 체크 (String id 기반)
        String authorId = post.getAuthorid();
        if (authorId == null || authorId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "게시글 작성자 정보 누락(boardId=" + post.getBoardId() + ")"
            );
        }

        if (!isAdmin && !loginId.equals(authorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "삭제 권한 없음");
        }

        // ✅ S3 삭제(선택): storedUrl에 key가 들어가도 deleteFileByUrl이 처리 가능
        // s3service.deleteFileByUrl(att.getStoredUrl());

        attachmentMapper.deleteById(attachmentId);
    }
    
    @Transactional(readOnly = true)
    public String getDownloadUrl(Long attachmentId, int expiresSeconds) {
        BoardAttachment att = attachmentMapper.selectById(attachmentId);
        if (att == null) throw new IllegalArgumentException("첨부 없음: " + attachmentId);

        // storedUrl에는 key가 들어있음. S3service가 key/URL 둘 다 처리 가능하게 되어있음.
        return s3service.generatePresignedDownloadUrl(
                att.getStoredUrl(),
                att.getOriginalName(),
                expiresSeconds
        );
    }
    
    

}
