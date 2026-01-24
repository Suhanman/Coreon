package com.coreon.board.mapper;

import com.coreon.board.domain.BoardAttachment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardAttachmentMapper {

    int insertAttachment(BoardAttachment att);

    List<BoardAttachment> selectByBoardId(@Param("boardId") Long boardId);

    BoardAttachment selectById(@Param("attachmentId") Long attachmentId);

    int deleteById(@Param("attachmentId") Long attachmentId);
    
    int deleteByBoardId(@Param("boardId") Long boardId);

}
