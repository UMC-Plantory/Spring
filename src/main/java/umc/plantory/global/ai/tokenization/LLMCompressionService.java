package umc.plantory.global.ai.tokenization;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import umc.plantory.global.ai.AIClient;

// AI 기반 압축 실행
@Service
@RequiredArgsConstructor
public class LLMCompressionService {

    private final AIClient aiClient;

    /**
     * 프롬프트: 원본 챗봇 응답에서 핵심 정보만 추출하되,
     * 감정과 대화 맥락은 유지하도록 지시합니다.
     * 불필요한 인사말이나 중복 설명은 제거하며,
     * 최대 50자 내외로 요약하도록 제한합니다.
     */
    private static final String COMPRESSION_PROMPT = """
    Extract only essential information from this chatbot response,
    while preserving the emotional tone and conversational context.
    Remove greetings, pleasantries, and redundant explanations,
    but keep the flow natural and empathetic.
    Keep core facts and advice only. Max 50 characters.
            
    Original: %s
    Essential:
    """;

    /**
     * LLM API를 호출해 원본 응답을 핵심 내용 위주로 요약/압축
     *
     * @param originalResponse - 압축 대상 원본 텍스트
     * @return 압축된 요약 결과
     */
    public String compressToEssential(String originalResponse) {
        // 별도 API 호출로 응답 압축
        String prompt = String.format(COMPRESSION_PROMPT, originalResponse);
        return aiClient.getResponse(new Prompt(prompt));
    }
}

