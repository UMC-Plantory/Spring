package umc.plantory.global.ai.processor.compression;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import umc.plantory.global.ai.AIClient;

// AI 기반 압축 실행
@Service
@RequiredArgsConstructor
public class LLMCompressionService {

    private final AIClient aiClient;

    private static final String COMPRESSION_PROMPT = """
        Extract only essential information from this chatbot response.
        Remove greetings, pleasantries, and redundant explanations.  
        Keep core facts and advice only. Max 50 characters.
        
        Original: %s
        Essential:
        """;

    public String compressToEssential(String originalResponse) {
        // 별도 API 호출로 응답 압축
        String prompt = String.format(COMPRESSION_PROMPT, originalResponse);
        return aiClient.getResponse(new Prompt(prompt));
    }
}

