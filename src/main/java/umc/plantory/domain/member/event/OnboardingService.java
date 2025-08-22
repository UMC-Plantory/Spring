package umc.plantory.domain.member.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.diary.repository.DiaryRepository;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.wateringCan.entity.WateringCan;
import umc.plantory.domain.wateringCan.repository.WateringCanRepository;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.MemberHandler;
import umc.plantory.global.enums.DiaryStatus;
import umc.plantory.global.enums.Emotion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnboardingService {
    private static final int DUMMY_COUNT = 30;
    private static final Emotion[] emotionList = {
            Emotion.HAPPY, Emotion.AMAZING, Emotion.SAD, Emotion.ANGRY, Emotion.SOSO
    };

    private final MemberRepository memberRepository;
    private final DiaryRepository diaryRepository;
    private final WateringCanRepository wateringCanRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createDummyData(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 이미 더미데이터가 있으면 스킵 (중복 방지)
        int diaryCount = diaryRepository.countByMember(member);
        if (diaryCount >= DUMMY_COUNT) {
            log.info("[Onboarding] memberId = {} 기존 더미 {}건 존재 -> 더미 데이터 생성 스킵", member.getId(), diaryCount);
            return;
        }

        // 더미 30건 생성
        List<Diary> dummyDiaryList = new ArrayList<>(DUMMY_COUNT);
        List<WateringCan> dummyWateringCanList = new ArrayList<>(DUMMY_COUNT);

        // 감정 랜덤 (멤버 ID 기반)
        Random rnd = new Random(member.getId());

        LocalDate today = LocalDate.now();
        for (int i = 1; i <= DUMMY_COUNT; i++) {
            LocalDate date = today.minusDays(i); //
            Emotion emotion = emotionList[rnd.nextInt(emotionList.length)];

            LocalDateTime sleepStart = randomSleepStart(date);
            LocalDateTime sleepEnd = randomSleepEnd(date);

            Diary diary = Diary.builder()
                    .member(member)
                    .title("데모데이용 일기 " + i)
                    .content("이곳에 오늘의 기분과 생각을 자유롭게 적어보세요 :)")
                    .emotion(emotion)
                    .status(DiaryStatus.NORMAL)
                    .diaryDate(date)
                    .sleepStartTime(sleepStart)
                    .sleepEndTime(sleepEnd)
                    .build();

            WateringCan wateringCan = WateringCan.builder()
                    .member(member)
                    .diary(diary)
                    .emotion(emotion)
                    .diaryDate(date)
                    .build();

            dummyDiaryList.add(diary);
            dummyWateringCanList.add(wateringCan);
        }

        diaryRepository.saveAll(dummyDiaryList);
        wateringCanRepository.saveAll(dummyWateringCanList);

        member.updateMemberDataForDemoDay();
        memberRepository.save(member);

        log.info("[Onboarding] memberId = {} 더미 일기 {}건 생성 완료", member.getId(), dummyDiaryList.size());
    }

    private LocalDateTime randomSleepStart(LocalDate baseDate) {
        LocalDateTime startRange = baseDate.minusDays(1).atTime(22, 0);
        LocalDateTime endRange = baseDate.atTime(3, 0);
        return randomDateTimeBetween(startRange, endRange);
    }

    private LocalDateTime randomSleepEnd(LocalDate baseDate) {
        LocalDateTime startRange = baseDate.atTime(7, 0);
        LocalDateTime endRange = baseDate.atTime(12, 0);
        return randomDateTimeBetween(startRange, endRange);
    }

    private LocalDateTime randomDateTimeBetween(LocalDateTime start, LocalDateTime end) {
        long startEpoch = start.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endEpoch   = end.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        long randomEpoch = ThreadLocalRandom.current().nextLong(startEpoch, endEpoch);
        return LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(randomEpoch), java.time.ZoneId.systemDefault());
    }
}
