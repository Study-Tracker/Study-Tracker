package io.studytracker.config.properties;

import io.studytracker.config.properties.ConfigurationModeConstraint.ConfigurationModeValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ConfigurationModeValidator.class})
public @interface ConfigurationModeConstraint {

  String[] options();
  boolean allowEmpty() default true;
  String message() default "Invalid configuration mode. Check the documentation for valid options.";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};

  class ConfigurationModeValidator implements ConstraintValidator<ConfigurationModeConstraint, String> {

    private String[] options;
    private boolean allowEmpty;

    @Override
    public void initialize(ConfigurationModeConstraint constraintAnnotation) {
      this.options = constraintAnnotation.options();
      this.allowEmpty = constraintAnnotation.allowEmpty();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
      boolean valid = false;
      if ((s == null || s.isEmpty())) {
        valid = allowEmpty;
      }
      for (String option : options) {
        if (option.equalsIgnoreCase(s.toLowerCase())) {
          valid = true;
          break;
        }
      }
      if (!valid) {
        context.buildConstraintViolationWithTemplate("Invalid configuration mode. Check the documentation for valid options.")
            .addConstraintViolation();
      }
      return valid;
    }
  }

}
