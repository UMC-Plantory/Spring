package umc.plantory.global.ai.processor.compression;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


// 압축 전략 결정
@Service
@RequiredArgsConstructor
public class SmartCompressionService {

    private final LLMCompressionService llmCompressor;

    private static final int LENGTH_THRESHOLD = 150;    // 150자 이상 시 압축 검토
    private static final int CONVERSATION_THRESHOLD = 5; // 5턴 이상 시 압축 적용

    public String processConditionally(String cleanedResponse, int conversationLength) {

        // 조건 1: 대화가 짧으면 압축 생략
        if (conversationLength <= CONVERSATION_THRESHOLD) {
            return limitLength(cleanedResponse, 120); // 단순 길이 제한만
        }

        // 조건 2: 응답이 짧으면 압축 생략
        if (cleanedResponse.length() <= LENGTH_THRESHOLD) {
            return cleanedResponse;
        }

        // 조건 3: 긴 대화 + 긴 응답 → LLM 압축
        return llmCompressor.compressToEssential(cleanedResponse);
    }

    private String limitLength(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}

