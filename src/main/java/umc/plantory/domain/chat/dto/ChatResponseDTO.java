package umc.plantory.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

public class ChatResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatsResponse {
        private boolean hasNext;
        private LocalDateTime nextCursor;
        List<ChatsDetail> chatsDetatilList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatsDetail{
        @Schema(description = "메시지 내용")
        private String content;
        @Schema(description = "생성 시간", example = "2025-07-20T12:00:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        private LocalDateTime createdAt;
        @Schema(description = "사용자 요청인지 챗봇 응답인지", example = "false")
        private Boolean isMember;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatResponse {
        @Schema(description = "챗봇 답변")
        private String content;

        @Schema(description = "채팅 시간", example = "2025-07-20T12:34")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        private LocalDateTime createdAt;

        @Schema(description = "사용자 요청인지 챗봇 응답인지", example = "false")
        private Boolean isMember;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatIdsResponse {
        @Schema(description = "채팅 아이디", example = "[14, 13, 12]")
        private List<Long> chatIdList;
    }
}
