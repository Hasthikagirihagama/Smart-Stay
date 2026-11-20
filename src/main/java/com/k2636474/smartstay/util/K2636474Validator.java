package com.k2636474.smartstay.util;

import com.k2636474.smartstay.exception.K2636474ValidationException;
import com.k2636474.smartstay.model.enums.K2636474IdType;

import java.util.regex.Pattern;

/**
 * Central input validation. Every check trims the value, tests it against a named
 * rule and either returns the cleaned value or throws a
 * {@link K2636474ValidationException} naming the field, so the UI can highlight it.
 */
public final class K2636474Validator {

    /** Old Sri Lankan NIC: 9 digits followed by V or X (e.g. 912345678V). */
    public static final Pattern NIC_OLD_PATTERN = Pattern.compile("^[0-9]{9}[VvXx]$");
    /** New Sri Lankan NIC: 12 digits (e.g. 199512345678). */
    public static final Pattern NIC_NEW_PATTERN = Pattern.compile("^[0-9]{12}$");
    /** Passport number: 6 to 9 letters or digits. */
    public static final Pattern PASSPORT_PATTERN = Pattern.compile("^[A-Za-z0-9]{6,9}$");
    /** Sri Lankan mobile (07XXXXXXXX or +947XXXXXXXX) or any international number (+ and 8-15 digits). */
    public static final Pattern PHONE_PATTERN =
            Pattern.compile("^(?:07[0-9]{8}|\\+947[0-9]{8}|\\+[0-9]{8,15})$");
    /** Practical e-mail pattern: local part, @, domain with a 2+ letter top-level domain. */
    public static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    /** Person name: letters, spaces and dots, 2 to 60 characters. */
    public static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z .]{2,60}$");
    /** Admin username: 3 to 20 letters, digits or underscores. */
    public static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,20}$");
    /** Minimum password length. */
    public static final int MIN_PASSWORD_LENGTH = 6;

    private K2636474Validator() {
    }

    /**
     * Validates a person's full name.
     *
     * @param fieldName field name reported on failure
     * @param value     raw input
     * @return the trimmed name
     * @throws K2636474ValidationException if blank or not 2-60 letters/spaces/dots
     */
    public static String requireName(String fieldName, String value) {
        String trimmed = requireText(fieldName, value, "Full name");
        if (!NAME_PATTERN.matcher(trimmed).matches() || trimmed.chars().noneMatch(Character::isLetter)) {
            throw new K2636474ValidationException(fieldName,
                    "Name must be 2-60 characters: letters, spaces and dots only.");
        }
        return trimmed;
    }

    /**
     * Validates a contact phone number.
     *
     * @param fieldName field name reported on failure
     * @param value     raw input (spaces and dashes are ignored)
     * @return the number without spaces or dashes
     * @throws K2636474ValidationException if it is not a valid Sri Lankan or international number
     */
    public static String requirePhone(String fieldName, String value) {
        String cleaned = requireText(fieldName, value, "Contact number").replaceAll("[\\s-]", "");
        if (!PHONE_PATTERN.matcher(cleaned).matches()) {
            throw new K2636474ValidationException(fieldName,
                    "Enter a mobile like 0771234567, +94771234567 or an international number starting with +.");
        }
        return cleaned;
    }

    /**
     * Validates an e-mail address.
     *
     * @param fieldName field name reported on failure
     * @param value     raw input
     * @return the trimmed address
     * @throws K2636474ValidationException if the address is not well formed
     */
    public static String requireEmail(String fieldName, String value) {
        String trimmed = requireText(fieldName, value, "Email");
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new K2636474ValidationException(fieldName, "Enter a valid email such as name@example.com.");
        }
        return trimmed;
    }

    /**
     * Validates an identity number against the rule for its document type.
     *
     * @param fieldName field name reported on failure
     * @param idType    NIC or passport; decides which pattern applies
     * @param value     raw input
     * @return the number in upper case (so "v" and "V" are treated as the same NIC)
     * @throws K2636474ValidationException if the type is missing or the number does not match
     */
    public static String requireIdNumber(String fieldName, K2636474IdType idType, String value) {
        if (idType == null) {
            throw new K2636474ValidationException("idType", "Choose NIC or Passport.");
        }
        String trimmed = requireText(fieldName, value, idType.getDisplayName() + " number").toUpperCase();
        boolean valid = idType == K2636474IdType.NIC
                ? NIC_OLD_PATTERN.matcher(trimmed).matches() || NIC_NEW_PATTERN.matcher(trimmed).matches()
                : PASSPORT_PATTERN.matcher(trimmed).matches();
        if (!valid) {
            String hint = idType == K2636474IdType.NIC
                    ? "NIC must be 9 digits + V/X (e.g. 912345678V) or 12 digits."
                    : "Passport number must be 6-9 letters or digits.";
            throw new K2636474ValidationException(fieldName, hint);
        }
        return trimmed;
    }

    /**
     * Validates an administrator username.
     *
     * @param fieldName field name reported on failure
     * @param value     raw input
     * @return the trimmed username
     * @throws K2636474ValidationException if not 3-20 letters, digits or underscores
     */
    public static String requireUsername(String fieldName, String value) {
        String trimmed = requireText(fieldName, value, "Username");
        if (!USERNAME_PATTERN.matcher(trimmed).matches()) {
            throw new K2636474ValidationException(fieldName, "Username must be 3-20 letters, digits or underscores.");
        }
        return trimmed;
    }

    /**
     * Validates a new password.
     *
     * @param fieldName field name reported on failure
     * @param value     raw password (not trimmed: spaces are allowed characters)
     * @return the same password
     * @throws K2636474ValidationException if shorter than {@link #MIN_PASSWORD_LENGTH}
     */
    public static String requirePassword(String fieldName, String value) {
        if (value == null || value.length() < MIN_PASSWORD_LENGTH) {
            throw new K2636474ValidationException(fieldName, "Use at least " + MIN_PASSWORD_LENGTH + " characters.");
        }
        return value;
    }

    /**
     * Ensures a required value has been chosen.
     *
     * @param fieldName field name reported on failure
     * @param value     value to check
     * @param label     human name of the field used in the message
     * @param <T>       value type
     * @return the same value
     * @throws K2636474ValidationException if {@code value} is null
     */
    public static <T> T requireNotNull(String fieldName, T value, String label) {
        if (value == null) {
            throw new K2636474ValidationException(fieldName, label + " is required.");
        }
        return value;
    }

    private static String requireText(String fieldName, String value, String label) {
        if (value == null || value.isBlank()) {
            throw new K2636474ValidationException(fieldName, label + " is required.");
        }
        return value.trim();
    }
}
