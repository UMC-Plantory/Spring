package umc.plantory.domain.chat.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import umc.plantory.domain.chat.dto.ChatRequestDTO;
import umc.plantory.domain.chat.converter.ChatConverter;
import umc.plantory.domain.chat.repository.ChatJpaRepository;
import umc.plantory.domain.chat.prompt.PromptFactory;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.token.provider.JwtProvider;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.ChatHandler;
import umc.plantory.global.apiPayload.exception.handler.MemberHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 챗봇 대화 처리 서비스. 사용자 메시지 저장, 프롬프트 생성, 챗봇 응답 저장을 담당합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatCommandService implements ChatCommandUseCase {
    private final OpenAiChatModel openAiChatModel;
    private final ChatMemory chatMemory;
    private final ChatJpaRepository chatJpaRepository;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    @Override
    public String ask(String authorization, ChatRequestDTO.ChatMessageDTO request) {
        Member member = getLoginedMember(authorization);

        // 메모리에서 사용자 대화 이력 불러오기
        List<Message> chatHistory = new ArrayList<>(chatMemory.get(String.valueOf(member.getId())));
        // 사용자가 입력한 메시지 추가
        chatHistory.add(new UserMessage(request.getContent()));
        // 프롬프트 생성
        Prompt prompt = PromptFactory.buildPrompt(chatHistory);
        // AI 기반 응답 생성
        String response = getResponse(prompt);

        // 사용자 메시지 및 AI 응답 메모리에 저장
        chatMemory.add(String.valueOf(member.getId()), new AssistantMessage(request.getContent()));
        chatMemory.add(String.valueOf(member.getId()), new AssistantMessage(response));
        // 사용자 메시지 및 AI 응답 DB에 저장
        chatJpaRepository.save(ChatConverter.toChat(request.getContent(), member, true, MessageType.USER));
        chatJpaRepository.save(ChatConverter.toChat(response, member, false, MessageType.ASSISTANT));

        return response;
    }

    // API 호출
    private String getResponse(Prompt prompt) {
        ChatClient chatClient = ChatClient.create(openAiChatModel);
        String response = "";

        try {
            response = chatClient.prompt(prompt).call().content();
        } catch (RestClientException e) {
            String msg = e.getMessage();
            if (msg != null && (msg.contains("401") || msg.contains("Unauthorized") || msg.contains("unauthorized"))) {
                throw new ChatHandler(ErrorStatus.INVALID_API_KEY);
            } else if (msg != null && msg.contains("429")) {
                throw new ChatHandler(ErrorStatus.QUOTA_EXCEEDED);
            } else if (msg != null && msg.contains("Error while extracting response")) {
                if (msg.contains("OpenAiApi$ChatCompletion")) {
                    throw new ChatHandler(ErrorStatus.INVALID_API_KEY);
                }
            }
        }

        return response;
    }

    // 로그인한 사용자 정보 받아오기
    private Member getLoginedMember(String authorization) {
        String token = jwtProvider.resolveToken(authorization);
        if (token == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }
        jwtProvider.validateToken(token);

        return memberRepository.findById(jwtProvider.getMemberId(token))
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

}
