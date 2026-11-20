package com.k2636474.smartstay.util;

import com.k2636474.smartstay.exception.K2636474ValidationException;
import com.k2636474.smartstay.model.enums.K2636474IdType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests the input validation rules and that failures name the field.
 */
class K2636474ValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {"912345678V", "912345678x", "199512345678"})
    void validNicsAreAccepted(String nic) {
        assertEquals(nic.toUpperCase(), K2636474Validator.requireIdNumber("idNumber", K2636474IdType.NIC, nic));
    }

    @ParameterizedTest
    @ValueSource(strings = {"91234567V", "912345678A", "19951234567", "1995123456789", "N1234567"})
    void invalidNicsAreRejected(String nic) {
        K2636474ValidationException error = assertThrows(K2636474ValidationException.class,
                () -> K2636474Validator.requireIdNumber("idNumber", K2636474IdType.NIC, nic));
        assertEquals("idNumber", error.getFieldName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"N12345", "N1234567", "AB1234567"})
    void validPassportsAreAccepted(String passport) {
        K2636474Validator.requireIdNumber("idNumber", K2636474IdType.PASSPORT, passport);
    }

    @ParameterizedTest
    @ValueSource(strings = {"N1234", "AB12345678", "N12-345"})
    void invalidPassportsAreRejected(String passport) {
        assertThrows(K2636474ValidationException.class,
                () -> K2636474Validator.requireIdNumber("idNumber", K2636474IdType.PASSPORT, passport));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0771234567", "+94771234567", "+447700900123", "077 123 4567"})
    void validPhonesAreAccepted(String phone) {
        K2636474Validator.requirePhone("contactNumber", phone);
    }

    @ParameterizedTest
    @ValueSource(strings = {"771234567", "0871234567", "+1234567", "phone"})
    void invalidPhonesAreRejected(String phone) {
        assertThrows(K2636474ValidationException.class, () -> K2636474Validator.requirePhone("contactNumber", phone));
    }

    @ParameterizedTest
    @ValueSource(strings = {"plainaddress", "a@b", "@example.com", "name@example."})
    void invalidEmailsAreRejected(String email) {
        assertThrows(K2636474ValidationException.class, () -> K2636474Validator.requireEmail("email", email));
    }

    @Test
    void passwordNeedsSixCharacters() {
        assertThrows(K2636474ValidationException.class, () -> K2636474Validator.requirePassword("password", "12345"));
        assertEquals("123456", K2636474Validator.requirePassword("password", "123456"));
    }

    @Test
    void nameRejectsDigits() {
        assertThrows(K2636474ValidationException.class, () -> K2636474Validator.requireName("fullName", "R2D2"));
        assertEquals("A. Perera", K2636474Validator.requireName("fullName", "  A. Perera "));
    }
}
