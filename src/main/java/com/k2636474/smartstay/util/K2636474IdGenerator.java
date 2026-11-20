package com.k2636474.smartstay.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Generates sequential, zero-padded identifiers per prefix (R001, BK0001, INV0001, G0001).
 * Counters only ever go up, so an ID is never reused even after the item is deleted.
 */
public class K2636474IdGenerator {

    public static final String ROOM_PREFIX = "R";
    public static final String BOOKING_PREFIX = "BK";
    public static final String INVOICE_PREFIX = "INV";
    public static final String GUEST_PREFIX = "G";
    public static final String ADMIN_PREFIX = "A";

    private static final int ROOM_DIGITS = 3;
    private static final int DEFAULT_DIGITS = 4;

    /** Last number issued for each prefix. HashMap gives O(1) lookup by prefix. */
    private final Map<String, Integer> lastIssued = new HashMap<>();

    /**
     * Issues the next ID for a prefix.
     *
     * @param prefix the ID prefix, e.g. "BK"
     * @param digits how many digits to zero-pad the number to
     * @return the new ID, e.g. "BK0007"
     */
    public synchronized String next(String prefix, int digits) {
        // merge() increments atomically per prefix; starting value is 1 for a new prefix.
        int number = lastIssued.merge(prefix, 1, Integer::sum);
        return prefix + String.format("%0" + digits + "d", number);
    }

    /**
     * Issues the next room ID.
     *
     * @return ID such as "R013"
     */
    public String nextRoomId() {
        return next(ROOM_PREFIX, ROOM_DIGITS);
    }

    /**
     * Issues the next booking ID.
     *
     * @return ID such as "BK0006"
     */
    public String nextBookingId() {
        return next(BOOKING_PREFIX, DEFAULT_DIGITS);
    }

    /**
     * Issues the next invoice ID.
     *
     * @return ID such as "INV0006"
     */
    public String nextInvoiceId() {
        return next(INVOICE_PREFIX, DEFAULT_DIGITS);
    }

    /**
     * Issues the next guest ID.
     *
     * @return ID such as "G0004"
     */
    public String nextGuestId() {
        return next(GUEST_PREFIX, DEFAULT_DIGITS);
    }

    /**
     * Issues the next administrator ID.
     *
     * @return ID such as "A0001"
     */
    public String nextAdminId() {
        return next(ADMIN_PREFIX, DEFAULT_DIGITS);
    }

    /**
     * Forgets every counter. Only used when the whole data store is reset.
     */
    public synchronized void reset() {
        lastIssued.clear();
    }
}
