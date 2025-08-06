package umc.plantory.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.chat.converter.ChatConverter;
import umc.plantory.domain.chat.dto.ChatResponseDTO;
import umc.plantory.domain.chat.entity.Chat;
import umc.plantory.domain.chat.repository.ChatRepositoryCustom;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.token.provider.JwtProvider;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.MemberHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ChatQueryService implements ChatQueryUseCase {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final ChatRepositoryCustom chatRepositoryCustom;

    @Override
    public ChatResponseDTO.ChatsResponse findChatList(String authorization, LocalDateTime cursor, int size) {
        // 인증 토큰을 통해 현재 로그인한 사용자 정보 조회
        Member loginedMember = getLoginedMember(authorization);

        // 사용자 ID와 커서 기반으로 채팅 목록 조회 (페이징을 위해 size + 1개 조회)
        List<Chat> chats = chatRepositoryCustom.findChats(loginedMember.getId(), cursor, size);

        // 다음 페이지 존재 여부 판단 (조회 결과가 요청 크기보다 크면 다음 페이지 존재)
        boolean hasNext = chats.size() > size;

        // 다음 페이지가 있다면 마지막 항목을 제거하여 실제 반환할 데이터만 남김
        if (hasNext) {
            chats = chats.subList(0, size);
        }

        // 다음 요청에 사용할 커서 값 설정 (다음 페이지가 있으면 마지막 채팅의 생성 시간)
        LocalDateTime nextCurosr = hasNext ? chats.get(chats.size() - 1).getCreatedAt() : null;

        // Chat 엔티티 리스트를 ChatsDetatil DTO 리스트로 변환
        List<ChatResponseDTO.ChatsDetatil> chatsDetatilList = chats.stream()
                .map(chat -> ChatConverter.toChatsDetail(chat))
                .collect(Collectors.toList());

        // 채팅 목록, 다음 페이지 존재 여부, 다음 커서를 포함한 응답 DTO 생성 및 반환
        return ChatConverter.toChatsResponse(chatsDetatilList, hasNext, nextCurosr);
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
