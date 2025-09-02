package com.In_need.inNeedApp.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum LogisticPreference {
    PICK_UP,
    DROP_OFF,
    DELIVERY;

    /**
     * Here we convert a string value to a Role enum,
     * making the conversion case-insensitive.
     * This allows JSON input like "pickup", "Pickup", or "PICKUP"
     * to all be correctly mapped to DonationType.PICKUP
     */
    @JsonCreator
    public static LogisticPreference fromString(String key) {
        if (key == null) return null;
        String normalized = key.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        return LogisticPreference.valueOf(normalized);
    }

}
