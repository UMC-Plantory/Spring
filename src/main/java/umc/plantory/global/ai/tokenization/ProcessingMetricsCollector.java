package umc.plantory.global.ai.tokenization;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessingMetricsCollector {

    private final MeterRegistry meterRegistry;

    /**
     * 응답 처리 메트릭 수집
     */
    public void recordProcessingMetrics(String original, String processed, String userId) {

        // 기본 수치 계산
        int originalLength = original.length();
        int processedLength = processed.length();
        double compressionRatio = (double)(originalLength - processedLength) / originalLength * 100;

        // 1. Micrometer로 메트릭 수집
        meterRegistry.gauge("response.compression.ratio", compressionRatio);
        meterRegistry.counter("response.processing.count").increment();

        // 2. 로그로 기록
        log.info("사용자 {}의 응답 처리 완료: {}자 → {}자 ({}% 압축)",
                userId, originalLength, processedLength, String.format("%.1f", compressionRatio));

        // 3. 비정상 상황 감지
        if (compressionRatio > 80) {
            log.warn("사용자 {}에게 과도한 압축률 감지: {}% - 의미 손실 가능성 확인 필요",
                    userId, String.format("%.1f", compressionRatio));
        }

        // 4. 비용 절약 계산
        double tokenSavings = calculateTokenSavings(originalLength, processedLength);
        meterRegistry.gauge("response.token.savings", tokenSavings);

        // 토큰 절약 로그 추가
        log.debug("사용자 {}의 토큰 절약: ${} ({}토큰 절약)",
                userId, String.format("%.4f", tokenSavings), (originalLength - processedLength));
    }

    private double calculateTokenSavings(int originalLength, int processedLength) {
        int savedTokens = originalLength - processedLength;
        double savedCost = (savedTokens / 1000.0) * 0.03;
        return savedCost;
    }
}
