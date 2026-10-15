package hooks;

import base.BaseTest;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import utils.VideoUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Hooks extends BaseTest {

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
