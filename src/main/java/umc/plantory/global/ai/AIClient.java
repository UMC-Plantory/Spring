package umc.plantory.global.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.ChatHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class AIClient {

    private final OpenAiChatModel openAiChatModel;

    public String getResponse(Prompt prompt) {
        ChatClient chatClient = ChatClient.create(openAiChatModel);

        try {
            return chatClient.prompt(prompt).call().content();
        } catch (RestClientException e) {
            String msg = e.getMessage();
            log.error("에러 내용 {}", msg);

            if (msg == null) {
                throw new ChatHandler(ErrorStatus.OPEN_AI_SERVER_ERROR);
            }

            String lowerMsg = msg.toLowerCase(); // 에러 메시지 소문자로 변경

            if (lowerMsg.contains("401") || lowerMsg.contains("unauthorized")) {
                throw new ChatHandler(ErrorStatus.INVALID_API_KEY);
            }
            if (lowerMsg.contains("429")) {
                throw new ChatHandler(ErrorStatus.QUOTA_EXCEEDED);
            }
            if (lowerMsg.contains("openaiapi$chatcompletion")) {
                throw new ChatHandler(ErrorStatus.INVALID_API_KEY);
            }

            throw new ChatHandler(ErrorStatus.OPEN_AI_SERVER_ERROR);
        }
    }
}
