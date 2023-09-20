package com.ssafy.enjoytrip.board.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ssafy.enjoytrip.board.model.dto.Board;
import com.ssafy.enjoytrip.board.model.dto.request.BoardModifyRequest;
import com.ssafy.enjoytrip.board.model.dto.request.BoardSaveRequest;
import com.ssafy.enjoytrip.board.model.dto.request.PageInfoRequest;
import com.ssafy.enjoytrip.board.model.dto.request.SearchDto;
import com.ssafy.enjoytrip.board.model.dto.response.PageResponse;
import com.ssafy.enjoytrip.board.model.mapper.BoardMapper;
import com.ssafy.enjoytrip.board.model.mapper.CommentMapper;
import com.ssafy.enjoytrip.global.error.BoardException;
import com.ssafy.enjoytrip.global.error.UserNotFoundException;
import com.ssafy.enjoytrip.media.FileService;
import com.ssafy.enjoytrip.user.model.dto.User;
import com.ssafy.enjoytrip.user.model.mapper.UserMapper;
import com.ssafy.enjoytrip.util.PageNavigationForPageHelper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper;
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;
    private final FileService fileService;

    @Override
    @Transactional
    public Long saveBoard(final String json, final List<MultipartFile> files, final String userId) {
        final ObjectMapper objectMapper = new ObjectMapper();

        try {
            final BoardSaveRequest request = objectMapper.readValue(json, BoardSaveRequest.class);
            final Board board = Board.builder()
                .boardType(request.getBoardType())
                .subject(request.getSubject())
                .content(request.getContent())
                .userId(userId)
                .build();
            final Long boardId = boardMapper.insertBoard(board);

            if (files != null) {
                fileService.insertFile(boardId, files, "board/");
            }

            return boardId;

        } catch (final Exception e) {
            throw new BoardException("json 파싱 에러");
        }
    }

    @Override
    public PageResponse getBoardList(PageInfoRequest pageInfoRequest, String path) {
        if (pageInfoRequest.getPage() == 0) {
            pageInfoRequest = new PageInfoRequest(1, 10);
        }

        PageHelper.startPage(pageInfoRequest.getPage(), pageInfoRequest.getPageSize());
        return new PageResponse(
            new PageNavigationForPageHelper(boardMapper.selectAll(), "/board/list?page"));
    }

    @Override
    public PageResponse getListByPage(
        final Integer currentPage,
        Integer pageSize,
        final String path
    ) {
        if (pageSize == null) {
            pageSize = 10;
        }
        PageHelper.startPage(currentPage, pageSize);

        return PageResponse.from(new PageNavigationForPageHelper(boardMapper.selectAll(), path));
    }

    @Override
    public PageResponse getBoardListBySearchDto(
        final SearchDto searchDto,
        PageInfoRequest pageInfoRequest,
        final String path
    ) {
        if (pageInfoRequest.getPage() == 0) {
            pageInfoRequest = new PageInfoRequest(1, 10);
        }
        PageHelper.startPage(pageInfoRequest.getPage(), pageInfoRequest.getPageSize());
        Page<Board> boards = boardMapper.selectBoardListBySearchDto(searchDto);

        return PageResponse.from(new PageNavigationForPageHelper(boards, path));
    }


    @Override
    public Board detail(final Long boardId) {
        return boardMapper
            .selectBoard(boardId)
            .orElseThrow(() -> new BoardException("해당 boardId에 해당하는 board가 없습니다."));
    }

    @Override
    @Transactional
    public void modify(
        final Long boardId,
        final String userId,
        final BoardModifyRequest boardModifyRequest
    ) {
        userMapper
            .selectByUserId(userId)
            .orElseThrow(() -> new BoardException("해당 유저가 없습니다."));
        Board board = boardMapper
            .selectBoard(boardId)
            .orElseThrow(() -> new BoardException("해당 boardId에 해당하는 board가 없습니다."));

        validateSameMember(userId, board.getUserId());

        Board modifyBoard = Board.builder()
            .boardId(boardId)
            .userId(userId)
            .subject(boardModifyRequest.getSubject())
            .content(boardModifyRequest.getContent())
            .build();

        boardMapper.updateBoard(modifyBoard);
    }

    private void validateSameMember(final String userId, final String boardUserId) {
        if (!userId.equals(boardUserId)) {
            throw new UserNotFoundException("해당 유저가 아닙니다.");
        }
    }

    @Override
    @Transactional
    public void delete(final Long boardId, final String userId) {
        User user = userMapper.selectByUserId(userId)
            .orElseThrow(() -> new BoardException("해당 유저가 없습니다."));
        Board board = boardMapper.selectBoard(boardId)
            .orElseThrow(() -> new BoardException("해당 boardId에 해당하는 Board가 없습니다."));

        validateSameMember(user.getUserId(), board.getUserId());
        // TODO : COMMENT -> 삭제를 BOARD 삭제할때로 바꿈
        commentMapper.deleteAll(boardId);
        boardMapper.deleteBoard(boardId);
    }

    @Override
    @Transactional
    public void updateHit(final Long boardId) {
        boardMapper.updateHit(boardId);
    }

}
