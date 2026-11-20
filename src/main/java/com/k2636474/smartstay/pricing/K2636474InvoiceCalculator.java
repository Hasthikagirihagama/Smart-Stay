package com.k2636474.smartstay.pricing;

import com.k2636474.smartstay.model.K2636474Booking;
import com.k2636474.smartstay.model.K2636474Invoice;
import com.k2636474.smartstay.model.enums.K2636474BookingStatus;
import com.k2636474.smartstay.model.room.K2636474RoomPackage;

import java.time.LocalDateTime;

/**
 * Works out a booking's bill. This is the ONLY place money is calculated: the booking
 * dialog's live preview, new invoices and regenerated invoices all come through here,
 * so the numbers can never disagree.
 */
public class K2636474InvoiceCalculator {

    private final K2636474DiscountPolicy discountPolicy;

    /**
     * Creates a calculator with a pluggable discount strategy.
     *
     * @param discountPolicy policy that decides the discount rate
     * @throws IllegalArgumentException if the policy is null
     */
    public K2636474InvoiceCalculator(K2636474DiscountPolicy discountPolicy) {
        if (discountPolicy == null) {
            throw new IllegalArgumentException("A discount policy is required.");
        }
        this.discountPolicy = discountPolicy;
    }

    /**
     * Calculates the full bill for a booking.
     *
     * @param booking     the booking to price
     * @param invoiceId   ID to give the invoice
     * @param generatedAt timestamp to record on the invoice
     * @return an immutable invoice with every step of the calculation
     */
    public K2636474Invoice calculate(K2636474Booking booking, String invoiceId, LocalDateTime generatedAt) {
        K2636474RoomPackage roomPackage = booking.getRoom().getRoomPackage();

        // Step 1: nights between check-in and check-out (check-out day is not charged).
        long nights = booking.getNights();
        // Step 2: base room charge for the whole stay.
        double baseCharge = roomPackage.getDailyRate() * nights;
        // Step 3: only guests above the base capacity pay extra; never negative.
        int extraGuests = Math.max(0, booking.getNumberOfGuests() - roomPackage.getBaseCapacity());
        // Step 4: extra guests are charged per person per night.
        double extraGuestCharge = extraGuests * roomPackage.getExtraGuestCharge() * nights;
        // Step 5: discount comes from the strategy and applies to the base charge only.
        double discountRate = discountPolicy.rateFor(booking);
        double discountAmount = baseCharge * discountRate;
        // Step 6: tax is charged on the discounted room charge plus extra guest charges.
        double taxableAmount = baseCharge - discountAmount + extraGuestCharge;
        // Step 7: tax rate depends on the room type.
        double taxRate = roomPackage.getTaxRate();
        double taxAmount = taxableAmount * taxRate;
        // Step 8: total the guest owes for the stay.
        double grossTotal = taxableAmount + taxAmount;
        // Step 9: the advance only counts while the booking is confirmed (it is refunded on cancel).
        double advanceDeducted = booking.isActive() ? booking.getAdvancePaid() : 0;
        // Step 10: what is left to pay at the hotel; a negative value means a refund is due.
        // A cancelled stay is never charged, so nothing is payable.
        double finalPayable = booking.getStatus() == K2636474BookingStatus.CANCELLED
                ? 0 : grossTotal - advanceDeducted;

        return new K2636474Invoice(invoiceId, booking, nights, baseCharge, extraGuests, extraGuestCharge,
                discountRate, discountAmount, taxableAmount, taxRate, taxAmount, grossTotal,
                advanceDeducted, finalPayable, generatedAt);
    }

    /**
     * Returns the discount strategy in use.
     *
     * @return discount policy
     */
    public K2636474DiscountPolicy getDiscountPolicy() {
        return discountPolicy;
    }
}
