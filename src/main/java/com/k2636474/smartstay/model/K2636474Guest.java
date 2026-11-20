package com.k2636474.smartstay.model;

import com.k2636474.smartstay.exception.K2636474ValidationException;
import com.k2636474.smartstay.model.enums.K2636474IdType;
import com.k2636474.smartstay.model.enums.K2636474UserRole;
import com.k2636474.smartstay.util.K2636474Validator;

/**
 * A registered hotel guest, identified by a Sri Lankan NIC or a passport.
 */
public class K2636474Guest extends K2636474User {

    private K2636474IdType idType;
    private String idNumber;

    /**
     * Creates a guest after validating every field.
     *
     * @param userId        unique guest ID (e.g. G0001)
     * @param idType        NIC or PASSPORT
     * @param idNumber      NIC or passport number matching {@code idType}
     * @param fullName      full name
     * @param contactNumber phone number
     * @param email         e-mail address (also the login)
     * @param rawPassword   plain password, hashed immediately
     * @throws K2636474ValidationException if any field is invalid
     */
    public K2636474Guest(String userId, K2636474IdType idType, String idNumber, String fullName,
                         String contactNumber, String email, String rawPassword) {
        super(userId, fullName, contactNumber, email, rawPassword);
        setIdentification(idType, idNumber);
    }

    @Override
    public K2636474UserRole getRole() {
        return K2636474UserRole.GUEST;
    }

    /**
     * Sets the identity document. Type and number are set together because the
     * valid format of the number depends on the type.
     *
     * @param idType   NIC or PASSPORT
     * @param idNumber the document number
     * @throws K2636474ValidationException if the number does not match the type's format
     */
    public final void setIdentification(K2636474IdType idType, String idNumber) {
        String validNumber = K2636474Validator.requireIdNumber("idNumber", idType, idNumber);
        this.idType = idType;
        this.idNumber = validNumber;
    }

    /**
     * Returns the identity document type.
     *
     * @return NIC or PASSPORT
     */
    public K2636474IdType getIdType() {
        return idType;
    }

    /**
     * Returns the identity document number in upper case.
     *
     * @return NIC or passport number
     */
    public String getIdNumber() {
        return idNumber;
    }
}
