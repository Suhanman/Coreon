package com.coreon.board.controller;
import org.springframework.http.HttpStatus;
import java.net.URI;
import com.coreon.board.dto.response.AddAttachmentsRes;
import com.coreon.board.dto.response.AttachmentItemRes;
import com.coreon.board.service.BoardAttachmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/board")
public class BoardAttachmentController {

    private final BoardAttachmentService attachmentService;

    public BoardAttachmentController(BoardAttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    // 1) 첨부 목록
    @GetMapping("/posts/{boardId}/attachments")
    public ResponseEntity<List<AttachmentItemRes>> list(@PathVariable Long boardId, HttpSession session) {
        requireLogin(session);
        return ResponseEntity.ok(attachmentService.list(boardId));
    }

    // 2) 첨부 추가 업로드
    @PostMapping(value = "/posts/{boardId}/attachments", consumes = "multipart/form-data")
    public ResponseEntity<AddAttachmentsRes> add(
            @PathVariable Long boardId,
            @RequestPart(name = "files") List<MultipartFile> files,
            HttpSession session
    ) {
        requireLogin(session);
        List<Long> ids = attachmentService.addAttachments(boardId, files);
        return ResponseEntity.status(201).body(new AddAttachmentsRes("첨부 추가 완료", boardId, ids));
    }

    // 3) 첨부 삭제
    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<Void> delete(@PathVariable Long attachmentId, HttpSession session) {
        String id = requireId(session);          // ✅ employeeNo -> id
        boolean isAdmin = isAdmin(session);

        attachmentService.deleteAttachment(attachmentId, id, isAdmin); // ✅ id 전달
        return ResponseEntity.noContent().build();
    }

    // ---- session helpers ----
    private void requireLogin(HttpSession session) {
        if (session.getAttribute("id") == null) {     // ✅ employeeNo -> id
            throw new UnauthorizedException("로그인 필요");
        }
    }

    private String requireId(HttpSession session) {
        Object v = session.getAttribute("id");
        if (v == null) throw new UnauthorizedException("로그인 필요");
        return String.valueOf(v); // Long이어도 String이어도 안전
    }


    private boolean isAdmin(HttpSession session) {
        Boolean isAdminAttr = (Boolean) session.getAttribute("isAdmin");
        String role = (String) session.getAttribute("role");
        return (isAdminAttr != null && isAdminAttr)
                || (role != null && (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("ROLE_ADMIN")));
    }

    @ResponseStatus(org.springframework.http.HttpStatus.UNAUTHORIZED)
    private static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) { super(message); }
    }
    
    
 // BoardAttachmentController 안에 추가
    @GetMapping("/attachments/{attachmentId}/download-url")
    public ResponseEntity<DownloadUrlRes> downloadUrl(
            @PathVariable Long attachmentId,
            @RequestParam(defaultValue = "300") int expiresSeconds,
            HttpSession session
    ) {
        requireLogin(session);
        String url = attachmentService.getDownloadUrl(attachmentId, expiresSeconds);
        return ResponseEntity.ok(new DownloadUrlRes(url));
    }

    public static class DownloadUrlRes {
        private final String url;
        public DownloadUrlRes(String url) { this.url = url; }
        public String getUrl() { return url; }
    }

}
