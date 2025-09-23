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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 챗봇 프롬프트(대화 맥락) 생성 유틸리티 클래스.
 */
@Slf4j
public class PromptFactory {
    private static final String CHATBOT_SYSTEM_PROMPT = """
            You are an empathetic counselor for Korean users.
            Analyze emotions and provide supportive responses.
            IMPORTANT: Always respond in Korean language only.
            Use warm, caring tone appropriate for Korean culture.
            Maximum 500 characters including spaces. No line breaks.
        """;

    private static final String DIARY_TITLE_SYSTEM_PROMPT = """
        사용자의 일기 내용을 바탕으로 제목을 지어줘.
        제목은 반드시 공백 포함 20자 미만으로 생성해야 해.
        제목이라고 명시할 필요는 없어. 내용만을 지어줘.
        "꿈같은 하루", "혼자 걷는 밤거리" 같은 느낌의 짧고 자연스러운 문장을 사용해줘.
        제목:", "Title:" 같은 단어는 포함하지 말고, 내용만 반환해줘.
        """;
    private static final String DIARY_COMMENT_SYSTEM_PROMPT = """
        아래 정보를 참고해 한두 문장 정도의 간단한 코멘트를 작성해줘.
        - 일기에서 인상 깊은 문장에 공감하거나 짧게 언급해줘.
        - 감정을 반영해 따뜻하게 격려하거나 추천을 해줘.
        - 수면 패턴이 눈에 띄면 간단히 언급해줘.
        전체적으로 친근하고 긍정적인 톤을 유지해.
        """;

    private static final String DIARY_COMMENT_USER_PROMPT = """
    일기 내용: %s
    일기 제목: %s
    감정: %s
    잠든 시간: %s
    기상 시간: %s
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

    // 유자가 작성한 일기에 AI 코멘트 생성을 위한 프롬프트 생성 메서드
    public static Prompt buildDiaryCommentPrompt(String content, String title, String emotion, LocalDateTime sleepStartTime, LocalDateTime sleepEndTime) {
        // DiaryComment 생성용 Prompt를 빌드하는 메서드
        Message systemMessage = SystemMessage.builder()
                .text(DIARY_COMMENT_SYSTEM_PROMPT)
                .build();

        // 유저 정보(일기 내용, 제목, 감정, 수면 시간)를 포함해 유저 프롬프트 텍스트 생성
        String userText = getUserPromptContent(content, title, emotion, sleepStartTime, sleepEndTime);

        // 생성한 유저 프롬프트 텍스트를 Message 객체로 생성
        Message userMessage = UserMessage.builder()
                .text(userText)
                .build();

        // 시스템 메시지와 유저 메시지를 포함한 Prompt 객체 생성 및 반환
        return Prompt.builder()
                .messages(systemMessage, userMessage)
                .chatOptions(options)
                .build();
    }

    // 유저 프롬프트에 쓸 문자열을 포맷팅해 생성하는 메서드
    private static String getUserPromptContent(String content, String title, String emotion, LocalDateTime sleepStartTime, LocalDateTime sleepEndTime) {
        String userText = String.format(DIARY_COMMENT_USER_PROMPT,
                content,
                title,
                emotion != null ? emotion : "없음",
                sleepStartTime != null ? sleepStartTime.toString() : "없음",
                sleepEndTime != null ? sleepEndTime.toString() : "없음"
        );
        return userText;
    }
}
