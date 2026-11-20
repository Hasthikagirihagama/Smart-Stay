package com.k2636474.smartstay.service;

import com.k2636474.smartstay.exception.K2636474AuthenticationException;
import com.k2636474.smartstay.exception.K2636474ValidationException;
import com.k2636474.smartstay.model.K2636474Admin;
import com.k2636474.smartstay.model.K2636474Guest;
import com.k2636474.smartstay.model.K2636474User;
import com.k2636474.smartstay.model.enums.K2636474IdType;
import com.k2636474.smartstay.util.K2636474Validator;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registration, login and the current session.
 */
public class K2636474AuthService {

    private final K2636474DataStore dataStore;
    private K2636474User currentUser;

    /**
     * Creates the service.
     *
     * @param dataStore shared data store
     */
    public K2636474AuthService(K2636474DataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Checks every registration field at once, including uniqueness of e-mail and
     * NIC/passport, so the form can show all problems together.
     *
     * @param idType          NIC or PASSPORT
     * @param idNumber        document number
     * @param fullName        full name
     * @param contactNumber   phone number
     * @param email           e-mail address
     * @param password        password
     * @param confirmPassword password typed again
     * @return field name → error message, in form order; empty when everything is valid
     */
    public Map<String, String> validateRegistration(K2636474IdType idType, String idNumber, String fullName,
                                                    String contactNumber, String email, String password,
                                                    String confirmPassword) {
        Map<String, String> errors = new LinkedHashMap<>();
        collect(errors, () -> {
            String cleanId = K2636474Validator.requireIdNumber("idNumber", idType, idNumber);
            if (dataStore.isIdNumberRegistered(cleanId)) {
                throw new K2636474ValidationException("idNumber", "This " + idType.getDisplayName()
                        + " number is already registered.");
            }
        });
        collect(errors, () -> K2636474Validator.requireName("fullName", fullName));
        collect(errors, () -> K2636474Validator.requirePhone("contactNumber", contactNumber));
        collect(errors, () -> {
            String cleanEmail = K2636474Validator.requireEmail("email", email);
            // The login map is keyed by lower-case e-mail, so this check is case-insensitive.
            if (dataStore.findUserByLogin(cleanEmail).isPresent()) {
                throw new K2636474ValidationException("email", "An account with this email already exists.");
            }
        });
        collect(errors, () -> K2636474Validator.requirePassword("password", password));
        if (password != null && !password.equals(confirmPassword)) {
            errors.putIfAbsent("confirmPassword", "Passwords do not match.");
        }
        return errors;
    }

    private static void collect(Map<String, String> errors, Runnable check) {
        try {
            check.run();
        } catch (K2636474ValidationException e) {
            errors.putIfAbsent(e.getFieldName(), e.getMessage());
        }
    }

    /**
     * Registers a new guest account.
     *
     * @param idType          NIC or PASSPORT
     * @param idNumber        document number, must be unique
     * @param fullName        full name
     * @param contactNumber   phone number
     * @param email           e-mail address, must be unique (case-insensitive)
     * @param password        password
     * @param confirmPassword password typed again
     * @return the new guest
     * @throws K2636474ValidationException for the first invalid field
     */
    public K2636474Guest registerGuest(K2636474IdType idType, String idNumber, String fullName,
                                       String contactNumber, String email, String password,
                                       String confirmPassword) {
        Map<String, String> errors = validateRegistration(idType, idNumber, fullName, contactNumber,
                email, password, confirmPassword);
        if (!errors.isEmpty()) {
            Map.Entry<String, String> first = errors.entrySet().iterator().next();
            throw new K2636474ValidationException(first.getKey(), first.getValue());
        }
        K2636474Guest guest = new K2636474Guest(dataStore.getIdGenerator().nextGuestId(), idType, idNumber,
                fullName, contactNumber, email, password);
        dataStore.saveUser(guest.getEmail(), guest);
        dataStore.registerIdNumber(guest.getIdNumber());
        return guest;
    }

    /**
     * Creates an administrator account (used by the data seeder).
     *
     * @param username      login username, must be unique
     * @param fullName      full name
     * @param contactNumber phone number
     * @param email         e-mail address
     * @param password      password
     * @return the new administrator
     * @throws K2636474ValidationException if a field is invalid or the username is taken
     */
    public K2636474Admin registerAdmin(String username, String fullName, String contactNumber,
                                       String email, String password) {
        if (dataStore.findUserByLogin(username).isPresent()) {
            throw new K2636474ValidationException("username", "Username is already taken.");
        }
        K2636474Admin admin = new K2636474Admin(dataStore.getIdGenerator().nextAdminId(), username, fullName,
                contactNumber, email, password);
        dataStore.saveUser(admin.getUsername(), admin);
        return admin;
    }

    /**
     * Logs a user in with e-mail (guests) or username (admins).
     *
     * @param loginId  e-mail or username, case-insensitive
     * @param password password
     * @return the logged-in user
     * @throws K2636474AuthenticationException if the login or password is wrong
     */
    public K2636474User login(String loginId, String password) {
        if (loginId == null || loginId.isBlank() || password == null || password.isEmpty()) {
            throw new K2636474AuthenticationException("Enter your email/username and password.");
        }
        // Same message for unknown user and wrong password, so logins cannot be probed.
        K2636474User user = dataStore.findUserByLogin(loginId)
                .filter(candidate -> candidate.checkPassword(password))
                .orElseThrow(() -> new K2636474AuthenticationException("Incorrect email/username or password."));
        currentUser = user;
        return user;
    }

    /**
     * Ends the current session.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Returns the logged-in user.
     *
     * @return current user, or null when nobody is logged in
     */
    public K2636474User getCurrentUser() {
        return currentUser;
    }

    /**
     * Returns every registered guest ordered by guest ID.
     *
     * @return guests
     */
    public List<K2636474Guest> getAllGuests() {
        return dataStore.getUsers().stream()
                .filter(K2636474Guest.class::isInstance)
                .map(K2636474Guest.class::cast)
                .sorted(Comparator.comparing(K2636474Guest::getUserId))
                .toList();
    }
}
