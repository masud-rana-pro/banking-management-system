package com.sbms.common.document;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

@Service
public class DocumentTemplateService {

    private final TemplateEngine templateEngine;

    public DocumentTemplateService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String render(String templateName, Map<String, Object> variables) {
        Context context = new Context(Locale.ENGLISH);
        context.setVariables(variables);
        return templateEngine.process(templateName, context);
    }
}
