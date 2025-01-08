package br.devluan.easystock.domain.exceptions.ProductExceptions;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
