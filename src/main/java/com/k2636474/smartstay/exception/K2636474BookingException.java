package com.k2636474.smartstay.exception;

/**
 * Thrown when a booking, modification or cancellation breaks a business rule
 * (advance days, date clash, occupancy, 24-hour cut-off, room status...).
 * The message is written to be shown directly to the user.
 */
public class K2636474BookingException extends RuntimeException {

    /**
     * Creates a booking rule violation.
     *
     * @param message user-facing explanation of the rule that was broken
     */
    public K2636474BookingException(String message) {
        super(message);
    }
}
