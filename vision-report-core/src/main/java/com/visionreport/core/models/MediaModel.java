package com.visionreport.core.models;

import java.io.Serializable;

/**
 * Internal data model representing a fully processed media item (screenshot,
 * visual artifact, or file preview) within the Vision Report.
 * <p>
 * Each instance is <b>presentation-ready</b> and serializable for template rendering.
 * This class contains a Base64-encoded representation of the content and an optional
 * descriptive title for display within the report UI.
 * </p>
 *
 * <h2>Design Intent</h2>
 * <ul>
 *   <li>Immutable and thread-safe.</li>
 *   <li>Does <b>not</b> perform any I/O or encoding logic — handled externally by
 *       {@code com.visionreport.core.engine.MediaUtil}.</li>
 *   <li>Used internally by the report generator after processing user-supplied media.</li>
 * </ul>
 *
 * <h3>Example (internal usage):</h3>
 * <pre>{@code
 * MediaProvider provider = MediaProvider.fromFile("screenshots/login.png");
 * MediaModel model = MediaUtil.toMediaModel(provider);
 * // model.getBase64() => "data:image/png;base64,..."
 * }</pre>
 *
 * @see com.visionreport.core.engine.MediaUtil
 * @see com.visionreport.core.models.LogModel
 * 
 * @author Deepak
 * @version 2.0
 * @since 1.0
 */
public final class MediaModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Base64-encoded media content, prefixed with data URI schema. */
    private final String base64;

    /** Optional title or description displayed in the report UI. */
    private final String title;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructs a new immutable {@code MediaModel} instance.
     *
     * @param base64 Base64-encoded content, already prefixed with MIME type (cannot be null)
     * @param title  Optional descriptive title (nullable)
     */
    public MediaModel(String base64, String title) {
        this.base64 = base64 != null ? base64.trim() : "";
        this.title = title != null ? title.trim() : "";
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /**
     * Returns the Base64-encoded media data.
     * <p>
     * Example: {@code data:image/png;base64,iVBORw0K...}
     * </p>
     *
     * @return Base64-encoded string suitable for HTML embedding (never null)
     */
    public String getBase64() {
        return base64;
    }

    /**
     * Returns the human-readable title or label for this media item.
     *
     * @return title string (may be empty but never null)
     */
    public String getTitle() {
        return title;
    }

    // -------------------------------------------------------------------------
    // Utility Methods
    // -------------------------------------------------------------------------

    /**
     * Returns whether this model contains valid Base64 media content.
     *
     * @return {@code true} if Base64 data is non-empty; {@code false} otherwise
     */
    public boolean hasMedia() {
        return base64 != null && !base64.isEmpty();
    }

    /**
     * Returns a safe string representation for debugging or logging.
     *
     * @return concise string with title and Base64 length
     */
    @Override
    public String toString() {
        return "MediaModel{" +
                "title='" + title + '\'' +
                ", base64Length=" + (base64 != null ? base64.length() : 0) +
                '}';
    }
}
