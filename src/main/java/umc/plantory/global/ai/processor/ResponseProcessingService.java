package umc.plantory.global.ai.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.plantory.global.ai.monitoring.ProcessingMetricsCollector;
import umc.plantory.global.ai.processor.cleaner.PatternBasedProcessor;
import umc.plantory.global.ai.processor.compression.SmartCompressionService;

// 전체 처리 흐름 (compression, cleaner 정의된 클래스 활용)
@Service
@RequiredArgsConstructor
public class ResponseProcessingService {

    private final PatternBasedProcessor patternProcessor;
    private final SmartCompressionService compressionService;
    private final ProcessingMetricsCollector metricsCollector;

    public String processResponseForMemory(String originalResponse,
                                           int conversationLength,
                                           String userId) {

        // 1단계: 정규식 기반 정제
        String cleanedResponse = patternProcessor.cleanResponse(originalResponse);

        // 2단계: 글자 수 조건부 압축
        String finalResponse = compressionService.processConditionally(
                cleanedResponse,
                conversationLength
        );

        // 3단계: 메트릭 수집 (별도 클래스로 분리)
        metricsCollector.recordProcessingMetrics(originalResponse, finalResponse, userId);

        return finalResponse;
    }
}


