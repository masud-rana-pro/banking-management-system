package com.sbms.common.document;

import com.sbms.report.dto.response.ReportColumnResponse;
import com.sbms.report.dto.response.ReportMetricResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExcelDocumentService {

    public byte[] buildReportWorkbook(
            String reportName,
            String dateFrom,
            String dateTo,
            String requestedBy,
            List<ReportMetricResponse> metrics,
            List<ReportColumnResponse> columns,
            List<Map<String, Object>> rows
    ) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet summarySheet = workbook.createSheet("Summary");
            Sheet dataSheet = workbook.createSheet("Report Data");

            CellStyle titleStyle = buildTitleStyle(workbook);
            CellStyle headerStyle = buildHeaderStyle(workbook);
            CellStyle valueStyle = buildValueStyle(workbook);

            int summaryRowIndex = 0;
            Row titleRow = summarySheet.createRow(summaryRowIndex++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(reportName);
            titleCell.setCellStyle(titleStyle);

            Row metaRow = summarySheet.createRow(summaryRowIndex++);
            metaRow.createCell(0).setCellValue("Date From");
            metaRow.createCell(1).setCellValue(dateFrom);
            metaRow.createCell(2).setCellValue("Date To");
            metaRow.createCell(3).setCellValue(dateTo);
            metaRow.createCell(4).setCellValue("Requested By");
            metaRow.createCell(5).setCellValue(requestedBy);

            summaryRowIndex++;
            Row metricsHeader = summarySheet.createRow(summaryRowIndex++);
            metricsHeader.createCell(0).setCellValue("Metric");
            metricsHeader.createCell(1).setCellValue("Value");
            metricsHeader.getCell(0).setCellStyle(headerStyle);
            metricsHeader.getCell(1).setCellStyle(headerStyle);

            for (ReportMetricResponse metric : metrics) {
                Row metricRow = summarySheet.createRow(summaryRowIndex++);
                Cell labelCell = metricRow.createCell(0);
                labelCell.setCellValue(metric.getLabel());
                labelCell.setCellStyle(valueStyle);
                Cell valueCell = metricRow.createCell(1);
                valueCell.setCellValue(metric.getValue());
                valueCell.setCellStyle(valueStyle);
            }

            int dataRowIndex = 0;
            Row dataHeader = dataSheet.createRow(dataRowIndex++);
            for (int i = 0; i < columns.size(); i++) {
                Cell headerCell = dataHeader.createCell(i);
                headerCell.setCellValue(columns.get(i).getLabel());
                headerCell.setCellStyle(headerStyle);
            }

            for (Map<String, Object> row : rows) {
                Row dataRow = dataSheet.createRow(dataRowIndex++);
                for (int i = 0; i < columns.size(); i++) {
                    Object value = row.get(columns.get(i).getKey());
                    Cell cell = dataRow.createCell(i);
                    writeCellValue(cell, value);
                    cell.setCellStyle(valueStyle);
                }
            }

            autoSize(summarySheet, 6);
            autoSize(dataSheet, columns.size());

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to generate Excel workbook", ex);
        }
    }

    public byte[] buildTabularWorkbook(
            String title,
            Map<String, String> metadata,
            List<String> headers,
            List<List<String>> rows
    ) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet summarySheet = workbook.createSheet("Summary");
            Sheet dataSheet = workbook.createSheet("Data");

            CellStyle titleStyle = buildTitleStyle(workbook);
            CellStyle headerStyle = buildHeaderStyle(workbook);
            CellStyle valueStyle = buildValueStyle(workbook);

            int summaryRowIndex = 0;
            Row titleRow = summarySheet.createRow(summaryRowIndex++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(title);
            titleCell.setCellStyle(titleStyle);

            Map<String, String> safeMetadata = metadata == null ? new LinkedHashMap<>() : metadata;
            for (Map.Entry<String, String> entry : safeMetadata.entrySet()) {
                Row row = summarySheet.createRow(summaryRowIndex++);
                Cell keyCell = row.createCell(0);
                keyCell.setCellValue(entry.getKey());
                keyCell.setCellStyle(headerStyle);
                Cell valueCell = row.createCell(1);
                valueCell.setCellValue(entry.getValue());
                valueCell.setCellStyle(valueStyle);
            }

            int dataRowIndex = 0;
            Row dataHeader = dataSheet.createRow(dataRowIndex++);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = dataHeader.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            for (List<String> rowValues : rows) {
                Row row = dataSheet.createRow(dataRowIndex++);
                for (int i = 0; i < rowValues.size(); i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(rowValues.get(i));
                    cell.setCellStyle(valueStyle);
                }
            }

            autoSize(summarySheet, 2);
            autoSize(dataSheet, headers.size());
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to generate Excel workbook", ex);
        }
    }

    private CellStyle buildTitleStyle(Workbook workbook) {
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setFontName("Arial");
        CellStyle style = workbook.createCellStyle();
        style.setFont(titleFont);
        return style;
    }

    private CellStyle buildHeaderStyle(Workbook workbook) {
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setFontName("Arial");
        CellStyle style = workbook.createCellStyle();
        style.setFont(headerFont);
        style.setFillForegroundColor(IndexedColors.TEAL.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle buildValueStyle(Workbook workbook) {
        Font valueFont = workbook.createFont();
        valueFont.setFontName("Arial");
        CellStyle style = workbook.createCellStyle();
        style.setFont(valueFont);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }

    private void writeCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
            return;
        }
        if (value instanceof Number number) {
            cell.setCellValue(number.doubleValue());
            return;
        }
        cell.setCellValue(String.valueOf(value));
    }

    private void autoSize(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
            int currentWidth = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, Math.min(currentWidth + 800, 18000));
        }
    }
}
