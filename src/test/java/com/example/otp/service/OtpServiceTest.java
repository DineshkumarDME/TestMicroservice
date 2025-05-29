package com.example.otp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class OtpServiceTest {

    private OtpService otpService;
    private Map<String, String> otpStorage;
    private Map<String, Long> otpExpiryStorage;

    private static final long TEST_OTP_VALIDITY_DURATION_MS = 100; // Short duration for expiry test

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        otpService = new OtpService();

        // Use reflection to make otpStorage and otpExpiryStorage accessible for assertions
        // and to manipulate expiry for one test case.

        Field otpStorageField = OtpService.class.getDeclaredField("otpStorage");
        otpStorageField.setAccessible(true);
        otpStorage = (Map<String, String>) otpStorageField.get(otpService);

        Field otpExpiryStorageField = OtpService.class.getDeclaredField("otpExpiryStorage");
        otpExpiryStorageField.setAccessible(true);
        otpExpiryStorage = (Map<String, Long>) otpExpiryStorageField.get(otpService);

        // Override OTP_VALIDITY_DURATION_MS for testing expiry
        Field otpValidityDurationField = OtpService.class.getDeclaredField("OTP_VALIDITY_DURATION_MS");
        otpValidityDurationField.setAccessible(true);
        // Modifiers should be removed from the final field to allow modification
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(otpValidityDurationField, otpValidityDurationField.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
        otpValidityDurationField.setLong(null, TEST_OTP_VALIDITY_DURATION_MS); // Set to static field
    }

    @Test
    void testGenerateOtp_success() {
        String mobileNumber = "1234567890";
        String otp = otpService.generateOtp(mobileNumber);

        assertNotNull(otp);
        assertTrue(otp.matches("\\d{6}"), "OTP should be a 6-digit string");
        assertTrue(otpStorage.containsKey(mobileNumber), "otpStorage should contain the mobile number");
        assertEquals(otp, otpStorage.get(mobileNumber), "otpStorage should contain the generated OTP");
        assertTrue(otpExpiryStorage.containsKey(mobileNumber), "otpExpiryStorage should contain the mobile number");
        assertTrue(otpExpiryStorage.get(mobileNumber) > System.currentTimeMillis(), "Expiry time should be in the future");
    }

    @Test
    void testValidateOtp_success() {
        String mobileNumber = "1234567890";
        String otp = otpService.generateOtp(mobileNumber);

        boolean isValid = otpService.validateOtp(mobileNumber, otp);

        assertTrue(isValid, "OTP should be valid");
        assertFalse(otpStorage.containsKey(mobileNumber), "otpStorage should not contain the mobile number after validation");
        assertFalse(otpExpiryStorage.containsKey(mobileNumber), "otpExpiryStorage should not contain the mobile number after validation");
    }

    @Test
    void testValidateOtp_failure_incorrectOtp() {
        String mobileNumber = "1234567890";
        otpService.generateOtp(mobileNumber); // Generate an OTP

        boolean isValid = otpService.validateOtp(mobileNumber, "000000"); // Use an incorrect OTP

        assertFalse(isValid, "OTP should be invalid due to incorrect OTP");
        assertTrue(otpStorage.containsKey(mobileNumber), "otpStorage should still contain the mobile number");
        assertTrue(otpExpiryStorage.containsKey(mobileNumber), "otpExpiryStorage should still contain the mobile number");
    }

    @Test
    void testValidateOtp_failure_otpExpired() throws InterruptedException {
        String mobileNumber = "1234567890";
        String otp = otpService.generateOtp(mobileNumber);

        // Wait for longer than the TEST_OTP_VALIDITY_DURATION_MS
        Thread.sleep(TEST_OTP_VALIDITY_DURATION_MS + 50);

        boolean isValid = otpService.validateOtp(mobileNumber, otp);

        assertFalse(isValid, "OTP should be invalid due to expiry");
        assertFalse(otpStorage.containsKey(mobileNumber), "otpStorage should be cleared after expired OTP validation attempt");
        assertFalse(otpExpiryStorage.containsKey(mobileNumber), "otpExpiryStorage should be cleared after expired OTP validation attempt");
    }
    
    @Test
    void testValidateOtp_failure_noOtpForNumber() {
        String mobileNumber = "0000000000"; // A number for which no OTP was generated
        boolean isValid = otpService.validateOtp(mobileNumber, "123456");

        assertFalse(isValid, "OTP validation should fail if no OTP was generated for the number");
        assertFalse(otpStorage.containsKey(mobileNumber), "otpStorage should not contain the mobile number");
        assertFalse(otpExpiryStorage.containsKey(mobileNumber), "otpExpiryStorage should not contain the mobile number");
    }
}
