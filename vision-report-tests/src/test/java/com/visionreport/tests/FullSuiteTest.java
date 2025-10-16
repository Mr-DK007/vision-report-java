package com.visionreport.tests;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.visionreport.api.Status;
import com.visionreport.api.MediaProvider;
import com.visionreport.api.VisionReport;

@Test(suiteName = "VisionReport Final Robust Test Suite")
public class FullSuiteTest {

    private VisionReport report;

    @BeforeSuite(alwaysRun = true)
    public void setUpSuite() {
        report = new VisionReport();
        report.config()
            .setTitle("Login: Smoke/Regression *Test*?")
            .setProjectName("Vision Report Library")
            .setApplicationName("Internal QA")
            .setEnvironment("Local Development")
            .setTesterName("Automation Bot")
            .setBrowser("Java Test Runner")
            .addCustomInfo("Build Number", "1.0.0-FINAL");
    }

    // =================================================================
    // 1. Core Functionality & Logging Variations
    // =================================================================
    @Test(priority = 1, description = "Verifies basic PASS, INFO, SKIP, FAIL logging and fluent API.")
    public void standardPassFailSkipTest() {
        report.createTest("Standard Test")
            .description("Validates basic logging and status hierarchy.")
            .assignCategory("Smoke", "Regression")
            .log(Status.INFO, "Initialization")
            .log(Status.PASS, "Step 1")
            .log(Status.SKIP, "Step 2 skipped")
            .log(Status.FAIL, "Step 3 fails");
    }

    @Test(priority = 2, description = "Exception logging with messages and null message.")
    public void exceptionLoggingTest() {
        var test = report.createTest("Exception Test").assignCategory("Failure Scenario");

        // Regular exception
        try { int x = 1 / 0; } 
        catch (Exception e) { test.logException(e); }

        // Exception with null message
        try { throw new NullPointerException(); }
        catch (Exception e) { test.logException(e); }
    }

    @Test(priority = 3, description = "Skipped logs are correctly prioritized.")
    public void skippedLogsTest() {
        report.createTest("Skipped Test")
            .assignCategory("Edge Case")
            .log(Status.PASS, "Prerequisite")
            .log(Status.SKIP, "Step skipped due to missing resource");
    }

    // =================================================================
    // 2. Media Handling Tests (including edge cases)
    // =================================================================
    @Test(priority = 10, description = "MediaProvider types and edge cases")
    public void mediaProviderEdgeCases() {
        var test = report.createTest("Media Provider Edge Cases").assignCategory("Media Handling");

        // Valid path
        test.log(Status.PASS, "Valid Path", "Sample image", MediaProvider.fromPath("sample-image.png"));
        // Null media provider
        test.log(Status.INFO, "Null Media", "No attachment", (MediaProvider) null);
        // Empty Base64 string
//        test.log(Status.PASS, "Empty Base64", "Should fallback", MediaProvider.fromBase64(""));
        // Large Base64 string simulation
        String largeBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8A" + "A".repeat(5000);
        test.log(Status.PASS, "Large Base64", "Simulated large image", MediaProvider.fromBase64(largeBase64));
        // URL
        test.log(Status.PASS, "Image URL", "Loaded from URL",
            MediaProvider.fromUrl("https://www.google.com/images/branding/googlelogo/2x/googlelogo_light_color_272x92dp.png"));
    }

    @Test(priority = 11, description = "Exception with media")
    public void exceptionWithMediaTest() {
        report.createTest("Exception + Media Test").assignCategory("Failure Scenario")
            .logException(new ArithmeticException("Calc failed"), MediaProvider.fromPath("sample-image.png"));
    }

    // =================================================================
    // 3. Edge Case & Null Handling
    // =================================================================
//    @Test(priority = 20, description = "Null and empty test/log handling")
//    public void nullEmptyInputsTest() {
//        var test = report.createTest(null); 
//        test.description(null);
//        test.assignCategory(null, "", "ValidCategory");
//        test.log(Status.INFO, null);
//        test.log(Status.PASS, null, null);
//        try { throw new NullPointerException(); } catch (Exception e) { test.logException(e); }
//        test.log(Status.PASS, "Valid Name", null);
//    }

    @Test(priority = 21, description = "Duplicate authors/categories")
    public void duplicateAuthorsCategoriesTest() {
        var test = report.createTest("Dup Test");
        test.assignCategory("Cat1", "Cat1", "Cat2");
        test.assignCategory("Cat2");
    }

    @Test(priority = 22, description = "Extreme lengths and Unicode/special chars")
    public void extremeUnicodeTest() {
        String longName = "A".repeat(5000);
        String longDesc = "B".repeat(5000);
        report.createTest(longName).description(longDesc)
            .assignCategory("Unicode测试", "Emoji🚀")
            .log(Status.PASS, "Log<>\"'&", "Details with & < > ' \" 🚀");
    }

    // =================================================================
    // 4. Final Status Calculation
    // =================================================================
    @Test(priority = 30, description = "PASS + INFO only → final status PASS")
    public void statusPassInfoTest() {
        var test = report.createTest("Status PASS+INFO");
        test.log(Status.INFO, "Info").log(Status.PASS, "Pass");
    }

    @Test(priority = 31, description = "SKIP overrides PASS")
    public void statusSkipOverridesPassTest() {
        var test = report.createTest("Status SKIP overrides PASS");
        test.log(Status.PASS, "Pass").log(Status.SKIP, "Skipped");
    }

    @Test(priority = 32, description = "FAIL overrides everything")
    public void statusFailOverridesAllTest() {
        var test = report.createTest("Status FAIL overrides all");
        test.log(Status.PASS, "Pass").log(Status.SKIP, "Skipped");
        test.logException(new RuntimeException("Explicit fail"));
    }

    // =================================================================
    // 5. Report Flush / Edge Cases
    // =================================================================
    @Test(priority = 40, description = "Flushing empty report")
    public void flushEmptyReportTest() {
        VisionReport emptyReport = new VisionReport();
        emptyReport.flush();
    }

    @Test(priority = 41, description = "Flush multiple times")
    public void flushMultipleTimesTest() {
        report.flush();
        report.flush(); // Should not fail
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        if (report != null) {
            report.flush();
        }
    }
}
