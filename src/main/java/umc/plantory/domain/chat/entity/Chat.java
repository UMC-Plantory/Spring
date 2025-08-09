package umc.plantory.domain.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.ai.chat.messages.MessageType;
import umc.plantory.domain.member.entity.Member;

import java.time.LocalDateTime;

/**
 * 채팅 메시지 엔티티. 사용자와 챗봇 간의 대화 내용을 저장합니다.
 */
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@DynamicInsert
@DynamicUpdate
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 2000, nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean isMember;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private MessageType messageType;

    private String test;
}
