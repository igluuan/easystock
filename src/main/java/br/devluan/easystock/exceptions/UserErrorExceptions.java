package br.devluan.easystock.exceptions;

public class UserErrorExceptions extends RuntimeException {
    public UserErrorExceptions(String message) {
        super(message);
    }
}
