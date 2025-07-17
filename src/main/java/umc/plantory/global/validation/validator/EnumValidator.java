package umc.plantory.global.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import umc.plantory.global.validation.annotation.ValidEnum;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

    private ValidEnum annotation;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        Enum<?>[] enumConstants = this.annotation.enumClass().getEnumConstants();
        for (Enum<?> enumConstant : enumConstants) {
            if (value.equals(enumConstant.name())) {
                return true;
            }
        }
        return false;
    }
}