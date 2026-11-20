package com.k2636474.smartstay.model.room;

/**
 * Anything that can be charged for per night. Separates "how much does it cost"
 * from "what kind of room is it", so pricing code depends only on this contract.
 */
public interface K2636474Chargeable {

    /**
     * Returns the base price per night.
     *
     * @return daily rate in LKR
     */
    double getDailyRate();

    /**
     * Returns the tax rate applied to the taxable amount.
     *
     * @return tax rate as a fraction (0.10 means 10%)
     */
    double getTaxRate();

    /**
     * Returns the charge for each guest above the base capacity, per night.
     *
     * @return extra guest charge in LKR per guest per night
     */
    double getExtraGuestCharge();
}
