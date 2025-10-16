package com.visionreport.core.models;

/**
 * Represents a single data point in a chart used within the Vision Report.
 * <p>
 * Each item corresponds to a visual element in charts such as pie charts,
 * bar charts, or other report visualizations.
 * </p>
 *
 * <p>
 * Example: a pie chart slice for "PASS" tests with a count of 120 and a specific color.
 * </p>
 * 
 * <b>Design Notes:</b>
 * <ul>
 * <li>Immutable once created.</li>
 * <li>Safe for use in multi-threaded rendering pipelines.</li>
 * </ul>
 * 
 * @author Deepak
 * @version 1.1
 * @since 1.0
 */
public final class ChartDataItem {

    /** The label of this chart item (e.g., "PASS", "FAIL", "Regression"). */
    private final String label;

    /** The numeric value of this chart item (e.g., count of tests). */
    private final long value;

    /** Optional color associated with this item (e.g., for pie chart slices). */
    private final String color;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructs a new {@code ChartDataItem}.
     *
     * @param label the label for the chart item (cannot be null or empty)
     * @param value the numeric value for the chart item
     * @param color optional color code (may be null or empty)
     */
    public ChartDataItem(String label, long value, String color) {
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("ChartDataItem label cannot be null or empty");
        }
        this.label = label.trim();
        this.value = value;
        this.color = color != null ? color.trim() : "";
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /**
     * Returns the label of this chart item.
     *
     * @return label string
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the numeric value of this chart item.
     *
     * @return value as long
     */
    public long getValue() {
        return value;
    }

    /**
     * Returns the color associated with this chart item.
     *
     * @return color string (may be empty)
     */
    public String getColor() {
        return color;
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "ChartDataItem{" +
                "label='" + label + '\'' +
                ", value=" + value +
                ", color='" + color + '\'' +
                '}';
    }
}
