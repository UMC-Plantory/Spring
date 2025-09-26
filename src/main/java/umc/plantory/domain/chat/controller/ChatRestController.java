package umc.plantory.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.plantory.domain.chat.dto.ChatRequestDTO;
import umc.plantory.domain.chat.dto.ChatResponseDTO;
import umc.plantory.domain.chat.service.ChatCommandUseCase;
import umc.plantory.domain.chat.service.ChatQueryUseCase;
import umc.plantory.global.apiPayload.ApiResponse;

import java.time.LocalDateTime;

/** 심현민 & 박형진 공동 작업 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/plantory/chats")
@Tag(name = "Chat", description = "채팅 관련 API")
public class ChatRestController {

    private final ChatQueryUseCase chatQueryUseCase;
    private final ChatCommandUseCase chatCommandUseCase;

    @Operation(
            summary = "챗봇 채팅 요청",
            description = "사용자가 챗봇에게 메시지를 보내면, 챗봇의 답변을 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHAT4001", description = "API 키가 잘못됐습니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHAT4002", description = "API 쿼터가 모두 소진되었습니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHAT4003", description = "OPENAI 서버 오류", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHAT4004", description = "서버 오류", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHAT4005", description = "서버 과부하", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHAT4006", description = "챗봇 응답이 없습니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHAT4007", description = "챗봇 응답이 허용 길이(500자)를 초과했습니다", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ChatResponseDTO.ChatResponse>> chat (
            @RequestBody @Valid ChatRequestDTO request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        ChatResponseDTO.ChatResponse response = chatCommandUseCase.ask(authorization, request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @GetMapping
    @Operation(
            summary = "최초 진입 이후, 챗봇 채팅창 이전 채팅 스크롤 조회",
            description = "스크롤 시, 기준 시각(cursor) 이전의 채팅을 추가로 조회 "
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4001", description = "존재하지 않는 회원입니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PAGINATION4001", description = "페이지 크기는 1 이상이어야 합니다", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<ChatResponseDTO.ChatsResponse>> getChats(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(value = "cursor", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime cursor,
            @RequestParam(value = "size", defaultValue = "6") @Parameter(description = "페이지 크기 (1 이상)", schema = @Schema(minimum = "1")) int size
    ) {
        ChatResponseDTO.ChatsResponse chatList = chatQueryUseCase.findChatList(authorization, cursor, size);
        return ResponseEntity.ok(ApiResponse.onSuccess(chatList));
    }

    @DeleteMapping
    @Operation(
            summary = "채팅창 초기화",
            description = "지금까지의 채팅 내역을 초기화"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4001", description = "존재하지 않는 회원입니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ResponseEntity<ApiResponse<Void>> deleteChats(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        chatCommandUseCase.delete(authorization);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    };

    @GetMapping("/search")
    @Operation(
            summary = "채팅 검색",
            description = "입력한 단어의 채팅 검색")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4001", description = "존재하지 않는 회원입니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHAT4008", description = "해당 키워드를 포함하는 채팅이 존재하지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ResponseEntity<ApiResponse<ChatResponseDTO.ChatIdsResponse>> searchChatIdsByKeyword(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(value = "keyword") String keyword
    ) {
        ChatResponseDTO.ChatIdsResponse result = chatQueryUseCase.searchChatIdsByKeyword(authorization, keyword);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }
}
