package umc.plantory.global.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import umc.plantory.global.validation.validator.DiaryFieldsValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DiaryFieldsValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDiaryFields {

    String message() default "일기 필수 항목이 누락되었습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}