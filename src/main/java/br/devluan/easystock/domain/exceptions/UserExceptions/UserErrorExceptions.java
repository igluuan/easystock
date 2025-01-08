package br.devluan.easystock.domain.exceptions.UserExceptions;

public class UserErrorExceptions extends RuntimeException {
    public UserErrorExceptions(String message) {
        super(message);
    }
}
