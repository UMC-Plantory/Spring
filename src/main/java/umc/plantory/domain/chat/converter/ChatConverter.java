package umc.plantory.domain.chat.converter;

import org.springframework.ai.chat.messages.MessageType;
import umc.plantory.domain.chat.dto.ChatResponseDTO;
import umc.plantory.domain.chat.entity.Chat;
import umc.plantory.domain.member.entity.Member;

import java.time.LocalDateTime;

public class ChatConverter {

    public static Chat toChat(String message, Member member, Boolean isMember, LocalDateTime now, MessageType type) {
        return Chat.builder()
                .member(member)
                .content(message)
                .isMember(isMember)
                .createdAt(now)
                .messageType(type)
                .build();
    }

    public static ChatResponseDTO toChatResponseDTO(Chat chat) {
        return ChatResponseDTO.builder()
                .content(chat.getContent())
                .createdAt(chat.getCreatedAt())
                .isMember(chat.getIsMember())
                .build();
    }

    public static ChatResponseDTO toChatResponseDTO(String response, LocalDateTime time, Boolean isMember) {
        return ChatResponseDTO.builder()
                .content(response)
                .createdAt(time)
                .isMember(isMember)
                .build();
    }
}
