package umc.plantory.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.diary.dto.DiaryResponseDTO;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.diary.repository.DiaryRepository;
import umc.plantory.domain.diary.service.DiaryQueryUseCase;
import umc.plantory.domain.flower.entity.Flower;
import umc.plantory.domain.member.converter.MemberConverter;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.terrarium.entity.Terrarium;
import umc.plantory.domain.terrarium.repository.TerrariumRepository;
import umc.plantory.domain.token.provider.JwtProvider;
import umc.plantory.domain.wateringCan.repository.WateringCanRepository;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.MemberHandler;
import umc.plantory.global.enums.DiaryStatus;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MemberQueryService implements MemberQueryUseCase {
    private final MemberRepository memberRepository;
    private final DiaryRepository diaryRepository;
    private final TerrariumRepository terrariumRepository;
    private final WateringCanRepository wateringCanRepository;
    private final DiaryQueryUseCase diaryQueryUseCase;
    private final JwtProvider jwtProvider;

    @Override
    public MemberResponseDTO.ProfileResponse getProfile(String authorization) {
        // Authorization 헤더에서 토큰 추출
        String token = jwtProvider.resolveToken(authorization);
        if (token == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        // JWT 토큰 검증 및 멤버 ID 추출
        jwtProvider.validateToken(token);
        Long memberId = jwtProvider.getMemberId(token);

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        
        return MemberConverter.toProfileResponse(member);
    }

    @Override
    public MemberResponseDTO.HomeResponse getHome(String authorization, YearMonth yearMonth) {
        // Authorization 헤더에서 토큰 추출
        String token = jwtProvider.resolveToken(authorization);
        if (token == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        // JWT 토큰 검증 및 멤버 ID 추출
        jwtProvider.validateToken(token);
        Long memberId = jwtProvider.getMemberId(token);

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 테라리움 조회 (꽃 정보)
        Optional<Terrarium> terrariumOpt = terrariumRepository.findByMember(member);
        Flower flower = null;
        if (terrariumOpt.isPresent()) {
            flower = terrariumOpt.get().getFlower();
        }

        // 해당 월의 일기 데이터 조회
        List<Diary> monthlyDiaries = diaryRepository.findByMemberAndYearAndMonthAndStatus(
                member, yearMonth.getYear(), yearMonth.getMonthValue(), DiaryStatus.NORMAL);

        // 월별 일기 데이터 변환
        List<MemberResponseDTO.HomeResponse.MonthlyDiary> monthlyDiaryList = new ArrayList<>();
        for (Diary diary : monthlyDiaries) {
            monthlyDiaryList.add(MemberResponseDTO.HomeResponse.MonthlyDiary.builder()
                    .diaryId(diary.getId())
                    .diaryDate(diary.getDiaryDate())
                    .emotion(diary.getEmotion())
                    .build());
        }

        // 연속 기록 횟수 실시간 계산
        Integer continuousRecordCnt = calculateContinuousRecordCount(member);
        
        // 실제 물 준 횟수 계산
        Integer wateringCount = calculateWateringCount(member);

        return MemberConverter.toHomeResponse(member, flower, yearMonth, monthlyDiaryList, continuousRecordCnt, wateringCount);
    }

    @Override
    public MemberResponseDTO.DailyDiaryResponse getDailyDiary(String authorization, LocalDate date) {
        // Authorization 헤더에서 토큰 추출
        String token = jwtProvider.resolveToken(authorization);
        if (token == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        // JWT 토큰 검증 및 멤버 ID 추출
        jwtProvider.validateToken(token);
        Long memberId = jwtProvider.getMemberId(token);

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 미래 날짜 체크
        if (date.isAfter(LocalDate.now())) {
            throw new MemberHandler(ErrorStatus.INVALID_DATE);
        }

        // DiaryQueryService를 사용하여 특정 날짜의 일기 정보 조회
        DiaryResponseDTO.DiarySimpleInfoDTO diaryInfo = diaryQueryUseCase.getDiarySimpleInfo(authorization, date);
        
        return MemberConverter.toDailyDiaryResponse(diaryInfo);
    }

    /**
     * 연속 기록 횟수를 실시간으로 계산
     * 규칙: 자정 기준으로 연속성 판단
     * 오늘 일기 작성 여부는 내일 자정에 반영됨
     */
    private Integer calculateContinuousRecordCount(Member member) {
        LocalDate today = LocalDate.now();
        
        // 어제까지의 연속 기록을 계산
        int continuousCount = 0;
        LocalDate currentDate = today.minusDays(1); // 어제부터 확인
        
        // 어제부터 과거로 거슬러 올라가면서 연속성 확인
        while (currentDate.isAfter(LocalDate.MIN) && currentDate.isBefore(today)) {
            Boolean hasDiary = diaryRepository.existsByMemberAndDiaryDateAndStatus(member, currentDate, DiaryStatus.NORMAL);
            
            if (hasDiary) {
                continuousCount++;
                currentDate = currentDate.minusDays(1);
            } else {
                // 연속이 끊어지면 종료
                break;
            }
        }
        
        return continuousCount;
    }

    /**
     * 실제 물 준 횟수를 계산
     */
    private Integer calculateWateringCount(Member member) {
        Integer wateringCount = wateringCanRepository.countByMember(member);
        return wateringCount != null ? wateringCount : 0;
    }
} 