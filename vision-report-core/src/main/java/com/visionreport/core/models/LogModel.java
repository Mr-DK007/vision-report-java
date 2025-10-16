package com.visionreport.core.models;

import com.visionreport.api.Status;

/**
 * Internal data model representing a single log event (step) within a test case,
 * used by the Vision Report rendering engine.
 * <p>
 * Mirrors the API-level {@link com.visionreport.api.Log} but is designed
 * specifically for internal processing and FreeMarker template binding.
 * Contains only serializable, presentation-ready fields such as {@link Status},
 * formatted timestamps, and optional Base64-encoded media.
 * </p>
 *
 * <p><b>Usage:</b> Created and populated internally by {@code ReportGenerator}.
 * Not intended for direct use by end users.</p>
 *
 * @see com.visionreport.api.Log
 * @see com.visionreport.api.Status
 * @see MediaModel
 * @see com.visionreport.core.engine.ReportGenerator
 * 
 * @author Deepak
 * @version 1.1
 * @since 1.0
 */
public final class LogModel {

    /** Formatted timestamp representing when this log event occurred (e.g., "14:52:11"). */
    private String timestamp;

    /** Status of this log event (PASS, FAIL, SKIP, INFO, etc.). */
    private final Status status;

    /** Short title or name of this log step. */
    private String name;

    /** Descriptive details or message associated with this log entry. */
    private String details;

    /** Optional media (e.g., screenshot) associated with this log entry. */
    private MediaModel media;

    // ---------------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------------

    /**
     * Creates a new {@code LogModel} instance with a given status, name, and details.
     *
     * @param status  {@link Status} of the log entry (cannot be null)
     * @param name    short title or name of the log step
     * @param details descriptive message for the log (may be null)
     * @throws IllegalArgumentException if {@code status} is null
     */
    public LogModel(Status status, String name, String details) {
        if (status == null) {
            throw new IllegalArgumentException("Log status cannot be null");
        }
        this.status = status;
        this.name = (name != null) ? name.trim() : "";
        this.details = (details != null) ? details.trim() : "";
    }

    // ---------------------------------------------------------------------
    // Internal Setters (used by Core Layer / ReportGenerator)
    // ---------------------------------------------------------------------

    /**
     * Sets the formatted timestamp for this log entry.
     *
     * @param timestamp formatted timestamp string (e.g., "14:52:11")
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = (timestamp != null) ? timestamp.trim() : "";
    }

    /**
     * Updates the descriptive details for this log entry.
     *
     * @param details descriptive message (null values are converted to empty string)
     */
    public void setDetails(String details) {
        this.details = (details != null) ? details.trim() : "";
    }

    /**
     * Sets or updates the short name/title for this log entry.
     *
     * @param name short name of the log step
     */
    public void setName(String name) {
        this.name = (name != null) ? name.trim() : "";
    }

    /**
     * Attaches a {@link MediaModel} (e.g., screenshot or file) to this log entry.
     *
     * @param media associated media object (nullable)
     */
    public void setMedia(MediaModel media) {
        this.media = media;
    }

    // ---------------------------------------------------------------------
    // Getters (for FreeMarker / Template Rendering)
    // ---------------------------------------------------------------------

    /** 
     * Returns the formatted timestamp of this log entry.
     * 
     * @return formatted timestamp of this log event
     */
    public String getTimestamp() { return timestamp; }

    /** 
     * Returns the status of this log entry.
     * 
     * @return {@link Status} of this log entry
     */
    public Status getStatus() { return status; }

    /** 
     * Returns the descriptive details associated with this log entry.
     * 
     * @return descriptive details of this log event
     */
    public String getDetails() { return details; }

    /** 
     * Returns the short name or title of this log step.
     * 
     * @return short name/title of this log step
     */
    public String getName() { return name; }

    /** 
     * Returns any media attached to this log entry, such as a screenshot.
     * 
     * @return attached {@link MediaModel}, or null if none
     */
    public MediaModel getMedia() { return media; }

    // ---------------------------------------------------------------------
    // Utility
    // ---------------------------------------------------------------------

    /**
     * Returns a concise string representation for debugging and logging.
     *
     * @return string containing timestamp, status, details, and media title
     */
    @Override
    public String toString() {
        return "LogModel{" +
                "timestamp='" + timestamp + '\'' +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", details='" + details + '\'' +
                ", media=" + (media != null ? media.getTitle() : "none") +
                '}';
    }
}
