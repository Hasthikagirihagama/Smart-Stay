package com.k2636474.smartstay.service;

import com.k2636474.smartstay.exception.K2636474AuthenticationException;
import com.k2636474.smartstay.exception.K2636474ValidationException;
import com.k2636474.smartstay.model.K2636474Guest;
import com.k2636474.smartstay.model.enums.K2636474IdType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests registration uniqueness rules and login.
 */
class K2636474AuthServiceTest {

    private K2636474AuthService authService;
    private K2636474Guest guest;

    @BeforeEach
    void setUp() {
        K2636474DataStore dataStore = K2636474DataStore.getInstance();
        dataStore.clear();
        authService = new K2636474AuthService(dataStore);
        guest = authService.registerGuest(K2636474IdType.NIC, "912345678V", "Nimal Perera", "0771234567",
                "nimal@example.com", "guest123", "guest123");
    }

    @Test
    void passwordIsStoredAsHash() {
        assertNotEquals("guest123", guest.getPasswordHash());
        assertEquals(64, guest.getPasswordHash().length());
    }

    @Test
    void duplicateEmailIsRejectedIgnoringCase() {
        K2636474ValidationException error = assertThrows(K2636474ValidationException.class,
                () -> authService.registerGuest(K2636474IdType.PASSPORT, "N1234567", "Other Person",
                        "0777654321", "NIMAL@Example.com", "guest123", "guest123"));
        assertEquals("email", error.getFieldName());
    }

    @Test
    void duplicateNicIsRejectedIgnoringCase() {
        K2636474ValidationException error = assertThrows(K2636474ValidationException.class,
                () -> authService.registerGuest(K2636474IdType.NIC, "912345678v", "Other Person",
                        "0777654321", "other@example.com", "guest123", "guest123"));
        assertEquals("idNumber", error.getFieldName());
    }

    @Test
    void validationReportsEveryBadField() {
        Map<String, String> errors = authService.validateRegistration(K2636474IdType.NIC, "123", "",
                "abc", "bad", "123", "456");

        assertEquals(Set.of("idNumber", "fullName", "contactNumber", "email", "password", "confirmPassword"),
                errors.keySet());
    }

    @Test
    void loginWorksWithAnyEmailCase() {
        assertSame(guest, authService.login("Nimal@Example.com", "guest123"));
        assertSame(guest, authService.getCurrentUser());
    }

    @Test
    void wrongPasswordIsRejected() {
        assertThrows(K2636474AuthenticationException.class, () -> authService.login("nimal@example.com", "nope12"));
        authService.logout();
        assertTrue(authService.getCurrentUser() == null);
    }
}
