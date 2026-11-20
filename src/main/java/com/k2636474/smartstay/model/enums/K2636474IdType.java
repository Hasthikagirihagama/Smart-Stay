package com.k2636474.smartstay.model.enums;

/**
 * Identity document a guest registers with.
 */
public enum K2636474IdType {
    NIC("NIC"),
    PASSPORT("Passport");

    private final String displayName;

    K2636474IdType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-friendly name shown in the user interface.
     *
     * @return display name such as "Passport"
     */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
