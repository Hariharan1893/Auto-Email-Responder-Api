package com.emailresponder.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.emailresponder.repository.EmailTemplateRepository;
import com.emailresponder.utils.EmailTemplateUtil;

import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateRepository templateRepository;
    private final KeywordDetectionService keywordDetectionService;
    private final EmailTemplateUtil emailTemplateUtil;

    public EmailService(JavaMailSender mailSender, 
                        EmailTemplateRepository templateRepository,
                        KeywordDetectionService keywordDetectionService, 
                        EmailTemplateUtil emailTemplateUtil) {
        this.mailSender = mailSender;
        this.templateRepository = templateRepository;
        this.keywordDetectionService = keywordDetectionService;
        this.emailTemplateUtil = emailTemplateUtil;
    }

    /**
     * Processes incoming email, detects keywords, and sends an auto-response.
     *
     * @param recipientEmail The sender's email address
     * @param subject        The subject of the email
     * @param emailContent   The content of the email
     * @return true if response is sent successfully; false otherwise
     */
    
    public boolean processAndSendResponse(String recipientEmail, String subject, String emailContent) {
        
    	Optional<String> detectedKeyword = keywordDetectionService.detectKeyword(emailContent);
        String keyword = detectedKeyword.orElse("default_response_template");
        return sendAutoResponse(recipientEmail, keyword, subject);
    }

    /**
     * Sends an auto-response based on the detected keyword and email template.
     *
     * @param recipientEmail The email address to reply to
     * @param keyword        The detected keyword for selecting the template
     * @param subject        The original subject of the email
     * @return true if the email was sent successfully; false otherwise
     */
    private boolean sendAutoResponse(String recipientEmail, String keyword, String subject) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return false;
            }

            Map<String, Object> model = new HashMap<>();
            model.put("recipientEmail", recipientEmail);
            model.put("subject", subject);
            model.put("timestamp", new Date().toString());

            String emailBody;
            try {
                emailBody = emailTemplateUtil.generateEmailBody(keyword, model);
            } catch (IOException | TemplateException e) {
                // Fallback to default template
                emailBody = emailTemplateUtil.generateEmailBody("default_response_template", model);
            }

            if (emailBody == null || emailBody.trim().isEmpty()) {
                return false;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(emailBody, true);

            mailSender.send(message);
            return true;

        } catch (MessagingException | IOException | TemplateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace(); 
        }
        return false;
    }
}
