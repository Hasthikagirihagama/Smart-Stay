package com.k2636474.smartstay.service;

import com.k2636474.smartstay.model.K2636474Booking;
import com.k2636474.smartstay.model.K2636474Guest;
import com.k2636474.smartstay.model.K2636474Invoice;
import com.k2636474.smartstay.model.K2636474Room;
import com.k2636474.smartstay.model.room.K2636474RoomPackage;
import com.k2636474.smartstay.pricing.K2636474InvoiceCalculator;
import com.k2636474.smartstay.util.K2636474Format;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Creates invoices through the invoice calculator and lays them out as text.
 */
public class K2636474InvoiceService {

    /** Invoice ID used for live previews that are never stored. */
    public static final String PREVIEW_ID = "PREVIEW";

    private static final int LINE_WIDTH = 64;
    private static final int LABEL_WIDTH = 17;

    private final K2636474DataStore dataStore;
    private final K2636474InvoiceCalculator calculator;
    private final Clock clock;

    /**
     * Creates the service.
     *
     * @param dataStore  shared data store (for invoice IDs)
     * @param calculator calculator that prices bookings
     * @param clock      source of the invoice timestamp
     */
    public K2636474InvoiceService(K2636474DataStore dataStore, K2636474InvoiceCalculator calculator, Clock clock) {
        this.dataStore = dataStore;
        this.calculator = calculator;
        this.clock = clock;
    }

    /**
     * Generates a new invoice for a booking and attaches it, replacing any previous one.
     *
     * @param booking booking to bill
     * @return the new invoice
     */
    public K2636474Invoice generateInvoice(K2636474Booking booking) {
        K2636474Invoice invoice = calculator.calculate(booking, dataStore.getIdGenerator().nextInvoiceId(),
                LocalDateTime.now(clock));
        booking.setInvoice(invoice);
        return invoice;
    }

    /**
     * Prices a booking without issuing an invoice ID or storing anything (live preview).
     *
     * @param booking booking (usually unsaved) to price
     * @return a preview invoice with ID {@link #PREVIEW_ID}
     */
    public K2636474Invoice previewInvoice(K2636474Booking booking) {
        return calculator.calculate(booking, PREVIEW_ID, LocalDateTime.now(clock));
    }

    /**
     * Returns a description of the discount policy for display.
     *
     * @return discount policy description
     */
    public String describeDiscountPolicy() {
        return calculator.getDiscountPolicy().describe();
    }

    /**
     * Lays an invoice out as fixed-width text for a monospaced view, copying or saving.
     *
     * @param invoice invoice to format
     * @return multi-line invoice text
     */
    public String formatInvoice(K2636474Invoice invoice) {
        K2636474Booking booking = invoice.getBooking();
        K2636474Guest guest = booking.getGuest();
        K2636474Room room = booking.getRoom();
        K2636474RoomPackage roomPackage = room.getRoomPackage();
        String heavyRule = "=".repeat(LINE_WIDTH);
        String lightRule = "-".repeat(LINE_WIDTH);

        StringBuilder text = new StringBuilder();
        text.append(heavyRule).append('\n')
                .append(centre("SMARTSTAY HOTELS")).append('\n')
                .append(centre("Booking Invoice")).append('\n')
                .append(heavyRule).append('\n');
        text.append(field("Invoice no.", invoice.getInvoiceId())).append('\n')
                .append(field("Booking no.", booking.getBookingId())).append('\n')
                .append(field("Issued", K2636474Format.dateTime(invoice.getGeneratedAt()))).append('\n')
                .append(field("Status", booking.getStatus().getDisplayName())).append('\n')
                .append(lightRule).append('\n');

        text.append("GUEST\n")
                .append(field("  Name", guest.getFullName())).append('\n')
                .append(field("  " + guest.getIdType().getDisplayName(), guest.getIdNumber())).append('\n')
                .append(field("  Contact", guest.getContactNumber())).append('\n')
                .append(field("  Email", guest.getEmail())).append('\n');
        text.append("ROOM\n")
                .append(field("  Room", room.getRoomId() + " (" + room.getRoomType().getDisplayName()
                        + ", " + room.getBedType().getDisplayName() + " bed)")).append('\n')
                .append(field("  Daily rate", K2636474Format.lkr(roomPackage.getDailyRate()))).append('\n')
                .append(field("  Breakfast", roomPackage.includesBreakfast() ? "Included" : "Not included"))
                .append('\n');
        text.append("STAY\n")
                .append(field("  Check-in", K2636474Format.date(booking.getCheckInDate()) + " from "
                        + K2636474Booking.CHECK_IN_TIME)).append('\n')
                .append(field("  Check-out", K2636474Format.date(booking.getCheckOutDate()))).append('\n')
                .append(field("  Nights", String.valueOf(invoice.getNights()))).append('\n')
                .append(field("  Guests", booking.getNumberOfGuests() + " (" + roomPackage.getBaseCapacity()
                        + " included, " + invoice.getExtraGuests() + " extra)")).append('\n')
                .append(lightRule).append('\n');

        text.append(amountLine("Room charge (" + invoice.getNights() + " x "
                + K2636474Format.amount(roomPackage.getDailyRate()) + ")", invoice.getBaseCharge())).append('\n');
        text.append(amountLine("Extra guests (" + invoice.getExtraGuests() + " x "
                + K2636474Format.amount(roomPackage.getExtraGuestCharge()) + " x " + invoice.getNights() + ")",
                invoice.getExtraGuestCharge())).append('\n');
        text.append(invoice.hasDiscount()
                ? amountLine("Discount (" + K2636474Format.percent(invoice.getDiscountRate()) + " long stay)",
                        -invoice.getDiscountAmount())
                : textLine("Discount", "None")).append('\n');
        text.append(amountLine("Taxable amount", invoice.getTaxableAmount())).append('\n')
                .append(amountLine("Tax (" + K2636474Format.percent(invoice.getTaxRate()) + ")",
                        invoice.getTaxAmount())).append('\n')
                .append(lightRule).append('\n')
                .append(amountLine("Gross total", invoice.getGrossTotal())).append('\n')
                .append(amountLine("Advance deducted", -invoice.getAdvanceDeducted())).append('\n')
                .append(heavyRule).append('\n');
        text.append(invoice.isRefundDue()
                ? amountLine("REFUND DUE", -invoice.getFinalPayable())
                : amountLine("FINAL PAYABLE", invoice.getFinalPayable())).append('\n')
                .append(heavyRule).append('\n');

        if (booking.isAdvanceRefunded()) {
            text.append("Booking cancelled - advance of ")
                    .append(K2636474Format.lkr(booking.getRefundedAmount())).append(" refunded.\n");
        }
        text.append("Amounts in Sri Lankan Rupees. Thank you for staying with SmartStay.\n");
        return text.toString();
    }

    private static String centre(String value) {
        int padding = Math.max(0, (LINE_WIDTH - value.length()) / 2);
        return " ".repeat(padding) + value;
    }

    private static String field(String label, String value) {
        return String.format("%-" + LABEL_WIDTH + "s%s", label, value);
    }

    private static String amountLine(String label, double amount) {
        String value = amount < 0 ? "-" + K2636474Format.lkr(-amount) : K2636474Format.lkr(amount);
        return textLine(label, value);
    }

    private static String textLine(String label, String value) {
        int gap = Math.max(1, LINE_WIDTH - label.length() - value.length());
        return label + " ".repeat(gap) + value;
    }
}
