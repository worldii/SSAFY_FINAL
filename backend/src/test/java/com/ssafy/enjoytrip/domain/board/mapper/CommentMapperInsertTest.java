package com.ssafy.enjoytrip.domain.board.mapper;

import com.ssafy.enjoytrip.core.board.model.entity.Comment;
import com.ssafy.enjoytrip.core.board.model.mapper.CommentMapper;
import com.ssafy.enjoytrip.core.board.service.BoardService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
class CommentMapperInsertTest {

    @Autowired
    CommentMapper commentMapper;
    @Autowired
    BoardService boardService;


    @Test
    @DisplayName("Comment Mapper 오류 : commentRequest에 참조하는 boardId가 없을 때 foreign key 오류가 난다.")
    @Transactional
    void insertCommentTest() {
        // given
        Comment comment = Comment.builder().boardId(2L).userId(null).content("hello").build();
        // when, then
        Assertions.assertThrows(Exception.class, () -> {
            commentMapper.insertComment(comment);
        });
    }

    @Test
    @DisplayName("Comment Mapper 오류 : commentRequest의 userId가 없을 때")
    @Transactional
    void insertCommentTest2() {
        // given
        Comment comment = Comment.builder().boardId(1L).content("hello").build();
        // when then
        Assertions.assertThrows(Exception.class, () -> {
            commentMapper.insertComment(comment);
        });
    }

    @Test
    @DisplayName("Comment Mapper 정상 작동")
    @Transactional
    void insertCommentTest4() {
        // given
        Comment comment = Comment.builder().boardId(1L).userId("ssafy").content("hello").build();
        // when
        Long res = commentMapper.insertComment(comment);
        // then
        Assertions.assertEquals(1, res);
    }

}
