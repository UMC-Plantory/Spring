package umc.plantory.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.diary.dto.DiaryResponseDTO;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.diary.repository.DiaryRepository;
import umc.plantory.domain.flower.entity.Flower;
import umc.plantory.domain.member.converter.MemberConverter;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.terrarium.entity.Terrarium;
import umc.plantory.domain.terrarium.repository.TerrariumRepository;
import umc.plantory.domain.token.provider.JwtProvider;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.MemberHandler;
import umc.plantory.global.enums.DiaryStatus;

import java.time.LocalDate;
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
    public MemberResponseDTO.HomeResponse getHome(String authorization, LocalDate selectedDate) {
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

        // 캘린더 감정 데이터 조회 (현재 월의 모든 일기)
        LocalDate currentDate = LocalDate.now();
        List<Diary> monthlyDiaries = diaryRepository.findByMemberAndYearAndMonthAndStatus(
                member, currentDate.getYear(), currentDate.getMonthValue(), DiaryStatus.NORMAL);

        List<MemberResponseDTO.HomeResponse.CalendarEmotion> calendarEmotions = new ArrayList<>();
        for (Diary diary : monthlyDiaries) {
            calendarEmotions.add(MemberResponseDTO.HomeResponse.CalendarEmotion.builder()
                    .date(diary.getDiaryDate())
                    .diaryId(diary.getId())
                    .isExist(true)
                    .emotion(diary.getEmotion())
                    .build());
        }

        // selectedDate가 null이면 플랜토리 정보만 반환
        if (selectedDate == null) {
            return MemberConverter.toHomeResponse(member, (Diary) null, flower, calendarEmotions, null);
        }

        // 선택된 날짜의 일기 조회
        Diary selectedDiary = null;
        
        // DiaryRepository에서 직접 조회하여 예외 방지
        Optional<Diary> diaryOpt = diaryRepository.findByMemberIdAndDiaryDateAndStatusIn(
                member.getId(), selectedDate, List.of(DiaryStatus.NORMAL, DiaryStatus.SCRAP));
        
        if (diaryOpt.isPresent()) {
            selectedDiary = diaryOpt.get();
        }
        
        return MemberConverter.toHomeResponse(member, selectedDiary, flower, calendarEmotions, selectedDate);
    }
} 