package com.visionreport.core.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.visionreport.api.Status;

/**
 * Internal data model representing a single test case within a Vision Report.
 * <p>
 * This model is populated by the report generation engine from API-level
 * {@link com.visionreport.api.Test} objects.
 * </p>
 * <p>
 * It holds test metadata, execution timestamps, duration, authors, categories,
 * logs, and the final status.
 * </p>
 * 
 * @author Deepak
 * @version 1.1
 * @since 1.0
 */
public final class TestModel {

    /** Unique test ID */
    private final String id;

    /** Name of the test */
    private final String name;

    /** Optional description */
    private final String description;

    /** Test start time (formatted string) */
    private String startTime;

    /** Test end time (formatted string) */
    private String endTime;

    /** Test duration (formatted string) */
    private String duration;

    /** Test authors */
    private final List<String> authors = new ArrayList<>();

    /** Test categories/tags */
    private final List<String> categories = new ArrayList<>();

    /** Test logs */
    private final List<LogModel> logs = new ArrayList<>();

    /** Computed final status of the test */
    private Status status;

    /**
     * Constructs a new {@code TestModel} instance.
     *
     * @param id          unique test identifier
     * @param name        test name
     * @param description test description
     * @throws IllegalArgumentException if id or name is null/empty
     */
    public TestModel(String id, String name, String description) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("TestModel id cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("TestModel name cannot be null or empty");
        }

        this.id = id.trim();
        this.name = name.trim();
        this.description = description != null ? description.trim() : "";
        this.status = Status.SKIP; // default
    }

    /**
     * Returns the unique test ID.
     *
     * @return test ID string
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name of the test.
     *
     * @return test name string
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the test.
     *
     * @return test description string
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the formatted start time of the test.
     *
     * @return start time string, or null if not set
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time of the test.
     *
     * @param startTime formatted start time string
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * Returns the formatted end time of the test.
     *
     * @return end time string, or null if not set
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Sets the end time of the test.
     *
     * @param endTime formatted end time string
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * Returns the duration of the test.
     *
     * @return duration string, or null if not set
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Sets the duration of the test.
     *
     * @param duration formatted duration string
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * Adds an author to this test.
     *
     * @param author name of the author to add
     */
    public void addAuthor(String author) {
        if (author != null && !author.trim().isEmpty()) {
            authors.add(author.trim());
        }
    }

    /**
     * Returns an unmodifiable list of authors for this test.
     *
     * @return list of author names
     */
    public List<String> getAuthors() {
        return Collections.unmodifiableList(authors);
    }

    /**
     * Adds a category or tag to this test.
     *
     * @param category category name to add
     */
    public void addCategory(String category) {
        if (category != null && !category.trim().isEmpty()) {
            categories.add(category.trim());
        }
    }

    /**
     * Returns an unmodifiable list of categories/tags for this test.
     *
     * @return list of category names
     */
    public List<String> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    /**
     * Adds a log entry to this test.
     *
     * @param log {@link LogModel} instance to add
     */
    public void addLog(LogModel log) {
        if (log != null) {
            logs.add(log);
        }
    }

    /**
     * Returns an unmodifiable list of logs for this test.
     *
     * @return list of {@link LogModel} entries
     */
    public List<LogModel> getLogs() {
        return Collections.unmodifiableList(logs);
    }

    /**
     * Sets the final status of the test.
     *
     * @param status {@link Status} to set (defaults to SKIP if null)
     */
    public void setStatus(Status status) {
        this.status = status != null ? status : Status.SKIP;
    }

    /**
     * Returns the final status of the test.
     *
     * @return {@link Status} of the test
     */
    public Status getStatus() {
        return status;
    }


    /**
     * Calculates the final status based on logs.
     * <p>
     * - PASS if all logs passed  
     * - FAIL if any log failed  
     * - SKIP if there are no logs
     * </p>
     */
    public void calculateFinalStatus() {
        if (logs.isEmpty()) {
            status = Status.SKIP;
        } else if (logs.stream().anyMatch(log -> log.getStatus() == Status.FAIL)) {
            status = Status.FAIL;
        } else {
            status = Status.PASS;
        }
    }

    /** String representation for debugging */
    @Override
    public String toString() {
        return "TestModel{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", description='" + description + '\''
                + ", startTime='" + startTime + '\'' + ", endTime='" + endTime + '\'' + ", duration='" + duration + '\''
                + ", authors=" + authors + ", categories=" + categories + ", logsCount=" + logs.size() + ", status="
                + status + '}';
    }
}
