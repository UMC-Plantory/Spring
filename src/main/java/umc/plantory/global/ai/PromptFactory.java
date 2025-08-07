package umc.plantory.global.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.global.enums.Emotion;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 챗봇 프롬프트(대화 맥락) 생성 유틸리티 클래스.
 */
@Slf4j
public class PromptFactory {
    private static final String CHATBOT_SYSTEM_PROMPT = """
        너는 공감과 위로에 능한 친구 같은 상담사야.
        입력받은 사용자의 최근 감정, 수면 시간, 일기 내용, 이전 대화를 바탕으로 따뜻하고 진심 어린 대화를 이어가줘.
        특히 힘든 감정엔 먼저 공감과 위로를 건네고, 필요할 땐 상황에 맞는 조언이나 질문을 다정하게 전해줘.
        """;

    private static final String DIARY_TITLE_SYSTEM_PROMPT = """
        사용자의 일기 내용을 바탕으로 제목을 지어줘.
        제목은 반드시 공백 포함 20 미만으로 생성해야 해.
        제목이라고 명시할 필요는 없어. 내용만을 지어줘.
        "꿈같은 하루", "혼자 걷는 밤거리" 같은 느낌의 짧고 자연스러운 문장을 사용해줘.
        제목:", "Title:" 같은 단어는 포함하지 말고, 내용만 반환해줘.
        """;

    /**
     * OpenAI 모델 호출 시 사용할 옵션
     * model: 사용할 LLM 모델 이름
     * temperature: 창의성 조절 (0에 가까울수록 사실 기반, 할루시네이션 낮아짐)
     */
    private static final OpenAiChatOptions options = OpenAiChatOptions.builder()
            .model("gpt-4.1-mini")
            .temperature(0.7)
            .build();
    /**
     * 최근 대화 기록과 사용자 입력을 바탕으로 프롬프트 문자열을 생성합니다.
     * @param chatHistory 최근 대화 목록
     * @return 프롬프트
     */
    public static Prompt buildChatPrompt(List<Message> chatHistory, List<Diary> diaries) {
        List<Message> totalMessage = new ArrayList<>();
        StringBuilder diaryInfoBuilder = new StringBuilder();

        for (Diary diary : diaries) {
            LocalDate date = diary.getDiaryDate();
            String content = diary.getContent();
            Emotion emotion = diary.getEmotion();
            Long sleepMinutes = Duration.between(diary.getSleepStartTime(), diary.getSleepEndTime()).toMinutes();

            diaryInfoBuilder.append(String.format(
                    "날짜 :%s 감정: %s 수면 시간: %d분 일기 내용: %s\n",
                    date,
                    emotion,
                    sleepMinutes,
                    content
            ));
        }

        Message userMessage = UserMessage.builder()
                .text(diaryInfoBuilder.toString())
                .build();

        Message systemMessage = SystemMessage.builder()
                .text(CHATBOT_SYSTEM_PROMPT)
                .build();

        totalMessage.add(systemMessage);
        totalMessage.addAll(chatHistory);
        totalMessage.add(userMessage);

        return Prompt.builder()
                .messages(totalMessage)
                .chatOptions(options)
                .build();
    }

    public static Prompt buildDiaryTitlePrompt(String content) {
        Message systemMessage = SystemMessage.builder()
                .text(DIARY_TITLE_SYSTEM_PROMPT)
                .build();

        Message userMessage = UserMessage.builder()
                .text(content)
                .build();

        return Prompt.builder()
                .messages(systemMessage, userMessage)
                .chatOptions(options)
                .build();
    }
}
