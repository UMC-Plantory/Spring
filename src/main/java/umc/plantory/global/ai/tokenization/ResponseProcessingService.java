package umc.plantory.global.ai.tokenization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 전체 처리 (compression, cleaner 정의된 클래스 활용)
@Service
@RequiredArgsConstructor
public class ResponseProcessingService {

    private final PatternBasedProcessor patternProcessor;
    private final SmartCompressionService compressionService;
    private final ProcessingMetricsCollector metricsCollector;

    public String processResponseForMemory(String originalResponse,
                                           int conversationLength,
                                           String userId,
                                           int totalTokensInMemory) {

        // 1. 정규식 기반 정제
        String cleanedResponse = patternProcessor.cleanResponse(originalResponse);

        // 2. 글자 수에 따른 조건부 압축
        String finalResponse = compressionService.processConditionally(
                cleanedResponse,
                conversationLength,
                totalTokensInMemory
        );

        // 3단계: 메트릭 수집 (별도 클래스로 분리)
        metricsCollector.recordProcessingMetrics(originalResponse, finalResponse, userId);

        return finalResponse;
    }
}


