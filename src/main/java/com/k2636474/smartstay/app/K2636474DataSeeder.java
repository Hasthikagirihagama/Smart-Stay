package com.k2636474.smartstay.app;

import com.k2636474.smartstay.model.K2636474Admin;
import com.k2636474.smartstay.model.K2636474Booking;
import com.k2636474.smartstay.model.K2636474Guest;
import com.k2636474.smartstay.model.enums.K2636474BedType;
import com.k2636474.smartstay.model.enums.K2636474IdType;
import com.k2636474.smartstay.model.enums.K2636474RoomStatus;
import com.k2636474.smartstay.model.enums.K2636474RoomType;
import com.k2636474.smartstay.service.K2636474AuthService;
import com.k2636474.smartstay.service.K2636474BookingService;
import com.k2636474.smartstay.service.K2636474RoomService;

import java.time.LocalDate;

/**
 * Loads demonstration data at start-up so every feature can be tried immediately:
 * one admin, three guests, twelve rooms and five bookings dated relative to today.
 * Everything goes through the normal services, so seed data obeys the same rules.
 */
public class K2636474DataSeeder {

    /** Password shared by the demo guest accounts. */
    public static final String GUEST_PASSWORD = "guest123";
    /** Demo administrator username. */
    public static final String ADMIN_USERNAME = "admin";
    /** Demo administrator password. */
    public static final String ADMIN_PASSWORD = "admin123";

    private final K2636474AuthService authService;
    private final K2636474RoomService roomService;
    private final K2636474BookingService bookingService;

    /**
     * Creates a seeder that writes through the given services.
     *
     * @param authService    service used to create accounts
     * @param roomService    service used to create rooms
     * @param bookingService service used to create bookings
     */
    public K2636474DataSeeder(K2636474AuthService authService, K2636474RoomService roomService,
                              K2636474BookingService bookingService) {
        this.authService = authService;
        this.roomService = roomService;
        this.bookingService = bookingService;
    }

    /**
     * Seeds accounts, rooms and bookings.
     *
     * @param today the current date; booking dates are placed relative to it
     */
    public void seed(LocalDate today) {
        K2636474Admin admin = authService.registerAdmin(ADMIN_USERNAME, "Hotel Administrator", "+94112345678",
                "admin@smartstay.lk", ADMIN_PASSWORD);
        K2636474Guest nimal = authService.registerGuest(K2636474IdType.NIC, "912345678V", "Nimal Perera",
                "0771234567", "nimal@example.com", GUEST_PASSWORD, GUEST_PASSWORD);
        K2636474Guest kamala = authService.registerGuest(K2636474IdType.NIC, "199512345678", "Kamala Silva",
                "+94712345678", "kamala@example.com", GUEST_PASSWORD, GUEST_PASSWORD);
        K2636474Guest emma = authService.registerGuest(K2636474IdType.PASSPORT, "N1234567", "Emma Clarke",
                "+447700900123", "emma@example.com", GUEST_PASSWORD, GUEST_PASSWORD);

        seedRooms();

        // Seeded bookings use recordExistingBooking, which skips only the "2 days in advance"
        // rule: they represent reservations made earlier, e.g. the one starting tomorrow.
        bookingService.recordExistingBooking(nimal, "R001", today.plusDays(1), today.plusDays(3), 2);
        bookingService.recordExistingBooking(kamala, "R004", today.plusDays(7), today.plusDays(13), 4);
        bookingService.recordExistingBooking(emma, "R010", today.plusDays(4), today.plusDays(7), 6);
        K2636474Booking cancelled = bookingService.recordExistingBooking(nimal, "R007",
                today.plusDays(9), today.plusDays(11), 2);
        bookingService.cancelBooking(cancelled.getBookingId(), admin);
        bookingService.recordExistingBooking(emma, "R002", today.plusDays(60), today.plusDays(63), 2);
    }

    private void seedRooms() {
        roomService.addRoom(K2636474RoomType.STANDARD, K2636474BedType.SINGLE, K2636474RoomStatus.AVAILABLE);
        roomService.addRoom(K2636474RoomType.STANDARD, K2636474BedType.DOUBLE, K2636474RoomStatus.AVAILABLE);
        roomService.addRoom(K2636474RoomType.STANDARD, K2636474BedType.QUEEN, K2636474RoomStatus.AVAILABLE);
        roomService.addRoom(K2636474RoomType.DELUXE, K2636474BedType.DOUBLE, K2636474RoomStatus.AVAILABLE);
        roomService.addRoom(K2636474RoomType.DELUXE, K2636474BedType.QUEEN, K2636474RoomStatus.AVAILABLE);
        roomService.addRoom(K2636474RoomType.DELUXE, K2636474BedType.KING, K2636474RoomStatus.AVAILABLE);
        roomService.addRoom(K2636474RoomType.SUITE, K2636474BedType.QUEEN, K2636474RoomStatus.AVAILABLE);
        roomService.addRoom(K2636474RoomType.SUITE, K2636474BedType.KING, K2636474RoomStatus.AVAILABLE);
        roomService.addRoom(K2636474RoomType.SUITE, K2636474BedType.KING, K2636474RoomStatus.UNDER_MAINTENANCE);
        roomService.addRoom(K2636474RoomType.FAMILY_SUITE, K2636474BedType.KING, K2636474RoomStatus.AVAILABLE);
        roomService.addRoom(K2636474RoomType.FAMILY_SUITE, K2636474BedType.QUEEN, K2636474RoomStatus.RESERVED);
        roomService.addRoom(K2636474RoomType.FAMILY_SUITE, K2636474BedType.KING, K2636474RoomStatus.AVAILABLE);
    }
}
