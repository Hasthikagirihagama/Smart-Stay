package com.k2636474.smartstay.service;

import com.k2636474.smartstay.exception.K2636474AuthenticationException;
import com.k2636474.smartstay.exception.K2636474BookingException;
import com.k2636474.smartstay.exception.K2636474NotFoundException;
import com.k2636474.smartstay.model.K2636474Booking;
import com.k2636474.smartstay.model.K2636474Guest;
import com.k2636474.smartstay.model.K2636474Invoice;
import com.k2636474.smartstay.model.K2636474Room;
import com.k2636474.smartstay.model.K2636474User;
import com.k2636474.smartstay.model.enums.K2636474RoomType;
import com.k2636474.smartstay.model.enums.K2636474UserRole;
import com.k2636474.smartstay.util.K2636474Format;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * Creates, modifies, cancels and searches bookings. Every business rule is enforced
 * here; violations throw {@link K2636474BookingException} with a message ready to
 * show the user. "Now" comes from an injectable {@link Clock} so boundary cases
 * (exactly 2 days ahead, exactly 24 hours before check-in) can be unit tested.
 */
public class K2636474BookingService {

    /** ID given to unsaved draft bookings that are only priced for the live preview. */
    public static final String DRAFT_BOOKING_ID = "DRAFT";

    private final K2636474DataStore dataStore;
    private final K2636474RoomService roomService;
    private final K2636474InvoiceService invoiceService;
    private final Clock clock;

    /**
     * Creates the service.
     *
     * @param dataStore      shared data store
     * @param roomService    room service (availability checks)
     * @param invoiceService invoice service (billing)
     * @param clock          source of the current date and time
     */
    public K2636474BookingService(K2636474DataStore dataStore, K2636474RoomService roomService,
                                  K2636474InvoiceService invoiceService, Clock clock) {
        this.dataStore = dataStore;
        this.roomService = roomService;
        this.invoiceService = invoiceService;
        this.clock = clock;
    }

    /**
     * Searches bookable rooms after checking the stay dates against the booking rules,
     * so guests never see rooms for dates they could not book.
     *
     * @param type   room type, or null for any
     * @param in     check-in date
     * @param out    check-out date
     * @param guests number of guests
     * @return rooms that can be booked
     * @throws K2636474BookingException if the dates or guest count break a rule
     */
    public List<K2636474Room> findAvailableRooms(K2636474RoomType type, LocalDate in, LocalDate out, int guests) {
        validateStayDates(in, out);
        return roomService.searchAvailable(type, in, out, guests);
    }

    /**
     * Books a room for a guest, recording the LKR 10,000 advance and issuing an invoice.
     *
     * @param guest  the guest booking
     * @param roomId the room to book
     * @param in     check-in date (at least 2 days from today)
     * @param out    check-out date (after check-in)
     * @param guests number of guests (1 to the room's maximum)
     * @return the confirmed booking
     * @throws K2636474NotFoundException if the room does not exist
     * @throws K2636474BookingException  if any booking rule is broken
     */
    public K2636474Booking createBooking(K2636474Guest guest, String roomId, LocalDate in, LocalDate out,
                                         int guests) {
        K2636474Room room = roomService.findById(roomId);
        validateStayDates(in, out);
        validateRoomFor(room, in, out, guests, null);
        return store(guest, room, in, out, guests);
    }

    /**
     * Records a booking that already exists (seed data), skipping only the
     * "2 days in advance" rule because the booking was made in the past. Room status,
     * occupancy and date-clash rules still apply.
     *
     * @param guest  the guest
     * @param roomId the room
     * @param in     check-in date
     * @param out    check-out date
     * @param guests number of guests
     * @return the stored booking
     * @throws K2636474BookingException if the room, occupancy or dates are invalid
     */
    public K2636474Booking recordExistingBooking(K2636474Guest guest, String roomId, LocalDate in, LocalDate out,
                                                 int guests) {
        K2636474Room room = roomService.findById(roomId);
        validateRoomFor(room, in, out, guests, null);
        return store(guest, room, in, out, guests);
    }

    private K2636474Booking store(K2636474Guest guest, K2636474Room room, LocalDate in, LocalDate out,
                                  int guests) {
        K2636474Booking booking = new K2636474Booking(dataStore.getIdGenerator().nextBookingId(), guest, room,
                in, out, guests, LocalDateTime.now(clock));
        dataStore.saveBooking(booking);
        invoiceService.generateInvoice(booking);
        return booking;
    }

    /**
     * Changes the dates and/or guest count of a booking, re-checking every rule
     * (ignoring the booking's own dates in the clash check) and issuing a new invoice.
     *
     * @param bookingId booking to change
     * @param actor     user making the change (guests may only change their own)
     * @param newIn     new check-in date
     * @param newOut    new check-out date
     * @param newGuests new number of guests
     * @return the updated booking
     * @throws K2636474NotFoundException       if the booking does not exist
     * @throws K2636474AuthenticationException if a guest tries to change someone else's booking
     * @throws K2636474BookingException        if the cut-off or any booking rule is broken
     */
    public K2636474Booking modifyBooking(String bookingId, K2636474User actor, LocalDate newIn, LocalDate newOut,
                                         int newGuests) {
        K2636474Booking booking = findById(bookingId);
        requireChangeAllowed(booking, actor);
        validateStayDates(newIn, newOut);
        validateRoomFor(booking.getRoom(), newIn, newOut, newGuests, bookingId);

        LocalDate previousCheckIn = booking.getCheckInDate();
        booking.setStayDates(newIn, newOut);
        booking.setNumberOfGuests(newGuests);
        dataStore.reindexBooking(booking, previousCheckIn);
        invoiceService.generateInvoice(booking);
        return booking;
    }

    /**
     * Cancels a booking: it stops holding the room and the advance is marked refunded.
     * The booking stays in the store and index so it still shows in history.
     *
     * @param bookingId booking to cancel
     * @param actor     user cancelling (guests may only cancel their own)
     * @return the cancelled booking
     * @throws K2636474NotFoundException       if the booking does not exist
     * @throws K2636474AuthenticationException if a guest tries to cancel someone else's booking
     * @throws K2636474BookingException        if the booking is already closed or inside the 24-hour cut-off
     */
    public K2636474Booking cancelBooking(String bookingId, K2636474User actor) {
        K2636474Booking booking = findById(bookingId);
        requireChangeAllowed(booking, actor);
        booking.cancel();
        return booking;
    }

    /**
     * Prices a prospective stay without saving anything, for the live preview.
     *
     * @param guest  the guest
     * @param roomId the room
     * @param in     check-in date
     * @param out    check-out date
     * @param guests number of guests
     * @return preview invoice
     * @throws com.k2636474.smartstay.exception.K2636474ValidationException if dates or guests are invalid
     */
    public K2636474Invoice previewInvoice(K2636474Guest guest, String roomId, LocalDate in, LocalDate out,
                                          int guests) {
        K2636474Room room = roomService.findById(roomId);
        K2636474Booking draft = new K2636474Booking(DRAFT_BOOKING_ID, guest, room, in, out, guests,
                LocalDateTime.now(clock));
        return invoiceService.previewInvoice(draft);
    }

    /**
     * Explains why a booking cannot be changed, or returns null if it can.
     *
     * @param booking booking to check
     * @return user-facing reason, or null when modify/cancel is allowed now
     */
    public String whyCannotChange(K2636474Booking booking) {
        if (!booking.isActive()) {
            return "This booking is " + booking.getStatus().getDisplayName().toLowerCase(Locale.ROOT)
                    + " and can no longer be changed.";
        }
        if (!booking.canModifyOrCancel(LocalDateTime.now(clock))) {
            return "Changes are only allowed more than " + K2636474Booking.CHANGE_CUTOFF_HOURS
                    + " hours before check-in (" + K2636474Format.dateTime(booking.getCheckInDateTime()) + ").";
        }
        return null;
    }

    private void requireChangeAllowed(K2636474Booking booking, K2636474User actor) {
        if (actor == null) {
            throw new K2636474AuthenticationException("Please log in first.");
        }
        if (actor.getRole() == K2636474UserRole.GUEST && booking.getGuest() != actor) {
            throw new K2636474AuthenticationException("You can only change your own bookings.");
        }
        String reason = whyCannotChange(booking);
        if (reason != null) {
            throw new K2636474BookingException(reason);
        }
    }

    /**
     * Checks the date rules shared by search, booking and modification:
     * check-out after check-in, and check-in at least 2 days from today.
     *
     * @param in  check-in date
     * @param out check-out date
     * @throws K2636474BookingException if a rule is broken
     */
    public void validateStayDates(LocalDate in, LocalDate out) {
        if (in == null || out == null) {
            throw new K2636474BookingException("Choose both a check-in and a check-out date.");
        }
        if (!out.isAfter(in)) {
            throw new K2636474BookingException("Check-out must be at least one night after check-in.");
        }
        LocalDate earliest = earliestCheckIn();
        // Exactly today + 2 is allowed, so only dates strictly before it are rejected.
        if (in.isBefore(earliest)) {
            throw new K2636474BookingException("Bookings must be made at least " + K2636474Booking.MIN_ADVANCE_DAYS
                    + " days in advance. The earliest check-in is " + K2636474Format.friendlyDate(earliest) + ".");
        }
    }

    /**
     * Returns the earliest check-in date a new booking may use (today + 2 days).
     *
     * @return earliest allowed check-in
     */
    public LocalDate earliestCheckIn() {
        return LocalDate.now(clock).plusDays(K2636474Booking.MIN_ADVANCE_DAYS);
    }

    private void validateRoomFor(K2636474Room room, LocalDate in, LocalDate out, int guests,
                                 String excludeBookingId) {
        if (!room.isBookable()) {
            throw new K2636474BookingException("Room " + room.getRoomId() + " is "
                    + room.getStatus().getDisplayName().toLowerCase(Locale.ROOT) + " and cannot be booked.");
        }
        if (in == null || out == null || !out.isAfter(in)) {
            throw new K2636474BookingException("Check-out must be at least one night after check-in.");
        }
        if (guests < 1 || guests > room.getMaxOccupancy()) {
            throw new K2636474BookingException("Room " + room.getRoomId() + " (" + room.getRoomType()
                    + ") takes 1 to " + room.getMaxOccupancy() + " guests.");
        }
        if (!roomService.isRoomFreeFor(room, in, out, excludeBookingId)) {
            throw new K2636474BookingException("Room " + room.getRoomId()
                    + " is already booked for some of those nights. Try other dates or another room.");
        }
    }

    /**
     * Finds a booking by ID (case-insensitive).
     *
     * @param bookingId booking ID
     * @return the booking
     * @throws K2636474NotFoundException if no booking has that ID
     */
    public K2636474Booking findById(String bookingId) {
        String key = bookingId == null ? "" : bookingId.trim().toUpperCase(Locale.ROOT);
        return dataStore.findBooking(key)
                .orElseThrow(() -> new K2636474NotFoundException("Booking " + key + " does not exist."));
    }

    /**
     * Finds bookings whose guest name contains the text (case-insensitive), sorted by check-in.
     *
     * @param partial part of a guest's name
     * @return matching bookings
     */
    public List<K2636474Booking> searchByGuestName(String partial) {
        String needle = partial == null ? "" : partial.trim().toLowerCase(Locale.ROOT);
        // Source list is already in check-in order (TreeMap), and filtering keeps that order.
        return dataStore.getBookingsSortedByCheckIn().stream()
                .filter(booking -> booking.getGuest().getFullName().toLowerCase(Locale.ROOT).contains(needle))
                .toList();
    }

    /**
     * Searches by booking ID or guest name, limited to what the viewer may see
     * (guests: own bookings only; admins: all).
     *
     * @param viewer the logged-in user
     * @param query  booking ID or part of a guest name; blank returns everything visible
     * @return matching visible bookings sorted by check-in
     */
    public List<K2636474Booking> searchReservations(K2636474User viewer, String query) {
        String needle = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        return dataStore.getBookingsSortedByCheckIn().stream()
                .filter(booking -> isVisibleTo(booking, viewer))
                .filter(booking -> booking.getBookingId().toLowerCase(Locale.ROOT).contains(needle)
                        || booking.getGuest().getFullName().toLowerCase(Locale.ROOT).contains(needle))
                .toList();
    }

    /**
     * Returns bookings checking in on a date.
     *
     * @param date check-in date
     * @return bookings for that day
     */
    public List<K2636474Booking> getByCheckInDate(LocalDate date) {
        return dataStore.getBookingsByCheckIn(date);
    }

    /**
     * Returns bookings checking in on a date that the viewer may see.
     *
     * @param viewer the logged-in user
     * @param date   check-in date
     * @return visible bookings for that day
     */
    public List<K2636474Booking> getByCheckInDate(K2636474User viewer, LocalDate date) {
        return dataStore.getBookingsByCheckIn(date).stream()
                .filter(booking -> isVisibleTo(booking, viewer))
                .toList();
    }

    /**
     * Returns bookings whose check-in lies in an inclusive date range.
     *
     * @param from first date (inclusive)
     * @param to   last date (inclusive)
     * @return bookings in check-in order
     */
    public List<K2636474Booking> getBookingsInRange(LocalDate from, LocalDate to) {
        return dataStore.getBookingsByCheckInRange(from, to);
    }

    /**
     * Returns one guest's bookings sorted by check-in.
     *
     * @param guest the guest
     * @return the guest's bookings
     */
    public List<K2636474Booking> getBookingsForGuest(K2636474Guest guest) {
        return dataStore.getBookingsSortedByCheckIn().stream()
                .filter(booking -> booking.getGuest() == guest)
                .toList();
    }

    /**
     * Returns every booking sorted by check-in.
     *
     * @return all bookings
     */
    public List<K2636474Booking> getAllBookings() {
        return dataStore.getBookingsSortedByCheckIn();
    }

    /**
     * Counts confirmed bookings.
     *
     * @return number of active bookings
     */
    public long countActiveBookings() {
        return dataStore.getBookingsSortedByCheckIn().stream().filter(K2636474Booking::isActive).count();
    }

    /**
     * Counts confirmed bookings checking in within the next 7 days (today included).
     *
     * @return check-ins this week
     */
    public long countCheckInsThisWeek() {
        LocalDate today = LocalDate.now(clock);
        return getBookingsInRange(today, today.plusDays(6)).stream().filter(K2636474Booking::isActive).count();
    }

    private static boolean isVisibleTo(K2636474Booking booking, K2636474User viewer) {
        // Privacy rule: admins see everything, guests only their own reservations.
        return viewer != null && (viewer.getRole() == K2636474UserRole.ADMIN || booking.getGuest() == viewer);
    }
}
