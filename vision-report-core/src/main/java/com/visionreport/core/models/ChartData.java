package com.visionreport.core.models;

import java.util.Collections;
import java.util.List;

/**
 * Internal model representing all dynamically generated chart data for a Vision Report.
 * <p>
 * This class holds information required for rendering summary charts in the HTML report,
 * such as test status distribution and tag/category distribution.
 * </p>
 *
 * <p>
 * Typically constructed by the report generator and fed to the reporting engine's charting
 * components. This class is immutable from an external perspective.
 * </p>
 *
 * @see ChartDataItem
 * @author Deepak
 * @version 1.1
 * @since 1.0
 */
public final class ChartData {

    /** Chart items representing the test status summary (PASS, FAIL, SKIP, etc.). */
    private final List<ChartDataItem> statusSummary;

    /** Chart items representing the distribution of tags/categories across tests. */
    private final List<ChartDataItem> tagDistribution;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructs a new {@code ChartData} instance.
     *
     * @param statusSummary   list of status summary chart items
     * @param tagDistribution list of tag/category distribution chart items
     */
    public ChartData(List<ChartDataItem> statusSummary, List<ChartDataItem> tagDistribution) {
        this.statusSummary = statusSummary != null ? List.copyOf(statusSummary) : Collections.emptyList();
        this.tagDistribution = tagDistribution != null ? List.copyOf(tagDistribution) : Collections.emptyList();
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /**
     * Returns the chart items representing test status summary.
     *
     * @return unmodifiable list of {@link ChartDataItem} for status summary
     */
    public List<ChartDataItem> getStatusSummary() {
        return statusSummary;
    }

    /**
     * Returns the chart items representing tag/category distribution.
     *
     * @return unmodifiable list of {@link ChartDataItem} for tag distribution
     */
    public List<ChartDataItem> getTagDistribution() {
        return tagDistribution;
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "ChartData{" +
                "statusSummary=" + statusSummary +
                ", tagDistribution=" + tagDistribution +
                '}';
    }
}
