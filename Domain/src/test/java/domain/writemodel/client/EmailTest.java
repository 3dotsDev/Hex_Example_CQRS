package domain.writemodel.client;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class EmailTest {
    private final Email.EmailISpecification specification = new Email.EmailISpecification();

    private static Stream<Arguments> emails() {
        return Stream.of(
                Arguments.of("email@example.com", true),
                Arguments.of("firstname.lastname@example.com", true),
                Arguments.of("email@subdomain.example.com", true),
                Arguments.of("firstname+lastname@example.com", true),
                Arguments.of("emailexample", false),
                Arguments.of("email@example", false),
                Arguments.of("email@-example.com", false),
                Arguments.of("email@example.web", false),
                Arguments.of("email@111.222.333.4444", false),
                Arguments.of("email@example..com", false),
                Arguments.of("abc..123@example.com", false)
        );
    }

    @MethodSource("emails")
    @ParameterizedTest
    void validate(String value, boolean isValid) {

        assertThat(specification.isSatisfiedBy(value), equalTo(isValid));
    }
}