package umc.plantory.domain.chat.converter;

import org.springframework.ai.chat.messages.MessageType;
import umc.plantory.domain.chat.dto.ChatResponseDTO;
import umc.plantory.domain.chat.entity.Chat;
import umc.plantory.domain.member.entity.Member;

import java.time.LocalDateTime;
import java.util.List;

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

    // 챗봇 대화 시
    public static ChatResponseDTO.ChatsDetail toChatsDetail(Chat chat) {
        return ChatResponseDTO.ChatsDetail.builder()
                .content(chat.getContent())
                .createdAt(chat.getCreatedAt())
                .isMember(chat.getIsMember())
                .build();
    }

    // 챗봇 대화 이력 조회 시 반환 DTO
    public static ChatResponseDTO.ChatsResponse toChatsResponse(List<ChatResponseDTO.ChatsDetail> chatsDetatilList, boolean hasNext, LocalDateTime nextCursor){
        return ChatResponseDTO.ChatsResponse.builder()
                .chatsDetatilList(chatsDetatilList)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .build();
    }

    public static ChatResponseDTO.ChatResponse toChatResponseDTO(String response, LocalDateTime time, Boolean isMember) {
        return ChatResponseDTO.ChatResponse.builder()
                .content(response)
                .createdAt(time)
                .isMember(isMember)
                .build();
    }

    public static ChatResponseDTO.ChatIdsResponse toChatIdsResponse(List<Long> chatIdList) {
        return ChatResponseDTO.ChatIdsResponse.builder()
                .chatIdList(chatIdList)
                .build();
    }
}
