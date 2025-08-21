package umc.plantory.domain.member.event;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OnboardingListener {
    private final OnboardingService onboardingService;

    @Async // 비동기 처리 (대량 추가라 가입 API 응답 지연 최소화를 위함)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) // 회원 저장 및 테라리움 생성 쿼리 커밋 후 실행
    public void createDummyData(MemberEvent event) {
        onboardingService.createDummyData(event.getMemberId());
    }
}
