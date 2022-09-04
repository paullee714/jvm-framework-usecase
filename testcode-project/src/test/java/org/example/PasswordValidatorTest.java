package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * 비밀번호는 최소 9자 이상 15자 이하
 * 비밀번호가 9자 미만 또는 15자 초과인경우 Exception ㅂ라생
 * 경계조건 확인
 */
public class PasswordValidatorTest {

    @DisplayName("비밀번호가 최소 9자 이상 15자 이하면 정상") // 테스트 의도
    @Test
    void validatePasswordTest() {
        assertThatCode(() -> PasswordValidator.validate("123456789"))
                .doesNotThrowAnyException();
    }

    @DisplayName("비밀번호가 9자 미만인 경우 Exception 발생")
    @Test
    void validatePasswordShortExceptionTest() {
        assertThatCode(() -> PasswordValidator.validate("1234"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(PasswordValidator.WRONG_PASSWORD_LENGTH_EXCEPTION_MESSAGE);
    }

    @DisplayName("비밀번호가 15자 초과인 경우 Exception 발생")
    @Test
    void validatePasswordLongExceptionTest() {
        assertThatCode(() -> PasswordValidator.validate("1234512345123456"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(PasswordValidator.WRONG_PASSWORD_LENGTH_EXCEPTION_MESSAGE);
    }

    @DisplayName("경계조건에 대해 테스트")
    @ParameterizedTest
    @ValueSource(strings = {"12345678", "1234567890123456"})
    void validatePasswordBoundaryTest(String password) {
        assertThatCode(() -> PasswordValidator.validate(password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(PasswordValidator.WRONG_PASSWORD_LENGTH_EXCEPTION_MESSAGE);

    }
}
