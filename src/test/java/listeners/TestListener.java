package listeners;

import base.BaseTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.ScreenshotUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TestListener implements ITestListener {

    private ExtentReports extent;

    @Override
    public void onStart(ITestContext context) {
        extent = BaseTest.getExtent();
    }

    @Override
    public void onTestStart(ITestResult result) {
        // Create a fresh ExtentTest node for every test when it starts
        if (extent != null) {
            ExtentTest test = extent.createTest(result.getMethod().getMethodName());
            BaseTest.setCurrentTest(test);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = BaseTest.getCurrentTest();
        if (test == null) return;
        try {
            ChromeDriver d = BaseTest.getCurrentDriver();
            if (d != null) {
                String base64 = ScreenshotUtils.captureBase64(d);
                test.pass("PASSED",
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
            } else {
                test.pass("PASSED");
            }
        } catch (Exception e) {
            test.pass("PASSED");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = BaseTest.getCurrentTest();
        if (test == null) return;
        try {
            ChromeDriver d = BaseTest.getCurrentDriver();
            if (d != null) {
                String timestamp = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss", Locale.ROOT));
                ScreenshotUtils.saveToFile(d, result.getMethod().getMethodName(), "FAIL", timestamp);
                String base64 = ScreenshotUtils.captureBase64(d);
                test.fail(result.getThrowable(),
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
            } else {
                test.fail(result.getThrowable());
            }
        } catch (Exception e) {
            test.fail(result.getThrowable());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = BaseTest.getCurrentTest();
        if (test == null && extent != null)
            test = extent.createTest(result.getMethod().getMethodName());
        if (test == null) return;
        String reason = result.getThrowable() != null
                ? result.getThrowable().getMessage()
                : "Skipped — dependency failed or test is disabled";
        test.skip("<b>SKIPPED</b> — " + reason);
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("\n========================================");
        System.out.println("         TEST EXECUTION SUMMARY         ");
        System.out.println("========================================");
        System.out.println("  PASSED  : " + context.getPassedTests().size());
        System.out.println("  FAILED  : " + context.getFailedTests().size());
        System.out.println("  SKIPPED : " + context.getSkippedTests().size());
        System.out.println("  TOTAL   : " + (context.getPassedTests().size()
                + context.getFailedTests().size()
                + context.getSkippedTests().size()));
        System.out.println("========================================\n");
        if (extent != null) extent.flush();
    }

    @Override public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}
}
