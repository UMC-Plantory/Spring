package umc.plantory.global.ai.token.compression;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


// 압축 전략 결정
@Service
@RequiredArgsConstructor
public class DynamicCompressionService {

    private final LLMCompressionService llmCompressor;

    // 대화 턴 수 임계값 (ex. 5턴 이상부터 압축 고려)
    private static final int CONVERSATION_THRESHOLD = 5;

    // 인메모리 내 전체 토큰 임계값 (ex. 1000 토큰 이상부터 LLM 압축)
    // 한국어로 약 400~500자
    private static final int TOKEN_THRESHOLD = 1000;

    // 압축할 필요 없는 길이 임계값
    private static final int LENGTH_THRESHOLD = 120;

    public String processConditionally(String cleanedResponse,
                                       int conversationLength,
                                       int totalTokensInMemory) {

        // 1. 누적 토큰이 많으면 → 추가 비용이 들어도 AI 기반 프롬프트 압축 실행
        // AI 압축 비용은 한 번 발생하지만, 압축된 결과를 인메모리 DB에 저장해서 이후 재사용 시 비용 절감 및 토큰 절감 효과가 누적
        if (totalTokensInMemory > TOKEN_THRESHOLD) {
            return llmCompressor.compressToEssential(cleanedResponse);
        }

        // 2. 대화 턴 수가 얼마 안 되고, 현재 인메모리에 저장된 대화의 토큰수도 작으면 → 단순 길이 제한만 수행, 압축 불필요
        if (conversationLength <= CONVERSATION_THRESHOLD && totalTokensInMemory <= TOKEN_THRESHOLD) {
            // 불필요한 비용과 작업을 줄이기 위해 단순히 120자까지만 제한
            return limitLength(cleanedResponse, 120);
        }

        // 3. 응답이 짧으면 → 압축할 필요 없이 원본 그대로 반환
        if (cleanedResponse.length() <= LENGTH_THRESHOLD) {
            return cleanedResponse;
        }

        // 위 조건에 해당되지 않아도 길이 제한으로 처리
        return limitLength(cleanedResponse, 120);
    }

    // 텍스트를 지정한 최대 길이 내에서 문장 단위로 자연스럽게 자르는 메서드
    // 자른 경우 내용 끝에 '...'를 붙여 생략되었음을 표시
    // 텍스트 길이가 최대 길이보다 짧으면 원본 그대로 반환
    // 중간에 문장이 잘리지 않도록 문장 구분자를 기준으로 분리해 처리
    private String limitLength(String text, int maxLength) {
        if (text.length() <= maxLength) return text;

        // 문장 단위 분리 (마침표, 느낌표, 물음표 기준)
        String[] sentences = text.split("(?<=[.!?])\\s+");
        StringBuilder sb = new StringBuilder();

        // 문장 도중에 잘림을 방지하기 위한 작업 (문장 의미 보존)
        // 문장 하나씩 추가하며 maxLength - 3 이내 유지
        // -3의 경우 문장 맨 마지막에 남은 문장이 있어 "..."으로 남기기 위함
        for (String sentence : sentences) {
            if (sb.length() + sentence.length() > maxLength - 3) break;
            sb.append(sentence).append(" ");
        }

        // 결과에 말줄임표(...) 추가
        return sb.toString().trim() + "...";
    }
}

