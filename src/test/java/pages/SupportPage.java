package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

public class SupportPage extends BasePage {

    private static final By SUBJECT_INPUT     = By.id("ticketSubject");
    private static final By CATEGORY_SELECT   = By.id("ticketCategory");
    private static final By PRIORITY_SELECT   = By.id("ticketPriority");
    private static final By DESCRIPTION_INPUT = By.id("ticketDescription");
    private static final By SUBMIT_BTN        = By.id("submitTicketBtn");

    public SupportPage(ChromeDriver d) { super(d); }

    public void raiseTicket(String subject, String description) {
        find(SUBJECT_INPUT).sendKeys(subject);
        new Select(find(CATEGORY_SELECT)).selectByIndex(1);
        new Select(find(PRIORITY_SELECT)).selectByIndex(1);
        find(DESCRIPTION_INPUT).sendKeys(description);
        click(SUBMIT_BTN).click();
    }
}
