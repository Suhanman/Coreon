package com.coreon.notice.service;

import com.coreon.notice.dto.*;
import com.coreon.notice.mapper.NoticeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper mapper;

    public NoticeServiceImpl(NoticeMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<ListDTO> list(String query, String category) {
        return mapper.selectNoticeList(query, category);
    }

    @Override
    public NoticeDetailResponse detail(Long noticeId) {
        return mapper.selectNoticeDetail(noticeId);
    }

    private void requireAdmin(String actorRole) {
        if (actorRole == null || !actorRole.equals("ADMIN")) {
            // 프로젝트 스타일에 맞춰 CustomException + @ControllerAdvice 권장
            throw new RuntimeException("FORBIDDEN: ADMIN 권한이 필요합니다.");
        }
    }

    @Override
    @Transactional
    public Long create(NoticeRequest req, String actorRole, String actorId, String actorDept) {
        requireAdmin(actorRole);
        
        req.setPublisherDept(actorDept);
        mapper.insertNotice(req, actorId);
        // MyBatis에서 생성 PK를 받으려면 useGeneratedKeys를 쓰거나 selectKey를 추가해야 함.
        // 일단 “생성 성공”만 필요하면 void로 시작해도 됨.
        return req.getNoticeId();
    }

    @Override
    @Transactional
    public void update(Long noticeId, NoticeRequest req, String actorRole) {
        requireAdmin(actorRole);
        mapper.updateNotice(noticeId, req);
    }

    @Override
    @Transactional
    public void delete(Long noticeId, String actorRole) {
        requireAdmin(actorRole);
        mapper.deleteNotice(noticeId);
    }
}
