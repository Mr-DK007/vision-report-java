package com.visionreport.core.models;

import com.visionreport.api.SystemInfo;

/**
 * Internal data model representing a system information entry for the Vision Report.
 * <p>
 * Mirrors the API-level {@link com.visionreport.api.SystemInfo} but is tailored
 * for internal processing and FreeMarker template rendering. Instances are
 * immutable and safe for internal data transfer.
 * </p>
 *
 * <p>
 * <b>Usage:</b> Typically constructed by the report generator from API-level
 * {@link SystemInfo} objects.
 * </p>
 *
 * @see com.visionreport.api.SystemInfo
 * @author Deepak
 * @version 1.1
 * @since 1.0
 */
public final class SystemInfoModel {

    /** The key or name of the system property (e.g., "Browser"). */
    private final String key;

    /** The corresponding value (e.g., "Chrome 128"). */
    private final String value;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructs a new immutable {@code SystemInfoModel}.
     *
     * @param key   the system property name
     * @param value the corresponding property value
     * @throws IllegalArgumentException if {@code key} is null or empty
     */
    public SystemInfoModel(String key, String value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("SystemInfo key cannot be null or empty");
        }
        this.key = key.trim();
        this.value = value != null ? value.trim() : "";
    }

    // -------------------------------------------------------------------------
    // Static Factory
    // -------------------------------------------------------------------------

    /**
     * Creates a {@code SystemInfoModel} from an API-level {@link SystemInfo} object.
     *
     * @param info the API-level {@link SystemInfo} instance
     * @return a new {@code SystemInfoModel} representing the same data
     * @throws IllegalArgumentException if {@code info} is null
     */
    public static SystemInfoModel from(SystemInfo info) {
        if (info == null) {
            throw new IllegalArgumentException("SystemInfo cannot be null");
        }
        return new SystemInfoModel(info.getKey(), info.getValue());
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /**
     * Returns the key (system property name).
     *
     * @return the key of this system info entry
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the value (system property value).
     *
     * @return the value of this system info entry
     */
    public String getValue() {
        return value;
    }


    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    /** Returns a concise string representation for debugging purposes. */
    @Override
    public String toString() {
        return "SystemInfoModel{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
