package com.visionreport.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a wrapper for media content (such as images, videos, or
 * screenshots) used within Vision Report.
 *
 * <p>
 * This class allows users to attach media in multiple formats:
 * </p>
 * <ul>
 * <li>Local file path</li>
 * <li>Remote URL</li>
 * <li>Base64-encoded string</li>
 * <li>Byte array (e.g., from automation screenshots)</li>
 * </ul>
 *
 * <p>
 * Internally, the Vision Report engine interprets this source and embeds it
 * appropriately in the generated report.
 * </p>
 *
 * <p>
 * <b>Example usage:</b>
 * </p>
 * 
 * <pre>{@code
 * MediaProvider fromFile = MediaProvider.fromPath("screenshots/login.png");
 * MediaProvider fromUrl = MediaProvider.fromUrl("https://example.com/image.png");
 * MediaProvider fromBase64 = MediaProvider.fromBase64("iVBORw0KGgoAAAANSUhEUg...");
 * MediaProvider fromBytes = MediaProvider.fromBytes(screenshotBytes);
 * }</pre>
 *
 * @author Deepak
 * @version 2.1
 * @since 1.0
 */
public final class MediaProvider {

	/** The raw source of the media (Base64, file path, or URL). */
	private final String source;

	/** Type of media source for internal classification. */
	private final MediaType type;

	/** Enum representing the source type of the media. */
	public enum MediaType {
		/** Media loaded from a local file path. */
		PATH,
		/** Media referenced from a remote URL. */
		URL,
		/** Media stored as a Base64-encoded string. */
		BASE64
	}

	/** Basic Base64 validation pattern (lenient form). */
	private static final Pattern BASE64_PATTERN = Pattern
			.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$");

	// ---------------------------------------------------------------------
	// Constructor
	// ---------------------------------------------------------------------

	/**
	 * Constructs a {@code MediaProvider} with the given source and type.
	 *
	 * @param source the media source (Base64, URL, or file path)
	 * @param type   the type of media source
	 * @throws NullPointerException if any parameter is null
	 */
	private MediaProvider(String source, MediaType type) {
		this.source = Objects.requireNonNull(source, "Media source cannot be null").trim();
		this.type = Objects.requireNonNull(type, "Media type cannot be null");
	}

	// ---------------------------------------------------------------------
	// Factory Methods
	// ---------------------------------------------------------------------

	/**
	 * Creates a {@code MediaProvider} from a local file path.
	 *
	 * @param filePath path to the local media file
	 * @return new {@code MediaProvider} instance
	 * @throws IllegalArgumentException if the path is null, empty, or invalid
	 */
	public static MediaProvider fromPath(String filePath) {
		if (filePath == null || filePath.trim().isEmpty()) {
			throw new IllegalArgumentException("File path cannot be null or empty.");
		}

		File file = new File(filePath.trim());
		if (!file.exists() || !file.isFile()) {
			throw new IllegalArgumentException("File not found or invalid: " + filePath);
		}

		return new MediaProvider(file.getAbsolutePath(), MediaType.PATH);
	}

	/**
	 * Creates a {@code MediaProvider} from a remote URL.
	 *
	 * @param url URL of the media resource
	 * @return new {@code MediaProvider} instance
	 * @throws IllegalArgumentException if the URL is null, empty, or malformed
	 */
	public static MediaProvider fromUrl(String url) {
		if (url == null || url.trim().isEmpty()) {
			throw new IllegalArgumentException("URL cannot be null or empty.");
		}

		String trimmed = url.trim().toLowerCase();
		if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
			throw new IllegalArgumentException("Invalid URL: must start with http:// or https://");
		}

		return new MediaProvider(url.trim(), MediaType.URL);
	}

	/**
	 * Creates a {@code MediaProvider} from a Base64 string.
	 *
	 * @param base64 Base64-encoded image or media data
	 * @return new {@code MediaProvider} instance
	 * @throws IllegalArgumentException if input is invalid or not Base64
	 */
	public static MediaProvider fromBase64(String base64) {
		if (base64 == null || base64.trim().isEmpty()) {
			throw new IllegalArgumentException("Base64 string cannot be null or empty.");
		}

		String trimmed = base64.trim();
		// Allow full data URL inputs
		if (trimmed.startsWith("data:image")) {
			return new MediaProvider(trimmed, MediaType.BASE64);
		}

		if (!BASE64_PATTERN.matcher(trimmed).matches()) {
			throw new IllegalArgumentException("Invalid Base64 content.");
		}

		// Default to PNG MIME type when unspecified
		return new MediaProvider("data:image/png;base64," + trimmed, MediaType.BASE64);
	}

	/**
	 * Creates a {@code MediaProvider} from raw bytes.
	 *
	 * @param bytes raw media bytes (e.g., screenshot from automation)
	 * @return new {@code MediaProvider} instance
	 * @throws IllegalArgumentException if byte array is null or empty
	 */
	public static MediaProvider fromBytes(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			throw new IllegalArgumentException("Byte array cannot be null or empty.");
		}

		String base64 = Base64.getEncoder().encodeToString(bytes);
		return new MediaProvider("data:image/png;base64," + base64, MediaType.BASE64);
	}

	// ---------------------------------------------------------------------
	// Accessors
	// ---------------------------------------------------------------------

	/**
	 * Returns the raw media source, which may be a Base64 string, a file path, or a URL.
	 * 
	 * @return the raw source string (Base64, file path, or URL)
	 */
	public String getSource() {
	    return source;
	}

	/**
	 * Returns the type of this media source, used for internal classification.
	 * 
	 * @return the {@link MediaType} classification of this media
	 */
	public MediaType getType() {
	    return type;
	}


	// ---------------------------------------------------------------------
	// Conversion Utility
	// ---------------------------------------------------------------------

	/**
	 * Converts this media to a Base64-encoded string if it represents a local file.
	 * <p>
	 * If the media is already Base64 or a URL, the original source string is returned.
	 * On failure to read the file, the original source is returned and no exception is thrown.
	 * </p>
	 * 
	 * @return Base64 string (possibly prefixed with {@code data:image/...})
	 */
	public String toBase64Safely() {
	    if (type != MediaType.PATH) {
	        return source;
	    }

	    try {
	        byte[] bytes = Files.readAllBytes(Paths.get(source));
	        String encoded = Base64.getEncoder().encodeToString(bytes);
	        return "data:image/png;base64," + encoded;
	    } catch (IOException | SecurityException ex) {
	        // Soft failure: do not interrupt report generation
	        System.err.println("[VisionReport] WARNING: Failed to convert file to Base64: " + source);
	        return source;
	    }
	}

	// ---------------------------------------------------------------------
	// Object Overrides
	// ---------------------------------------------------------------------

	@Override
	public String toString() {
		return "MediaProvider{type=" + type + ", source='" + (type == MediaType.BASE64 ? "[base64-data]" : source)
				+ "'}";
	}

	@Override
	public int hashCode() {
		return Objects.hash(source, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof MediaProvider))
			return false;
		MediaProvider other = (MediaProvider) obj;
		return Objects.equals(this.source, other.source) && this.type == other.type;
	}
}
