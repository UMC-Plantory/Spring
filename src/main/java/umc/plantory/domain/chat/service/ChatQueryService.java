package umc.plantory.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.chat.dto.ChatResponseDTO;
import umc.plantory.domain.chat.repository.ChatJpaRepository;
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
    private final ChatJpaRepository chatJpaRepository;
    private final MemberRepository memberRepository;

    // 챗봇 대화창 최초 진입: 최신 6개 채팅 조회
    @Override
    public List<ChatResponseDTO.ChatResponse> findLatestChats(String authorization) {
        Member member = getLoginedMember(authorization);

        return chatJpaRepository.findTop6ByMemberOrderByCreatedAtDesc(member)
                .stream()
                .map(chat -> new ChatResponseDTO.ChatResponse(
                                chat.getContent(), chat.getCreatedAt(), chat.getType())
                ).collect(Collectors.toList());
    }

    // 최초 이후, 커서 페이징: 특정 시점 이전 6개
    @Override
    public List<ChatResponseDTO.ChatResponse> findBeforeChats(String authorization, LocalDateTime before) {
        Member member = getLoginedMember(authorization);

        return chatJpaRepository.findTop6ByMemberAndCreatedAtLessThanOrderByCreatedAtDesc(member, before)
                .stream()
                .map(chat -> new ChatResponseDTO.ChatResponse(
                        chat.getContent(), chat.getCreatedAt(), chat.getType()))
                .collect(Collectors.toList());
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
