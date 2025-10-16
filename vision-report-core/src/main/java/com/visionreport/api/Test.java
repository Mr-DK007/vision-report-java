package com.visionreport.api;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a single test case within the <b>Vision Report</b> framework.
 * <p>
 * Each {@code Test} instance encapsulates all relevant information about a
 * specific test case, including metadata (authors, categories), execution
 * timing, and an ordered collection of {@link Log} entries representing test
 * steps or checkpoints.
 * </p>
 *
 * <p>
 * The class provides a fluent interface for logging events of various
 * {@link Status} levels (PASS, FAIL, INFO, SKIP) and attaching media or
 * exceptions to each log entry.
 * </p>
 *
 * <p>
 * <b>Example Usage:</b>
 * </p>
 * 
 * <pre>
 * VisionReport report = new VisionReport();
 * 
 * report.createTest("Login Test").assignAuthor("Deepak").assignCategory("Smoke", "UI")
 * 		.log(Status.INFO, "Open Browser", "Launching Chrome browser")
 * 		.log(Status.PASS, "Login", "User logged in successfully")
 * 		.logException(new RuntimeException("Dashboard timeout"));
 * </pre>
 *
 * @see Log
 * @see Status
 * @see MediaProvider
 * @author Deepak
 * @version 1.4
 * @since 1.0
 */
public final class Test {

	/** Counter used for generating unique test IDs (TC001, TC002, etc.). */
	private static final AtomicInteger ID_COUNTER = new AtomicInteger(0);

	/** Unique identifier for this test case. */
	private final String id;

	/** Display name of the test case. */
	private final String name;

	/** Optional descriptive text providing test context. */
	private String description;

	/** List of authors associated with this test. */
	private final List<String> authors = new ArrayList<>();

	/** List of categories or tags assigned to this test. */
	private final List<String> categories = new ArrayList<>();

	/** Ordered list of log entries captured during the test. */
	private final List<Log> logs = new ArrayList<>();

	/** Start time of the test execution. */
	private final Instant startTime;

	/** Last update time (typically the time of the last log entry). */
	private Instant endTime;

	// ---------------------------------------------------------------------
	// Constructors
	// ---------------------------------------------------------------------

	/**
	 * Package-private constructor for creating a test with an auto-generated ID.
	 *
	 * @param testName The display name of the test case.
	 */
	Test(String testName) {
		this(null, testName, null);
	}

	/**
	 * Package-private constructor for creating a test with a user-defined ID.
	 *
	 * @param id       The custom ID for this test.
	 * @param testName The display name of the test case.
	 */
	Test(String id, String testName) {
		this(id, testName, null);
	}

	/**
	 * Main internal constructor used for initialization.
	 *
	 * @param id          The custom or auto-generated test ID.
	 * @param testName    The name of the test case.
	 * @param description Optional description providing test details.
	 */
	Test(String id, String testName, String description) {
		this.id = (id == null || id.trim().isEmpty()) ? String.format("TC%03d", ID_COUNTER.incrementAndGet())
				: id.trim();

		this.name = (testName == null || testName.trim().isEmpty()) ? "Untitled Test" : testName.trim();

		this.description = (description == null) ? "" : description.trim();
		this.startTime = Instant.now();
		this.endTime = this.startTime;
	}

	// ---------------------------------------------------------------------
	// Fluent Metadata Setters
	// ---------------------------------------------------------------------

	/**
	 * Sets or updates the description for this test case.
	 *
	 * @param description A brief explanation of the test's purpose.
	 * @return The current {@link Test} instance for fluent chaining.
	 */
	public Test description(String description) {
		this.description = (description == null) ? "" : description.trim();
		return this;
	}

	/**
	 * Assigns one or more authors to this test case.
	 *
	 * @param authors One or more author names. Null or empty values are ignored.
	 * @return The current {@link Test} instance for fluent chaining.
	 */
	public Test assignAuthor(String... authors) {
		if (authors != null) {
			Arrays.stream(authors).filter(a -> a != null && !a.trim().isEmpty()).map(String::trim)
					.forEach(this.authors::add);
		}
		return this;
	}

	/**
	 * Assigns one or more categories or tags to this test case.
	 *
	 * @param categories One or more category names. Null or empty values are
	 *                   ignored.
	 * @return The current {@link Test} instance for fluent chaining.
	 */
	public Test assignCategory(String... categories) {
		if (categories != null) {
			Arrays.stream(categories).filter(c -> c != null && !c.trim().isEmpty()).map(String::trim)
					.forEach(this.categories::add);
		}
		return this;
	}

	// ---------------------------------------------------------------------
	// Logging Methods
	// ---------------------------------------------------------------------

	/**
	 * Logs an event with a name, details, and optional attached media.
	 *
	 * @param status  The {@link Status} of the log event. Must not be null.
	 * @param name    The title or name of the log step.
	 * @param details A detailed description for the log.
	 * @param media   Optional media to attach, created via
	 *                {@link MediaProvider#fromPath(String)}
	 * @return The current {@link Test} instance for fluent chaining.
	 * @throws IllegalArgumentException if {@code status} is null.
	 */
	public Test log(Status status, String name, String details, MediaProvider media) {
		validateStatus(status);
		this.endTime = Instant.now();

		String safeName = (name == null || name.trim().isEmpty()) ? "[Unnamed Step]" : name.trim();
		String safeDetails = (details == null) ? "" : details.trim();

		logs.add(new Log(status, safeName, safeDetails, media, null));
		return this;
	}

	/**
     * Logs an event with a name and details (no media).
     *
     * @param status  The {@link Status} of the log event. Must not be null.
     * @param name    The title or name of the log step.
     * @param details A detailed description for the log.
     * @return The current {@link Test} instance for fluent chaining.
     */
    public Test log(Status status, String name, String details) {
        return log(status, name, details, null);
    }

    /**
     * Logs a simple event with just a name.
     *
     * @param status The {@link Status} of the log event. Must not be null.
     * @param name   The title or name of the log step.
     * @return The current {@link Test} instance for fluent chaining.
     */
    public Test log(Status status, String name) {
        return log(status, name, null, null);
    }

	/**
	 * Logs a failure caused by a {@link Throwable}.
	 *
	 * @param throwable The exception or error to log.
	 * @return The current {@code Test} instance for fluent chaining.
	 */
	public Test logException(Throwable throwable) {
		return logException(throwable, null);
	}

	/**
	 * Logs a failure caused by an {@link Exception}.
	 *
	 * @param e The exception to log.
	 * @return The current {@code Test} instance for fluent chaining.
	 */
	public Test logException(Exception e) {
		return logException(e, null);
	}

	/**
	 * Logs a failure caused by a {@link Throwable} and attaches optional media.
	 *
	 * @param throwable The {@link Throwable} that occurred.
	 * @param media     Optional media attachment.
	 * @return The current {@code Test} instance for fluent chaining.
	 */
	public Test logException(Throwable throwable, MediaProvider media) {
		this.endTime = Instant.now();

		if (throwable == null) {
			logs.add(new Log(Status.FAIL, "Null exception passed",
					"[A null throwable was passed to the logException method]", media, null));
			return this;
		}

		String logName = (throwable.getMessage() != null && !throwable.getMessage().trim().isEmpty())
				? throwable.getMessage().trim()
				: "Exception Occurred";

		logs.add(new Log(Status.FAIL, logName, null, media, throwable));
		return this;
	}

	/**
     * Returns the unique test ID.
     *
     * @return The unique test ID (e.g., "TC001").
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the display name of this test case.
     *
     * @return The display name of this test case.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the optional description of this test.
     *
     * @return The description or context of the test.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the timestamp when the test started.
     *
     * @return The {@link Instant} representing the test start time.
     */
    public Instant getStartTime() {
        return startTime;
    }

    /**
     * Returns the timestamp when the last log was recorded.
     *
     * @return The {@link Instant} representing the last log time.
     */
    public Instant getEndTime() {
        return endTime;
    }

    /**
     * Returns an unmodifiable list of all log entries recorded for this test.
     *
     * @return A read-only {@link List} of {@link Log} objects.
     */
    public List<Log> getLogs() {
        return Collections.unmodifiableList(logs);
    }

    /**
     * Returns an unmodifiable list of all authors assigned to this test.
     *
     * @return A read-only {@link List} of author names.
     */
    public List<String> getAuthors() {
        return Collections.unmodifiableList(authors);
    }

    /**
     * Returns an unmodifiable list of all categories assigned to this test.
     *
     * @return A read-only {@link List} of category names.
     */
    public List<String> getCategories() {
        return Collections.unmodifiableList(categories);
    }

	// ---------------------------------------------------------------------
	// Validation & Utility
	// ---------------------------------------------------------------------

	/**
	 * Ensures that a {@link Status} value is provided before logging.
	 *
	 * @param status The status to validate.
	 * @throws IllegalArgumentException if {@code status} is null.
	 */
	private void validateStatus(Status status) {
		if (status == null) {
			throw new IllegalArgumentException("Status cannot be null");
		}
	}

	/** @return A concise summary for debugging or logging purposes. */
	@Override
	public String toString() {
		return String.format("Test{id='%s', name='%s', logs=%d, authors=%s, categories=%s}", id, name, logs.size(),
				authors, categories);
	}
}
