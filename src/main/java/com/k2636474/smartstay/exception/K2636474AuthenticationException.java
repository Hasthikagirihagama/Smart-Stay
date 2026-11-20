package com.k2636474.smartstay.exception;

/**
 * Thrown when a login attempt fails or an action needs a different logged-in user.
 */
public class K2636474AuthenticationException extends RuntimeException {

    /**
     * Creates an authentication error.
     *
     * @param message user-facing explanation of why access was refused
     */
    public K2636474AuthenticationException(String message) {
        super(message);
    }
}
