package umc.plantory.global.ai.token.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.plantory.global.ai.token.compression.PatternBasedProcessor;
import umc.plantory.global.ai.token.compression.DynamicCompressionService;
import umc.plantory.global.ai.token.metrics.logProcessingMetrics;

// 전체 응답 처리 및 토큰 최적화 흐름 서비스
@Service
@RequiredArgsConstructor
public class ResponseProcessingService {

    private final PatternBasedProcessor patternProcessor; // 패턴 기반 최적화
    private final DynamicCompressionService compressionService;  // 조건부 압축
    private final logProcessingMetrics metricsCollector; // 처리 성능 및 품질 메트릭 수집

    public String processResponseForMemory(String originalResponse,
                                           int conversationLength,
                                           String userId,
                                           int totalTokensInMemory) {

        // 1. 정규식 기반 정제 수행
        // 원본 응답에서 불필요한 문장, 반복, 특수문자 등을 제거한 텍스트 생성
        String cleanedResponse = patternProcessor.cleanResponse(originalResponse);

        // 2. 대화 턴 수 및 인메모리 DB 토큰 수에 따른 조건부 압축 처리
        // 대화 턴 수, 인메모리 토큰 수 등을 고려해 필요시 AI 압축 또는 단순 길이 제한 적용
        String finalResponse = compressionService.processConditionally(
                cleanedResponse,
                conversationLength,
                totalTokensInMemory
        );

        // 3. 처리 메트릭 수집
        // 처리 시간, 토큰 감소율 등 다양한 지표를 기록하여 성능 모니터링에 활용
        // 지속적인 품질 개선, 비용 절감 최적화 포인트 도출을 위한 부분
        metricsCollector.recordProcessingMetrics(originalResponse, finalResponse, userId);

        return finalResponse;
    }
}


