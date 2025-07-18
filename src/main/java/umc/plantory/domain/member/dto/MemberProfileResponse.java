package umc.plantory.domain.member.dto;

import lombok.Getter;
import umc.plantory.domain.member.entity.Member;

@Getter
public class MemberProfileResponse {

    private final Long id;
    private final String nickname;
    private final String email;
    private final String profileImgUrl;

    public MemberProfileResponse(Member member) {
        this.id = member.getId();
        this.nickname = member.getNickname();
        this.email = member.getEmail();
        this.profileImgUrl = member.getProfileImgUrl();
    }
}