package com.dutra.dsCatalog.dtos.exceptions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ValidationError extends CustomError{
    private List<FieldMessage> errors = new ArrayList<FieldMessage>();

    public ValidationError() {}
    public ValidationError(Instant timestamp, int status, String error, String path, String message) {
        super(timestamp, status, error, path);
    }

    public List<FieldMessage> getErrors() {
        return errors;
    }

    public void addError(String fieldName, String message) {
        errors.removeIf(x -> x.getFieldName().equals(fieldName));
        errors.add(new FieldMessage(fieldName, message));
    }
}
