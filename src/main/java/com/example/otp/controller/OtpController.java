package com.example.otp.controller;

import com.example.otp.controller.dto.OtpRequestPayload;
import com.example.otp.controller.dto.OtpValidationPayload;
import com.example.otp.service.OtpService;
import com.example.otp.service.SmsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otp")
public class OtpController {

    private final OtpService otpService;
    private final SmsService smsService;

    public OtpController(OtpService otpService, SmsService smsService) {
        this.otpService = otpService;
        this.smsService = smsService;
    }

    @PostMapping("/request")
    public ResponseEntity<String> requestOtp(@RequestBody OtpRequestPayload payload) {
        String generatedOtp = otpService.generateOtp(payload.getMobileNumber());
        smsService.sendOtp(payload.getMobileNumber(), generatedOtp);
        return ResponseEntity.ok("OTP sent successfully (check console).");
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateOtp(@RequestBody OtpValidationPayload payload) {
        boolean isValid = otpService.validateOtp(payload.getMobileNumber(), payload.getOtp());
        if (isValid) {
            return ResponseEntity.ok("OTP is valid. JWT was also validated by Keycloak/Spring Security.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired OTP.");
        }
    }
}
