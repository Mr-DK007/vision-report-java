package com.visionreport.core.models;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.visionreport.api.Status;

/**
 * Represents the internal data model for an entire Vision Report.
 * <p>
 * Aggregates system information, test cases, statistics, and chart data, and
 * serves as the primary data source for FreeMarker template rendering.
 * Populated by {@link com.visionreport.core.engine.ReportGenerator} from
 * API-level {@link com.visionreport.api.VisionReport} data.
 * </p>
 *
 * <p>
 * Includes metadata such as report title, generation date and time, and summary
 * statistics (pass/fail/skip counts). Can also hold {@link ChartData} for
 * visual analytics.
 * </p>
 *
 * @see SystemInfoModel
 * @see TestModel
 * @see Status
 * @see ChartData
 * @author Deepak
 * @version 1.1
 * @since 1.0
 */
public final class ReportModel {

    /** Report title; defaults to "Automation Test Report". */
    private String title;

    /** Formatted report generation date. */
    private final String reportDate;

    /** Formatted report generation time. */
    private final String reportTime;

    /** List of system/environment information entries. */
    private final List<SystemInfoModel> systemInfo = new ArrayList<>();

    /** List of all test cases in the report. */
    private final List<TestModel> tests = new ArrayList<>();

    /** Chart data for visualization (status summaries, tag distributions, etc.). */
    private ChartData chartData;

    /** Number of passed tests. */
    private long passCount;

    /** Number of failed tests. */
    private long failCount;

    /** Number of skipped tests. */
    private long skipCount;

    /** Total number of tests in the report. */
    private long totalTests;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Constructs a new {@code ReportModel} with default title and initializes
     * report date/time to the current system time.
     */
    public ReportModel() {
        this.title = "Automation Test Report";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a z");
        ZonedDateTime now = ZonedDateTime.now();
        this.reportDate = now.format(dateFormatter);
        this.reportTime = now.format(timeFormatter);
    }

    // -------------------------------------------------------------------------
    // Internal Operations
    // -------------------------------------------------------------------------

    /**
     * Calculates summary statistics for the report, including counts of passed,
     * failed, and skipped tests.
     * <p>
     * Should be called after all tests are added to ensure accurate statistics.
     * </p>
     */
    public void calculateStats() {
        this.passCount = tests.stream().filter(t -> t.getStatus() == Status.PASS).count();
        this.failCount = tests.stream().filter(t -> t.getStatus() == Status.FAIL).count();
        this.skipCount = tests.stream().filter(t -> t.getStatus() == Status.SKIP).count();
        this.totalTests = tests.size();
    }

    /**
     * Sets the report title.
     *
     * @param title desired report title
     */
    public void setTitle(String title) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
    }

    /**
     * Adds a system information entry.
     *
     * @param info system info entry to add
     */
    public void addSystemInfo(SystemInfoModel info) {
        if (info != null) {
            this.systemInfo.add(info);
        }
    }

    /**
     * Adds a test case to the report.
     *
     * @param test test case to add
     */
    public void addTest(TestModel test) {
        if (test != null) {
            this.tests.add(test);
        }
    }

    /**
     * Sets the chart data for the report.
     *
     * @param chartData chart data for visualization
     */
    public void setChartData(ChartData chartData) {
        this.chartData = chartData;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /**
     * Returns the report title.
     *
     * @return the title of the report
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the formatted report generation date.
     *
     * @return report generation date as a string (e.g., "15 Oct, 2025")
     */
    public String getReportDate() {
        return reportDate;
    }

    /**
     * Returns the formatted report generation time.
     *
     * @return report generation time as a string (e.g., "10:13:22 PM IST")
     */
    public String getReportTime() {
        return reportTime;
    }

    /**
     * Returns an unmodifiable list of all system/environment information entries.
     *
     * @return list of {@link SystemInfoModel} objects
     */
    public List<SystemInfoModel> getSystemInfo() {
        return Collections.unmodifiableList(systemInfo);
    }

    /**
     * Returns an unmodifiable list of all test cases in the report.
     *
     * @return list of {@link TestModel} objects
     */
    public List<TestModel> getTests() {
        return Collections.unmodifiableList(tests);
    }

    /**
     * Returns the chart data associated with the report.
     *
     * @return {@link ChartData} for visualization, or null if not set
     */
    public ChartData getChartData() {
        return chartData;
    }

    /**
     * Returns the number of tests that passed.
     *
     * @return count of passed tests
     */
    public long getPassCount() {
        return passCount;
    }

    /**
     * Returns the number of tests that failed.
     *
     * @return count of failed tests
     */
    public long getFailCount() {
        return failCount;
    }

    /**
     * Returns the number of tests that were skipped.
     *
     * @return count of skipped tests
     */
    public long getSkipCount() {
        return skipCount;
    }

    /**
     * Returns the total number of tests in the report.
     *
     * @return total test count
     */
    public long getTotalTests() {
        return totalTests;
    }


    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "ReportModel{" +
                "title='" + title + '\'' +
                ", reportDate='" + reportDate + '\'' +
                ", reportTime='" + reportTime + '\'' +
                ", systemInfoCount=" + systemInfo.size() +
                ", testsCount=" + tests.size() +
                ", passCount=" + passCount +
                ", failCount=" + failCount +
                ", skipCount=" + skipCount +
                ", totalTests=" + totalTests +
                ", chartData=" + (chartData != null ? chartData : "null") +
                '}';
    }
}
