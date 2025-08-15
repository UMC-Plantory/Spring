package umc.plantory.global.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import umc.plantory.domain.diary.dto.DiaryRequestDTO;
import umc.plantory.global.validation.annotation.ValidDiaryFields;

import java.util.ArrayList;
import java.util.List;

@Component
public class DiaryFieldsValidator implements ConstraintValidator<ValidDiaryFields, DiaryRequestDTO.DiaryUploadDTO> {

    @Override
    public boolean isValid(DiaryRequestDTO.DiaryUploadDTO request, ConstraintValidatorContext context) {
        List<String> missingFields = new ArrayList<>();

        // 일반 저장일때만 체크
        if ("NORMAL".equals(request.getStatus())) {
            if (request.getEmotion() == null) missingFields.add("emotion");
            if (request.getContent() == null) missingFields.add("content");
            if (request.getSleepStartTime() == null) missingFields.add("sleepStartTime");
            if (request.getSleepEndTime() == null) missingFields.add("sleepEndTime");
        }

        if (!missingFields.isEmpty()) {
            context.disableDefaultConstraintViolation();
            for (String field : missingFields) {
                context.buildConstraintViolationWithTemplate(field + " 항목은 필수입니다.")
                        .addPropertyNode(field)
                        .addConstraintViolation();
            }
            return false;
        }

        return true;
    }
}