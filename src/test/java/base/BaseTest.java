package base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.monte.media.Format;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.awt.*;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class BaseTest {

    protected ChromeDriver d;
    protected ScreenRecorder recorder;
    protected String testName;

    protected static final String REPORT_DIR = "test-output/";
    protected static final String VIDEO_DIR  = "test-output/videos/";

    private static ExtentReports extent;

    // Static references so TestListener can access current test node and driver
    private static ExtentTest currentTest;
    private static ChromeDriver currentDriver;

    @BeforeSuite
    public void initReport() {
        ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_DIR + "report.html");
        spark.config().setReportName("PolicyDekho Test Report");
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    public void startRecording(Method method) throws Exception {
        testName = method.getName();
        currentDriver = d;
        Files.createDirectories(Path.of(VIDEO_DIR));

        GraphicsConfiguration gc = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();

        recorder = new ScreenRecorder(gc,
                new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()),
                new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                new Format(MediaTypeKey, MediaType.VIDEO,
                        EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        DepthKey, 24, FrameRateKey, Rational.valueOf(15), QualityKey, 1.0f,
                        KeyFrameIntervalKey, 15 * 60),
                null, null, new File(VIDEO_DIR));
        recorder.start();
    }

    @AfterSuite
    public void printReportPath() {
        System.out.println("Report: " + new File(REPORT_DIR + "report.html").getAbsolutePath());
    }

    public static ExtentReports getExtent()            { return extent; }
    public static ExtentTest getCurrentTest()          { return currentTest; }
    public static void setCurrentTest(ExtentTest test) { currentTest = test; }
    public static ChromeDriver getCurrentDriver()      { return currentDriver; }
}
