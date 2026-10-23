package pages.todaysUpdate;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class RenewPolicyPage extends BasePage {

    private static final By RENEW_POLICY_LINK =
            By.xpath("//a[@id='linkRenew']");
    private static final By FIRST_POLICY_RENEW_BUTTON =
            By.xpath("//div[@class='grid grid-cols-1 gap-4']//div[1]//div[3]//button[1]");
    private static final By CONFIRM_RENEWAL_BUTTON =
            By.id("renewConfirmBtn");
    private static final By RENEWAL_SUCCESS =
            By.cssSelector("[data-testid='renewal-success']");

    public RenewPolicyPage(ChromeDriver d) {
        super(d);
    }

    public void clickRenewPolicyLink() {
        click(RENEW_POLICY_LINK).click();
    }

    public void renewFirstPolicy() {
        click(FIRST_POLICY_RENEW_BUTTON).click();
    }

    public void confirmRenewal() {
        click(CONFIRM_RENEWAL_BUTTON).click();
    }

    public boolean isRenewalSuccessful() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(RENEWAL_SUCCESS));
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }
}
