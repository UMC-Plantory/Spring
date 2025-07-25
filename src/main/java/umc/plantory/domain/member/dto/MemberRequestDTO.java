package umc.plantory.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.plantory.global.enums.Gender;

import java.time.LocalDate;
import java.util.List;

public class MemberRequestDTO {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberSignupRequest {
        private Long memberId;

        private String nickname;

        private String userCustomId;

        private Gender gender;

        private LocalDate birth;

        private List<Long> agreeTermIdList;

        private List<Long> disagreeTermIdList;
    }
}
