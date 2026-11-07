package com.studentmaster.report;

import com.studentmaster.db.DatabaseConnector;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReportGenerator {

    private static final String TEMPLATE = "/reports/student_report.jrxml";
    private static final String LOGO = "/images/logo.png";
    private static final DateTimeFormatter STAMP =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH);

    private JasperReport compiled; // the template is compiled once and reused

    /**
     * @param fromId    first student ID to print (inclusive)
     * @param toId      last student ID to print (inclusive)
     * @param outputDir folder for the PDF (created if missing)
     * @return the generated PDF file
     */
    public Path generateStudentReport(int fromId, int toId, Path outputDir) {
        try (InputStream logo = ReportGenerator.class.getResourceAsStream(LOGO);
             Connection conn = DatabaseConnector.getConnection()) {

            Files.createDirectories(outputDir);

            Map<String, Object> params = new HashMap<>();
            params.put("ID_FROM", fromId);
            params.put("ID_TO", toId);
            params.put("LOGO", logo);
            params.put("GENERATED_ON", LocalDateTime.now().format(STAMP));

            JasperPrint print = JasperFillManager.fillReport(template(), params, conn);

            Path pdf = outputDir.resolve(String.format("StudentReport_%d-%d.pdf", fromId, toId));
            JasperExportManager.exportReportToPdfFile(print, pdf.toString());
            return pdf;
        } catch (JRException | SQLException | IOException e) {
            throw new ReportException("Could not generate the report: " + e.getMessage(), e);
        }
    }

    private synchronized JasperReport template() throws JRException, IOException {
        if (compiled == null) {
            try (InputStream jrxml = ReportGenerator.class.getResourceAsStream(TEMPLATE)) {
                if (jrxml == null) throw new IOException("Report template not found: " + TEMPLATE);
                compiled = JasperCompileManager.compileReport(jrxml);
            }
        }
        return compiled;
    }
}
