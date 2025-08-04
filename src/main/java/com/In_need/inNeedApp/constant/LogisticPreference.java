package com.In_need.inNeedApp.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum LogisticPreference {
    PICK_UP,
    DELIVERY;

    /**
     * Here we convert a string value to a Role enum,
     * making the conversion case-insensitive.
     * This allows JSON input like "pickup", "Pickup", or "PICKUP"
     * to all be correctly mapped to DonationType.PICKUP
     */
    @JsonCreator
    public static LogisticPreference fromString(String key) {
        // Handle null input gracefully
        if (key == null) return null;
        // Convert input to uppercase to match enum names
        return LogisticPreference.valueOf(key.toUpperCase());
    }
}
