package umc.plantory.domain.chat.converter;

import umc.plantory.domain.chat.entity.Chat;
import umc.plantory.domain.member.entity.Member;

public class ChatConverter {

    public static Chat toChat(String message, Member member, Boolean isMemberFlag) {
        return Chat.builder()
                .member(member)
                .content(message)
                .isMember(isMemberFlag)
                .build();
    }
}
