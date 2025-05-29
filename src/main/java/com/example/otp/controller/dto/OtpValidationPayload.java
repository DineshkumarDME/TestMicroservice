package com.example.otp.controller.dto;

import lombok.Data;

@Data
public class OtpValidationPayload {
    private String mobileNumber;
    private String otp;
}
