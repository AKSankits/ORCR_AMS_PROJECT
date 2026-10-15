import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features  = "src/test/resources/features",
        glue      = { "stepdefs", "hooks" },
        plugin    = {
                "pretty",
                "html:test-output/cucumber-report.html",
                "json:test-output/cucumber-report.json",
                "junit:test-output/cucumber-report.xml"
        },
        tags       = "not @ignore",
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {
}
