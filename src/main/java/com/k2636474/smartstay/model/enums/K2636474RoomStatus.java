package com.k2636474.smartstay.model.enums;

/**
 * Operational status of a room, controlled by the administrator.
 * Only {@link #AVAILABLE} rooms can be booked; date clashes are handled separately.
 */
public enum K2636474RoomStatus {
    AVAILABLE("Available"),
    RESERVED("Reserved"),
    UNDER_MAINTENANCE("Under Maintenance");

    private final String displayName;

    K2636474RoomStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-friendly name shown in the user interface.
     *
     * @return display name such as "Under Maintenance"
     */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
