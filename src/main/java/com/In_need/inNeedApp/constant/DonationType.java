package com.In_need.inNeedApp.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DonationType {
    FOOD,
    ITEM,
    SERVICE;


    /**
     * Here we convert a string value to a Role enum,
     * making the conversion case-insensitive.
     * This allows JSON input like "food", "Food", or "FOOD"
     * to all be correctly mapped to DonationType.FOOD
     */
    @JsonCreator
    public static DonationType fromString(String key) {
        // Handle null input gracefully
        if (key == null) return null;
        // Convert input to uppercase to match enum names
        return DonationType.valueOf(key.toUpperCase());
    }
}
