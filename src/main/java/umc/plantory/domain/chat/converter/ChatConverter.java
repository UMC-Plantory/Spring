package umc.plantory.domain.chat.converter;

import org.springframework.ai.chat.messages.MessageType;
import umc.plantory.domain.chat.entity.Chat;
import umc.plantory.domain.member.entity.Member;

public class ChatConverter {

    public static Chat toChat(String message, Member member, Boolean isMember, MessageType type) {
        return Chat.builder()
                .member(member)
                .content(message)
                .isMember(isMember)
                .messageType(type)
                .build();
    }

}
