package umc.plantory.domain.chat.service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import umc.plantory.domain.chat.repository.ChatJpaRepository;
import umc.plantory.domain.chat.entity.Chat;
import umc.plantory.domain.chat.prompt.PromptFactory;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.chat.exception.ChatApiException;

import java.util.List;

/**
 * 챗봇 대화 처리 서비스. 사용자 메시지 저장, 프롬프트 생성, 챗봇 응답 저장을 담당합니다.
 */
@Service
@RequiredArgsConstructor
public class ChatCommandService implements ChatCommandUseCase {
    private final OpenAiChatClient chatClient;
    private final ChatJpaRepository chatJpaRepository;
    private final MemberRepository memberRepository;

    /**
     * 사용자의 메시지와 memberId를 받아 챗봇 응답을 반환합니다.
     * @param message 사용자 메시지
     * @param memberId 회원 ID
     * @return 챗봇 응답
     */
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
            // Spring AI가 에러를 일반적인 RestClientException으로 감싸서 HTTP 상태 코드를 직접 확인 불가능하다.
            // 따라서 msg 매칭으로 예외 처리
            if (msg != null && (msg.contains("401") || msg.contains("Unauthorized") || msg.contains("unauthorized"))) {
                throw new ChatApiException(ChatApiException.ErrorType.INVALID_API_KEY);
            } else if (msg != null && msg.contains("429")) {
                throw new ChatApiException(ChatApiException.ErrorType.QUOTA_EXCEEDED);
            } else if (msg != null && msg.contains("Error while extracting response")) {
                if (msg.contains("OpenAiApi$ChatCompletion")) {
                    throw new ChatApiException(ChatApiException.ErrorType.INVALID_API_KEY);
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
