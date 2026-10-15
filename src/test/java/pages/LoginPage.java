package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

public class LoginPage extends BasePage {

    private static final By EMAIL_INPUT    = By.id("email");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By LOGIN_BTN      = By.id("loginBtn");

    public LoginPage(ChromeDriver d) { super(d); }

    public void login(String email, String password) {
        find(EMAIL_INPUT).sendKeys(email);
        find(PASSWORD_INPUT).sendKeys(password);
        click(LOGIN_BTN).click();
    }

    public boolean isLoginSuccessful() {
        return waitForUrlContains("insurance");
    }
}
