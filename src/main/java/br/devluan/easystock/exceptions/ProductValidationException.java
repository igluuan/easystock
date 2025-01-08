package br.devluan.easystock.exceptions;

import java.util.List;

public class ProductValidationException extends RuntimeException {
    public ProductValidationException(String message, List<String> errors) {
        super(message);
    }

}
