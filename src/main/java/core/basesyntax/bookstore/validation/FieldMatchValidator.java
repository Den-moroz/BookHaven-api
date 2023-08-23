package core.basesyntax.bookstore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String field1;
    private String field2;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        field1 = constraintAnnotation.first();
        field2 = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return false;
        }
        try {
            Class<?> clazz = value.getClass();
            Field firstField = clazz.getDeclaredField(field1);
            Field secondField = clazz.getDeclaredField(field2);

            firstField.setAccessible(true);
            secondField.setAccessible(true);

            Object firstValue = firstField.get(value);
            Object secondValue = secondField.get(value);

            return firstValue == null && secondValue == null
                    || firstValue != null && firstValue.equals(secondValue);
        } catch (Exception ignore) { }

        return false;
    }
}