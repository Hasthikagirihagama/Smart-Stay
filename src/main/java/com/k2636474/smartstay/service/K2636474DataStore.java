package com.k2636474.smartstay.service;

import com.k2636474.smartstay.model.K2636474Booking;
import com.k2636474.smartstay.model.K2636474Room;
import com.k2636474.smartstay.model.K2636474User;
import com.k2636474.smartstay.util.K2636474IdGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

/**
 * In-memory database for the whole application (Singleton: one shared store).
 * Each collection is chosen for the way it is queried; see the field comments.
 * Only services use this class; the UI never touches it directly.
 */
public final class K2636474DataStore {

    private static final K2636474DataStore INSTANCE = new K2636474DataStore();

    /** LinkedHashMap: O(1) lookup by room ID and keeps insertion order for display. */
    private final Map<String, K2636474Room> rooms = new LinkedHashMap<>();
    /** HashMap: O(1) lookup by booking ID. */
    private final Map<String, K2636474Booking> bookings = new HashMap<>();
    /** HashMap: O(1) login lookup by lower-cased e-mail (guests) or username (admins). */
    private final Map<String, K2636474User> usersByLogin = new HashMap<>();
    /** HashSet: O(1) duplicate check on NIC/passport numbers at registration. */
    private final Set<String> registeredIdNumbers = new HashSet<>();
    /** TreeMap: bookings kept sorted by check-in date, for by-date views and range queries. */
    private final NavigableMap<LocalDate, List<K2636474Booking>> bookingsByCheckIn = new TreeMap<>();
    private final K2636474IdGenerator idGenerator = new K2636474IdGenerator();

    private K2636474DataStore() {
    }

    /**
     * Returns the single shared store.
     *
     * @return the data store
     */
    public static K2636474DataStore getInstance() {
        return INSTANCE;
    }

    /**
     * Empties every collection and resets ID counters. Used by the unit tests so each
     * test starts from a clean store.
     */
    public synchronized void clear() {
        rooms.clear();
        bookings.clear();
        usersByLogin.clear();
        registeredIdNumbers.clear();
        bookingsByCheckIn.clear();
        idGenerator.reset();
    }

    /**
     * Returns the shared ID generator.
     *
     * @return ID generator
     */
    public K2636474IdGenerator getIdGenerator() {
        return idGenerator;
    }

    // ----- Rooms -----

    /**
     * Stores a room, replacing any with the same ID.
     *
     * @param room room to store
     */
    public synchronized void saveRoom(K2636474Room room) {
        rooms.put(room.getRoomId(), room);
    }

    /**
     * Deletes a room.
     *
     * @param roomId ID of the room to delete
     */
    public synchronized void deleteRoom(String roomId) {
        rooms.remove(roomId);
    }

    /**
     * Finds a room by ID.
     *
     * @param roomId room ID
     * @return the room, or empty if not found
     */
    public synchronized Optional<K2636474Room> findRoom(String roomId) {
        return Optional.ofNullable(rooms.get(roomId));
    }

    /**
     * Returns all rooms in the order they were added.
     *
     * @return read-only snapshot of all rooms
     */
    public synchronized List<K2636474Room> getRooms() {
        return List.copyOf(rooms.values());
    }

    // ----- Users -----

    /**
     * Stores a user under a login key (case-insensitive).
     *
     * @param loginKey e-mail for guests, username for admins
     * @param user     user to store
     */
    public synchronized void saveUser(String loginKey, K2636474User user) {
        usersByLogin.put(normaliseLogin(loginKey), user);
    }

    /**
     * Finds a user by login key (case-insensitive).
     *
     * @param loginKey e-mail or username
     * @return the user, or empty if none
     */
    public synchronized Optional<K2636474User> findUserByLogin(String loginKey) {
        return Optional.ofNullable(usersByLogin.get(normaliseLogin(loginKey)));
    }

    /**
     * Returns every registered user.
     *
     * @return read-only snapshot of all users
     */
    public synchronized Collection<K2636474User> getUsers() {
        return List.copyOf(usersByLogin.values());
    }

    /**
     * Records an identity number as used.
     *
     * @param idNumber NIC or passport number
     */
    public synchronized void registerIdNumber(String idNumber) {
        registeredIdNumbers.add(idNumber.toUpperCase(Locale.ROOT));
    }

    /**
     * Checks whether an identity number is already used.
     *
     * @param idNumber NIC or passport number
     * @return true if a guest already registered with it
     */
    public synchronized boolean isIdNumberRegistered(String idNumber) {
        return registeredIdNumbers.contains(idNumber.toUpperCase(Locale.ROOT));
    }

    private static String normaliseLogin(String loginKey) {
        return loginKey == null ? "" : loginKey.trim().toLowerCase(Locale.ROOT);
    }

    // ----- Bookings -----

    /**
     * Stores a new booking and adds it to the check-in date index.
     *
     * @param booking booking to store
     */
    public synchronized void saveBooking(K2636474Booking booking) {
        bookings.put(booking.getBookingId(), booking);
        bookingsByCheckIn.computeIfAbsent(booking.getCheckInDate(), date -> new ArrayList<>()).add(booking);
    }

    /**
     * Moves a booking in the check-in index after its check-in date changed, keeping
     * the TreeMap in sync with the booking.
     *
     * @param booking         the modified booking
     * @param previousCheckIn the check-in date it had before the change
     */
    public synchronized void reindexBooking(K2636474Booking booking, LocalDate previousCheckIn) {
        List<K2636474Booking> oldBucket = bookingsByCheckIn.get(previousCheckIn);
        if (oldBucket != null) {
            oldBucket.remove(booking);
            if (oldBucket.isEmpty()) {
                bookingsByCheckIn.remove(previousCheckIn);
            }
        }
        bookingsByCheckIn.computeIfAbsent(booking.getCheckInDate(), date -> new ArrayList<>()).add(booking);
    }

    /**
     * Finds a booking by ID.
     *
     * @param bookingId booking ID
     * @return the booking, or empty if not found
     */
    public synchronized Optional<K2636474Booking> findBooking(String bookingId) {
        return Optional.ofNullable(bookings.get(bookingId));
    }

    /**
     * Returns every booking sorted by check-in date (walks the TreeMap in key order).
     *
     * @return sorted list of bookings
     */
    public synchronized List<K2636474Booking> getBookingsSortedByCheckIn() {
        List<K2636474Booking> sorted = new ArrayList<>(bookings.size());
        bookingsByCheckIn.values().forEach(sorted::addAll);
        return sorted;
    }

    /**
     * Returns bookings that check in on one date. O(log n) TreeMap lookup.
     *
     * @param date check-in date
     * @return bookings checking in that day (possibly empty)
     */
    public synchronized List<K2636474Booking> getBookingsByCheckIn(LocalDate date) {
        return List.copyOf(bookingsByCheckIn.getOrDefault(date, Collections.emptyList()));
    }

    /**
     * Returns bookings whose check-in falls in an inclusive date range, using the
     * TreeMap's {@code subMap} view so only the matching keys are visited.
     *
     * @param from first check-in date (inclusive)
     * @param to   last check-in date (inclusive)
     * @return bookings in check-in order
     */
    public synchronized List<K2636474Booking> getBookingsByCheckInRange(LocalDate from, LocalDate to) {
        List<K2636474Booking> result = new ArrayList<>();
        bookingsByCheckIn.subMap(from, true, to, true).values().forEach(result::addAll);
        return result;
    }

    /**
     * Returns bookings that check in strictly before a date. Any booking that could
     * overlap a stay ending on {@code date} must start before it, so the overlap
     * check only needs to scan this {@code headMap} view instead of every booking.
     *
     * @param date exclusive upper bound on check-in date
     * @return bookings with check-in before {@code date}
     */
    public synchronized List<K2636474Booking> getBookingsCheckingInBefore(LocalDate date) {
        List<K2636474Booking> result = new ArrayList<>();
        bookingsByCheckIn.headMap(date, false).values().forEach(result::addAll);
        return result;
    }
}
