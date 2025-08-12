package umc.plantory.global.ai.token.metrics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class logProcessingMetrics {

    public void recordProcessingMetrics(String original, String processed, String userId) {

        if (original == null || processed == null) {
            return;
        }

        // 원본과 후처리된 응답의 길이(글자 수)를 측정
        int originalLength = original.length();
        int processedLength = processed.length();

        if (originalLength == 0) {
            return;
        }

        // 압축률 계산: (원본 - 후처리) / 원본 * 100
        double compressionRatio = (double)(originalLength - processedLength) / originalLength * 100;

        // 압축률 로그 출력 (운영 중 모니터링용)
        log.info("사용자 {}의 응답 처리 완료: {}자 → {}자 ({}% 압축)",
                userId, originalLength, processedLength, String.format("%.1f", compressionRatio));

        // 과도한 압축률 경고
        if (compressionRatio > 80) {
            log.warn("사용자 {}에게 과도한 압축률 감지: {}% - 의미 손실 가능성 확인 필요",
                    userId, String.format("%.1f", compressionRatio));
        }

        // 비용 절약 계산
        // 이전 대화 기준 한글 1,000토큰 ≈ 400~500자, GPT-4.1-mini 모델 토큰당 비용 약 $0.00003 (GPT-4 기준)
        double tokenSavings = calculateTokenSavingsByModel(originalLength, processedLength);

        // 토큰 절약 로그 추가
        log.debug("사용자 {}의 토큰 절약: ${} (추정 토큰 절약량: {})",
                userId, String.format("%.6f", tokenSavings), estimateSavedTokens(originalLength, processedLength));
    }

    private double calculateTokenSavingsByModel(int originalLength, int processedLength) {
        // 한글 글자 → 토큰 환산율 (평균 약 450자 = 1000토큰 가정)
        double charsPerThousandTokens = 450.0;

        // GPT-4.1-mini 모델 기준 1,000 토큰당 비용
        double costPerThousandTokens = 0.03;

        // 실제 절약된 글자 수 계산 (원본 - 후처리)
        int savedChars = originalLength - processedLength;
        if (savedChars <= 0) {
            return 0.0;
        }

        // 절약된 토큰 수 추정
        // 한글 1000 토큰 ≈ 450 자
        // → 즉, 450 글자가 1000 토큰과 거의 비슷한 양
        // savedChars 글자를 토큰 단위로 변환:
        // (절약된 글자 수 / 450자) * 1000 토큰 = 절약된 토큰 수
        double savedTokens = savedChars / charsPerThousandTokens * 1000;

        // 절약 비용 계산
        // API 요금 단위가 1000토큰당 비용으로 책정되어 있음
        // 천 단위 절약량 × 1,000 토큰 당 가격 => 절약 비용
        double savedCost = (savedTokens / 1000) * costPerThousandTokens;

        return savedCost;
    }
    private int estimateSavedTokens(int originalLength, int processedLength) {
        double charsPerToken = 450.0 / 1000.0;  // 450자/1000토큰 = 0.45자당 토큰 1개 비율
        int savedChars = originalLength - processedLength;

        if (savedChars <= 0) {
            return 0;
        }
        // 절약된 글자 수를 토큰 단위로 환산하려면
        // 절약된 글자 수를 1토큰 당 글자 수(charsPerToken)로 나누면 됨
        return (int) Math.round(savedChars / charsPerToken);
    }
}
