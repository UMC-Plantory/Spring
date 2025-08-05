package umc.plantory.domain.chat.prompt;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * 챗봇 프롬프트(대화 맥락) 생성 유틸리티 클래스.
 */
public class PromptFactory {
    private static final String SYSTEM_PROMPT = """
        너는 사용자의 감정을 세심하게 살펴주고, 진심으로 공감하며, 친구처럼 따뜻하게 대화하는 심리 상담사 챗봇이야.
        아래는 너와 사용자가 지금까지 나눈 대화야. 사용자의 감정, 말투, 고민을 잘 파악해서 진심으로 공감하고, 친구처럼 따뜻하게 답변해줘.
        특히, 사용자가 힘들거나 슬픈 감정을 표현하면 먼저 진심 어린 공감과 위로를 건네고, 해결책이나 조언이 필요할 때는 사용자의 말투와 상황을 고려해서 자연스럽고 다정하게 이야기해줘.
        대화를 이어갈 때는 사용자의 감정이나 고민을 기억하고, 감정 이해를 돕는 질문도 함께 던져줘. 마지막에 너무 성급히 마무리하지 말고, 사용자의 감정을 한 번 더 되짚으며 여유 있게 이어가줘.
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
    public static Prompt buildPrompt(List<Message> chatHistory) {
        List<Message> messages = new ArrayList<>(); // 프롬프트에 담길 메시지
        messages.add(new SystemMessage(SYSTEM_PROMPT)); // 프롬프트 규칙 추가
        messages.addAll(chatHistory); // 사용자와 에이전트의 대화 대용 추가

        return new Prompt(messages, options);
    }
}
