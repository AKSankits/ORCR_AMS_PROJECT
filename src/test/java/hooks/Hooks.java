package hooks;

import base.BaseTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import io.cucumber.java.Scenario;
import io.cucumber.java.Status;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.openqa.selenium.chrome.ChromeDriver;
import utils.VideoUtils;
import utils.ScreenshotUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Hooks extends BaseTest {

    @io.cucumber.java.Before
    public void beforeScenario(Scenario scenario) throws Exception {
        ExtentReports report = getExtent();
        if (report == null) {
            throw new IllegalStateException("Extent report has not been initialized");
        }
        setCurrentTest(report.createTest(scenario.getName()));
        startRecording(scenario.getName());
    }

    @io.cucumber.java.After
    public void afterScenario(Scenario scenario) throws Exception {
        ExtentTest test = getCurrentTest();
        if (test == null) {
            throw new IllegalStateException("Extent test was not initialized for " + scenario.getName());
        }

        ChromeDriver driver = getCurrentDriver();
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss", Locale.ROOT));
        Path screenshotPath = null;
        try {
            if (driver != null) {
                String screenshot = ScreenshotUtils.captureBase64(driver);
                ScreenshotUtils.saveToFile(driver, testName, scenario.getStatus().name(), timestamp);
                screenshotPath = Path.of("test-output/screenshots",
                        testName + "_" + scenario.getStatus().name() + "_" + timestamp + ".png");
                var screenshotMedia = MediaEntityBuilder.createScreenCaptureFromBase64String(screenshot).build();

                if (scenario.getStatus() == Status.PASSED) {
                    test.pass("PASSED", screenshotMedia);
                } else if (scenario.getStatus() == Status.SKIPPED) {
                    test.skip("SKIPPED", screenshotMedia);
                } else {
                    test.fail("FAILED: " + scenario.getStatus(), screenshotMedia);
                }
            } else {
                test.warning("Screenshot unavailable because the browser was not started");
                if (scenario.getStatus() == Status.PASSED) {
                    test.pass("PASSED");
                } else if (scenario.getStatus() == Status.SKIPPED) {
                    test.skip("SKIPPED");
                } else {
                    test.fail("FAILED: " + scenario.getStatus());
                }
            }
        } finally {
            try {
                File video = stopRecording();
                if (video != null) {
                    Path namedVideo = video.toPath().resolveSibling(
                            testName + "_" + scenario.getStatus().name() + "_" + timestamp + ".avi");
                    video = Files.move(video.toPath(), namedVideo, StandardCopyOption.REPLACE_EXISTING).toFile();
                }
                if (video != null) {
                    String videoName = video.getName().replace(" ", "%20");
                    test.info("<a href='videos/" + videoName
                            + "' target='_blank'>Open UI recording (.avi)</a>");
                }
                Reporter.log("<b>UI scenario:</b> " + scenario.getName(), false);
                if (screenshotPath != null) {
                    String screenshotUrl = "../../" + screenshotPath.toString().replace('\\', '/');
                    Reporter.log("<a href='" + screenshotUrl
                            + "' target='_blank'><img src='" + screenshotUrl
                            + "' alt='UI screenshot for " + scenario.getName()
                            + "' style='max-width:800px;width:100%;height:auto;border:1px solid #ccc;'></a>", false);
                }
                if (video != null) {
                    String videoUrl = "../../test-output/videos/" + video.getName();
                    Reporter.log("<video controls preload='metadata' width='800'>"
                            + "<source src='" + videoUrl + "' type='video/x-msvideo'>"
                            + "Your browser cannot play AVI recordings. "
                            + "<a href='" + videoUrl + "' download>Download the recording</a>."
                            + "</video><br><a href='" + videoUrl
                            + "' download>Download video recording (.avi)</a>", false);
                } else {
                    test.warning("UI recording was not found in " + VIDEO_DIR);
                    Reporter.log("UI video recording was not found in " + VIDEO_DIR, false);
                }
            } finally {
                if (driver != null) {
                    driver.quit();
                }
                setCurrentDriver(null);
            }
        }
    }

    private File stopRecording() throws Exception {
        if (recorder != null) {
            recorder.stop();
            recorder = null;
        }

        File[] recordings = new File(VIDEO_DIR)
                .listFiles((directory, name) -> name.toLowerCase(Locale.ROOT).endsWith(".avi"));
        if (recordings == null || recordings.length == 0) {
            return null;
        }

        File latest = recordings[0];
        for (File recording : recordings) {
            if (recording.lastModified() > latest.lastModified()) {
                latest = recording;
            }
        }
        return latest;
    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        String status    = result.isSuccess() ? "PASS" : (result.getStatus() == ITestResult.SKIP ? "SKIP" : "FAIL");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss", Locale.ROOT));

        // Stop recording and convert video in background — next test starts immediately
        VideoUtils.stopAndProcessAsync(recorder, VIDEO_DIR, testName, status, timestamp,
                videoTag -> {
                    if (getCurrentTest() != null) getCurrentTest().info(videoTag);
                });

        if (d != null) {
            d.quit();
            d = null;
        }
    }
}
