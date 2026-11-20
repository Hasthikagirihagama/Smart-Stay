package com.k2636474.smartstay.service;

import com.k2636474.smartstay.exception.K2636474AuthenticationException;
import com.k2636474.smartstay.exception.K2636474BookingException;
import com.k2636474.smartstay.model.K2636474Admin;
import com.k2636474.smartstay.model.K2636474Booking;
import com.k2636474.smartstay.model.K2636474Guest;
import com.k2636474.smartstay.model.K2636474Invoice;
import com.k2636474.smartstay.model.enums.K2636474BedType;
import com.k2636474.smartstay.model.enums.K2636474BookingStatus;
import com.k2636474.smartstay.model.enums.K2636474IdType;
import com.k2636474.smartstay.model.enums.K2636474RoomStatus;
import com.k2636474.smartstay.model.enums.K2636474RoomType;
import com.k2636474.smartstay.pricing.K2636474InvoiceCalculator;
import com.k2636474.smartstay.pricing.K2636474LongStayDiscountPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests every booking rule at its exact boundary using a fixed clock.
 */
class K2636474BookingServiceTest {

    private static final ZoneId ZONE = ZoneId.of("Asia/Colombo");
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 12, 1, 9, 0);
    private static final LocalDate TODAY = NOW.toLocalDate();

    private K2636474DataStore dataStore;
    private K2636474BookingService bookingService;
    private K2636474RoomService roomService;
    private K2636474Guest guest;
    private K2636474Guest otherGuest;
    private K2636474Admin admin;

    @BeforeEach
    void setUp() {
        dataStore = K2636474DataStore.getInstance();
        dataStore.clear();
        bookingService = bookingServiceAt(NOW);
        K2636474AuthService authService = new K2636474AuthService(dataStore);
        guest = authService.registerGuest(K2636474IdType.NIC, "912345678V", "Nimal Perera", "0771234567",
                "nimal@example.com", "guest123", "guest123");
        otherGuest = authService.registerGuest(K2636474IdType.PASSPORT, "N1234567", "Emma Clarke",
                "+447700900123", "emma@example.com", "guest123", "guest123");
        admin = authService.registerAdmin("admin", "Hotel Admin", "+94112345678", "admin@smartstay.lk", "admin123");
        roomService.addRoom(K2636474RoomType.STANDARD, K2636474BedType.DOUBLE, K2636474RoomStatus.AVAILABLE);
        roomService.addRoom(K2636474RoomType.DELUXE, K2636474BedType.KING, K2636474RoomStatus.AVAILABLE);
        roomService.addRoom(K2636474RoomType.SUITE, K2636474BedType.KING, K2636474RoomStatus.UNDER_MAINTENANCE);
        roomService.addRoom(K2636474RoomType.FAMILY_SUITE, K2636474BedType.KING, K2636474RoomStatus.RESERVED);
    }

    /** Builds services that share the store but see a different "now". */
    private K2636474BookingService bookingServiceAt(LocalDateTime now) {
        Clock clock = Clock.fixed(now.atZone(ZONE).toInstant(), ZONE);
        roomService = new K2636474RoomService(dataStore, clock);
        K2636474InvoiceService invoiceService = new K2636474InvoiceService(dataStore,
                new K2636474InvoiceCalculator(new K2636474LongStayDiscountPolicy()), clock);
        return new K2636474BookingService(dataStore, roomService, invoiceService, clock);
    }

    private K2636474Booking bookStandard(int daysAhead, int nights) {
        return bookingService.createBooking(guest, "R001", TODAY.plusDays(daysAhead),
                TODAY.plusDays(daysAhead + nights), 2);
    }

    @Test
    @DisplayName("Rule 5: check-in 1 day ahead is rejected")
    void bookingOneDayAheadIsRejected() {
        assertThrows(K2636474BookingException.class, () -> bookStandard(1, 2));
    }

    @Test
    @DisplayName("Rule 5: check-in exactly 2 days ahead is accepted")
    void bookingExactlyTwoDaysAheadIsAccepted() {
        K2636474Booking booking = bookStandard(2, 2);

        assertEquals("BK0001", booking.getBookingId());
        assertEquals(K2636474BookingStatus.CONFIRMED, booking.getStatus());
        assertEquals(K2636474Booking.ADVANCE_AMOUNT, booking.getAdvancePaid());
        assertEquals("INV0001", booking.getInvoice().getInvoiceId());
    }

    @Test
    @DisplayName("Rule 6: check-out on the check-in day is rejected")
    void zeroNightStayIsRejected() {
        assertThrows(K2636474BookingException.class,
                () -> bookingService.createBooking(guest, "R001", TODAY.plusDays(5), TODAY.plusDays(5), 1));
    }

    @Test
    @DisplayName("Rule 4: overlapping stay in the same room is rejected")
    void overlappingBookingIsRejected() {
        bookStandard(5, 3);

        assertThrows(K2636474BookingException.class,
                () -> bookingService.createBooking(otherGuest, "R001", TODAY.plusDays(7), TODAY.plusDays(9), 1));
    }

    @Test
    @DisplayName("Rule 4: checking in on another booking's check-out day is accepted")
    void checkOutDayIsFreeForNextCheckIn() {
        bookStandard(5, 3);

        K2636474Booking next = bookingService.createBooking(otherGuest, "R001", TODAY.plusDays(8),
                TODAY.plusDays(10), 1);

        assertTrue(next.isActive());
    }

    @Test
    @DisplayName("Rule 3: under-maintenance room cannot be booked")
    void maintenanceRoomIsRejected() {
        assertThrows(K2636474BookingException.class,
                () -> bookingService.createBooking(guest, "R003", TODAY.plusDays(5), TODAY.plusDays(6), 2));
    }

    @Test
    @DisplayName("Rule 3: reserved room cannot be booked")
    void reservedRoomIsRejected() {
        assertThrows(K2636474BookingException.class,
                () -> bookingService.createBooking(guest, "R004", TODAY.plusDays(5), TODAY.plusDays(6), 2));
    }

    @Test
    @DisplayName("Rule 7: base capacity + 2 is allowed, one more is rejected")
    void occupancyBoundary() {
        K2636474Booking full = bookingService.createBooking(guest, "R002", TODAY.plusDays(5),
                TODAY.plusDays(6), 4);
        assertEquals(4, full.getNumberOfGuests());

        assertThrows(K2636474BookingException.class,
                () -> bookingService.createBooking(guest, "R002", TODAY.plusDays(10), TODAY.plusDays(11), 5));
    }

    @Test
    @DisplayName("Rule 9: cancel 25 hours before check-in is accepted")
    void cancelAt25HoursIsAccepted() {
        K2636474Booking booking = bookStandard(5, 2);
        K2636474BookingService later = bookingServiceAt(booking.getCheckInDateTime().minusHours(25));

        K2636474Booking cancelled = later.cancelBooking(booking.getBookingId(), guest);

        assertEquals(K2636474BookingStatus.CANCELLED, cancelled.getStatus());
        assertEquals(K2636474Booking.ADVANCE_AMOUNT, cancelled.getRefundedAmount());
    }

    @Test
    @DisplayName("Rule 10: cancelling re-issues the invoice with no advance deducted and nothing payable")
    void cancellationRegeneratesInvoice() {
        K2636474Booking booking = bookStandard(5, 2);
        K2636474Invoice original = booking.getInvoice();

        bookingService.cancelBooking(booking.getBookingId(), guest);
        K2636474Invoice cancelled = booking.getInvoice();

        assertNotSame(original, cancelled);
        assertEquals("INV0002", cancelled.getInvoiceId());
        assertEquals(0, cancelled.getAdvanceDeducted());
        assertEquals(0, cancelled.getFinalPayable());
        String text = new K2636474InvoiceService(dataStore,
                new K2636474InvoiceCalculator(new K2636474LongStayDiscountPolicy()), Clock.systemDefaultZone())
                .formatInvoice(cancelled);
        assertTrue(text.contains("Cancelled"));
        assertTrue(text.contains("Booking cancelled - advance of LKR 10,000.00 refunded."));
    }

    @Test
    @DisplayName("Rule 9: cancel exactly 24 hours before check-in is rejected")
    void cancelAtExactly24HoursIsRejected() {
        K2636474Booking booking = bookStandard(5, 2);
        K2636474BookingService later = bookingServiceAt(booking.getCheckInDateTime().minusHours(24));

        assertThrows(K2636474BookingException.class, () -> later.cancelBooking(booking.getBookingId(), guest));
        assertTrue(booking.isActive());
    }

    @Test
    @DisplayName("Rule 9: a cancelled booking cannot be cancelled or modified again")
    void cancelledBookingIsFinal() {
        K2636474Booking booking = bookStandard(5, 2);
        bookingService.cancelBooking(booking.getBookingId(), guest);

        assertThrows(K2636474BookingException.class,
                () -> bookingService.cancelBooking(booking.getBookingId(), guest));
        assertThrows(K2636474BookingException.class, () -> bookingService.modifyBooking(
                booking.getBookingId(), guest, TODAY.plusDays(6), TODAY.plusDays(8), 2));
    }

    @Test
    @DisplayName("Rule 10: cancelling frees the room for the same dates")
    void cancellationFreesTheDates() {
        K2636474Booking booking = bookStandard(5, 2);
        bookingService.cancelBooking(booking.getBookingId(), guest);

        K2636474Booking rebooked = bookingService.createBooking(otherGuest, "R001", TODAY.plusDays(5),
                TODAY.plusDays(7), 2);

        assertTrue(rebooked.isActive());
    }

    @Test
    @DisplayName("Rule 11: modify may overlap its own old dates and regenerates the invoice")
    void modifyIgnoresOwnDatesAndReissuesInvoice() {
        K2636474Booking booking = bookStandard(5, 3);
        K2636474Invoice oldInvoice = booking.getInvoice();

        bookingService.modifyBooking(booking.getBookingId(), guest, TODAY.plusDays(6), TODAY.plusDays(11), 3);

        assertEquals(5, booking.getNights());
        assertEquals(3, booking.getNumberOfGuests());
        assertNotSame(oldInvoice, booking.getInvoice());
        assertEquals(0.12, booking.getInvoice().getDiscountRate(), 0.0001);
        assertEquals(List.of(booking), bookingService.getByCheckInDate(TODAY.plusDays(6)));
        assertTrue(bookingService.getByCheckInDate(TODAY.plusDays(5)).isEmpty());
    }

    @Test
    @DisplayName("Rule 11: modification re-runs the 2-day and clash rules")
    void modifyRerunsRules() {
        K2636474Booking first = bookStandard(5, 2);
        bookingService.createBooking(otherGuest, "R001", TODAY.plusDays(10), TODAY.plusDays(12), 1);

        assertThrows(K2636474BookingException.class, () -> bookingService.modifyBooking(
                first.getBookingId(), guest, TODAY.plusDays(1), TODAY.plusDays(3), 2));
        assertThrows(K2636474BookingException.class, () -> bookingService.modifyBooking(
                first.getBookingId(), guest, TODAY.plusDays(9), TODAY.plusDays(11), 2));
    }

    @Test
    @DisplayName("A guest cannot cancel another guest's booking")
    void guestCannotCancelSomeoneElsesBooking() {
        K2636474Booking booking = bookStandard(5, 2);

        assertThrows(K2636474AuthenticationException.class,
                () -> bookingService.cancelBooking(booking.getBookingId(), otherGuest));
    }

    @Test
    @DisplayName("Search: excludes booked and non-available rooms; null type means any")
    void searchAvailableFiltersRooms() {
        bookStandard(5, 2);

        List<String> ids = bookingService.findAvailableRooms(null, TODAY.plusDays(5), TODAY.plusDays(7), 2)
                .stream().map(room -> room.getRoomId()).toList();

        assertEquals(List.of("R002"), ids);
    }

    @Test
    @DisplayName("Privacy: guests only find their own reservations, admins find all")
    void reservationSearchRespectsPrivacy() {
        bookStandard(5, 2);
        bookingService.createBooking(otherGuest, "R002", TODAY.plusDays(5), TODAY.plusDays(7), 2);

        assertEquals(1, bookingService.searchReservations(otherGuest, "").size());
        assertTrue(bookingService.searchReservations(otherGuest, "nimal").isEmpty());
        assertEquals(1, bookingService.searchReservations(admin, "NIMAL").size());
        assertEquals(2, bookingService.searchReservations(admin, "").size());
    }

    @Test
    @DisplayName("Rooms with upcoming bookings cannot be removed")
    void roomWithUpcomingBookingCannotBeRemoved() {
        bookStandard(5, 2);

        assertThrows(K2636474BookingException.class, () -> roomService.removeRoom("R001"));
        roomService.removeRoom("R002");
        assertFalse(roomService.getAllRooms().stream().anyMatch(room -> room.getRoomId().equals("R002")));
        assertEquals("R005", roomService.addRoom(K2636474RoomType.DELUXE, K2636474BedType.KING,
                K2636474RoomStatus.AVAILABLE).getRoomId());
    }
}
