package com.coreon.board.controller;

import com.coreon.board.dto.request.CreatePostReq;
import com.coreon.board.dto.request.UpdatePostReq;
import com.coreon.board.dto.response.CreatePostRes;
import com.coreon.board.dto.response.UpdatePostRes;
import com.coreon.board.service.BoardPostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/board")
public class BoardPostController {

    private final BoardPostService boardPostService;

    public BoardPostController(BoardPostService boardPostService) {
        this.boardPostService = boardPostService;
    }

    @GetMapping("/posts")
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String dept,
            @RequestParam(required = false) String authorId,   // ✅ Long -> String
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "latest") String sort,
            HttpSession session
    ) {
        requireLogin(session);

        return ResponseEntity.ok(
                boardPostService.getPosts(page, size, category, dept, authorId, q, sort) // ✅ authorId String 전달
        );
    }

    // ✅ 게시글 작성 + 첨부 포함 (multipart)
    @PostMapping(value = "/posts", consumes = "multipart/form-data")
    public ResponseEntity<CreatePostRes> createPost(
            @ModelAttribute CreatePostReq req,
            @RequestPart(name = "files", required = false) List<MultipartFile> files,
            HttpSession session
    ) {
        String id = requireId(session); // ✅ Long -> String
        String username = (String) session.getAttribute("username");
        String myDept = (String) session.getAttribute("dept");

        Long boardId = boardPostService.createPost(req, files, id, username, myDept); // ✅ id String 전달

        return ResponseEntity.status(201).body(new CreatePostRes("작성 완료", boardId));
    }

    // ✅ 게시글 상세
    @GetMapping("/posts/{boardId}")
    public ResponseEntity<?> detail(@PathVariable Long boardId, HttpSession session) {
        requireLogin(session);
        return ResponseEntity.ok(boardPostService.getPostDetail(boardId));
    }

    // ✅ 게시글 수정
    @PutMapping("/posts/{boardId}")
    public ResponseEntity<UpdatePostRes> updatePost(
            @PathVariable Long boardId,
            @RequestBody UpdatePostReq req,
            HttpSession session
    ) {
        String id = requireId(session); // ✅ Long -> String
        boolean isAdmin = isAdmin(session);

        boardPostService.updatePost(boardId, req, id, isAdmin); // ✅ id String 전달

        return ResponseEntity.ok(new UpdatePostRes("수정 완료", boardId));
    }

    // ✅ 게시글 삭제: 작성자 or 관리자만
    @DeleteMapping("/posts/{boardId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long boardId, HttpSession session) {
        String id = requireId(session); // ✅ Long -> String
        boolean isAdmin = isAdmin(session);

        boardPostService.deletePost(boardId, id, isAdmin); // ✅ id String 전달
        return ResponseEntity.noContent().build(); // 204
    }

    // ---------------------------
    // Session Helpers
    // ---------------------------
    private void requireLogin(HttpSession session) {
        if (session.getAttribute("id") == null) {
            throw new UnauthorizedException("로그인 필요");
        }
    }

    private String requireId(HttpSession session) { // ✅ 반환 타입 String
        Object v = session.getAttribute("id");
        if (v == null) throw new UnauthorizedException("로그인 필요");

        // ✅ id는 문자열이라고 했으니 String으로 고정
        if (v instanceof String) return (String) v;

        // 혹시 실수로 다른 타입이 들어오면 방어
        return String.valueOf(v);
    }

    private boolean isAdmin(HttpSession session) {
        Boolean isAdminAttr = (Boolean) session.getAttribute("isAdmin");
        String role = (String) session.getAttribute("role");
        return (isAdminAttr != null && isAdminAttr)
                || (role != null && (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("ROLE_ADMIN")));
    }

    @ResponseStatus(org.springframework.http.HttpStatus.UNAUTHORIZED)
    private static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
}
