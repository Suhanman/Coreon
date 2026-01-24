package com.coreon.board.controller;

import com.coreon.board.domain.BoardAttachment;
import com.coreon.board.domain.BoardPost;
import com.coreon.board.mapper.BoardAttachmentMapper;
import com.coreon.board.mapper.BoardPostMapper;
import com.coreon.board.service.BoardPostService;
import com.coreon.board.service.S3service;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/board")
public class BoardPostController {

    private final BoardPostMapper boardPostMapper;
    private final BoardAttachmentMapper boardAttachmentMapper;
    private final S3service storageService;
    private final BoardPostService boardPostService;

    // ✅ 생성자에 boardPostService를 반드시 주입해야 final 필드 컴파일 에러가 안 남
    public BoardPostController(BoardPostMapper boardPostMapper,
                               BoardAttachmentMapper boardAttachmentMapper,
                               S3service storageService,
                               BoardPostService boardPostService) {
        this.boardPostMapper = boardPostMapper;
        this.boardAttachmentMapper = boardAttachmentMapper;
        this.storageService = storageService;
        this.boardPostService = boardPostService;
    }

    @GetMapping("/posts")
    public List<BoardPost> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String dept,
            @RequestParam(required = false) Long authorEmployeeNo,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "latest") String sort
    ) {
        int offset = page * size;
        return boardPostMapper.selectPosts(offset, size, category, dept, authorEmployeeNo, q, sort);
    }

    // ✅ 게시글 작성 + 첨부 포함 (multipart)
    @PostMapping(value = "/posts", consumes = "multipart/form-data")
    public ResponseEntity<?> createPost(
            @ModelAttribute BoardPost post,
            @RequestPart(name = "files", required = false) List<MultipartFile> files,
            HttpSession session
    ) {
        Long employeeNo = (Long) session.getAttribute("employeeNo");
        String username = (String) session.getAttribute("username");
        String myDept = (String) session.getAttribute("dept");

        if (employeeNo == null) {
            return ResponseEntity.status(401).body("로그인 필요");
        }

        // 1) 작성자/부서 자동 세팅
        post.setAuthorEmployeeNo(employeeNo);
        post.setAuthorName(username);

        if (post.getDept() == null || post.getDept().isBlank()) {
            post.setDept(myDept);
        }

        // 2) board_post INSERT (useGeneratedKeys로 boardId 채워짐)
        boardPostMapper.insertPost(post);

        // 3) 첨부 업로드 + DB 저장
        if (files != null && !files.isEmpty()) {
            for (MultipartFile f : files) {
                if (f == null || f.isEmpty()) continue;

                // ✅ S3 업로드 -> URL 리턴
                String storedUrl = storageService.upload(post.getBoardId(), f);

                BoardAttachment att = new BoardAttachment();
                att.setBoardId(post.getBoardId());
                att.setOriginalName(f.getOriginalFilename());
                att.setStoredUrl(storedUrl);
                att.setContentType(f.getContentType());
                att.setSizeBytes(f.getSize());

                boardAttachmentMapper.insertAttachment(att);
            }
        }

        return ResponseEntity.status(201).body(
                Map.of(
                        "message", "작성 완료",
                        "boardId", post.getBoardId()
                )
        );
    }

    // ✅ 게시글 삭제: 작성자 or 관리자만
    @DeleteMapping("/posts/{boardId}")
    public ResponseEntity<?> deletePost(@PathVariable Long boardId, HttpSession session) {
        Long employeeNo = (Long) session.getAttribute("employeeNo");

        if (employeeNo == null) {
            return ResponseEntity.status(401).body("로그인 필요");
        }

        // 관리자 판별 (프로젝트마다 세션 키가 다를 수 있어서 2가지 케이스 지원)
        Boolean isAdminAttr = (Boolean) session.getAttribute("isAdmin"); // true/false
        String role = (String) session.getAttribute("role");            // "ADMIN" or "ROLE_ADMIN"

        boolean isAdmin =
                (isAdminAttr != null && isAdminAttr)
                        || (role != null && (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("ROLE_ADMIN")));

        boardPostService.deletePost(boardId, employeeNo, isAdmin);
        return ResponseEntity.noContent().build(); // 204
    }
    //게시물 상세 확인 및 삭제 
    
    @GetMapping("/posts/{boardId}")
    public ResponseEntity<?> detail(@PathVariable Long boardId) {
        BoardPost post = boardPostMapper.selectPostById(boardId);
        if (post == null) return ResponseEntity.status(404).body("게시글 없음");
        return ResponseEntity.ok(post);
    }
    
    //게시글 수정
    
    @PutMapping("/posts/{boardId}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long boardId,
            @RequestBody BoardPost req,
            HttpSession session
    ) {
        Long employeeNo = (Long) session.getAttribute("employeeNo");
        if (employeeNo == null) return ResponseEntity.status(401).body("로그인 필요");

        Boolean isAdminAttr = (Boolean) session.getAttribute("isAdmin");
        String role = (String) session.getAttribute("role");
        boolean isAdmin =
                (isAdminAttr != null && isAdminAttr)
                        || (role != null && (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("ROLE_ADMIN")));

        boardPostService.updatePost(boardId, req, employeeNo, isAdmin);
        return ResponseEntity.ok(Map.of("message", "수정 완료", "boardId", boardId));
    }

}
