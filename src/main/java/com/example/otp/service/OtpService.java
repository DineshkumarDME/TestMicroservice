package com.example.otp.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, Long> otpExpiryStorage = new ConcurrentHashMap<>();
    private static final long OTP_VALIDITY_DURATION_MS = 5 * 60 * 1000; // 5 minutes

    private final Random random = new SecureRandom();

    public String generateOtp(String mobileNumber) {
        String otp = String.format("%06d", random.nextInt(1000000));
        otpStorage.put(mobileNumber, otp);
        long expiryTime = System.currentTimeMillis() + OTP_VALIDITY_DURATION_MS;
        otpExpiryStorage.put(mobileNumber, expiryTime);
        return otp;
    }

    public boolean validateOtp(String mobileNumber, String otp) {
        if (!otpExpiryStorage.containsKey(mobileNumber)) {
            return false; // No OTP generated for this number
        }

        long expiryTime = otpExpiryStorage.get(mobileNumber);
        if (System.currentTimeMillis() > expiryTime) {
            otpStorage.remove(mobileNumber);
            otpExpiryStorage.remove(mobileNumber);
            return false; // OTP expired
        }

        String storedOtp = otpStorage.get(mobileNumber);
        if (storedOtp != null && storedOtp.equals(otp)) {
            // Valid OTP, remove after use (single-use)
            otpStorage.remove(mobileNumber);
            otpExpiryStorage.remove(mobileNumber);
            return true;
        }

        return false; // Invalid OTP
    }
}
