package com.k2636474.smartstay.pricing;

import com.k2636474.smartstay.model.K2636474Booking;

/**
 * Long-stay promotion: 12% off the base room charge for stays of 5 nights or more.
 */
public class K2636474LongStayDiscountPolicy implements K2636474DiscountPolicy {

    /** Shortest stay that qualifies for the discount. */
    public static final int MIN_NIGHTS = 5;
    /** Discount rate applied to the base charge. */
    public static final double RATE = 0.12;

    @Override
    public double rateFor(K2636474Booking booking) {
        // ">=" so a stay of exactly MIN_NIGHTS nights qualifies.
        return booking.getNights() >= MIN_NIGHTS ? RATE : 0;
    }

    @Override
    public String describe() {
        return "Long stay: " + Math.round(RATE * 100) + "% off the room charge for " + MIN_NIGHTS + "+ nights";
    }
}
