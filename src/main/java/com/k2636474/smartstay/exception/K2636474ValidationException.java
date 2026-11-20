package com.k2636474.smartstay.exception;

/**
 * Thrown when a single input field fails validation. Carries the field name so the
 * user interface can show the message next to the offending field.
 */
public class K2636474ValidationException extends RuntimeException {

    private final String fieldName;

    /**
     * Creates a validation error for one field.
     *
     * @param fieldName logical name of the invalid field (e.g. "email")
     * @param message   user-facing explanation of what is wrong
     */
    public K2636474ValidationException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    /**
     * Returns the logical name of the field that failed validation.
     *
     * @return field name such as "email" or "idNumber"
     */
    public String getFieldName() {
        return fieldName;
    }
}
