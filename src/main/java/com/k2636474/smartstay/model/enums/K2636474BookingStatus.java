package com.k2636474.smartstay.model.enums;

/**
 * Lifecycle state of a booking. Only {@link #CONFIRMED} bookings occupy a room.
 */
public enum K2636474BookingStatus {
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed");

    private final String displayName;

    K2636474BookingStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-friendly name shown in the user interface.
     *
     * @return display name such as "Cancelled"
     */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
