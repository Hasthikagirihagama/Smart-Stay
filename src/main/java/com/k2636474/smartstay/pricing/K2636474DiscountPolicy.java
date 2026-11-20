package com.k2636474.smartstay.pricing;

import com.k2636474.smartstay.model.K2636474Booking;

/**
 * Strategy for deciding a booking's discount. The invoice calculator depends only on
 * this interface, so a new promotion (e.g. seasonal) can be plugged in without
 * touching the bill calculation.
 */
public interface K2636474DiscountPolicy {

    /**
     * Returns the discount rate for a booking.
     *
     * @param booking the booking being priced
     * @return rate as a fraction of the base charge (0.12 means 12%); 0 for no discount
     */
    double rateFor(K2636474Booking booking);

    /**
     * Describes the policy for display.
     *
     * @return short human-readable description
     */
    String describe();
}
