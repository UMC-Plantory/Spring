package umc.plantory.domain.terrarium.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.global.apiPayload.ApiResponse;

public interface TerrariumQueryApi {
    ResponseEntity<ApiResponse<TerrariumResponseDto.TerrariumResponse>> getTerrariumData(@RequestParam Long memberId);
}
