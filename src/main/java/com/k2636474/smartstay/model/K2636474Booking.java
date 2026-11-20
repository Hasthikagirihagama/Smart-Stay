package com.k2636474.smartstay.model;

import com.k2636474.smartstay.exception.K2636474ValidationException;
import com.k2636474.smartstay.model.enums.K2636474BookingStatus;
import com.k2636474.smartstay.util.K2636474Validator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * A guest's reservation of one room for a date range. Holds the booking rule
 * constants and the date logic (nights, overlap, cut-off) that belongs to a single
 * booking; rules that need other bookings live in the booking service.
 */
public class K2636474Booking {

    /** Advance (LKR) recorded as paid when a booking is created. */
    public static final double ADVANCE_AMOUNT = 10_000;
    /** Check-in must be at least this many days after today. */
    public static final int MIN_ADVANCE_DAYS = 2;
    /** Modify/cancel is only allowed MORE than this many hours before check-in. */
    public static final int CHANGE_CUTOFF_HOURS = 24;
    /** Standard check-in time; the cut-off is measured to this time on the check-in date. */
    public static final LocalTime CHECK_IN_TIME = LocalTime.of(14, 0);
    /** Guests allowed above a room's base capacity. */
    public static final int MAX_EXTRA_GUESTS = 2;

    private final String bookingId;
    private final K2636474Guest guest;
    private final K2636474Room room;
    private final LocalDateTime createdAt;
    private final double advancePaid;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfGuests;
    private K2636474BookingStatus status;
    private boolean advanceRefunded;
    private K2636474Invoice invoice;

    /**
     * Creates a confirmed booking with the advance recorded as paid.
     *
     * @param bookingId      unique ID (e.g. BK0001)
     * @param guest          the guest who booked
     * @param room           the booked room
     * @param checkInDate    arrival date
     * @param checkOutDate   departure date, after {@code checkInDate}
     * @param numberOfGuests number of people staying, at least 1
     * @param createdAt      when the booking was made
     * @throws K2636474ValidationException if any argument is missing or out of range
     */
    public K2636474Booking(String bookingId, K2636474Guest guest, K2636474Room room, LocalDate checkInDate,
                           LocalDate checkOutDate, int numberOfGuests, LocalDateTime createdAt) {
        this.bookingId = K2636474Validator.requireNotNull("bookingId", bookingId, "Booking ID");
        this.guest = K2636474Validator.requireNotNull("guest", guest, "Guest");
        this.room = K2636474Validator.requireNotNull("room", room, "Room");
        this.createdAt = K2636474Validator.requireNotNull("createdAt", createdAt, "Creation time");
        this.advancePaid = ADVANCE_AMOUNT;
        this.status = K2636474BookingStatus.CONFIRMED;
        setStayDates(checkInDate, checkOutDate);
        setNumberOfGuests(numberOfGuests);
    }

    /**
     * Returns the number of nights between check-in and check-out.
     *
     * @return nights, at least 1
     */
    public long getNights() {
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    /**
     * Tests whether this booking's dates overlap another date range.
     * Ranges are half-open [in, out): the check-out day is free for the next
     * guest's check-in, so back-to-back stays do not clash.
     *
     * @param in  other range's check-in date
     * @param out other range's check-out date
     * @return true if the two stays share at least one night
     */
    public boolean overlaps(LocalDate in, LocalDate out) {
        // Two ranges overlap exactly when each one starts before the other ends.
        return in.isBefore(checkOutDate) && out.isAfter(checkInDate);
    }

    /**
     * Returns whether the booking currently holds its room.
     *
     * @return true when CONFIRMED
     */
    public boolean isActive() {
        return status == K2636474BookingStatus.CONFIRMED;
    }

    /**
     * Checks the 24-hour rule: changes are allowed only when the booking is active and
     * {@code now} is MORE than 24 hours before the check-in date-time.
     *
     * @param now the current date-time
     * @return true if the booking may still be modified or cancelled
     */
    public boolean canModifyOrCancel(LocalDateTime now) {
        LocalDateTime cutOff = getCheckInDateTime().minusHours(CHANGE_CUTOFF_HOURS);
        // isBefore is strict: exactly 24h before check-in is already too late.
        return isActive() && now.isBefore(cutOff);
    }

    /**
     * Returns the check-in date at the standard check-in time (14:00).
     *
     * @return check-in date-time
     */
    public LocalDateTime getCheckInDateTime() {
        return checkInDate.atTime(CHECK_IN_TIME);
    }

    /**
     * Returns the amount to show the guest after cancelling: the advance, if refunded.
     *
     * @return refunded amount in LKR, or 0
     */
    public double getRefundedAmount() {
        return advanceRefunded ? advancePaid : 0;
    }

    /**
     * Sets both stay dates together so the "check-out after check-in" rule can be checked.
     *
     * @param checkInDate  arrival date
     * @param checkOutDate departure date
     * @throws K2636474ValidationException if a date is missing or check-out is not after check-in
     */
    public final void setStayDates(LocalDate checkInDate, LocalDate checkOutDate) {
        K2636474Validator.requireNotNull("checkInDate", checkInDate, "Check-in date");
        K2636474Validator.requireNotNull("checkOutDate", checkOutDate, "Check-out date");
        if (!checkOutDate.isAfter(checkInDate)) {
            throw new K2636474ValidationException("checkOutDate", "Check-out must be after check-in.");
        }
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    /**
     * Sets the number of guests. The upper limit depends on the room and is checked by the service.
     *
     * @param numberOfGuests guests staying, at least 1
     * @throws K2636474ValidationException if less than 1
     */
    public final void setNumberOfGuests(int numberOfGuests) {
        if (numberOfGuests < 1) {
            throw new K2636474ValidationException("numberOfGuests", "At least one guest is required.");
        }
        this.numberOfGuests = numberOfGuests;
    }

    /**
     * Cancels the booking and marks the advance as refunded.
     */
    public void cancel() {
        this.status = K2636474BookingStatus.CANCELLED;
        this.advanceRefunded = true;
    }

    /**
     * Returns the booking ID.
     *
     * @return booking ID
     */
    public String getBookingId() {
        return bookingId;
    }

    /**
     * Returns the guest who made the booking.
     *
     * @return guest
     */
    public K2636474Guest getGuest() {
        return guest;
    }

    /**
     * Returns the booked room.
     *
     * @return room
     */
    public K2636474Room getRoom() {
        return room;
    }

    /**
     * Returns the check-in date.
     *
     * @return check-in date
     */
    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    /**
     * Returns the check-out date.
     *
     * @return check-out date
     */
    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    /**
     * Returns the number of guests.
     *
     * @return guests staying
     */
    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    /**
     * Returns the advance recorded as paid.
     *
     * @return advance in LKR
     */
    public double getAdvancePaid() {
        return advancePaid;
    }

    /**
     * Returns whether the advance has been refunded (after a valid cancellation).
     *
     * @return true if refunded
     */
    public boolean isAdvanceRefunded() {
        return advanceRefunded;
    }

    /**
     * Returns the booking status.
     *
     * @return status
     */
    public K2636474BookingStatus getStatus() {
        return status;
    }

    /**
     * Returns when the booking was created.
     *
     * @return creation time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns the current invoice (replaced whenever the booking is modified).
     *
     * @return invoice, or null before one has been generated
     */
    public K2636474Invoice getInvoice() {
        return invoice;
    }

    /**
     * Attaches a new invoice, replacing any previous one (one booking has one invoice).
     *
     * @param invoice the invoice generated for this booking
     * @throws K2636474ValidationException if the invoice is null or belongs to another booking
     */
    public void setInvoice(K2636474Invoice invoice) {
        K2636474Validator.requireNotNull("invoice", invoice, "Invoice");
        if (invoice.getBooking() != this) {
            throw new K2636474ValidationException("invoice", "Invoice belongs to a different booking.");
        }
        this.invoice = invoice;
    }

    @Override
    public String toString() {
        return bookingId + " · " + room.getRoomId() + " · " + checkInDate + " → " + checkOutDate;
    }
}
