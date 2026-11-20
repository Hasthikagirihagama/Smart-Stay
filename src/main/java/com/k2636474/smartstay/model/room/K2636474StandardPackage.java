package com.k2636474.smartstay.model.room;

import com.k2636474.smartstay.model.enums.K2636474RoomType;

/**
 * Standard room: LKR 6,000 per night, no breakfast, sleeps 2, 10% tax.
 */
public class K2636474StandardPackage extends K2636474RoomPackage {

    private static final double DAILY_RATE = 6_000;
    private static final double EXTRA_GUEST_CHARGE = 1_000;
    private static final double TAX_RATE = 0.10;
    private static final int BASE_CAPACITY = 2;

    @Override
    public double getDailyRate() {
        return DAILY_RATE;
    }

    @Override
    public double getTaxRate() {
        return TAX_RATE;
    }

    @Override
    public double getExtraGuestCharge() {
        return EXTRA_GUEST_CHARGE;
    }

    @Override
    public boolean includesBreakfast() {
        return false;
    }

    @Override
    public int getBaseCapacity() {
        return BASE_CAPACITY;
    }

    @Override
    public K2636474RoomType getRoomType() {
        return K2636474RoomType.STANDARD;
    }
}
