package umc.plantory.domain.chat.converter;

import org.springframework.ai.chat.messages.MessageType;
import umc.plantory.domain.chat.dto.ChatResponseDTO;
import umc.plantory.domain.chat.entity.Chat;
import umc.plantory.domain.member.entity.Member;

import java.time.LocalDateTime;
import java.util.List;

public class ChatConverter {

    public static Chat toChat(String message, Member member, Boolean isMember, MessageType type) {
        return Chat.builder()
                .member(member)
                .content(message)
                .isMember(isMember)
                .messageType(type)
                .build();
    }

    public static ChatResponseDTO.ChatsDetatil toChatsDetail(Chat chat) {
        return ChatResponseDTO.ChatsDetatil.builder()
                .content(chat.getContent())
                .createAt(chat.getCreatedAt())
                .isMember(chat.getIsMember())
                .build();
    }

    public static ChatResponseDTO.ChatsResponse toChatsResponse(List<ChatResponseDTO.ChatsDetatil> chatsDetatilList, boolean hasNext, LocalDateTime nextCursor){
        return ChatResponseDTO.ChatsResponse.builder()
                .chatsDetatilList(chatsDetatilList)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .build();
    }

}
