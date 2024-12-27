package music_service.service;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import music_service.dto.authenticationDto.response.EmailAuthenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.Random;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailService {


    @NotNull
    @Value("${email-sender}")
    protected String EMAIL_SENDER;

    @NotNull
    @Value("${email-app-password}")
    protected String APP_PASSWORD;

    @Autowired
    RedisCacheService redisCacheService;

    //Save to cache :D
    public boolean sendEmail(String recipientEmail) {

        // Set up email properties
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        // Your email and password
        final String senderEmail = EMAIL_SENDER; // Replace with your email
        final String senderPassword = APP_PASSWORD; // Replace with your password or app password

        //Create Subject and Message
        String subject = "Confirm message for Spotify App";

        // Generate a random 6-digit number
        int randomCode = new Random().nextInt(900000) + 100000; // Range 100000-999999
        String htmlContent = "<h1>Your verification code is: " + randomCode + "</h1>";

        // Create a session with authentication
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Create email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setContent(htmlContent, "text/html");

            // Send email
            Transport.send(message);
//            System.out.println("Email sent successfully!");

            // Save to cache to verify
            redisCacheService.saveToCache(recipientEmail+"::FORGET", randomCode, 300);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error while sending email.");
            return false;
        }
        return true;
    }

    public EmailAuthenResponse verifyCode(String email, int code) {
        var redisCode = redisCacheService.getFromCache(email+"::FORGET");
        if (redisCode == null) {
            return EmailAuthenResponse
                    .builder()
                    .code(404)
                    .message("Code Expired")
                    .isValid(false)
                    .build();
        }

        if (redisCacheService.getFromCache(email+"::FORGET") != null) {
            if (redisCacheService.getFromCache(email+"::FORGET").equals(code)) {
                redisCacheService.deleteFromCache(email+"::FORGET");
                return EmailAuthenResponse
                        .builder()
                        .code(200)
                        .message("Success")
                        .isValid(true)
                        .build();
            }
        }
        return EmailAuthenResponse
                .builder()
                .code(400)
                .message("Incorrect code")
                .isValid(false)
                .build();
    }
}
