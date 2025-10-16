package com.visionreport.api;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a single piece of system information as a key-value pair.
 * <p>
 * This class is immutable and commonly used to store metadata about the
 * environment or execution context (e.g., browser version, OS, build number)
 * that appears in the Vision Report summary.
 * </p>
 *
 * <p>
 * <b>Example usage:</b>
 * </p>
 * 
 * <pre>
 * SystemInfo osInfo = new SystemInfo("Operating System", "Windows 11");
 * SystemInfo browserInfo = new SystemInfo("Browser", "Chrome 128");
 * </pre>
 *
 * @author Deepak
 * @version 1.1
 * @since 1.0
 */
public final class SystemInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	/** The key or name of the system property. */
	private final String key;

	/** The value associated with the system property key. */
	private final String value;

	/**
	 * Constructs a new {@code SystemInfo} instance with the specified key and
	 * value.
	 *
	 * @param key   the key or name of the system property (e.g., "OS", "Browser")
	 * @param value the value corresponding to the key (e.g., "Windows 11", "Chrome
	 *              128")
	 * @throws IllegalArgumentException if the key is {@code null} or empty
	 */
	public SystemInfo(String key, String value) {
		if (key == null || key.trim().isEmpty()) {
			throw new IllegalArgumentException("SystemInfo key cannot be null or empty");
		}
		this.key = key.trim();
		this.value = (value == null) ? "" : value.trim();
	}

	/**
	 * Returns the key of this system information entry.
	 *
	 * @return the key as a non-null string
	 */
	public String getKey() {
	    return key;
	}

	/**
	 * Returns the value associated with this system information entry.
	 *
	 * @return the value as a string (may be empty but never null)
	 */
	public String getValue() {
	    return value;
	}


	/**
	 * Returns a string representation of this {@code SystemInfo} entry in
	 * "key=value" format.
	 *
	 * @return a string representing this system info entry
	 */
	@Override
	public String toString() {
		return key + "=" + value;
	}

	/**
	 * Compares this {@code SystemInfo} to another object for equality. Two
	 * instances are considered equal if both their keys and values are identical.
	 *
	 * @param o the object to compare
	 * @return {@code true} if the given object represents the same key-value pair
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof SystemInfo))
			return false;
		SystemInfo that = (SystemInfo) o;
		return key.equals(that.key) && value.equals(that.value);
	}

	/**
	 * Returns a hash code based on the key and value.
	 *
	 * @return the hash code for this system info entry
	 */
	@Override
	public int hashCode() {
		return Objects.hash(key, value);
	}
}
