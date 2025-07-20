package umc.plantory.domain.terrarium.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.global.apiPayload.ApiResponse;

@Repository
@RequiredArgsConstructor
public class TerrariumQueryController implements TerrariumQueryApi {

    @Override
    public ResponseEntity<ApiResponse<TerrariumResponseDto.TerrariumResponse>> getTerrariumData(
            @RequestParam("memberId") Long memberId) {

        return null;
    }
}
