package com.k2636474.smartstay.model;

import com.k2636474.smartstay.exception.K2636474ValidationException;
import com.k2636474.smartstay.model.enums.K2636474UserRole;
import com.k2636474.smartstay.util.K2636474Validator;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Abstract base for everyone who can log in. Holds the shared contact details and a
 * SHA-256 password hash (the plain password is never stored). Every setter validates
 * its input, so an invalid user object can never exist.
 */
public abstract class K2636474User {

    private static final String HASH_ALGORITHM = "SHA-256";

    private final String userId;
    private String fullName;
    private String contactNumber;
    private String email;
    private String passwordHash;

    /**
     * Creates a user, validating every field through its setter.
     *
     * @param userId        unique system ID (e.g. G0001)
     * @param fullName      person's full name
     * @param contactNumber phone number
     * @param email         e-mail address
     * @param rawPassword   plain password, hashed immediately
     * @throws K2636474ValidationException if any field is invalid
     */
    protected K2636474User(String userId, String fullName, String contactNumber, String email, String rawPassword) {
        if (userId == null || userId.isBlank()) {
            throw new K2636474ValidationException("userId", "User ID is required.");
        }
        this.userId = userId;
        setFullName(fullName);
        setContactNumber(contactNumber);
        setEmail(email);
        setPassword(rawPassword);
    }

    /**
     * Returns the role of this user; implemented differently by each subclass.
     *
     * @return GUEST or ADMIN
     */
    public abstract K2636474UserRole getRole();

    /**
     * Checks a password attempt by comparing hashes.
     *
     * @param raw the password typed by the user
     * @return true if it matches the stored hash
     */
    public boolean checkPassword(String raw) {
        if (raw == null) {
            return false;
        }
        // Constant-time comparison so response time does not leak how much of the hash matched.
        return MessageDigest.isEqual(hash(raw).getBytes(StandardCharsets.UTF_8),
                passwordHash.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Replaces the password after validating it; only the hash is kept.
     *
     * @param rawPassword new plain password
     * @throws K2636474ValidationException if the password is too short
     */
    public final void setPassword(String rawPassword) {
        this.passwordHash = hash(K2636474Validator.requirePassword("password", rawPassword));
    }

    private static String hash(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            return HexFormat.of().formatHex(digest.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            // Every Java platform is required to ship SHA-256, so this cannot happen in practice.
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    /**
     * Returns the unique system ID.
     *
     * @return user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Returns the full name.
     *
     * @return full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full name after validation.
     *
     * @param fullName new name
     * @throws K2636474ValidationException if the name is invalid
     */
    public final void setFullName(String fullName) {
        this.fullName = K2636474Validator.requireName("fullName", fullName);
    }

    /**
     * Returns the contact phone number.
     *
     * @return phone number
     */
    public String getContactNumber() {
        return contactNumber;
    }

    /**
     * Sets the contact number after validation.
     *
     * @param contactNumber new phone number
     * @throws K2636474ValidationException if the number is invalid
     */
    public final void setContactNumber(String contactNumber) {
        this.contactNumber = K2636474Validator.requirePhone("contactNumber", contactNumber);
    }

    /**
     * Returns the e-mail address.
     *
     * @return e-mail
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the e-mail address after validation.
     *
     * @param email new e-mail
     * @throws K2636474ValidationException if the address is invalid
     */
    public final void setEmail(String email) {
        this.email = K2636474Validator.requireEmail("email", email);
    }

    /**
     * Returns the stored SHA-256 password hash (hex). Never the plain password.
     *
     * @return password hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public String toString() {
        return fullName + " (" + userId + ")";
    }
}
