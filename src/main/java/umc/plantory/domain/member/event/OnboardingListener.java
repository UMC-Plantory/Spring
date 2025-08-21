package umc.plantory.domain.member.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import umc.plantory.domain.diary.repository.DiaryRepository;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.wateringCan.repository.WateringCanRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class OnboardingListener {
    private static final int DUMMY_COUNT = 30;

    private final MemberRepository memberRepository;
    private final DiaryRepository diaryRepository;
    private final WateringCanRepository wateringCanRepository;

    @Async // 비동기 처리 (대량 추가라 가입 API 응답 지연 최소화를 위함)
    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) // 회원 저장 및 테라리움 생성 쿼리 커밋 후 실행
    public void createDummyData(Member)


}
