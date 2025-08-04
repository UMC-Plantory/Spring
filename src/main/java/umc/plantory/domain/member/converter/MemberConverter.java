package umc.plantory.domain.member.converter;

import umc.plantory.domain.diary.dto.DiaryResponseDTO;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.flower.entity.Flower;
import umc.plantory.domain.member.dto.MemberDataDTO;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.global.enums.Emotion;
import umc.plantory.global.enums.Gender;
import umc.plantory.global.enums.MemberRole;
import umc.plantory.global.enums.MemberStatus;
import umc.plantory.global.enums.Provider;
import umc.plantory.domain.member.mapping.MemberTerm;
import umc.plantory.domain.term.entity.Term;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

public class MemberConverter {
    private static final String DEFAULT_PROFILE_IMG_URL = "https://plantory.s3.ap-northeast-2.amazonaws.com/profile/plantory_default_img.png";
    private static final String DEFAULT_NICKNAME = "토리";
    private static final String DEFAULT_USER_CUSTOM_ID = "temp_plantory";
    
    // 물 진행도 계산을 위한 상수
    private static final int MAX_WATERING_STEPS = 6;
    private static final int MAX_PROGRESS_PERCENTAGE = 100;

    public static MemberResponseDTO.TermAgreementResponse toTermAgreementResponse(Member member) {
        return MemberResponseDTO.TermAgreementResponse.builder()
                .memberId(member.getId())
                .message("약관 동의가 완료되었습니다.")
                .build();
    }

    public static MemberResponseDTO.MemberSignupResponse toMemberSignupResponse(Member member) {
        return MemberResponseDTO.MemberSignupResponse.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .userCustomId(member.getUserCustomId())
                .profileImgUrl(member.getProfileImgUrl() != null ? member.getProfileImgUrl() : DEFAULT_PROFILE_IMG_URL)
                .build();
    }

    public static MemberResponseDTO.ProfileResponse toProfileResponse(Member member) {
        return MemberResponseDTO.ProfileResponse.builder()
                .userCustomId(member.getUserCustomId())
                .nickname(member.getNickname())
                .profileImgUrl(member.getProfileImgUrl() != null ? member.getProfileImgUrl() : DEFAULT_PROFILE_IMG_URL)
                .continuousRecordCnt(member.getContinuousRecordCnt())
                .totalRecordCnt(member.getTotalRecordCnt())
                .avgSleepTime(member.getAvgSleepTime()) // 분단위 수면 시간
                .totalBloomCnt(member.getTotalBloomCnt())
                .build();
    }

    public static MemberResponseDTO.ProfileUpdateResponse toProfileUpdateResponse(Member member) {
        return MemberResponseDTO.ProfileUpdateResponse.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .userCustomId(member.getUserCustomId())
                .gender(member.getGender() != null ? member.getGender().name().toLowerCase() : null)
                .birth(member.getBirth() != null ? member.getBirth().toString() : null)
                .profileImgUrl(member.getProfileImgUrl() != null ? member.getProfileImgUrl() : DEFAULT_PROFILE_IMG_URL)
                .message("프로필 수정이 완료되었습니다.")
                .build();
    }

    public static MemberTerm toMemberTerm(Member member, Term term, Boolean isAgree) {
        return MemberTerm.builder()
                .member(member)
                .term(term)
                .isAgree(isAgree)
                .build();
    }

    public static Member toMember (MemberDataDTO.KakaoMemberData kakaoMemberData) {
        return Member.builder()
                .email(kakaoMemberData.getEmail())
                .nickname(DEFAULT_NICKNAME)
                .userCustomId(DEFAULT_USER_CUSTOM_ID)
                .profileImgUrl(DEFAULT_PROFILE_IMG_URL)
                .provider(Provider.KAKAO)
                .providerId(kakaoMemberData.getSub())
                .gender(Gender.NONE) // 다시 추가
                .status(MemberStatus.PENDING)
                .role(MemberRole.USER)
                .build();
    }

    // 월별 일기 데이터 변환 (일기 존재 날짜만)
    public static List<MemberResponseDTO.HomeResponse.DiaryDate> toDiaryDateList(List<Diary> diaries) {
        return diaries.stream()
                .map(diary -> MemberResponseDTO.HomeResponse.DiaryDate.builder()
                        .date(diary.getDiaryDate())
                        .emotion(diary.getEmotion())
                        .build())
                .collect(Collectors.toList());
    }

    // 물 진행도 계산 (상수 사용으로 성능 최적화)
    public static Integer calculateWateringProgress(Integer wateringCount) {
        if (wateringCount == null || wateringCount <= 0) {
            return 0;
        }
        return Math.min((wateringCount * MAX_PROGRESS_PERCENTAGE) / MAX_WATERING_STEPS, MAX_PROGRESS_PERCENTAGE);
    }

    public static MemberResponseDTO.HomeResponse toHomeResponse(Member member, YearMonth yearMonth, List<MemberResponseDTO.HomeResponse.DiaryDate> diaryDates, Integer continuousRecordCnt, Integer wateringCount) {
        // 물 진행도 계산 (상수 사용)
        Integer wateringProgress = calculateWateringProgress(wateringCount);
        
        return MemberResponseDTO.HomeResponse.builder()
                .yearMonth(yearMonth.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")))
                .wateringProgress(wateringProgress)
                .continuousRecordCnt(continuousRecordCnt)
                .diaryDates(diaryDates)
                .build();
    }
}

