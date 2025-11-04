package umc.plantory.domain.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import umc.plantory.domain.event.entity.S3DeleteEvent;
import umc.plantory.domain.image.service.ImageUseCase;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3DeleteEventListener {
    private final ImageUseCase imageUseCase;

    // 트랜잭션 "성공적으로 커밋"된 후 호출
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(S3DeleteEvent event) {
        for (String imgUrl : event.getUrlList()) {
            try {
                imageUseCase.validateImageExistence(imgUrl);
                imageUseCase.deleteImage(imgUrl);
            } catch (Exception e) {
                log.error("S3 Img Delete Fail. | imgUrl : {}", imgUrl);
                continue;
            }
        }
    }

}
