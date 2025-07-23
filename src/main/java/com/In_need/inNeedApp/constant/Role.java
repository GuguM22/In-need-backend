package com.In_need.inNeedApp.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    ORGANIZATION,
    SPONSORS,
    INDIVIDUAL,
    ADMIN;

    /**
     * Here we convert a string value to a Role enum,
     * making the conversion case-insensitive.
     * This allows JSON input like "admin", "Admin", or "ADMIN"
     * to all be correctly mapped to Role.ADMIN.
     */
    @JsonCreator
    public static Role fromString(String key) {
        // Handle null input gracefully
        if (key == null) return null;
        // Convert input to uppercase to match enum names
        return Role.valueOf(key.toUpperCase());
    }
}