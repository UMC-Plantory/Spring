package umc.plantory.domain.terrarium.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.domain.terrarium.converter.TerrariumConverter;
import umc.plantory.domain.terrarium.entity.Terrarium;
import umc.plantory.domain.terrarium.repository.TerrariumJpaRepository;
import umc.plantory.domain.token.provider.JwtProvider;
import umc.plantory.domain.wateringCan.repository.WateringEventJpaRepository;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.MemberHandler;
import umc.plantory.global.apiPayload.exception.handler.TerrariumHandler;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TerrariumQueryService implements TerrariumQueryUseCase {

    private final TerrariumJpaRepository terrariumJpaRepository;
    private final MemberRepository memberRepository;
    private final WateringEventJpaRepository wateringEventJpaRepository;
    private final JwtProvider jwtProvider;

    /**
     * 회원이 현재 키우고 있는(아직 개화하지 않은) 테라리움의 상세 데이터를 조회합니다.
     * @param authorization 인증용 JWT 토큰
     * @return 현재 키우고 있는 테라리움의 상세 정보 DTO
     * @throws TerrariumHandler 해당 회원의 테라리움이 없거나, 데이터 조회에 실패한 경우
     */
    @Override
    public TerrariumResponseDto.TerrariumResponse findCurrentTerrariumData(String authorization) {
        // Authorization 헤더에서 토큰 추출
        String token = jwtProvider.resolveToken(authorization);
        if (token == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        // JWT 토큰 검증 및 멤버 ID 추출
        jwtProvider.validateToken(token);
        Long memberId = jwtProvider.getMemberId(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Terrarium currentTerrarium = terrariumJpaRepository.findByMemberIdAndIsBloomFalse(memberId);
        if (currentTerrarium == null) {
            throw new TerrariumHandler(ErrorStatus.MEMBER_HAS_NO_TERRARIUM);
        }
        if (currentTerrarium.getFlower() == null) {
            throw new TerrariumHandler(ErrorStatus.FLOWER_NOT_FOUND_IN_TERRARIUM);
        }

        String flowerImgUrl = currentTerrarium.getFlower().getFlowerImgUrl();
        if (flowerImgUrl == null) {
            throw new TerrariumHandler(ErrorStatus.FLOWER_IMG_NOT_FOUND_IN_TERRARIUM);
        }

        Integer wateringCanCnt = member.getWateringCanCnt();
        int wateringEventCnt = wateringEventJpaRepository.countByTerrariumId(currentTerrarium.getId());

        return TerrariumConverter.toTerrariumResponse(
                currentTerrarium.getId(),
                flowerImgUrl,
                wateringCanCnt,
                wateringEventCnt
        );
    }

    /**
     * 회원 ID와 연도, 월을 기준으로 개화가 완료된 테라리움 목록을 조회합니다.
     *
     * @param authorization 인증용 JWT 토큰
     * @param year 조회할 연도 (예: 2025)
     * @param month 조회할 월 (1~12)
     * @return 지정한 회원이 해당 연도와 월에 개화가 완료된 테라리움 정보를 담은 CompletedTerrariumResponse 리스트
     */
    @Override
    public List<TerrariumResponseDto.CompletedTerrariumResponse> findCompletedTerrariumsByMonth(
            String authorization,
            int year,
            int month) {
        // Authorization 헤더에서 토큰 추출
        String token = jwtProvider.resolveToken(authorization);
        if (token == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        // JWT 토큰 검증 및 멤버 ID 추출
        jwtProvider.validateToken(token);
        Long memberId = jwtProvider.getMemberId(token);

        return terrariumJpaRepository.findAllByMemberIdAndIsBloomTrueAndBloomAtYearAndMonth(memberId, year, month)
                .stream()
                .map(terrarium -> new TerrariumResponseDto.CompletedTerrariumResponse(
                        terrarium.getId(),
                        terrarium.getBloomAt(),
                        terrarium.getFlower().getFlowerImgUrl(),
                        terrarium.getFlower().getName()
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 완료된 테라리움 상세 정보를 조회합니다.
     *
     * @param terrariumId 조회할 테라리움 ID
     * @return 해당 테라리움 상세 DTO (감정 집계 정보는 포함하지 않음)
     */
    @Override
    public TerrariumResponseDto.CompletedTerrariumDetatilResponse findCompletedTerrariumDetail(Long terrariumId) {

        Terrarium terrarium = terrariumJpaRepository.findById(terrariumId)
                .orElseThrow(() -> new TerrariumHandler(ErrorStatus.TERRARIUM_NOT_FOUND));

        if (!terrarium.getIsBloom()) {
            throw new TerrariumHandler(ErrorStatus.TERRARIUM_NOT_BLOOMED);
        }

        List<LocalDate> usedDiaries = wateringEventJpaRepository.findWateringCanListByTerrariumId(terrariumId).stream()
                .map(wateringCan -> wateringCan.getDiaryDate())
                .collect(Collectors.toList());


        return TerrariumResponseDto.CompletedTerrariumDetatilResponse.builder()
                .startAt(terrarium.getStartAt())
                .bloomAt(terrarium.getBloomAt())
                .firstStepDate(terrarium.getFirstStepDate())
                .secondStepDate(terrarium.getSecondStepDate())
                .thirdStepDate(terrarium.getThirdStepDate())
                .usedDiaries(usedDiaries)
                .build();
    }
}
