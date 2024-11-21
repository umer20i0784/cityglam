package com.cityglam.cityglam.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.cityglam.cityglam.entity.*;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OTPService.class);

    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, String> userTempStorage = new ConcurrentHashMap<>();
    private final JavaMailSender mailSender;

    @Value("${app.otp.expiration:600000}") // Default OTP expiration is 10 minutes
    private long otpExpirationMillis;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public OTPService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public String generateOTP(String email) {
        String otp = String.valueOf(new Random().nextInt(999999 - 100000 + 1) + 100000);
        otpStorage.put(email, otp);
        LOGGER.debug("Generated OTP [{}] for email [{}]", otp, email);
        return otp;
    }

    public void storeTempUser(String email, String password, String role) {
        userTempStorage.put(email, password + ":" + role);
        LOGGER.debug("Stored temporary user: email=[{}], role=[{}]", email, role);
    }

    public boolean verifyOTP(String email, String otp) {
        if (!otpStorage.containsKey(email)) {
            LOGGER.warn("OTP verification failed: No OTP found for email [{}]", email);
            return false;
        }
        String storedOtp = otpStorage.get(email);
        boolean isValid = storedOtp.equals(otp);
        if (isValid) {
            otpStorage.remove(email); // Clear OTP after successful verification
            LOGGER.debug("OTP verification successful for email [{}]", email);
        } else {
            LOGGER.warn("OTP verification failed for email [{}]: Invalid OTP", email);
        }
        return isValid;
    }

    public Optional<User> retrieveTempUser(String email) {
        if (userTempStorage.containsKey(email)) {
            String[] tempData = userTempStorage.get(email).split(":");
            String password = tempData[0];
            String role = tempData[1];
            userTempStorage.remove(email);
            User user = new User();
            user.setEmail(email);
            user.setPassword(password); // Password is already encoded
            user.setRole(Role.valueOf(role));
            user.setStatus(Status.APPLIED);
            LOGGER.debug("Retrieved temporary user: email=[{}], role=[{}]", email, role);
            return Optional.of(user);
        }
        LOGGER.warn("No temporary user found for email [{}]", email);
        return Optional.empty();
    }

    public void sendOTPEmail(String email, String otp) {
        String subject = "Your CityGlam OTP Code";
        String content = getEmailContent(otp);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(senderEmail);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content, true); // Enable HTML content
            mailSender.send(message);
            LOGGER.debug("Sent OTP email to [{}]", email);
        } catch (Exception e) {
            LOGGER.error("Failed to send OTP email to [{}]: {}", email, e.getMessage());
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
        }
    }

    private String getEmailContent(String otp) {
        String emailTemplate = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>CityGlam OTP Verification</title>
                </head>
                <body style="font-family: 'Montserrat', sans-serif; background-color: #f9f9f9; padding: 20px;">
                  <div style="max-width: 600px; margin: 0 auto; background: #fff; padding: 20px; border-radius: 8px;">
                    <h2 style="color: #4CAF50;">CityGlam OTP Verification</h2>
                    <p>Hello,</p>
                    <p>Thank you for signing up with CityGlam! Please use the following One-Time Password (OTP) to complete your verification:</p>
                    <h1 style="text-align: center; color: #7367f0;">{{OTP}}</h1>
                    <p>The OTP is valid for 10 minutes. If you did not request this, please contact our support team.</p>
                    <p>Best Regards,<br>CityGlam Team</p>
                  </div>
                </body>
                </html>
                """;
        LOGGER.debug("Generated email content with OTP [{}]", otp);
        return emailTemplate.replace("{{OTP}}", otp);
    }
}
