package br.devluan.easystock.domain.exceptions.ProductExceptions;

import java.util.List;

public class ProductValidationException extends RuntimeException {
    private final List<String> errors;

    public ProductValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
