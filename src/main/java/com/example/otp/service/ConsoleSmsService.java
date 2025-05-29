package com.example.otp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConsoleSmsService implements SmsService {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleSmsService.class);

    @Override
    public void sendOtp(String mobileNumber, String otp) {
        // In a real application, this method would integrate with an SMS gateway.
        // For this example, we'll just log it to the console.
        logger.info("Mock SMS: Sending OTP [{}] to [{}]", otp, mobileNumber);
        // System.out.println("Mock SMS: Sending OTP " + otp + " to " + mobileNumber); // Alternative
    }
}
