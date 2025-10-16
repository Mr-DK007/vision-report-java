package com.visionreport.api;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a single log entry within a test case in the Vision Report.
 *
 * <p>
 * Each {@code Log} corresponds to one step, checkpoint, or event recorded
 * during a test's execution. Logs are created automatically through the
 * {@link Test#log(Status, String, String)} and related methods.
 * </p>
 *
 * <ul>
 * <li>Timestamp of the event</li>
 * <li>Status (e.g., PASS, FAIL, INFO, WARNING, SKIP)</li>
 * <li>Step name or short description</li>
 * <li>Detailed message (optional)</li>
 * <li>Attached media such as screenshots (optional)</li>
 * <li>Associated exceptions or errors (optional)</li>
 * </ul>
 *
 * <b>Example usage:</b>
 * <pre>{@code
 * test.log(Status.PASS, "Verify Login", "User successfully logged in");
 * test.log(Status.FAIL, "Load Dashboard", "Timeout while loading", MediaProvider.fromString("screenshot.png"));
 * test.log(Status.FAIL, "Unexpected Error", new RuntimeException("Null pointer"));
 * }</pre>
 *
 * @author Deepak
 * @version 1.3
 * @since 1.0
 * @see Status
 * @see MediaProvider
 * @see Test
 */
public final class Log {

    /** The formatted timestamp when this log was created (hh:mm:ss a). */
    private final String timestamp;

    /** The status or outcome level associated with this log (e.g., PASS, FAIL, INFO). */
    private final Status status;

    /** The short name or label for the log entry. */
    private final String name;

    /** Detailed message or context for the log entry. */
    private final String details;

    /** Attached media (such as screenshots or video clips). May be {@code null}. */
    private final MediaProvider media;

    /** Throwable or exception captured with this log, if any. */
    private final Throwable throwable;

    /**
     * Package-private constructor.
     * Instances of {@code Log} are created exclusively by the {@link Test} class to
     * ensure consistency and proper metadata association.
     *
     * @param status    the {@link Status} level of this log (nullable — defaults to INFO)
     * @param name      the title or name of the log step (nullable — defaults to "[Unnamed Log]")
     * @param details   additional descriptive information (nullable — defaults to "[No details provided]")
     * @param media     optional attached {@link MediaProvider} (nullable)
     * @param throwable optional {@link Throwable} if the log records an exception (nullable)
     */
    Log(Status status, String name, String details, MediaProvider media, Throwable throwable) {
        this.timestamp = generateTimestamp();
        this.status = (status != null) ? status : Status.INFO;
        this.name = sanitizeOrDefault(name, "[Unnamed Log]");
        this.details = sanitizeOrDefault(details, "[No details provided]");
        this.media = media;
        this.throwable = throwable;
    }

    // ---------------------------------------------------------------------
    // Private Helpers
    // ---------------------------------------------------------------------

    /**
     * Safely generates a formatted timestamp.
     *
     * @return the formatted timestamp, or "00:00:00 AM" if formatting fails
     */
    private static String generateTimestamp() {
        try {
            return LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
        } catch (DateTimeParseException | IllegalArgumentException ex) {
            return "00:00:00 AM";
        }
    }

    /**
     * Returns a trimmed string or a fallback if null or blank.
     *
     * @param input    the string to sanitize
     * @param fallback default value to use if input is null or blank
     * @return sanitized, non-null string
     */
    private static String sanitizeOrDefault(String input, String fallback) {
        if (input == null) {
            return fallback;
        }
        String trimmed = input.trim();
        return trimmed.isEmpty() ? fallback : trimmed;
    }

    // ---------------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------------

    /**
     * Returns the timestamp when this log was created.
     *
     * @return the timestamp string in {@code hh:mm:ss a} format
     * <strong>NOTE: </strong> Useful for displaying when a specific step occurred within a test.
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the status associated with this log.
     *
     * @return the log status (never {@code null})
     * <strong>NOTE: </strong> Indicates the outcome level of the log (PASS, FAIL, INFO, etc.).
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Returns the name or short title of this log entry.
     *
     * @return the log name, guaranteed non-empty
     * <strong>NOTE: </strong> Useful for identifying the step or action performed.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns detailed information or context associated with this log.
     *
     * @return the log details, guaranteed non-null
     * <strong>NOTE: </strong> Can include messages, error explanations, or extra notes.
     */
    public String getDetails() {
        return details;
    }

    /**
     * Returns any attached media associated with this log.
     *
     * @return the media object or {@code null} if none is attached
     * <strong>NOTE: </strong> Can include screenshots, videos, or other visual evidence.
     */
    public MediaProvider getMedia() {
        return media;
    }

    /**
     * Returns any captured exception associated with this log.
     *
     * @return the throwable instance or {@code null} if none was recorded
     * <strong>NOTE: </strong> Provides insight into failures or unexpected behavior during the test step.
     */
    public Throwable getThrowable() {
        return throwable;
    }

    // ---------------------------------------------------------------------
    // Utility Methods
    // ---------------------------------------------------------------------

    /**
     * Returns a human-readable string representation of the log entry.
     *
     * @return a concise summary of the log entry including timestamp, status, name, details, and media/throwable info
     * <strong>NOTE: </strong> Useful for debugging, logging, or quick inspection of log content.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Log{");
        sb.append("timestamp='").append(timestamp).append('\'')
          .append(", status=").append(status)
          .append(", name='").append(name).append('\'')
          .append(", details='").append(details).append('\'')
          .append(", media=").append(media != null ? "Attached" : "None")
          .append(", throwable=").append(throwable != null ? throwable.getClass().getSimpleName() : "None")
          .append('}');
        return sb.toString();
    }
}
