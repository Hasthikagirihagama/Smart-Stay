package com.k2636474.smartstay.exception;

/**
 * Thrown when a room, booking or user cannot be found by its identifier.
 */
public class K2636474NotFoundException extends RuntimeException {

    /**
     * Creates a not-found error.
     *
     * @param message user-facing explanation, e.g. "Room R099 does not exist."
     */
    public K2636474NotFoundException(String message) {
        super(message);
    }
}
