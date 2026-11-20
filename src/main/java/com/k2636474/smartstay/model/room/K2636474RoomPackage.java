package com.k2636474.smartstay.model.room;

import com.k2636474.smartstay.model.K2636474Booking;
import com.k2636474.smartstay.model.enums.K2636474RoomType;
import com.k2636474.smartstay.util.K2636474Format;

/**
 * Abstract price-and-capacity package for one room type. Each subclass supplies its
 * own values; shared behaviour such as {@link #describe()} and
 * {@link #getMaxOccupancy()} is written once here using the abstract methods
 * (template-method style), so it works for every room type polymorphically.
 */
public abstract class K2636474RoomPackage implements K2636474Chargeable {

    /**
     * Returns whether breakfast is included in the nightly rate.
     *
     * @return true when breakfast is included
     */
    public abstract boolean includesBreakfast();

    /**
     * Returns how many guests the daily rate covers before extra charges apply.
     *
     * @return base capacity in guests
     */
    public abstract int getBaseCapacity();

    /**
     * Returns the room type this package prices.
     *
     * @return the room type
     */
    public abstract K2636474RoomType getRoomType();

    /**
     * Returns the absolute guest limit: base capacity plus the allowed extra guests.
     *
     * @return maximum number of guests for one room of this type
     */
    public int getMaxOccupancy() {
        return getBaseCapacity() + K2636474Booking.MAX_EXTRA_GUESTS;
    }

    /**
     * Builds a one-line summary of the package from the subclass values.
     *
     * @return summary such as "Deluxe · LKR 9,000.00/night · breakfast included · sleeps 2 (max 4)"
     */
    public String describe() {
        String breakfast = includesBreakfast() ? "breakfast included" : "room only";
        return getRoomType().getDisplayName()
                + " · " + K2636474Format.lkr(getDailyRate()) + "/night"
                + " · " + breakfast
                + " · sleeps " + getBaseCapacity() + " (max " + getMaxOccupancy() + ")"
                + " · tax " + K2636474Format.percent(getTaxRate());
    }

    @Override
    public String toString() {
        return describe();
    }
}
