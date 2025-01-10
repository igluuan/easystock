package br.devluan.easystock.application.exceptions.ProductExceptions;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
