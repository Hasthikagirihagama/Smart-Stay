package com.k2636474.smartstay.model.enums;

/**
 * Bed configuration of a room. It is descriptive only and does not affect price.
 */
public enum K2636474BedType {
    SINGLE("Single"),
    DOUBLE("Double"),
    QUEEN("Queen"),
    KING("King");

    private final String displayName;

    K2636474BedType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-friendly name shown in the user interface.
     *
     * @return display name such as "Queen"
     */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
