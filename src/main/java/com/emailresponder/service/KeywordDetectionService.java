package com.emailresponder.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class KeywordDetectionService {

	private final List<String> keywords = List.of("pricing", "service", "support");

    public Optional<String> detectKeyword(String emailContent) {
        for (String keyword : keywords) {
            if (emailContent.toLowerCase().contains(keyword.toLowerCase())) {
                return Optional.of(keyword);
            }
        }

        return Optional.empty();
    }
}
