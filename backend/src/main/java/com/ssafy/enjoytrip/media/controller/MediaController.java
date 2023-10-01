package com.ssafy.enjoytrip.media.controller;

import com.ssafy.enjoytrip.global.auth.model.dto.NoAuth;
import com.ssafy.enjoytrip.media.model.dto.FileInfoResponse;
import com.ssafy.enjoytrip.media.service.FileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {

    private final FileService fileService;

    @NoAuth
    @GetMapping("/{boardId}")
    public ResponseEntity<List<FileInfoResponse>> getFileInfo(
        @PathVariable final Long boardId
    ) {
        return ResponseEntity.ok(fileService.selectFile(boardId));
    }
}
