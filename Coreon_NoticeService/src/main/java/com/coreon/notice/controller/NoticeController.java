package com.coreon.notice.controller;

import com.coreon.notice.dto.ListDTO;
import com.coreon.notice.dto.NoticeDetailResponse;
import com.coreon.notice.dto.AttachResponseDTO;
import com.coreon.notice.dto.NoticeRequest;
import com.coreon.notice.service.NoticeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    private final NoticeService service;
    
    private String requireDept(HttpSession session) {
        Object v = session.getAttribute("dept"); // 실제 저장 키가 다르면 여기만 바꾸세요.
        if (v == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "세션에 부서 정보(dept)가 없습니다.");
        }
        String dept = String.valueOf(v).trim();
        if (dept.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "세션에 부서 정보(dept)가 없습니다.");
        }
        return dept;
    }


    public NoticeController(NoticeService service) {
        this.service = service;
    }

    // =========================
    // Session Helpers
    // =========================
    private String requireLoginId(HttpSession session) {
        Object v = session.getAttribute("id");
        if (v == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        String id = String.valueOf(v).trim();
        if (id.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return id;
    }

    private String requireAdminRole(HttpSession session) {
        // service.create/update/delete가 actorRole로 ADMIN 체크하므로
        // 여기서 선차단해서 403을 안정적으로 내려주기
        Object v = session.getAttribute("role");
        if (v == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ADMIN 권한이 필요합니다.");
        }
        String role = String.valueOf(v).trim();
        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ADMIN 권한이 필요합니다.");
        }
        return role;
    }

    // =========================
    // 1) 공지 목록 조회
    // GET /api/notice/items?query=&category=
    // (네 서비스가 page/size는 없으므로 단순 목록 형태로 구현)
    // =========================
    @GetMapping("/items")
    public ResponseEntity<List<ListDTO>> list(
            HttpSession session,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            // 명세에 q 파라미터가 있으니 호환용으로 추가(둘 다 오면 query 우선)
            @RequestParam(required = false, name = "q") String q
    ) {
        requireLoginId(session);

        String finalQuery = (query != null && !query.isBlank()) ? query : q;
        List<ListDTO> res = service.list(finalQuery, category);
        return ResponseEntity.ok(res);
    }

    // =========================
    // 2) 공지 상세 조회
    // GET /api/notice/items/{noticeId}
    // (조회수 증가 로직이 필요하면 Service 쪽에서 처리하도록 확장)
    // =========================
    @GetMapping("/items/{noticeId}")
    public ResponseEntity<NoticeDetailResponse> detail(
            HttpSession session,
            @PathVariable Long noticeId
    ) {
        requireLoginId(session);

        NoticeDetailResponse res = service.detail(noticeId);
        if (res == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "공지 없음");
        }
        return ResponseEntity.ok(res);
    }

    // =========================
    // 3) 공지 작성 (ADMIN)
    // POST /api/notice/items
    // =========================
    @PostMapping("/items")
    public ResponseEntity<Map<String, Object>> create(HttpSession session, @RequestBody NoticeRequest req) {
        String actorId   = requireLoginId(session);
        String actorRole = requireAdminRole(session);
        String actorDept = requireDept(session);

        Long noticeId = service.create(req, actorRole, actorId, actorDept);

        if (noticeId == null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "공지 등록 완료"));
        }
        return ResponseEntity
                .created(URI.create("/api/notice/items/" + noticeId))
                .body(Map.of("message", "공지 등록 완료", "noticeId", noticeId));
    }


    // =========================
    // 4) 공지 수정 (ADMIN)
    // PUT /api/notice/items/{noticeId}
    // =========================
    @PutMapping("/items/{noticeId}")
    public ResponseEntity<Map<String, Object>> update(
            HttpSession session,
            @PathVariable Long noticeId,
            @RequestBody NoticeRequest req
    ) {
        requireLoginId(session);
        String actorRole = requireAdminRole(session);

        service.update(noticeId, req, actorRole);
        return ResponseEntity.ok(Map.of("message", "공지 수정 완료"));
    }

    // =========================
    // 5) 공지 삭제 (ADMIN)
    // DELETE /api/notice/items/{noticeId}
    // =========================
    @DeleteMapping("/items/{noticeId}")
    public ResponseEntity<Map<String, Object>> delete(
            HttpSession session,
            @PathVariable Long noticeId
    ) {
        requireLoginId(session);
        String actorRole = requireAdminRole(session);

        service.delete(noticeId, actorRole);
        return ResponseEntity.ok(Map.of("message", "공지 삭제 완료"));
    }
}
