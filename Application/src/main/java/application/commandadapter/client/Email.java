package application.commandadapter.client;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation Interface fuer Emailfelder -> nutzt emailSpezification
 */
@NotNull
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Email.EmailValidator.class)
public @interface Email {

    String message() default "not a well-formed email address";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class EmailValidator implements ConstraintValidator<Email, String> {

        private domain.writemodel.client.Email.EmailISpecification emailSpecification = new domain.writemodel.client.Email.EmailISpecification();

        @Override
        public void initialize(Email constraintAnnotation) {
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return emailSpecification.isSatisfiedBy(value);
        }
    }
}
