package com.k2636474.smartstay.model.enums;

/**
 * The four room categories offered by SmartStay. Each type maps to exactly one
 * shared {@code K2636474RoomPackage} that holds its prices and capacity.
 */
public enum K2636474RoomType {
    STANDARD("Standard"),
    DELUXE("Deluxe"),
    SUITE("Suite"),
    FAMILY_SUITE("Family Suite");

    private final String displayName;

    K2636474RoomType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-friendly name shown in the user interface.
     *
     * @return display name such as "Family Suite"
     */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
