package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

public class HomePage extends BasePage {

    private static final String URL = "https://www.testerrank.com/insurance";

    // Locator constants — all locators defined here, not inline
    private static final By LOGOUT_BTN     = By.id("logoutBtn");
    private static final By RENEW_BTN      = By.id("navRenew");
    private static final By SUPPORT_LINK   = By.id("linkSupport");
    private static final By BUY_POLICY_BTN = By.id("linkBuyPolicy");
    private static final By NOMINEES_LINK = By.xpath("//a[@id='linkNominees']");

    public HomePage(ChromeDriver d) { super(d); }

    public void open()            { d.get(URL); }
    public String getTitle()      { return d.getTitle(); }
    public String getCurrentUrl() { return d.getCurrentUrl(); }

    public void clickLogout()    { click(LOGOUT_BTN).click(); }
    public void clickRenew()     { click(RENEW_BTN).click(); }
    public void clickSupport()   { click(SUPPORT_LINK).click(); }
    public void clickBuyPolicy() { click(BUY_POLICY_BTN).click(); }
    public void clickNominees() {
        click(NOMINEES_LINK).click();
    }

    // Returns true once URL contains fragment — used for assertion after logout
    public boolean isOnInsurancePage() {
        return waitForUrlContains("insurance");
    }
}
