package umc.plantory.domain.chat.prompt;

import umc.plantory.domain.chat.entity.Chat;

import java.util.Comparator;
import java.util.List;

public class PromptFactory {
    private static final String SYSTEM_PROMPT = """
        너는 사용자의 감정을 세심하게 살펴주고, 진심으로 공감하며, 친구처럼 따뜻하게 대화하는 심리 상담사 챗봇이야.
        아래는 너와 사용자가 지금까지 나눈 대화야. 사용자의 감정, 말투, 고민을 잘 파악해서 진심으로 공감하고, 친구처럼 따뜻하게 답변해줘.
        특별히, 사용자가 힘들거나 슬픈 감정을 표현하면 먼저 진심 어린 공감과 위로를 건네고, 해결책이나 조언이 필요할 때는 사용자의 말투와 상황을 고려해서 자연스럽고 다정하게 이야기해줘.
        대화를 이어갈 때는 사용자의 감정이나 고민을 기억하고, 감정 이해를 돕는 질문도 함께 던져줘. 마지막에 너무 성급히 마무리하지 말고, 사용자의 감정을 한 번 더 되짚으며 여유 있게 이어가줘.
        """;

    private static final String FIRST_TIME_PROMPT = """
        너는 사용자의 감정을 세심하게 살펴주고, 진심으로 공감하며, 친구처럼 따뜻하게 대화하는 심리 상담사 챗봇이야.
        지금 사용자가 처음으로 너에게 말을 걸었으니, 먼저 따뜻하게 인사하고, 상대방이 편하게 자신의 감정이나 고민을 이야기할 수 있도록 부드럽게 대화를 시작해줘.
        상대방이 어떤 감정이든 안전하게 표현할 수 있다고 느끼게 해주고, 상담사이지만 너무 형식적이지 않고, 진짜 친구처럼 자연스럽고 다정하게 대화를 이끌어줘.
        """;

    public static String buildPrompt(List<Chat> recentChats, String message) {
        StringBuilder prompt = new StringBuilder();

        if (recentChats == null || recentChats.isEmpty()) {
            // 첫 대화: 전용 프롬프트 사용
            prompt.append(FIRST_TIME_PROMPT).append("\n");
        } else {
            // 기존 대화: 기존 시스템 프롬프트와 대화 맥락 활용
            prompt.append(SYSTEM_PROMPT).append("\n\n");
            recentChats.sort(Comparator.comparing(Chat::getCreatedAt));

            // recentChats 통해 기존 대화 추출
            for (Chat chat : recentChats) {
                if (Boolean.TRUE.equals(chat.getIsMember())) {
                    prompt.append("사용자: ").append(chat.getContent()).append("\n");
                } else {
                    prompt.append("챗봇: ").append(chat.getContent()).append("\n");
                }
            }
        }
        // 현재 사용자 입력 추가
        prompt.append("사용자(이번 입력): ").append(message).append("\n");
        prompt.append("챗봇:");

        return prompt.toString();

    }
}
