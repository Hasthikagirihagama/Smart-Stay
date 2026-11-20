package com.k2636474.smartstay.model;

import com.k2636474.smartstay.exception.K2636474ValidationException;
import com.k2636474.smartstay.model.enums.K2636474UserRole;
import com.k2636474.smartstay.util.K2636474Validator;

/**
 * Hotel administrator who manages rooms and views all reservations.
 * Logs in with a username instead of an e-mail address.
 */
public class K2636474Admin extends K2636474User {

    private String username;

    /**
     * Creates an administrator after validating every field.
     *
     * @param userId        unique admin ID (e.g. A0001)
     * @param username      login username
     * @param fullName      full name
     * @param contactNumber phone number
     * @param email         e-mail address
     * @param rawPassword   plain password, hashed immediately
     * @throws K2636474ValidationException if any field is invalid
     */
    public K2636474Admin(String userId, String username, String fullName, String contactNumber,
                         String email, String rawPassword) {
        super(userId, fullName, contactNumber, email, rawPassword);
        setUsername(username);
    }

    @Override
    public K2636474UserRole getRole() {
        return K2636474UserRole.ADMIN;
    }

    /**
     * Returns the login username.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the login username after validation.
     *
     * @param username new username
     * @throws K2636474ValidationException if not 3-20 letters, digits or underscores
     */
    public final void setUsername(String username) {
        this.username = K2636474Validator.requireUsername("username", username);
    }
}
