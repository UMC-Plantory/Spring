package umc.plantory.domain.push.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.plantory.domain.push.dto.PushRequestDTO;
import umc.plantory.domain.push.service.PushQueryUseCase;

@RestController
@RequestMapping("/v1/plantory/push")
@RequiredArgsConstructor
public class PushRestController {
    private final PushQueryUseCase pushQueryService;

    @PostMapping("/test")
    public String alertTest(@RequestBody PushRequestDTO.PushNotificationTestRequest request) throws Exception {
        return pushQueryService.sendIosAlertTest(request);
    }
}
