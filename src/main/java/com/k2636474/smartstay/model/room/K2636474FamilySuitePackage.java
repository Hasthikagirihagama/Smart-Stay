package com.k2636474.smartstay.model.room;

import com.k2636474.smartstay.model.enums.K2636474RoomType;

/**
 * Family Suite: LKR 18,000 per night, breakfast included, sleeps 4, 15% tax.
 */
public class K2636474FamilySuitePackage extends K2636474RoomPackage {

    private static final double DAILY_RATE = 18_000;
    private static final double EXTRA_GUEST_CHARGE = 2_500;
    private static final double TAX_RATE = 0.15;
    private static final int BASE_CAPACITY = 4;

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
        return K2636474RoomType.FAMILY_SUITE;
    }
}
