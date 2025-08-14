package com.In_need.inNeedApp.services;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    // token -> expiryEpochMillis
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    public void blacklist(String token, long expiryEpochMillis) {
        if (token != null) {
            blacklist.put(token, expiryEpochMillis);
        }
    }

    public boolean isBlacklisted(String token) {
        if (token == null) return false;
        Long exp = blacklist.get(token);
        if (exp == null) return false;
        // If expiry already passed, remove entry and return false
        if (System.currentTimeMillis() > exp) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }

    public void remove(String token) {
        blacklist.remove(token);
    }
}
