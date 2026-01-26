package com.coreon.notice.service;

import com.coreon.notice.dto.*;

import java.util.List;

public interface NoticeService {
    List<ListDTO> list(String query, String category);
    NoticeDetailResponse detail(Long noticeId);

    Long create(NoticeRequest req, String actorRole, String actorId,  String actorDept);
    void update(Long noticeId, NoticeRequest req, String actorRole);
    void delete(Long noticeId, String actorRole);
}
