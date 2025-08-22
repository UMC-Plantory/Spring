package umc.plantory.domain.chat.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.chat.dto.ChatResponseDTO;
import umc.plantory.global.ai.AIClient;
import umc.plantory.domain.chat.dto.ChatRequestDTO;
import umc.plantory.domain.chat.converter.ChatConverter;
import umc.plantory.domain.chat.repository.ChatRepository;
import umc.plantory.global.ai.PromptFactory;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.diary.repository.DiaryRepository;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.token.provider.JwtProvider;
import umc.plantory.global.ai.token.util.TokenCounter;
import umc.plantory.global.ai.token.application.ResponseProcessingService;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.ChatHandler;
import umc.plantory.global.apiPayload.exception.handler.MemberHandler;
import umc.plantory.global.enums.DiaryStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 챗봇 대화 처리 서비스. 사용자 메시지 저장, 프롬프트 생성, 챗봇 응답 저장을 담당합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatCommandService implements ChatCommandUseCase {
    private final AIClient aiClient;
    private final ChatMemory chatMemory;
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final DiaryRepository diaryRepository;
    private final ResponseProcessingService responseProcessingService;
    private final TokenCounter tokenCounter;

    private static final List<DiaryStatus> VALID_STATUSES = List.of(DiaryStatus.NORMAL, DiaryStatus.SCRAP);
    private static final int MAX_RESPONSE_CHARS = 500; // 응답 최대 글자 수

    @Override
    public ChatResponseDTO.ChatResponse ask(String authorization, ChatRequestDTO request) {
        Member member = getLoginedMember(authorization);

        // 사용자가 메시지 시간 기록
        LocalDateTime userSentAt = LocalDateTime.now();

        // 시작일 및 종료일 설정
        LocalDate startDate = LocalDate.now().minusDays(6);
        LocalDate endDate = LocalDate.now();

        // 일기 리스트 가져오기
        List<Diary> diaries = diaryRepository.findByMemberAndStatusInAndDiaryDateBetween(member, VALID_STATUSES, startDate, endDate);

        // 메모리에서 사용자 대화 이력 불러오기
        List<Message> chatHistory = new ArrayList<>(chatMemory.get(String.valueOf(member.getId())));

        // 사용자가 입력한 메시지 추가
        chatHistory.add(new UserMessage(request.getContent()));

        // 프롬프트 생성
        Prompt prompt = PromptFactory.buildChatPrompt(chatHistory, diaries);

        // AI 기반 응답 생성
        String response = aiClient.getResponse(prompt);

        // 챗봇 답변 검사
        validationResponse(response);

        // AI 답변 시간 기록
        LocalDateTime assistantSentAt = LocalDateTime.now();

        // 기존 대화 턴 수 = (전체 메시지 수 - 1) / 2
        // 사용자 메시지 1개 + 챗봇 메시지 1개 = 1 대화 턴으로 간주하여
        // 전체 메시지 수를 2로 나누어 대화 턴 수를 계산함
        int conversationLength = chatHistory.size() / 2;

        // 인메모리 DB에 저장된 누적 토큰 수 계산
        List<String> inMemoryTexts = chatHistory.stream()
                .map(Message::getText)
                .collect(Collectors.toList());
        int totalTokensInMemory = tokenCounter.calculateTotalTokens(inMemoryTexts);

        // 원본 응답을 후처리하여 인메모리 DB 저장용 버전 생성
        String processedResponse = responseProcessingService.processResponseForMemory(
                response,
                conversationLength,
                String.valueOf(member.getId()),
                totalTokensInMemory
        );

        // 사용자 메시지는 원본 그대로, AI 응답은 후처리된 버전으로 메모리에 저장
        chatMemory.add(String.valueOf(member.getId()), new UserMessage(request.getContent()));
        chatMemory.add(String.valueOf(member.getId()), new AssistantMessage(processedResponse));
        // 사용자 메시지 및 AI 응답 DB에 저장
        chatRepository.save(ChatConverter.toChat(request.getContent(), member, true, userSentAt, MessageType.USER));
        chatRepository.save(ChatConverter.toChat(response, member, false, assistantSentAt, MessageType.ASSISTANT));

        // 데모데이용
        log.info("[채팅 API] ( MemberId = {} ) 채팅 API 진행완료", member.getId());

        return ChatConverter.toChatResponseDTO(response, assistantSentAt, false);
    }

    // 챗봇 응답 에러 처리
    private void validationResponse(String response) {
        // 답변이 없는 경우
        if (response == null || response.trim().isEmpty()) {
            throw new ChatHandler(ErrorStatus.CHAT_RESPONSE_NONE);
        }

        // 글자수 500자 초과한 경우
        if (response.codePointCount(0, response.length()) > MAX_RESPONSE_CHARS) {
            throw new ChatHandler(ErrorStatus.CHAT_RESPONSE_TOO_LONG);
        }
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
