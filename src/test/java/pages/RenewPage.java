package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

public class RenewPage extends BasePage {

    private static final By FIRST_PLAN_BTN = By.xpath("//div[@class='grid grid-cols-1 gap-4']//div[1]//div[3]//button[1]");
    private static final By CONFIRM_BTN    = By.id("renewConfirmBtn");

    public RenewPage(ChromeDriver d) { super(d); }

    public void selectFirstPlanAndConfirm() {
        click(FIRST_PLAN_BTN).click();
        click(CONFIRM_BTN).click();
    }
}
