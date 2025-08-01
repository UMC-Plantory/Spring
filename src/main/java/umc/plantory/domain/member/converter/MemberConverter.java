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

    public static MemberResponseDTO.HomeResponse toHomeResponse(Member member, Diary selectedDiary, Flower flower, 
                                                              List<MemberResponseDTO.HomeResponse.CalendarEmotion> calendarEmotions, 
                                                              LocalDate selectedDate) {
        return toHomeResponseInternal(member, selectedDiary, null, flower, calendarEmotions, selectedDate);
    }

    public static MemberResponseDTO.HomeResponse toHomeResponse(Member member, DiaryResponseDTO.DiarySimpleInfoDTO diarySimpleInfo, Flower flower, 
                                                              List<MemberResponseDTO.HomeResponse.CalendarEmotion> calendarEmotions, 
                                                              LocalDate selectedDate) {
        return toHomeResponseInternal(member, null, diarySimpleInfo, flower, calendarEmotions, selectedDate);
    }

    private static MemberResponseDTO.HomeResponse toHomeResponseInternal(Member member, Diary selectedDiary, 
                                                                       DiaryResponseDTO.DiarySimpleInfoDTO diarySimpleInfo, 
                                                                       Flower flower, 
                                                                       List<MemberResponseDTO.HomeResponse.CalendarEmotion> calendarEmotions, 
                                                                       LocalDate selectedDate) {
        // 현재 날짜 기준으로 연도와 월 설정
        LocalDate currentDate = LocalDate.now();
        
        // 플랜토리 정보 계산
        String flowerName = null;
        String flowerStage = null;
        Integer growthRate = 0;
        
        if (flower != null) {
            flowerName = flower.getName();
            // 성장 단계 계산 (연속 기록 수에 따라)
            if (member.getContinuousRecordCnt() >= 30) {
                flowerStage = "꽃나무";
                growthRate = 100;
            } else if (member.getContinuousRecordCnt() >= 7) {
                flowerStage = "잎새";
                growthRate = 70;
            } else {
                flowerStage = "새싹";
                growthRate = 30;
            }
        }
        
        // 선택된 날짜의 일기 정보
        Long diaryId = null;
        LocalDate date = null;
        Emotion emotion = null;
        String title = null;
        Boolean isExist = false;
        
        if (selectedDiary != null) {
            diaryId = selectedDiary.getId();
            date = selectedDiary.getDiaryDate();
            emotion = selectedDiary.getEmotion();
            title = selectedDiary.getTitle();
            isExist = true;
        } else if (diarySimpleInfo != null) {
            diaryId = diarySimpleInfo.getDiaryId();
            date = diarySimpleInfo.getDiaryDate();
            emotion = diarySimpleInfo.getEmotion();
            title = diarySimpleInfo.getTitle();
            isExist = true;
        } else if (selectedDate != null) {
            date = selectedDate;
            isExist = false;
        }
        
        return MemberResponseDTO.HomeResponse.builder()
                .userCustomId(member.getUserCustomId())
                .diaryId(diaryId)
                .date(date)
                .emotion(emotion)
                .title(title)
                .isExist(isExist)
                .year(currentDate.getYear())
                .month(currentDate.getMonthValue())
                .calendarEmotions(calendarEmotions)
                .flowerName(flowerName)
                .flowerStage(flowerStage)
                .growthRate(growthRate)
                .continuousRecordCnt(member.getContinuousRecordCnt())
                .build();
    }
}
