package com.visionreport.demo;

import com.visionreport.api.MediaProvider;
import com.visionreport.api.Status;
import com.visionreport.api.Test;
import com.visionreport.api.VisionReport;

/**
 * Generates a comprehensive demo report showcasing all Vision Report features.
 * This report is used for GitHub Pages and documentation.
 */
public class DemoReportGenerator {

	@SuppressWarnings("null")
	public static void main(String[] args) {
		VisionReport report = new VisionReport();

		// Configure report
		report.config().setTitle("Vision Report Demo - Feature Showcase").setProjectName("Vision Report Library")
				.setApplicationName("E-Commerce Test Suite").setEnvironment("Production")
				.setTesterName("QA Automation Team").setBrowser("Chrome 120.0").addCustomInfo("Build Number", "1.0.0")
				.addCustomInfo("Test Type", "Regression & Smoke").addCustomInfo("Platform", "Windows 11");

		// Test 1: Successful Login Flow
		report.createTest("User Login - Successful Authentication")
				.description("Validates that users can successfully log in with valid credentials")
				.assignCategory("Smoke", "Authentication", "Critical Path")
				.log(Status.INFO, "Navigate to login page", "Opening https://example.com/login")
				.log(Status.PASS, "Enter username", "Username: testuser@example.com")
				.log(Status.PASS, "Enter password", "Password field populated successfully")
				.log(Status.PASS, "Click login button", "Login button clicked")
				.log(Status.PASS, "Verify dashboard loaded", "User redirected to dashboard successfully");

//        Media Test

		Test mediaTest = report.createTest("Media Provider Edge Cases").assignCategory("Media Handling");
		mediaTest.log(Status.PASS, "Valid Path", "Sample image", MediaProvider.fromPath("sample-image.png"));
		mediaTest.log(Status.INFO, "Null Media", "No attachment", (MediaProvider) null);
		String largeBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8A"
				+ "A".repeat(5000);
		mediaTest.log(Status.PASS, "Large Base64", "Simulated large image", MediaProvider.fromBase64(largeBase64));
		mediaTest.log(Status.PASS, "Image URL", "Loaded from URL", MediaProvider
				.fromUrl("https://www.google.com/images/branding/googlelogo/2x/googlelogo_light_color_272x92dp.png"));

		// Test 2: Shopping Cart Operations
		report.createTest("Add Items to Shopping Cart")
				.description("Validates adding multiple items to cart and calculating totals")
				.assignCategory("E2E", "Shopping Cart", "Core Features")
				.log(Status.INFO, "Navigate to products page", "Opening product catalog")
				.log(Status.PASS, "Select Product 1", "Added 'Wireless Mouse' to cart - $29.99")
				.log(Status.PASS, "Select Product 2", "Added 'USB-C Cable' to cart - $12.99")
				.log(Status.PASS, "View cart", "Cart displays 2 items")
				.log(Status.PASS, "Verify cart total", "Total: $42.98 (correct calculation)");

		// Test 3: Payment Processing
		report.createTest("Checkout and Payment - Credit Card")
				.description("End-to-end checkout flow with credit card payment")
				.assignCategory("E2E", "Payment", "Critical Path")
				.log(Status.INFO, "Proceed to checkout", "Navigating to checkout page")
				.log(Status.PASS, "Enter shipping address", "Address: 123 Main St, City, State 12345")
				.log(Status.PASS, "Select shipping method", "Standard Shipping - 5-7 business days")
				.log(Status.PASS, "Enter payment details", "Credit Card ending in **** 4242")
				.log(Status.PASS, "Submit order", "Order #12345 placed successfully")
				.log(Status.PASS, "Verify confirmation email", "Confirmation email sent to user");

		// Test 4: Search Functionality
		report.createTest("Product Search with Filters")
				.description("Tests search functionality with multiple filters applied")
				.assignCategory("Search", "Filters", "Core Features")
				.log(Status.INFO, "Navigate to search page", "Opening product search")
				.log(Status.PASS, "Enter search query", "Searching for 'wireless headphones'")
				.log(Status.PASS, "Apply price filter", "Filter: $50 - $150")
				.log(Status.PASS, "Apply brand filter", "Filter: Sony, Bose")
				.log(Status.PASS, "Verify results", "Found 12 products matching criteria")
				.log(Status.PASS, "Sort by rating", "Products sorted by customer rating (descending)");

		// Test 5: User Profile Update
		report.createTest("Update User Profile Information").description("Validates user can update profile details")
				.assignCategory("User Management", "Profile", "Settings")
				.log(Status.INFO, "Navigate to profile settings", "Opening user profile page")
				.log(Status.PASS, "Update display name", "Changed name to 'John Doe'")
				.log(Status.PASS, "Update email", "Email updated to john.doe@example.com")
				.log(Status.PASS, "Update phone number", "Phone: +1 (555) 123-4567")
				.log(Status.PASS, "Save changes", "Profile updated successfully")
				.log(Status.PASS, "Verify changes persisted", "Changes visible after page refresh");

		// Test 6: Failed Login Attempt
		report.createTest("Login - Invalid Credentials")
				.description("Validates error handling for invalid login attempts")
				.assignCategory("Negative Testing", "Authentication", "Security")
				.log(Status.INFO, "Navigate to login page", "Opening login page")
				.log(Status.PASS, "Enter invalid username", "Username: invalid@example.com")
				.log(Status.PASS, "Enter wrong password", "Password: wrongpassword123")
				.log(Status.PASS, "Click login button", "Attempting login")
				.log(Status.FAIL, "Verify error message", "Expected: 'Invalid credentials' but got: 'Server Error'")
				.log(Status.INFO, "Screenshot captured", "Error state documented");

		// Test 7: Skipped Test Example
		report.createTest("Mobile App Payment - Skipped")
				.description("Mobile payment test skipped due to device unavailability")
				.assignCategory("Mobile", "Payment", "iOS")
				.log(Status.INFO, "Check device availability", "Looking for iOS device")
				.log(Status.SKIP, "Device not found", "No iOS devices available in test lab")
				.log(Status.INFO, "Test deferred", "Will retry when device becomes available");

		// Test 8: API Integration Test
		report.createTest("REST API - Get User Details").description("Validates GET /api/users/{id} endpoint")
				.assignCategory("API", "Integration", "Backend")
				.log(Status.INFO, "Send GET request", "GET https://api.example.com/users/123")
				.log(Status.PASS, "Verify status code", "Status: 200 OK")
				.log(Status.PASS, "Verify response time", "Response time: 245ms (< 300ms threshold)")
				.log(Status.PASS, "Validate response schema", "JSON schema validation passed")
				.log(Status.PASS, "Verify user data", "User ID, name, email fields present and valid");

		// Test 9: Exception Handling Test
		Test test = report.createTest("Exception Handling - Null Pointer")
				.description("Tests graceful handling of null pointer exceptions")
				.assignCategory("Exception Handling", "Negative Testing", "Edge Cases")
				.log(Status.INFO, "Initialize test data", "Setting up test scenario")
				.log(Status.PASS, "Attempt null operation", "Calling method with null parameter");

		try {
			String nullString = null;
			nullString.length(); // This will throw NullPointerException
		} catch (Exception e) {
			test.logException(e);
		}

		// Test 10: Performance Test
		report.createTest("Page Load Performance").description("Measures and validates page load times")
				.assignCategory("Performance", "Non-Functional", "Monitoring")
				.log(Status.INFO, "Start performance monitoring", "Initiating page load test")
				.log(Status.PASS, "Measure time to first byte", "TTFB: 120ms")
				.log(Status.PASS, "Measure DOM content loaded", "DCL: 850ms")
				.log(Status.PASS, "Measure full page load", "Load: 1.2s")
				.log(Status.PASS, "Performance threshold met", "All metrics within acceptable range")
				.log(Status.INFO, "Performance report generated", "Details saved to metrics dashboard");

		// Test 11: Multi-step Workflow
		report.createTest("Complete User Journey - Registration to Purchase")
				.description("End-to-end user journey from registration through first purchase")
				.assignCategory("E2E", "User Journey", "Critical Path", "Regression")
				.log(Status.INFO, "Start user journey", "Beginning complete workflow test")
				.log(Status.PASS, "Step 1: User registration", "New user account created successfully")
				.log(Status.PASS, "Step 2: Email verification", "Verification email received and confirmed")
				.log(Status.PASS, "Step 3: Profile setup", "User profile completed with preferences")
				.log(Status.PASS, "Step 4: Browse products", "User browses catalog for 45 seconds")
				.log(Status.PASS, "Step 5: Add to cart", "3 items added to shopping cart")
				.log(Status.PASS, "Step 6: Apply coupon", "Discount code 'WELCOME10' applied")
				.log(Status.PASS, "Step 7: Checkout", "Proceed to checkout with saved payment method")
				.log(Status.PASS, "Step 8: Complete purchase", "Order placed and confirmation displayed")
				.log(Status.PASS, "Step 9: Verify email", "Order confirmation email received")
				.log(Status.INFO, "User journey completed", "Total time: 3m 42s");

		// Test 12: Data Validation Test
		report.createTest("Form Validation - Registration Form")
				.description("Validates all form field validations on registration page")
				.assignCategory("Validation", "Forms", "Frontend")
				.log(Status.INFO, "Open registration form", "Navigate to /register")
				.log(Status.PASS, "Validate email format", "Rejects invalid email formats")
				.log(Status.PASS, "Validate password strength",
						"Enforces minimum 8 characters, 1 number, 1 special char")
				.log(Status.PASS, "Validate phone number", "Accepts only valid phone formats")
				.log(Status.PASS, "Validate required fields", "Form submission blocked with empty required fields")
				.log(Status.PASS, "Validate terms checkbox", "Must accept terms before submission");

		// Generate the report
		report.flush("demo-report.html");

		System.out.println("========================================");
		System.out.println("✅ Demo Report Generated Successfully!");
		System.out.println("========================================");
		System.out.println("📊 Report Statistics:");
		System.out.println("   - Total Tests: 12");
		System.out.println("   - Passed: 9");
		System.out.println("   - Failed: 1");
		System.out.println("   - Skipped: 1");
		System.out.println("   - With Exceptions: 1");
		System.out.println("========================================");
		System.out.println("📁 Report saved in current directory");
		System.out.println("🌐 Use this report for GitHub Pages demo");
		System.out.println("========================================");
	}
}
