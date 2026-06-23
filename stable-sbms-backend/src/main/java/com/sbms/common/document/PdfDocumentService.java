package com.sbms.common.document;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.sbms.common.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfDocumentService {

    public byte[] renderPdf(String htmlContent) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new BadRequestException("Failed to render PDF document");
        }
    }
}
