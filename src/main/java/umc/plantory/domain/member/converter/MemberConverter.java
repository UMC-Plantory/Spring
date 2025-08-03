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

public class MemberConverter {
    private static final String DEFAULT_PROFILE_IMG_URL = "https://plantory.s3.ap-northeast-2.amazonaws.com/profile/plantory_default_img.png";
    private static final String DEFAULT_NICKNAME = "토리";
    private static final String DEFAULT_USER_CUSTOM_ID = "temp_plantory";

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
                .email(member.getEmail())
                .gender(member.getGender() != null ? member.getGender().name().toLowerCase() : null)
                .birth(member.getBirth() != null ? member.getBirth().toString() : null)
                .profileImgUrl(member.getProfileImgUrl() != null ? member.getProfileImgUrl() : DEFAULT_PROFILE_IMG_URL)
                .wateringCanCnt(member.getWateringCanCnt())
                .continuousRecordCnt(member.getContinuousRecordCnt())
                .totalRecordCnt(member.getTotalRecordCnt())
                .avgSleepTime(member.getAvgSleepTime()) // 분단위 수면 시간
                .totalBloomCnt(member.getTotalBloomCnt())
                .status(member.getStatus() != null ? member.getStatus().name() : null)
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

    public static MemberResponseDTO.HomeResponse toHomeResponse(Member member, Flower flower, YearMonth yearMonth, 
        List<MemberResponseDTO.HomeResponse.MonthlyDiary> monthlyDiaries) {
        // 테라리움 식물 정보 계산
        Integer wateringProgress = 0;
        
        if (flower != null) {
            // 성장 단계 계산 (연속 기록 수에 따라)
            if (member.getContinuousRecordCnt() >= 30) {
                wateringProgress = 100;
            } else if (member.getContinuousRecordCnt() >= 7) {
                wateringProgress = 70;
            } else {
                wateringProgress = 30;
            }
        }
        
        return MemberResponseDTO.HomeResponse.builder()
                .yearMonth(yearMonth.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")))
                .wateringCount(member.getWateringCanCnt() != null ? member.getWateringCanCnt() : 0)
                .wateringProgress(wateringProgress)
                .continuousRecordCnt(member.getContinuousRecordCnt())
                .monthlyDiaries(monthlyDiaries)
                .build();
    }

    public static MemberResponseDTO.DailyDiaryResponse toDailyDiaryResponse(DiaryResponseDTO.DiarySimpleInfoDTO diaryInfo) {
        return MemberResponseDTO.DailyDiaryResponse.builder()
                .diaryId(diaryInfo.getDiaryId())
                .title(diaryInfo.getTitle())
                .emotion(diaryInfo.getEmotion())
                .build();
    }
}

