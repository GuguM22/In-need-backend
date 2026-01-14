package com.In_need.inNeedApp.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DonationFrequency {

    ONE_TIME,
    MONTHLY,
    WEEKLY,
    QUARTERLY;

    /**
     * Here we convert a string value to a Role enum,
     * making the conversion case-insensitive.
     * This allows JSON input like "onetime", "Onetime", or "ONETIME"
     * to all be correctly mapped to DonationFrequency.ONETIME
     */
    @JsonCreator
    public static DonationFrequency fromString(String key) {
        // Handle null input gracefully
        if (key == null) return null;
        // Convert input to uppercase to match enum names
        return DonationFrequency.valueOf(key.toUpperCase());
    }
}
