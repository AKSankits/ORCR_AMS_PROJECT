package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

public class ScreenshotUtils {

    private static final String SCREENSHOT_DIR = "test-output/screenshots/";

    public static String captureBase64(ChromeDriver d) {
        return Base64.getEncoder().encodeToString(
                ((TakesScreenshot) d).getScreenshotAs(OutputType.BYTES));
    }

    public static void saveToFile(ChromeDriver d, String testName, String status, String timestamp) throws IOException {
        Files.createDirectories(Path.of(SCREENSHOT_DIR));
        File src = ((TakesScreenshot) d).getScreenshotAs(OutputType.FILE);
        Path dest = Path.of(SCREENSHOT_DIR + testName + "_" + status + "_" + timestamp + ".png");
        Files.copy(src.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
    }
}
