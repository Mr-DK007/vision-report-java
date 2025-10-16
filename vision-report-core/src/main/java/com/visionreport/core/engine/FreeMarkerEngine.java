package com.visionreport.core.engine;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.visionreport.core.models.ReportModel;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

/**
 * Singleton engine for processing a {@link ReportModel} with FreeMarker templates
 * to generate the final HTML report.
 * <p>
 * Handles configuration, template loading, and execution of FreeMarker rendering.
 * Ensures consistent settings across the application.
 *
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * String html = FreeMarkerEngine.getInstance().process(reportModel);
 * }</pre>
 * 
 * @author Deepak
 * @version 1.0
 * @since 1.0
 */
public final class FreeMarkerEngine {

    /** Singleton instance of the engine */
    private static FreeMarkerEngine instance;

    /** FreeMarker configuration */
    private final Configuration cfg;

    /**
     * Private constructor to initialize FreeMarker configuration.
     * Configures encoding, template loading path, exception handling, and
     * recommended modern settings.
     */
    private FreeMarkerEngine() {
        cfg = new Configuration(new Version("2.3.32"));

        // Load templates from the classpath: /templates/
        cfg.setClassForTemplateLoading(FreeMarkerEngine.class, "/templates/");

        // Recommended FreeMarker settings
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
    }

    /**
     * Returns the singleton instance of FreeMarkerEngine.
     *
     * @return the singleton {@link FreeMarkerEngine} instance
     */
    public static synchronized FreeMarkerEngine getInstance() {
        if (instance == null) {
            instance = new FreeMarkerEngine();
        }
        return instance;
    }

    /**
     * Processes a {@link ReportModel} using the configured FreeMarker template.
     * Renders the report model into a complete HTML string.
     *
     * @param reportModel fully populated report model
     * @return generated HTML report as a string
     * @throws Exception if the template cannot be loaded or processed
     */
    public String process(ReportModel reportModel) throws Exception {
        if (reportModel == null) {
            throw new IllegalArgumentException("ReportModel cannot be null");
        }

        // Load the template (ensure correct template name)
        Template template = cfg.getTemplate("UI-Testing-Template.ftl");

        // Build the data model for FreeMarker
        Map<String, Object> data = new HashMap<>();
        data.put("report", reportModel);

        // Render the template into a string
        try (Writer out = new StringWriter()) {
            template.process(data, out);
            return out.toString();
        }
    }
}
