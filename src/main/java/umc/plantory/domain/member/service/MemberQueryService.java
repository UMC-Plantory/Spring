package umc.plantory.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.diary.repository.DiaryRepository;
import umc.plantory.domain.member.converter.MemberConverter;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.terrarium.entity.Terrarium;
import umc.plantory.domain.terrarium.repository.TerrariumRepository;
import umc.plantory.domain.token.provider.JwtProvider;
import umc.plantory.domain.wateringCan.repository.WateringEventRepository;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.MemberHandler;
import umc.plantory.global.enums.DiaryStatus;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MemberQueryService implements MemberQueryUseCase {
    private final MemberRepository memberRepository;
    private final DiaryRepository diaryRepository;
    private final TerrariumRepository terrariumRepository;
    private final WateringEventRepository wateringEventRepository;
    private final JwtProvider jwtProvider;

    @Override
    public MemberResponseDTO.ProfileResponse getProfile(String authorization) {
        // JWT 토큰 검증 및 멤버 ID 추출
        Long memberId = jwtProvider.getMemberIdAndValidateToken(authorization);

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        
        return MemberConverter.toProfileResponse(member);
    }

    @Override
    public MemberResponseDTO.HomeResponse getHome(String authorization, YearMonth yearMonth) {
        // JWT 토큰 검증 및 멤버 ID 추출
        Long memberId = jwtProvider.getMemberIdAndValidateToken(authorization);

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 해당 월의 일기 데이터 조회 (diaryDate 기준)
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        List<Diary> monthlyDiaries = diaryRepository.findByMemberAndStatusInAndDiaryDateBetween(
                member, List.of(DiaryStatus.NORMAL, DiaryStatus.SCRAP), startDate, endDate);

        // 일기 존재 날짜만 변환
        List<MemberResponseDTO.HomeResponse.DiaryDate> diaryDateList = MemberConverter.toDiaryDateList(monthlyDiaries);

        // 연속 기록 횟수 가져오기
        Integer continuousRecordCnt = member.getContinuousRecordCnt();
        
        // 실제 물 준 횟수 계산
        Integer wateringCount = calculateWateringCount(member);

        return MemberConverter.toHomeResponse(member, yearMonth, diaryDateList, continuousRecordCnt, wateringCount);
    }

    /**
     * 실제 물 준 횟수를 계산
     * isBloom = false인 테라리움의 WateringEvent 테이블에서 물 준 횟수 계산
     */
    private Integer calculateWateringCount(Member member) {
        // isBloom = false인 테라리움 조회
        Optional<Terrarium> terrariumOpt = terrariumRepository.findByMemberAndIsBloomFalse(member);
        
        if (terrariumOpt.isPresent()) {
            Terrarium terrarium = terrariumOpt.get();
            return wateringEventRepository.countByTerrarium(terrarium);
        }
        
        return 0;
    }
} 