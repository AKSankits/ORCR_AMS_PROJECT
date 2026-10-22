package pages.todaysUpdate;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AddNomineePage extends BasePage {

    private static final By ADD_NOMINEE_BUTTON = By.id("addNomineeBtn");
    private static final By NOMINEE_NAME = By.id("nomineeName");
    private static final By NOMINEE_RELATION = By.id("nomineeRelation");
    private static final By NOMINEE_SHARE = By.id("nomineeShare");
    private static final By NOMINEE_DOB = By.id("nomineeDob");
    private static final By SAVE_NOMINEE_BUTTON = By.id("saveNomineeBtn");

    private static final List<String> FIRST_NAMES =
            List.of("Aarav", "Ananya", "Ishaan", "Meera", "Rohan", "Sanya");
    private static final List<String> LAST_NAMES =
            List.of("Sharma", "Patel", "Verma", "Singh", "Gupta", "Rao");

    public AddNomineePage(ChromeDriver d) {
        super(d);
    }

    public String addRandomNominee() {
        click(ADD_NOMINEE_BUTTON).click();

        String name = randomName();
        find(NOMINEE_NAME).sendKeys(name);

        Select relation = new Select(find(NOMINEE_RELATION));
        relation.selectByIndex(ThreadLocalRandom.current()
                .nextInt(1, relation.getOptions().size()));

        find(NOMINEE_SHARE).sendKeys("40");

        WebElement dob = find(NOMINEE_DOB);
        LocalDate randomDob = LocalDate.now().minusYears(18)
                .minusDays(ThreadLocalRandom.current().nextInt(365 * 30));
        String dateFormat = "date".equalsIgnoreCase(dob.getAttribute("type"))
                ? "yyyy-MM-dd"
                : "MM/dd/yyyy";
        dob.sendKeys(randomDob.format(DateTimeFormatter.ofPattern(dateFormat)));

        click(SAVE_NOMINEE_BUTTON).click();
        return name;
    }

    public boolean waitForNominee(String name) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[normalize-space()='" + name + "']")));
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    private String randomName() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int attempt = 0; attempt < 20; attempt++) {
            String name = FIRST_NAMES.get(random.nextInt(FIRST_NAMES.size())) + " "
                    + LAST_NAMES.get(random.nextInt(LAST_NAMES.size()));
            if (!d.getPageSource().contains(name)) {
                return name;
            }
        }
        throw new IllegalStateException("Could not generate a unique nominee name.");
    }
}
