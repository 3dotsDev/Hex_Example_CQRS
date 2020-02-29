package domain.model.client;

import domain.model.ISpecification;
import domain.model.ValueObject;
import org.apache.commons.validator.routines.EmailValidator;

import static com.google.common.base.Preconditions.checkArgument;

public class Email extends ValueObject {
    private final String value;

    public Email(String value) {
        checkArgument(new EmailISpecification().isSatisfiedBy(value));
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static class EmailISpecification implements ISpecification<String> {
        @Override
        public boolean isSatisfiedBy(String value) {
            return EmailValidator.getInstance().isValid(value);
        }
    }
}
