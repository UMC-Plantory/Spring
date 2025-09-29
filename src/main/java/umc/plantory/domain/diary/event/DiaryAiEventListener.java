package umc.plantory.domain.diary.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.diary.repository.DiaryRepository;
import umc.plantory.global.ai.AIClient;
import umc.plantory.global.ai.PromptFactory;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.DiaryHandler;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiaryAiEventListener {
    private final DiaryRepository diaryRepository;
    private final AIClient aiClient;

    @Async
    @EventListener(DiaryAiEvent.class)
    @Transactional
    public void handleDiaryAiEvent(DiaryAiEvent event) {

        Diary diary = diaryRepository.findById(event.getDiaryId())
                .orElseThrow(() -> new DiaryHandler(ErrorStatus.DIARY_NOT_FOUND));

        // 새로 추가될 제목, 코멘트
        // 현재 상태로 미리 초기화
        String newTitle = diary.getTitle();
        String newAiComment = diary.getAiComment();

        boolean needsUpdate = false;

        // 상태 유무 판단은 DiaryCommandService에서 수행 -> 현재 diary는 모두 NORMAL 상태
        // NORMAL 상태의 일기이지만 제목이 "임시 제목"일 경우 AI 제목 새로 생성
        if (Objects.equals("임시 제목", diary.getTitle()) && diary.getContent() != null &&
            !diary.getContent().isBlank()) {
            newTitle = generateDiaryTitle(diary.getContent());
            needsUpdate = true;
        }

        // NORMAL 상태의 일기이지만 AI 코멘트가 없을 경우 새로 생성
        if (Objects.equals(diary.getAiComment(), "임시 코멘트")) {
            newAiComment = generateDiaryComment(diary, newTitle);
            needsUpdate = true;
        }

        if (needsUpdate) {
            diary.updateTitleAndComment(newTitle, newAiComment);
        }

    }

    // 프롬프트 생성 및 AI 호출 후, 제목 응답 받기
    private String generateDiaryTitle(String content) {
        Prompt prompt = PromptFactory.buildDiaryTitlePrompt(content);
        return aiClient.getResponse(prompt);
    }


    // 프롬프트 생성 및 AI 호출 후, 코멘트 응답 받기
    private String generateDiaryComment(Diary diary, String title) {
        Prompt prompt = PromptFactory.buildDiaryCommentPrompt(
                diary.getContent(),
                title,
                diary.getEmotion() != null ? diary.getEmotion().name() : null,
                diary.getSleepStartTime(),
                diary.getSleepEndTime()
        );
        return aiClient.getResponse(prompt);
    }
}
