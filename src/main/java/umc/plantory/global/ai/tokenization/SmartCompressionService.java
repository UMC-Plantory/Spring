package umc.plantory.global.ai.tokenization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


// 압축 전략 결정
@Service
@RequiredArgsConstructor
public class SmartCompressionService {

    private final LLMCompressionService llmCompressor;

    // 대화 턴 수 임계값 (예: 5턴 이상부터 본격 압축 고려)
    private static final int CONVERSATION_THRESHOLD = 5;

    // 인메모리 내 전체 토큰 임계값 (예: 1000 토큰 이상부터 LLM 압축)
    private static final int TOKEN_THRESHOLD = 1000;

    // 응답이 너무 짧으면 압축할 필요 없는 길이 임계값
    private static final int LENGTH_THRESHOLD = 150;

    /**
     * 조건에 따라 AI 응답 후처리 (압축 또는 단순 길이 제한)
     *
     * @param cleanedResponse : 정규식 등으로 정제된 AI 응답
     * @param conversationLength : 현재까지 대화 턴 수 (사용자-AI 쌍)
     * @param totalTokensInMemory : 인메모리 DB에 저장된 대화 히스토리 총 토큰 수 (추정치)
     * @return 후처리된 응답 문자열
     */
    public String processConditionally(String cleanedResponse,
                                       int conversationLength,
                                       int totalTokensInMemory) {

        // 1. 대화가 짧고 현재 인메모리에 저장된 토큰수도 작으면 → 단순 길이 제한만 수행
        if (conversationLength <= CONVERSATION_THRESHOLD && totalTokensInMemory <= TOKEN_THRESHOLD) {
            // 단순 길이 제한: 120자로 자르기
            return limitLength(cleanedResponse, 120);
        }

        // 2. 응답 자체가 짧으면 압축할 필요 없음
        if (cleanedResponse.length() <= LENGTH_THRESHOLD) {
            return cleanedResponse;
        }

        // 3. 누적 토큰량이 크면 → LLM 압축 요청 (비용 들지만 큰 절감 효과 기대)
        if (totalTokensInMemory > TOKEN_THRESHOLD) {
            return llmCompressor.compressToEssential(cleanedResponse);
        }

        // 위 조건에 해당되지 않아도 길이 제한으로 처리
        return limitLength(cleanedResponse, 120);
    }


    /**
     * 주어진 최대 길이 내에서 문장 단위로 잘라 반환합니다.
     * 자른 경우 말줄임표(...)를 붙여 내용이 생략되었음을 표시합니다.
     *
     * 나중에 감정 공감 문장 보존 로직을 추가하기 쉽도록 설계되어 있습니다.
     *
     * @param text 원본 텍스트
     * @param maxLength 최대 허용 글자 수 (말줄임표 포함)
     * @return maxLength 내 자연스러운 문장 단위로 자른 텍스트
     */
    private String limitLength(String text, int maxLength) {
        if (text.length() <= maxLength) return text;

        // 문장 단위 분리 (마침표, 느낌표, 물음표 기준)
        String[] sentences = text.split("(?<=[.!?])\\s+");
        StringBuilder sb = new StringBuilder();

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

