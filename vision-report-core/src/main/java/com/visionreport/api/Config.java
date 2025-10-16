package com.visionreport.api;

import java.util.Objects;

/**
 * Provides a fluent API for configuring global metadata of the
 * {@link VisionReport}.
 * <p>
 * This class acts as a builder-style configuration interface that allows setting
 * high-level contextual details such as project name, environment, tester name,
 * and other attributes that appear in the final report header.
 * </p>
 *
 * <p>
 * Instances of {@code Config} are obtained internally through the
 * {@link VisionReport} class and are not intended to be instantiated directly.
 * </p>
 *
 * <p><b>Example usage:</b></p>
 * 
 * <pre>
 * VisionReport report = new VisionReport();
 * report.config()
 *       .setTitle("Vision Report - UI Regression")
 *       .setProjectName("Vision Reporting System")
 *       .setApplicationName("Automation Suite")
 *       .setEnvironment("Staging")
 *       .setTesterName("Deepak")
 *       .setBrowser("Chrome 128")
 *       .addCustomInfo("Build Version", "1.0.0");
 * </pre>
 *
 * @author Deepak
 * @version 1.2
 * @since 1.0
 */
public final class Config {

    /** The associated {@link VisionReport} instance being configured. */
    private final VisionReport report;

    /**
     * Package-private constructor to restrict instantiation to the {@link VisionReport} class.
     *
     * @param report the {@link VisionReport} instance to configure
     * @throws NullPointerException if {@code report} is {@code null}
     */
    Config(VisionReport report) {
        this.report = Objects.requireNonNull(report, "VisionReport reference cannot be null");
    }

    // ---------------------------------------------------------------------
    // Fluent Configuration Methods
    // ---------------------------------------------------------------------

    /**
     * Sets the main title of the Vision Report.
     *
     * @param title the title to display at the top of the report (can be {@code null})
     * @return the current {@code Config} instance for fluent chaining
     */
    public Config setTitle(String title) {
        safe(() -> report.setTitle(title));
        return this;
    }

    /**
     * Sets the project name associated with the test execution.
     *
     * @param name the project name (can be {@code null})
     * @return the current {@code Config} instance for fluent chaining
     */
    public Config setProjectName(String name) {
        safe(() -> report.setProjectName(name));
        return this;
    }

    /**
     * Sets the application name under test.
     *
     * @param name the application name (can be {@code null})
     * @return the current {@code Config} instance for fluent chaining
     */
    public Config setApplicationName(String name) {
        safe(() -> report.setApplicationName(name));
        return this;
    }

    /**
     * Sets the environment in which tests are executed (e.g., QA, Staging, Production).
     *
     * @param environment the test environment name (can be {@code null})
     * @return the current {@code Config} instance for fluent chaining
     */
    public Config setEnvironment(String environment) {
        safe(() -> report.setEnvironment(environment));
        return this;
    }

    /**
     * Sets the tester or automation engineer responsible for the execution.
     *
     * @param testerName the tester’s name (can be {@code null})
     * @return the current {@code Config} instance for fluent chaining
     */
    public Config setTesterName(String testerName) {
        safe(() -> report.setTesterName(testerName));
        return this;
    }

    /**
     * Sets the browser or execution platform used for testing.
     *
     * @param browser the browser or platform name (e.g., "Chrome", "Firefox", "API Runner")
     * @return the current {@code Config} instance for fluent chaining
     */
    public Config setBrowser(String browser) {
        safe(() -> report.setBrowser(browser));
        return this;
    }

    /**
     * Adds a custom key-value metadata entry to the report’s system information section.
     * <p>
     * This can be used for arbitrary contextual information such as build number,
     * operating system, or device details.
     * </p>
     *
     * @param key   the metadata key (e.g., "Build Version")
     * @param value the corresponding value (e.g., "1.0.0-SNAPSHOT")
     * @return the current {@code Config} instance for fluent chaining
     * @throws IllegalArgumentException if {@code key} is {@code null} or empty
     */
    public Config addCustomInfo(String key, String value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Custom info key cannot be null or empty");
        }
        safe(() -> report.addCustomInfo(key.trim(), (value == null) ? "" : value.trim()));
        return this;
    }

    // ---------------------------------------------------------------------
    // Internal Utility
    // ---------------------------------------------------------------------

    /**
     * Safely executes a configuration operation, suppressing unexpected runtime exceptions
     * to ensure that configuration chaining is resilient.
     *
     * @param action the configuration action to perform
     */
    private void safe(Runnable action) {
        try {
            action.run();
        } catch (RuntimeException ex) {
            // Defensive guard: do not break fluent chain
            System.err.println("[VisionReport Config] Warning: " + ex.getMessage());
        }
    }
}
