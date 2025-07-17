package umc.plantory.global.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import umc.plantory.global.validation.validator.EnumValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EnumValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEnum {
    Class<? extends Enum<?>> enumClass();
    String message() default "올바르지 않은 Enum 값입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}