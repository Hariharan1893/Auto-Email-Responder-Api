package com.emailresponder.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.Map;

@Component
public class EmailTemplateUtil {

    private final Configuration freemarkerConfig;

    public EmailTemplateUtil(Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
    }

    public String generateEmailBody(String templateName, Map<String, Object> model) throws IOException, TemplateException {
        Template template = freemarkerConfig.getTemplate(templateName + ".ftl");
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }
}
