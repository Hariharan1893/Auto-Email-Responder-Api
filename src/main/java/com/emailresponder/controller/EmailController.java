package com.emailresponder.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emailresponder.service.EmailService;

@RestController
@RequestMapping("/api/email")
public class EmailController {
	@Autowired
    private EmailService emailService;
	
	/**
     * API to receive an email request and send an auto-response.
     *
     * @param requestBody A JSON payload containing sender email, subject, and message.
     * @return Response message indicating success or failure.
     */
	
	@PostMapping("/send-auto-response")
    public String sendAutoResponse(@RequestBody Map<String, String> requestBody) {
        String senderEmail = requestBody.get("senderEmail");
        String subject = requestBody.get("subject");
        String message = requestBody.get("message");

        if (senderEmail == null || message == null) {
            return "Error: senderEmail and message are required fields.";
        }

        boolean emailSent = emailService.processAndSendResponse(senderEmail, subject, message);

        return emailSent ? "Auto-response sent successfully!" : "Failed to send auto-response.";
    }

}
