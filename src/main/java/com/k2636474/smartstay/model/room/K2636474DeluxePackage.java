package com.k2636474.smartstay.model.room;

import com.k2636474.smartstay.model.enums.K2636474RoomType;

/**
 * Deluxe room: LKR 9,000 per night, breakfast included, sleeps 2, 12% tax.
 */
public class K2636474DeluxePackage extends K2636474RoomPackage {

    private static final double DAILY_RATE = 9_000;
    private static final double EXTRA_GUEST_CHARGE = 1_500;
    private static final double TAX_RATE = 0.12;
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
        return true;
    }

    @Override
    public int getBaseCapacity() {
        return BASE_CAPACITY;
    }

    @Override
    public K2636474RoomType getRoomType() {
        return K2636474RoomType.DELUXE;
    }
}
