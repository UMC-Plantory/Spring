package umc.plantory.domain.chat.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import umc.plantory.domain.chat.adapter.in.dto.ChatReqDto;
import umc.plantory.domain.chat.adapter.out.dto.ChatResDto;
import umc.plantory.global.apiPayload.ApiResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatQueryApi {
    @Operation(summary = "챗봇 이전 채팅 최초 조회 API", description = "이전에 챗봇과 대화를 나눈 채팅을 최초로 6개 조회하는 API 입니다.")
    ResponseEntity<ApiResponse<List<ChatResDto.ChatResponse>>> getChatHistoryLatest(@RequestParam("memberId") Long memberId);

    @Operation(summary = "챗봇 이전 채팅 스크롤 조회 API", description = "챗봇 대화 페이지 접속 후 스크롤 시 6개 조회하는 API 입니다.")
    ResponseEntity<ApiResponse<List<ChatResDto.ChatResponse>>> getChatHistoryBefore(@RequestParam("memberId") Long memberId,
                                                                                    @RequestParam("before")LocalDateTime before);
}
