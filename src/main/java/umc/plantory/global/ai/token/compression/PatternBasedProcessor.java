package umc.plantory.global.ai.token.compression;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

// 정규식 기반 텍스트 정제
@Component
public class PatternBasedProcessor {
    /**
     * 대화 응답에서 제거할 불필요한 문구 패턴들.
     * 각 정규식은 다음과 같은 상황에 매칭됩니다:
     *
     * 1. "안녕하세요" 뒤에 느낌표가 0개 이상 반복되는 경우
     * 2. "도움이 되"로 시작해서 마침표까지 이어지는 문장 (예: "도움이 되셨길 바랍니다.")
     * 3. "혹시"로 시작하는 질문형 문장 (느낌표가 아닌 물음표 끝)
     * 4. "언제든" 혹은 "언제든지"로 시작하는 문장 (마침표로 끝)
     * 5. "감사합니다" 뒤 느낌표 반복
     * 6. "아", "오", "우", "와" 등의 감탄사 반복과 느낌표
     * 7. "정말", "정말로" 등의 감탄 표현과 느낌표
     */
    private static final Pattern[] REMOVAL_PATTERNS = {
            Pattern.compile("안녕하세요[!]*"),
            Pattern.compile("도움이\\s*되[^.]*[.]"),
            Pattern.compile("혹시\\s*[^?]*[?]"),
            Pattern.compile("언제든[지]*\\s*[^.]*[.]"),
            Pattern.compile("감사합니다[!]*"),
            Pattern.compile("[아오우와]+[!]*"),
            Pattern.compile("정말[로]*[!]*")
    };

    public String cleanResponse(String response) {
        String cleaned = response;

        // 각 패턴을 순회하며 매칭되는 부분을 빈 문자열("")로 제거
        for (Pattern pattern : REMOVAL_PATTERNS) {
            cleaned = pattern.matcher(cleaned).replaceAll("");
        }

        // 문장 사이 혹은 단어 사이 과도한 공백을 하나의 공백으로 축소
        // 그리고 앞뒤 공백 제거
        cleaned = cleaned.replaceAll("\\s+", " ").trim();

        return cleaned;
    }
}

