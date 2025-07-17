package umc.plantory.domain.chat.application;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import umc.plantory.domain.chat.adapter.out.repository.ChatJpaRepository;
import umc.plantory.domain.chat.entity.Chat;
import umc.plantory.domain.chat.port.in.ChatCommandUseCase;
import umc.plantory.domain.chat.prompt.PromptFactory;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.global.apiPayload.exception.ChatApiException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatCommandService implements ChatCommandUseCase {
    private final OpenAiChatClient chatClient;
    private final ChatJpaRepository chatJpaRepository;
    private final MemberRepository memberRepository;

    // 챗봇한테 chat 요청
    // 데이터베이스 조회 : 이전 대화 기록 JSON (데이터베이스로 조회) (요청, 응답 각각 5개씩 조회)
    // 데이터베이스 저장 : 현재 대화 요청/응답, 사용자 구분, 시간
    @Override
    public String ask(String message, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다."));
        // 사용자 메시지 저장
        chatJpaRepository.save(Chat.builder()
                .member(member)
                .content(message)
                .isMember(true)
                .build());

        List<Chat> recentChats = chatJpaRepository.findTop10ByMemberOrderByCreatedAtDesc(member);
        String prompt = PromptFactory.buildPrompt(recentChats, message);
        String response;
        try {
            response = chatClient.call(prompt);
        } catch (RestClientException e) {
            String msg = e.getMessage();
            // Spring AI가 에러를 일반적인 RestClientException으로 감싸서 HTTP 상태 코드를 직접 확인 불가능
            if (msg != null && (msg.contains("401") || msg.contains("Unauthorized") || msg.contains("unauthorized"))) {
                throw new ChatApiException(ChatApiException.ErrorType.INVALID_API_KEY, "API 키가 잘못되었습니다.");
            } else if (msg != null && msg.contains("429")) {
                throw new ChatApiException(ChatApiException.ErrorType.QUOTA_EXCEEDED, "API 쿼터가 모두 소진되었습니다.");
            } else if (msg != null && msg.contains("Error while extracting response")) {
                if (msg.contains("OpenAiApi$ChatCompletion")) {
                    throw new ChatApiException(ChatApiException.ErrorType.INVALID_API_KEY, "API 키가 잘못되었습니다.");
                }
            }
            throw e;
        }

        // 챗봇 응답 저장
        chatJpaRepository.save(Chat.builder()
                .member(member)
                .content(response)
                .isMember(false)
                .build());

        return response;
    }
}
