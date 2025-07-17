package umc.plantory.domain.chat.application;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.plantory.domain.chat.adapter.out.dto.ChatResDto;
import umc.plantory.domain.chat.adapter.out.repository.ChatJpaRepository;
import umc.plantory.domain.chat.entity.Chat;
import umc.plantory.domain.chat.port.in.ChatQueryUseCase;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatQueryService implements ChatQueryUseCase {

    private final ChatJpaRepository chatJpaRepository;
    private final MemberRepository memberRepository;

    // 챗봇 대화창 최초 진입: 최신 6개 채팅 조회
    @Override
    public List<ChatResDto.ChatResponse> findLatestChatsByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다."));

        return chatJpaRepository.findTop6ByMemberOrderByCreatedAtDesc(member)
                .stream().map(
                        chat -> new ChatResDto.ChatResponse(
                                chat.getContent(), chat.getCreatedAt(), chat.getIsMember())
                ).collect(Collectors.toList());
    }

    // 최초 이후, 커서 페이징: 특정 시점 이전 6개
    @Override
    public List<ChatResDto.ChatResponse> findBeforeChatsByMemberId(Long memberid, LocalDateTime before) {
        Member member = memberRepository.findById(memberid)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다."));
        
                return chatJpaRepository
                .findTop6ByMemberAndCreatedAtLessThanOrderByCreatedAtDesc(member, before)
                .stream()
                .map(chat -> new ChatResDto.ChatResponse(
                        chat.getContent(), chat.getCreatedAt(), chat.getIsMember()))
                .collect(Collectors.toList());
    }
}
