package umc.plantory.global.ai.processor.cleaner;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

// 정규식 기반 텍스트 정제
@Component
public class PatternBasedProcessor {

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

        // 정규식 패턴으로 불필요한 문구 제거
        for (Pattern pattern : REMOVAL_PATTERNS) {
            cleaned = pattern.matcher(cleaned).replaceAll("");
        }

        // 공백 정리
        cleaned = cleaned.replaceAll("\\s+", " ").trim();

        return cleaned;
    }
}

