package com.k2636474.smartstay.model;

import java.time.LocalDateTime;

/**
 * Immutable bill for a booking. All amounts are worked out by the invoice calculator
 * and frozen here; modifying a booking produces a brand-new invoice instead of
 * editing this one, so an issued invoice can never silently change.
 */
public final class K2636474Invoice {

    private final String invoiceId;
    private final K2636474Booking booking;
    private final long nights;
    private final double baseCharge;
    private final int extraGuests;
    private final double extraGuestCharge;
    private final double discountRate;
    private final double discountAmount;
    private final double taxableAmount;
    private final double taxRate;
    private final double taxAmount;
    private final double grossTotal;
    private final double advanceDeducted;
    private final double finalPayable;
    private final LocalDateTime generatedAt;

    /**
     * Creates an invoice from already-calculated values. Only the invoice calculator
     * should call this.
     *
     * @param invoiceId        unique ID (e.g. INV0001)
     * @param booking          the booking being billed
     * @param nights           number of nights
     * @param baseCharge       daily rate × nights
     * @param extraGuests      guests above base capacity
     * @param extraGuestCharge extra guests × extra rate × nights
     * @param discountRate     discount rate applied to the base charge
     * @param discountAmount   base charge × discount rate
     * @param taxableAmount    base − discount + extra guest charge
     * @param taxRate          tax rate of the room type
     * @param taxAmount        taxable amount × tax rate
     * @param grossTotal       taxable amount + tax
     * @param advanceDeducted  advance taken off the total
     * @param finalPayable     gross total − advance (negative means refund due)
     * @param generatedAt      when the invoice was produced
     */
    public K2636474Invoice(String invoiceId, K2636474Booking booking, long nights, double baseCharge,
                           int extraGuests, double extraGuestCharge, double discountRate, double discountAmount,
                           double taxableAmount, double taxRate, double taxAmount, double grossTotal,
                           double advanceDeducted, double finalPayable, LocalDateTime generatedAt) {
        this.invoiceId = invoiceId;
        this.booking = booking;
        this.nights = nights;
        this.baseCharge = baseCharge;
        this.extraGuests = extraGuests;
        this.extraGuestCharge = extraGuestCharge;
        this.discountRate = discountRate;
        this.discountAmount = discountAmount;
        this.taxableAmount = taxableAmount;
        this.taxRate = taxRate;
        this.taxAmount = taxAmount;
        this.grossTotal = grossTotal;
        this.advanceDeducted = advanceDeducted;
        this.finalPayable = finalPayable;
        this.generatedAt = generatedAt;
    }

    /**
     * Returns whether the guest is owed money (advance larger than the total).
     *
     * @return true when the final payable amount is negative
     */
    public boolean isRefundDue() {
        return finalPayable < 0;
    }

    /**
     * Returns whether any discount was applied.
     *
     * @return true when the discount rate is above zero
     */
    public boolean hasDiscount() {
        return discountRate > 0;
    }

    /**
     * Returns the invoice ID.
     *
     * @return invoice ID
     */
    public String getInvoiceId() {
        return invoiceId;
    }

    /**
     * Returns the booking this invoice bills.
     *
     * @return booking
     */
    public K2636474Booking getBooking() {
        return booking;
    }

    /**
     * Returns the number of nights billed.
     *
     * @return nights
     */
    public long getNights() {
        return nights;
    }

    /**
     * Returns the base room charge (daily rate × nights).
     *
     * @return base charge in LKR
     */
    public double getBaseCharge() {
        return baseCharge;
    }

    /**
     * Returns the number of guests above the base capacity.
     *
     * @return extra guests
     */
    public int getExtraGuests() {
        return extraGuests;
    }

    /**
     * Returns the total extra guest charge.
     *
     * @return extra guest charge in LKR
     */
    public double getExtraGuestCharge() {
        return extraGuestCharge;
    }

    /**
     * Returns the discount rate applied to the base charge.
     *
     * @return discount rate as a fraction
     */
    public double getDiscountRate() {
        return discountRate;
    }

    /**
     * Returns the discount amount.
     *
     * @return discount in LKR
     */
    public double getDiscountAmount() {
        return discountAmount;
    }

    /**
     * Returns the amount tax is charged on.
     *
     * @return taxable amount in LKR
     */
    public double getTaxableAmount() {
        return taxableAmount;
    }

    /**
     * Returns the tax rate of the room type.
     *
     * @return tax rate as a fraction
     */
    public double getTaxRate() {
        return taxRate;
    }

    /**
     * Returns the tax amount.
     *
     * @return tax in LKR
     */
    public double getTaxAmount() {
        return taxAmount;
    }

    /**
     * Returns the total before the advance is deducted.
     *
     * @return gross total in LKR
     */
    public double getGrossTotal() {
        return grossTotal;
    }

    /**
     * Returns the advance deducted from the total.
     *
     * @return advance deducted in LKR
     */
    public double getAdvanceDeducted() {
        return advanceDeducted;
    }

    /**
     * Returns the amount still to pay (negative means a refund is due).
     *
     * @return final payable in LKR
     */
    public double getFinalPayable() {
        return finalPayable;
    }

    /**
     * Returns when the invoice was generated.
     *
     * @return generation time
     */
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
}
