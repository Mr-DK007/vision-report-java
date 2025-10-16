package com.visionreport.core.engine;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;

import org.apache.commons.io.IOUtils;

import com.visionreport.api.MediaProvider;
import com.visionreport.core.models.MediaModel;

/**
 * Internal utility class for converting {@link MediaProvider} sources into
 * Base64-encoded {@link MediaModel} instances suitable for embedding in Vision
 * Report HTML templates.
 * <p>
 * This class is part of the {@code core.engine} package and is intended for
 * internal use only. API-layer components pass user-supplied
 * {@link MediaProvider} objects here during report generation.
 * </p>
 *
 * <p>
 * <b>Supported source types:</b>
 * </p>
 * <ul>
 * <li>Local file path</li>
 * <li>Remote HTTP/HTTPS URL</li>
 * <li>Base64-encoded string</li>
 * </ul>
 *
 * <p>
 * All errors are safely logged to the console; {@code null} is returned on
 * failure.
 * </p>
 *
 * @author Deepak
 * @version 2.0
 * @since 1.0
 */
public final class MediaUtil {

    // Prevent instantiation
    private MediaUtil() {
    }

    /**
     * Converts a {@link MediaProvider} into a {@link MediaModel} suitable for
     * report rendering.
     *
     * @param provider the media wrapper from the API layer
     * @return {@link MediaModel} containing Base64 data, or {@code null} if
     *         processing fails
     */
    public static MediaModel getMediaModel(MediaProvider provider) {
        if (provider == null) {
            System.err.println("VisionReport WARNING: MediaProvider is null.");
            return null;
        }

        try {
            switch (provider.getType()) {
                case BASE64:
                    return fromBase64(provider.getSource());

                case PATH:
                    return fromPath(provider.getSource());

                case URL:
                    return fromUrl(provider.getSource());

                default:
                    System.err.println("VisionReport WARNING: Unsupported media type: " + provider.getType());
                    return null;
            }
        } catch (Exception e) {
            System.err.println("VisionReport ERROR: Failed to process media: " + e.getMessage());
            return null;
        }
    }

    // ------------------------------------------------------------------------
    // Private helper methods for each source type
    // ------------------------------------------------------------------------

    /**
     * Converts a Base64 string into a {@link MediaModel}.
     *
     * @param source Base64-encoded string
     * @return {@link MediaModel} or {@code null} if failed
     */
    private static MediaModel fromBase64(String source) {
        try {
            if (source == null || source.trim().isEmpty()) {
                return null;
            }
            String finalBase64 = source.startsWith("data:image") ? source : "data:image/png;base64," + source;
            return new MediaModel(finalBase64, "Screenshot (Base64)");
        } catch (Exception e) {
            System.err.println("VisionReport WARNING: Failed to handle Base64 media source.");
            return null;
        }
    }

    /**
     * Converts a local file path into a {@link MediaModel}.
     *
     * @param path path to the local media file
     * @return {@link MediaModel} or {@code null} if file not found or read fails
     */
    private static MediaModel fromPath(String path) {
        if (path == null || path.trim().isEmpty()) {
            System.err.println("VisionReport WARNING: File path is null or empty.");
            return null;
        }

        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            System.err.println("VisionReport WARNING: Media file not found: " + path);
            return null;
        }

        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            String base64 = Base64.getEncoder().encodeToString(fileBytes);
            return new MediaModel("data:image/png;base64," + base64, file.getName());
        } catch (Exception e) {
            System.err.println("VisionReport WARNING: Error reading file: " + path);
            System.err.println("  > " + e.getMessage());
            return null;
        }
    }

    /**
     * Converts a remote URL into a {@link MediaModel}.
     *
     * @param url HTTP/HTTPS URL of the media
     * @return {@link MediaModel} or {@code null} if fetch or encoding fails
     */
    private static MediaModel fromUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            System.err.println("VisionReport WARNING: URL is null or empty.");
            return null;
        }

        try (InputStream inputStream = new URL(url).openStream()) {
            byte[] imageBytes = IOUtils.toByteArray(inputStream);
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            return new MediaModel("data:image/png;base64," + base64, "Screenshot (URL)");
        } catch (Exception e) {
            System.err.println("VisionReport WARNING: Failed to fetch or encode media from URL: " + url);
            System.err.println("  > " + e.getMessage());
            return null;
        }
    }
}
