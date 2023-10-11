package com.ssafy.enjoytrip.core.hotplace.service;

import com.ssafy.enjoytrip.core.hotplace.model.dao.HotPlaceArticleRepository;
import com.ssafy.enjoytrip.core.hotplace.model.dao.HotPlaceRepository;
import com.ssafy.enjoytrip.core.hotplace.model.dao.HotPlaceTagRepository;
import com.ssafy.enjoytrip.core.hotplace.model.dto.request.HotPlaceArticleSaveRequest;
import com.ssafy.enjoytrip.core.hotplace.model.dto.request.HotPlaceSaveRequest;
import com.ssafy.enjoytrip.core.hotplace.model.dto.request.HotPlaceVoteRequest;
import com.ssafy.enjoytrip.core.hotplace.model.dto.response.HotPlaceArticleResponse;
import com.ssafy.enjoytrip.core.hotplace.model.dto.response.HotPlaceDetailResponse;
import com.ssafy.enjoytrip.core.hotplace.model.dto.response.HotPlaceResponse;
import com.ssafy.enjoytrip.core.hotplace.model.entity.HotPlace;
import com.ssafy.enjoytrip.core.hotplace.model.entity.HotPlaceArticle;
import com.ssafy.enjoytrip.core.hotplace.model.entity.HotPlaceTag;
import com.ssafy.enjoytrip.core.user.dao.UserRepository;
import com.ssafy.enjoytrip.core.user.model.entity.User;
import com.ssafy.enjoytrip.global.error.HotPlaceException;
import com.ssafy.enjoytrip.global.error.PageInfoRequest;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class HotPlaceServiceImpl implements HotPlaceService {

    private final HotPlaceRepository hotPlaceRepository;
    private final HotPlaceArticleRepository hotPlaceArticleRepository;
    private final HotPlaceTagRepository hotPlaceTagRepository;
    private final UserRepository userRepository;

    @Override
    public String insertHotPlace(final HotPlaceSaveRequest request) {
        final HotPlace hotPlace = HotPlace.builder()
            .hotPlaceId(request.getHotPlaceId())
            .hotPlaceName(request.getHotPlaceName())
            .placeUrl(request.getPlaceUrl())
            .x(request.getX())
            .y(request.getY())
            .addressName(request.getAddressName())
            .roadAddressName(request.getRoadAddressName())
            .build();

        hotPlaceRepository.insertHotPlace(hotPlace);

        return hotPlace.getHotPlaceId();
    }

    @Override
    public Long insertHotPlaceArticle(final String hotPlaceId,
        final HotPlaceArticleSaveRequest request, final String userId) {
        final HotPlace hotPlace = findHotPlaceByHotPlaceId(hotPlaceId);
        final User user = findUserByUserId(userId);

        final HotPlaceArticle hotPlaceArticle = HotPlaceArticle.builder()
            .hotPlaceId(hotPlace.getHotPlaceId())
            .hotPlaceName(request.getHotPlaceName())
            .content(request.getContent())
            .imageUrl(request.getImageUrl())
            .userId(user.getUserId())
            .build();

        hotPlaceArticleRepository.insertHotPlaceArticle(hotPlaceArticle);
        if (request.getTagName() != null) {
            insertHotPlaceTags(hotPlace.getHotPlaceId(), request.getTagName());
        }
        return hotPlaceArticle.getHotPlaceArticleId();
    }

    private HotPlace findHotPlaceByHotPlaceId(final String hotPlaceId) {
        return hotPlaceRepository.selectHotPlaceByHotPlaceId(hotPlaceId)
            .orElseThrow(() -> new HotPlaceException("존재하지 않는 핫플레이스입니다."));
    }

    @Override
    public List<HotPlaceResponse> selectAllHotPlace(
        final PageInfoRequest pageInfoRequest,
        final String keyword
    ) {
        final List<HotPlace> hotPlaces = hotPlaceRepository.selectAllHotPlace(keyword);

        return hotPlaces.stream().map(HotPlaceResponse::from).collect(Collectors.toList());
    }

    @Override
    public HotPlaceDetailResponse selectAllByHotPlaceId(final String hotPlaceId) {
        HotPlace hotPlace = hotPlaceRepository.selectAllByHotPlaceId(hotPlaceId)
            .orElseThrow(() -> new HotPlaceException("존재하지 않는 핫플레이스입니다."));

        return HotPlaceDetailResponse.from(hotPlace);
    }

    @Override
    public HotPlaceArticleResponse selectHotPlaceArticleByArticleId(final String hotPlaceId,
        final Long articleId) {
        final HotPlace hotPlace = findHotPlaceByHotPlaceId(hotPlaceId);
        final HotPlaceArticle hotPlaceArticle = findHotPlaceArticleById(articleId);

        validateHotPlaceArticle(hotPlace.getHotPlaceId(), hotPlaceArticle.getHotPlaceId());

        return HotPlaceArticleResponse.from(hotPlaceArticle);
    }

    @Override
    public void updateVoteCount(final String hotPlaceId, final HotPlaceVoteRequest voteRequest) {
        final HotPlace hotPlace = findHotPlaceByHotPlaceId(hotPlaceId);

        hotPlace.updateVoteCount(voteRequest.getVoteCount());

        hotPlaceRepository.updateHotPlace(hotPlace);
    }

    private void insertHotPlaceTags(final String hotPlaceId, final List<String> tagName) {
        final List<HotPlaceTag> hotPlaceTags = tagName.stream()
            .map(name -> HotPlaceTag.builder().hotPlaceId(hotPlaceId).tagName(name).build())
            .collect(Collectors.toList());

        hotPlaceTagRepository.insertTags(hotPlaceTags);
    }

    private User findUserByUserId(final String userId) {
        return userRepository.selectByUserId(userId)
            .orElseThrow(() -> new HotPlaceException("존재하지 않는 유저입니다."));
    }

    private void validateHotPlaceArticle(final String hotPlaceId,
        final String hotPlaceArticlePlaceId) {
        if (!hotPlaceId.equals(hotPlaceArticlePlaceId)) {
            throw new HotPlaceException("핫플레이스 게시글이 아닙니다.");
        }
    }

    private HotPlaceArticle findHotPlaceArticleById(final Long articleId) {
        return hotPlaceArticleRepository.selectHotPlaceArticleByArticleId(articleId)
            .orElseThrow(() -> new HotPlaceException("존재하지 않는 핫플레이스 게시글입니다."));
    }
}
