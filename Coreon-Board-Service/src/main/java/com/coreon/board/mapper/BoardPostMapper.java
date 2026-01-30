package com.coreon.board.mapper;

import com.coreon.board.domain.BoardPost;
import com.coreon.board.dto.response.BoardPostListItemResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardPostMapper {

    // 목록 조회 (페이징 + 필터 + 검색 + 정렬)
    List<BoardPostListItemResponse> selectPosts(
            @Param("offset") int offset,
            @Param("size") int size,
            @Param("category") String category,
            @Param("dept") String dept,
            @Param("authorid") String authorid,    // ✅ Long authorEmployeeNo -> String authorId
            @Param("q") String q,
            @Param("sort") String sort
    );

    // 목록 total count
    long countPosts(
            @Param("category") String category,
            @Param("dept") String dept,
            @Param("authorid") String authorid,    // ✅ Long -> String
            @Param("q") String q
    );

    // 상세 조회
    BoardPost selectPostById(@Param("boardId") Long boardId);

    // 조회수 증가
    int incrementViewCount(@Param("boardId") Long boardId);

    // 데이터 저장
    int insertPost(BoardPost post);

    // ✅ 작성자 조회 (삭제/수정 권한 체크용)
    String selectAuthorId(@Param("boardId") Long boardId); // ✅ selectAuthorEmployeeNo -> selectAuthorId

    // 게시글 삭제
    int deletePostById(@Param("boardId") Long boardId);

    // 게시글 수정
    int updatePost(BoardPost post);
}
