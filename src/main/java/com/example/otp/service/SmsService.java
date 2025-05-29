package com.example.otp.service;

public interface SmsService {
    void sendOtp(String mobileNumber, String otp);
}
