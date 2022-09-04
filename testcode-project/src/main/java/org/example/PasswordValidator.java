package org.example;

public class PasswordValidator {

    public static final String WRONG_PASSWORD_LENGTH_EXCEPTION_MESSAGE = "비밀번호는 최소 9자 이상 15자 이하입니다.";

    public static void validate(String password) {
        int length = password.length();

        if(length < 9 || length > 15) {
            throw new IllegalArgumentException(WRONG_PASSWORD_LENGTH_EXCEPTION_MESSAGE);
        }

    }
}
