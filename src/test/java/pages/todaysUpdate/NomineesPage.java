
package pages.todaysUpdate;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class NomineesPage extends BasePage {

    private static final By DELETE_FIRST_NOMINEE =
            By.id("deleteNomineeBtn0");

    private static final By NOMINEE_DELETE_BUTTONS =
            By.cssSelector("[id^='deleteNomineeBtn']");

    public NomineesPage(ChromeDriver d) {
        super(d);
    }

    public int getNomineeCount() {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                NOMINEE_DELETE_BUTTONS, 0));
        return d.findElements(NOMINEE_DELETE_BUTTONS).size();
    }

    public void deleteFirstNominee() {
        if (getNomineeCount() < 2) {
            throw new AssertionError(
                    "At least two nominees are required for this test."
            );
        }

        click(DELETE_FIRST_NOMINEE).click();
    }

    public boolean waitForNomineeCount(int expectedCount) {
        try {
            wait.until(ExpectedConditions.numberOfElementsToBe(
                    NOMINEE_DELETE_BUTTONS, expectedCount));
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }
}
