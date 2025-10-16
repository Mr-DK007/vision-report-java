package com.visionreport.api;

/**
 * <p>
 * Enumeration representing the possible statuses of a test case or a log event
 * within the <b>Vision Report</b> system.
 * </p>
 *
 * <p>
 * Each status conveys a specific semantic meaning and is visually
 * differentiated in the final HTML report. The status helps categorize the
 * outcome or state of a test step, test case, or logging event.
 * </p>
 *
 * <p>
 * <b>Example usage:</b>
 * </p>
 * 
 * <pre>
 * Test test = report.createTest("Login Test");
 * test.log(Status.PASS, "Login was successful");
 * </pre>
 *
 * <p>
 * <b>Version:</b> 1.0
 * </p>
 * 
 * @author Deepak
 * @since 1.0
 */
public enum Status {

	/**
	 * Indicates a successful outcome.
	 * <p>
	 * Typically used when a verification or validation step passes without errors.
	 * </p>
	 */
	PASS("PASS"),

	/**
	 * Indicates a failure in a test or step.
	 * <p>
	 * Usually used when an assertion fails or an exception is encountered during
	 * execution.
	 * </p>
	 */
	FAIL("FAIL"),

	/**
	 * Indicates that a test or step was deliberately skipped.
	 * <p>
	 * This can occur when a dependency test fails or a condition prevents
	 * execution.
	 * </p>
	 */
	SKIP("SKIP"),

	/**
	 * Represents general informational messages.
	 * <p>
	 * Useful for logging contextual details, configuration data, or checkpoints
	 * that are not directly tied to pass/fail criteria.
	 * </p>
	 */
	INFO("INFO");

	/** Readable string value of the status. */
	private final String value;

	Status(String value) {
		this.value = value;
	}

	/**
	 * Returns the string value associated with this status.
	 *
	 * @return a human-readable name for this status
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Returns the corresponding {@code Status} for a given string.
	 * <p>
	 * This method is case-insensitive and safe against invalid or null inputs. If
	 * the input does not match any known status, it defaults to {@link #INFO}.
	 * </p>
	 *
	 * @param status the string representation of the status
	 * @return the corresponding {@link Status}, or {@link #INFO} if not recognized
	 */
	public static Status fromString(String status) {
		if (status == null || status.trim().isEmpty()) {
			return INFO;
		}

		try {
			return Status.valueOf(status.trim().toUpperCase());
		} catch (IllegalArgumentException ex) {
			return INFO; // Fallback to INFO for unknown values
		}
	}

	@Override
	public String toString() {
		return value;
	}
}
