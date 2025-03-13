/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.studytracker.config.properties;

import io.studytracker.config.properties.ConfigurationModeConstraint.ConfigurationModeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
      } else {
        for (String option : options) {
          if (option.equalsIgnoreCase(s)) {
            valid = true;
            break;
          }
        }
      }
      if (!valid) {
        context.buildConstraintViolationWithTemplate("Invalid configuration mode. Valid options are: "
                + String.join(", ", options)
                + (allowEmpty ? "  This field may also be null or empty." : ""))
            .addConstraintViolation();
      }
      return valid;
    }
  }

}
