package com.visionreport.core.engine;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.visionreport.api.Log;
import com.visionreport.api.Status;
import com.visionreport.api.Test;
import com.visionreport.api.VisionReport;
import com.visionreport.core.models.ChartData;
import com.visionreport.core.models.ChartDataItem;
import com.visionreport.core.models.LogModel;
import com.visionreport.core.models.ReportModel;
import com.visionreport.core.models.SystemInfoModel;
import com.visionreport.core.models.TestModel;
import com.visionreport.core.models.MediaModel;

/**
 * The main engine responsible for converting API objects into the internal
 * {@link ReportModel} and generating the HTML report.
 * <p>
 * This class maps API-level {@link VisionReport}, {@link Test}, and {@link Log}
 * objects to core models, calculates statistics and chart data, and delegates
 * rendering to {@link FreeMarkerEngine}.
 * 
 * <p>
 * <b>Usage Example:</b>
 * <pre>
 * ReportGenerator generator = new ReportGenerator();
 * generator.generate(report, "output/vision-report.html");
 * </pre>
 * 
 * @author Deepak
 * @version 1.2
 * @since 1.0
 */
public final class ReportGenerator {
	
	/**
	 * Constructs a new {@code ReportGenerator}.
	 * <p>
	 * This constructor is intentionally public to allow instantiation
	 * of the report engine. No additional setup is required.
	 * </p>
	 */
	public ReportGenerator() {
	}

    /**
     * Generates the HTML report from the given API-level {@link VisionReport} and
     * writes it to the specified file path.
     * <p>
     * This method also calculates test statistics and builds chart data for
     * visualization.
     *
     * @param report   the fully populated VisionReport object
     * @param filePath the destination path for the generated HTML file
     */
    public void generate(VisionReport report, String filePath) {
        if (report == null) {
            throw new IllegalArgumentException("VisionReport cannot be null");
        }

        try {
            // Build internal report model
            ReportModel reportModel = buildReportModel(report);

            // Finalize test statuses and calculate statistics
            reportModel.getTests().forEach(TestModel::calculateFinalStatus);
            reportModel.calculateStats();

            // Build chart data and attach to the report model
            reportModel.setChartData(buildChartData(reportModel));

            // Generate HTML report
            String html = FreeMarkerEngine.getInstance().process(reportModel);

            // Ensure parent directories exist and write file
            File reportFile = new File(filePath);
            FileUtils.forceMkdirParent(reportFile);
            FileUtils.writeStringToFile(reportFile, html, StandardCharsets.UTF_8);

            System.out.println("Vision Report generated successfully at: " + reportFile.getAbsolutePath());

        } catch (Exception e) {
            System.err.println("VisionReport CRITICAL: Failed to generate report.");
            e.printStackTrace();
        }
    }

    /**
     * Builds the internal {@link ReportModel} from the API-level
     * {@link VisionReport}.
     *
     * @param report the API-level VisionReport
     * @return a populated internal {@link ReportModel}
     */
    private static ReportModel buildReportModel(VisionReport report) {
        ReportModel model = new ReportModel();
        model.setTitle(report.getTitle());

        if (report.getSystemInfo() != null) {
            report.getSystemInfo()
                    .forEach(info -> model.addSystemInfo(new SystemInfoModel(info.getKey(), info.getValue())));
        }

        if (report.getTests() != null) {
            report.getTests().forEach(test -> model.addTest(buildTestModel(test)));
        }

        return model;
    }

    /**
     * Builds the internal {@link TestModel} from the API-level {@link Test}.
     * <p>
     * This method maps authors, categories, logs, and calculates test duration.
     *
     * @param test the API-level Test object
     * @return a populated internal {@link TestModel}
     */
    private static TestModel buildTestModel(Test test) {
        String description = (test.getDescription() == null || test.getDescription().trim().isEmpty())
                ? "[No description provided]"
                : test.getDescription();
        TestModel model = new TestModel(test.getId(), test.getName(), description);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a").withZone(ZoneId.systemDefault());
        model.setStartTime(timeFormatter.format(test.getStartTime()));
        model.setEndTime(timeFormatter.format(test.getEndTime()));

        // Calculate and set test duration
        Duration duration = Duration.between(test.getStartTime(), test.getEndTime());
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        model.setDuration(String.format("%02d:%02d:%02d", hours, minutes, seconds));

        if (test.getAuthors() != null) {
            test.getAuthors().forEach(model::addAuthor);
        }

        if (test.getCategories() != null) {
            test.getCategories().forEach(model::addCategory);
        }

        if (test.getLogs() != null) {
            for (Log log : test.getLogs()) {
                model.addLog(buildLogModel(log));
            }
        }

        return model;
    }

    /**
     * Builds an internal {@link LogModel} from the API-level {@link Log} object.
     * <p>
     * This method handles all log content normalization — including HTML escaping,
     * stack trace formatting, and embedded media conversion. It ensures the
     * resulting {@link LogModel} is always valid and safe for rendering in the
     * final HTML report.
     *
     * <p>
     * <b>Key Features:</b>
     * <ul>
     * <li>Gracefully handles {@code null} fields (status, details, media, etc.).</li>
     * <li>Escapes all HTML characters to prevent markup corruption.</li>
     * <li>Formats stack traces for display in collapsible code blocks.</li>
     * <li>Automatically attaches Base64-encoded media when available.</li>
     * </ul>
     *
     * @param log the API-level {@link Log} object representing a single test event
     * @return a safe, fully formatted {@link LogModel} for the report
     */
    private static LogModel buildLogModel(Log log) {
        if (log == null) {
            // Defensive fallback: ensure no NPE propagates
            LogModel emptyModel = new LogModel(Status.FAIL, "[Invalid Log Entry]", "[Log object was null]");
            emptyModel.setTimestamp("00:00:00");
            return emptyModel;
        }

        try {
            // --- Normalize and sanitize core log data ---
            String logName = (log.getName() != null && !log.getName().trim().isEmpty())
                    ? escapeHtml(log.getName().trim())
                    : "[No log name provided]";

            String logDetails = (log.getDetails() != null && !log.getDetails().trim().isEmpty())
                    ? escapeHtml(log.getDetails().trim())
                    : "[No details provided]";

            // --- Handle Throwable (takes precedence over details) ---
            if (log.getThrowable() != null) {
                StringWriter sw = new StringWriter();
                try (PrintWriter pw = new PrintWriter(sw)) {
                    log.getThrowable().printStackTrace(pw);
                }
                String escapedStackTrace = escapeHtml(sw.toString());
                logDetails = "<pre class='stack-trace'>" + escapedStackTrace + "</pre>";
            }

            // --- Build and populate the LogModel ---
            LogModel model = new LogModel(log.getStatus(), logName, logDetails);
            model.setTimestamp(log.getTimestamp());

            // --- Handle Media Attachments ---
            if (log.getMedia() != null) { // Pass the entire provider object
                MediaModel mediaModel = MediaUtil.getMediaModel(log.getMedia());
                if (mediaModel != null) {
                    model.setMedia(mediaModel);
                }
            }

            return model;

        } catch (Exception e) {
            // Safety net for any unexpected issues during conversion
            System.err.println("[VisionReport ERROR] Failed to process log entry: " + e.getMessage());
            e.printStackTrace();

            LogModel fallbackModel = new LogModel(Status.FAIL, "[Log Processing Error]",
                    "An unexpected error occurred while building this log entry. See console for details.");
            fallbackModel.setTimestamp("00:00:00");
            return fallbackModel;
        }
    }

    /**
     * Escapes basic HTML characters to prevent markup corruption in the report.
     *
     * @param text the raw input text (may be {@code null})
     * @return a safely escaped string
     */
    private static String escapeHtml(String text) {
        if (text == null)
            return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'",
                "&#39;");
    }

    /**
     * Builds the {@link ChartData} for the report, including status summary and tag
     * distribution.
     *
     * @param reportModel the internal {@link ReportModel} containing all test data
     * @return a {@link ChartData} object ready to attach to the report
     */
    private ChartData buildChartData(ReportModel reportModel) {
        // Build Status Summary
        List<ChartDataItem> statusSummary = new ArrayList<>();
        if (reportModel.getPassCount() > 0)
            statusSummary.add(new ChartDataItem("Pass", reportModel.getPassCount(), "var(--success-color)"));
        if (reportModel.getFailCount() > 0)
            statusSummary.add(new ChartDataItem("Fail", reportModel.getFailCount(), "var(--danger-color)"));
        if (reportModel.getSkipCount() > 0)
            statusSummary.add(new ChartDataItem("Skip", reportModel.getSkipCount(), "var(--warning-color)"));

        // Build Tag Distribution
        Map<String, Long> tagCounts = reportModel.getTests().stream().flatMap(test -> test.getCategories().stream())
                .collect(Collectors.groupingBy(tag -> tag, Collectors.counting()));

        List<ChartDataItem> tagDistribution = tagCounts.entrySet().stream()
                .map(entry -> new ChartDataItem(entry.getKey(), entry.getValue(), null)).collect(Collectors.toList());

        return new ChartData(statusSummary, tagDistribution);
    }
}
