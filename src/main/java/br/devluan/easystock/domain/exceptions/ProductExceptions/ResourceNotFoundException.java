package br.devluan.easystock.domain.exceptions.ProductExceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
