package com.k2636474.smartstay.model.enums;

/**
 * Role of a logged-in user; decides which dashboard is shown and what data is visible.
 */
public enum K2636474UserRole {
    GUEST("Guest"),
    ADMIN("Administrator");

    private final String displayName;

    K2636474UserRole(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-friendly name shown in the user interface.
     *
     * @return display name such as "Administrator"
     */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
