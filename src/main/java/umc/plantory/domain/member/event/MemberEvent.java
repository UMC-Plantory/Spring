package umc.plantory.domain.member.event;

import lombok.Getter;
import umc.plantory.domain.member.entity.Member;

// 데모데이용 - 삭제 예정
@Getter
public class MemberEvent {
    private final Long memberId;

    public MemberEvent(Member member) {
        this.memberId = member.getId();
    }
}
