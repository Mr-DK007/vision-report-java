package com.visionreport.api;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.visionreport.core.engine.ReportGenerator;

/**
 * The central data container for a Vision Report execution.
 * <p>
 * This class manages test suites, configuration metadata, and report generation.
 * A {@code VisionReport} instance collects all tests, system information, and
 * metadata before being rendered into an HTML report via the {@link #flush()} methods.
 * </p>
 *
 * <p><b>Example usage:</b></p>
 * 
 * <pre>
 * VisionReport report = new VisionReport();
 *
 * report.config()
 *       .setTitle("Vision Report - Regression Suite")
 *       .setProjectName("Vision Report Framework")
 *       .setEnvironment("Staging")
 *       .setTesterName("Deepak")
 *       .addCustomInfo("Build", "v1.0.0");
 *
 * report.createTest("Login Test")
 *       .assignAuthor("Deepak")
 *       .assignCategory("Smoke")
 *       .log(Status.PASS, "Login successful");
 *
 * report.flush(); // or report.flush("reports");
 * </pre>
 *
 * @author Deepak
 * @version 1.2
 * @since 1.0
 */
public final class VisionReport {

    /** Default folder for generated reports. */
    private static final String DEFAULT_OUTPUT_DIR = "vision-reports";

    /** Default title if none provided. */
    private static final String DEFAULT_TITLE = "Automation Test Report";

    /** Report title displayed in the generated HTML header. */
    private String title = DEFAULT_TITLE;

    /** Collection of all system/environment metadata entries. */
    private final List<SystemInfo> systemInfo = new ArrayList<>();

    /** Collection of all test cases included in this report. */
    private final List<Test> tests = new ArrayList<>();

    /** Internal configuration handler for fluent metadata setup. */
    private final Config config = new Config(this);

    /** Guard to avoid recursive flush fallback loops. */
    private boolean flushInProgress = false;
    
    /**
     * Constructs a new {@code VisionReport} instance.
     * <p>
     * Initializes the report with default title, empty system info, and empty test list.
     * Use {@link #config()} to customize metadata and {@link #createTest(String)} to add tests.
     * </p>
     */
    public VisionReport() {
    }


    // ---------------------------------------------------------------------
    // Configuration API
    // ---------------------------------------------------------------------

    /**
     * Returns the configuration interface for setting up report metadata.
     *
     * @return the {@link Config} instance for fluent chaining
     */
    public Config config() {
        return this.config;
    }

    // ---------------------------------------------------------------------
    // Test Management
    // ---------------------------------------------------------------------

    /**
     * Creates a new test case with an auto-generated ID (e.g., {@code TC-1}, {@code TC-2}).
     *
     * @param testName the name of the test case
     * @return a new {@link Test} instance
     * @throws IllegalArgumentException if {@code testName} is null or blank
     */
    public Test createTest(String testName) {
        if (testName == null || testName.trim().isEmpty()) {
            throw new IllegalArgumentException("Test name cannot be null or empty");
        }
        Test test = new Test(testName.trim());
        this.tests.add(test);
        return test;
    }

    /**
     * Creates a new test case with a custom identifier.
     *
     * @param id       the custom test ID (e.g., "API-001")
     * @param testName the display name of the test
     * @return a new {@link Test} instance
     * @throws IllegalArgumentException if {@code testName} is null or blank
     */
    public Test createTest(String id, String testName) {
        if (testName == null || testName.trim().isEmpty()) {
            throw new IllegalArgumentException("Test name cannot be null or empty");
        }
        Test test = new Test(id, testName.trim());
        this.tests.add(test);
        return test;
    }

    // ---------------------------------------------------------------------
    // Internal Configuration Methods
    // ---------------------------------------------------------------------

    void setTitle(String title) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
    }

    void setProjectName(String name) {
        addSystemInfo("standard_project", name);
    }

    void setApplicationName(String name) {
        addSystemInfo("standard_application", name);
    }

    void setEnvironment(String environment) {
        addSystemInfo("standard_environment", environment);
    }

    void setTesterName(String testerName) {
        addSystemInfo("standard_tester", testerName);
    }

    void setBrowser(String browser) {
        addSystemInfo("standard_browser", browser);
    }

    void addCustomInfo(String key, String value) {
        addSystemInfo(key, value);
    }

    private void addSystemInfo(String key, String value) {
        if (key == null || key.trim().isEmpty()) {
            return;
        }
        String safeValue = (value == null || value.trim().isEmpty()) ? "Not provided" : value.trim();
        this.systemInfo.add(new SystemInfo(key.trim(), safeValue));
    }

    // ---------------------------------------------------------------------
    // Report Generation
    // ---------------------------------------------------------------------

    /**
     * Generates the report in the default folder {@code vision-reports} using
     * a timestamped filename.
     */
    public void flush() {
        flush(DEFAULT_OUTPUT_DIR);
    }

    /**
     * Flushes the report to a given directory or full HTML path.
     * <ul>
     *   <li>If the path ends with <b>.html</b> or <b>.htm</b>, it is treated as a full file path.</li>
     *   <li>Otherwise, it is treated as a folder path where a timestamped file will be generated.</li>
     * </ul>
     *
     * @param pathOrFileName the directory or file path
     */
    public void flush(String pathOrFileName) {
        if (flushInProgress) return; // prevent recursion
        flushInProgress = true;

        try {
            if (pathOrFileName == null || pathOrFileName.trim().isEmpty()) {
                flush(DEFAULT_OUTPUT_DIR);
                return;
            }

            Path path = Paths.get(pathOrFileName.trim());
            if (pathOrFileName.toLowerCase().endsWith(".html") || pathOrFileName.toLowerCase().endsWith(".htm")) {
                Path parent = (path.getParent() != null) ? path.getParent() : Paths.get(".");
                flushInternal(parent, path.getFileName().toString());
            } else {
                String fileName = generateTimestampedFileName(this.title);
                flushInternal(path, fileName);
            }

        } catch (InvalidPathException e) {
            System.err.println("[VisionReport ERROR] Invalid path: " + e.getMessage());
            flush(DEFAULT_OUTPUT_DIR);
        } catch (SecurityException e) {
            System.err.println("[VisionReport ERROR] Permission denied for: " + pathOrFileName);
            flush(DEFAULT_OUTPUT_DIR);
        } finally {
            flushInProgress = false;
        }
    }

    private void flushInternal(Path folder, String fileName) {
        try {
            if (!Files.exists(folder)) {
                Files.createDirectories(folder);
            }

            Path fullPath = folder.resolve(fileName.trim());
            new ReportGenerator().generate(this, fullPath.toString());

            System.out.println("[VisionReport SUCCESS] Report generated at: " + fullPath.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("[VisionReport ERROR] I/O failure: " + e.getMessage());
        } catch (SecurityException e) {
            System.err.println("[VisionReport ERROR] No write permissions for: " + folder);
        } catch (Exception e) {
            System.err.println("[VisionReport CRITICAL] Unexpected error: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------------
    // Utilities
    // ---------------------------------------------------------------------

    private String generateTimestampedFileName(String baseTitle) {
        String titlePart = (baseTitle == null || baseTitle.trim().isEmpty()) ? ""
                : " - " + baseTitle.trim().replaceAll("[\\\\/:*?\"<>|]", "_");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy - hh-mm-ss a"));
        return "VR" + titlePart + " - " + timestamp + ".html";
    }

    // ---------------------------------------------------------------------
    // Accessors
    // ---------------------------------------------------------------------

    /**
     * Returns the report title.
     *
     * @return the report title string
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns an unmodifiable list of system/environment metadata entries.
     *
     * @return list of {@link SystemInfo} objects
     */
    public List<SystemInfo> getSystemInfo() {
        return Collections.unmodifiableList(systemInfo);
    }

    /**
     * Returns an unmodifiable list of test cases included in this report.
     *
     * @return list of {@link Test} objects
     */
    public List<Test> getTests() {
        return Collections.unmodifiableList(tests);
    }

}
