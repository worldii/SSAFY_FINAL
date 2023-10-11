package com.ssafy.enjoytrip.core.media.service;

import com.ssafy.enjoytrip.core.media.model.FileUrlResponse;
import com.ssafy.enjoytrip.global.error.MediaException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

// TODO : 추후에 이벤트 발행 방식으로 바꿀지 고민 중임.
@Service
@RequiredArgsConstructor
public class MediaService {

    private final FileService fileService;
    private final UploadService uploadService;

    public void insertMedias(
        final Long boardId,
        final List<MultipartFile> imageFiles,
        final String folderName
    ) {
        if (imageFiles.isEmpty()) {
            return;
        }
        final List<String> fileUrls = getFileUrls(imageFiles, folderName);
        
        try {
            fileService.insertFile(boardId, fileUrls);
        } catch (final Exception e) {
            uploadService.deleteMedias(fileUrls);
            throw new MediaException("파일 업로드에 실패했습니다.");
        }
    }

    private List<String> getFileUrls(
        final List<MultipartFile> imageFiles,
        final String folderName
    ) {
        return uploadService.uploadMedias(imageFiles, folderName)
            .stream()
            .map(FileUrlResponse::getUrl)
            .collect(Collectors.toList());
    }
}
