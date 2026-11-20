package com.k2636474.smartstay.pricing;

import com.k2636474.smartstay.model.K2636474Booking;
import com.k2636474.smartstay.model.K2636474Guest;
import com.k2636474.smartstay.model.K2636474Invoice;
import com.k2636474.smartstay.model.K2636474Room;
import com.k2636474.smartstay.model.enums.K2636474BedType;
import com.k2636474.smartstay.model.enums.K2636474IdType;
import com.k2636474.smartstay.model.enums.K2636474RoomStatus;
import com.k2636474.smartstay.model.enums.K2636474RoomType;
import com.k2636474.smartstay.model.room.K2636474RoomPackageFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests the bill calculation against the worked example and its boundaries.
 */
class K2636474InvoiceCalculatorTest {

    private static final double DELTA = 0.001;
    private static final LocalDate CHECK_IN = LocalDate.of(2026, 12, 10);
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 12, 1, 9, 0);

    private final K2636474InvoiceCalculator calculator =
            new K2636474InvoiceCalculator(new K2636474LongStayDiscountPolicy());
    private final K2636474Guest guest = new K2636474Guest("G0001", K2636474IdType.NIC, "912345678V",
            "Test Guest", "0771234567", "test@example.com", "secret1");

    private K2636474Invoice invoiceFor(K2636474RoomType type, int nights, int guests) {
        K2636474Room room = new K2636474Room("R001", K2636474RoomPackageFactory.getPackage(type),
                K2636474BedType.DOUBLE, K2636474RoomStatus.AVAILABLE);
        K2636474Booking booking = new K2636474Booking("BK0001", guest, room, CHECK_IN,
                CHECK_IN.plusDays(nights), guests, NOW);
        return calculator.calculate(booking, "INV0001", NOW);
    }

    @Test
    @DisplayName("Worked example: Deluxe, 6 nights, 4 guests")
    void workedExampleMatchesBrief() {
        K2636474Invoice invoice = invoiceFor(K2636474RoomType.DELUXE, 6, 4);

        assertEquals(6, invoice.getNights());
        assertEquals(54_000.00, invoice.getBaseCharge(), DELTA);
        assertEquals(2, invoice.getExtraGuests());
        assertEquals(18_000.00, invoice.getExtraGuestCharge(), DELTA);
        assertEquals(0.12, invoice.getDiscountRate(), DELTA);
        assertEquals(6_480.00, invoice.getDiscountAmount(), DELTA);
        assertEquals(65_520.00, invoice.getTaxableAmount(), DELTA);
        assertEquals(7_862.40, invoice.getTaxAmount(), DELTA);
        assertEquals(73_382.40, invoice.getGrossTotal(), DELTA);
        assertEquals(10_000.00, invoice.getAdvanceDeducted(), DELTA);
        assertEquals(63_382.40, invoice.getFinalPayable(), DELTA);
    }

    @Test
    @DisplayName("4 nights: no discount (one below the threshold)")
    void fourNightsGetsNoDiscount() {
        K2636474Invoice invoice = invoiceFor(K2636474RoomType.STANDARD, 4, 2);

        assertEquals(0, invoice.getDiscountRate(), DELTA);
        assertEquals(0, invoice.getDiscountAmount(), DELTA);
        assertEquals(24_000.00, invoice.getTaxableAmount(), DELTA);
    }

    @Test
    @DisplayName("5 nights: 12% discount (exactly on the threshold)")
    void fiveNightsGetsDiscount() {
        K2636474Invoice invoice = invoiceFor(K2636474RoomType.STANDARD, 5, 2);

        assertEquals(0.12, invoice.getDiscountRate(), DELTA);
        assertEquals(3_600.00, invoice.getDiscountAmount(), DELTA);
        assertEquals(26_400.00, invoice.getTaxableAmount(), DELTA);
    }

    @Test
    @DisplayName("Guests at base capacity pay no extra guest charge")
    void noExtraGuestsAtBaseCapacity() {
        K2636474Invoice invoice = invoiceFor(K2636474RoomType.SUITE, 2, 3);

        assertEquals(0, invoice.getExtraGuests());
        assertEquals(0, invoice.getExtraGuestCharge(), DELTA);
    }

    @Test
    @DisplayName("Fewer guests than capacity never produce a negative extra charge")
    void singleGuestHasNoNegativeExtras() {
        K2636474Invoice invoice = invoiceFor(K2636474RoomType.FAMILY_SUITE, 1, 1);

        assertEquals(0, invoice.getExtraGuests());
        assertEquals(18_000.00, invoice.getBaseCharge(), DELTA);
    }

    @ParameterizedTest(name = "{0}: tax {1} on one night, 1 guest")
    @CsvSource({
        "STANDARD,     0.10, 6000,  600",
        "DELUXE,       0.12, 9000,  1080",
        "SUITE,        0.15, 14000, 2100",
        "FAMILY_SUITE, 0.15, 18000, 2700"
    })
    void eachRoomTypeUsesItsTaxRate(K2636474RoomType type, double taxRate, double base, double tax) {
        K2636474Invoice invoice = invoiceFor(type, 1, 1);

        assertEquals(taxRate, invoice.getTaxRate(), DELTA);
        assertEquals(base, invoice.getBaseCharge(), DELTA);
        assertEquals(tax, invoice.getTaxAmount(), DELTA);
        assertEquals(base + tax - 10_000, invoice.getFinalPayable(), DELTA);
    }

    @Test
    @DisplayName("Cheap stay: advance larger than total gives a refund due")
    void advanceLargerThanTotalIsRefundDue() {
        K2636474Invoice invoice = invoiceFor(K2636474RoomType.STANDARD, 1, 1);

        assertEquals(6_600.00, invoice.getGrossTotal(), DELTA);
        assertEquals(-3_400.00, invoice.getFinalPayable(), DELTA);
        assertEquals(true, invoice.isRefundDue());
    }

    @Test
    @DisplayName("Factory returns one shared package per room type")
    void factorySharesPackages() {
        assertSame(K2636474RoomPackageFactory.getPackage(K2636474RoomType.DELUXE),
                K2636474RoomPackageFactory.getPackage(K2636474RoomType.DELUXE));
    }
}
