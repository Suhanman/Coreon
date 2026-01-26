package com.coreon.notice.mapper;

import com.coreon.notice.dto.ListDTO;
import com.coreon.notice.dto.NoticeDetailResponse;
import com.coreon.notice.dto.AttachResponseDTO;
import com.coreon.notice.dto.NoticeRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeMapper {

    List<ListDTO> selectNoticeList(
            @Param("query") String query,
            @Param("category") String category
    );

    NoticeDetailResponse selectNoticeDetail(@Param("noticeId") Long noticeId);

    int insertNotice(@Param("req") NoticeRequest req,
                     @Param("actorId") String actorId);

    int updateNotice(@Param("noticeId") Long noticeId,
                     @Param("req") NoticeRequest req);

    int deleteNotice(@Param("noticeId") Long noticeId);

	Integer existsNotice(Long noticeId);

	String selectWriterIdByNoticeId(Long noticeId);
}
