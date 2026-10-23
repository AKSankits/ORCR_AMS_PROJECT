package pages.todaysUpdate;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PremiumCalculatorPage extends BasePage {

    private static final By CALCULATOR_LINK = By.xpath("//a[@id='linkCalculator']");
    private static final By POLICY_TYPE = By.id("calcPolicyType");
    private static final By PLAN = By.id("calcPlan");
    private static final By DATE_OF_BIRTH = By.id("calcDob");
    private static final By SUM_INSURED = By.id("sumInsured");
    private static final By COVERAGE_TERM = By.id("coverageTerm");
    private static final By HEART_CONDITION = By.id("calcCondHeart");
    private static final By CALCULATE_BUTTON = By.id("calculatePremiumBtn");
    private static final By VALIDATION_MESSAGES =
            By.cssSelector("#calculatorForm p.text-red-600");
    private static final By CALCULATION_RESULT =
            By.cssSelector("[data-testid='calculation-result']");

    public PremiumCalculatorPage(ChromeDriver d) {
        super(d);
    }

    public void openCalculator() {
        click(CALCULATOR_LINK).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='calculator-form']")));
    }

    public void submitCalculator() {
        click(CALCULATE_BUTTON).click();
    }

    public void fillRandomValidDetails() {
        selectRandomNonEmptyOption(POLICY_TYPE);
        wait.until(driver -> new Select(driver.findElement(PLAN)).getOptions().size() > 1);
        selectRandomNonEmptyOption(PLAN);
        selectRandomNonEmptyOption(SUM_INSURED);
        selectRandomNonEmptyOption(COVERAGE_TERM);

        int age = ThreadLocalRandom.current().nextInt(18, 61);
        String dateOfBirth = LocalDate.now().minusYears(age)
                .minusDays(ThreadLocalRandom.current().nextInt(365))
                .toString();
        setDateOfBirth(dateOfBirth);

        WebElement heartCondition = find(HEART_CONDITION);
        if (!heartCondition.isSelected()) {
            heartCondition.click();
        }
    }

    public void fillRandomDetailsWithUnderageDateOfBirth() {
        selectRandomNonEmptyOption(POLICY_TYPE);
        wait.until(driver -> new Select(driver.findElement(PLAN)).getOptions().size() > 1);
        selectRandomNonEmptyOption(PLAN);
        selectRandomNonEmptyOption(SUM_INSURED);
        selectRandomNonEmptyOption(COVERAGE_TERM);
        setDateOfBirth(LocalDate.now().minusYears(10).toString());
    }

    public boolean hasValidationMessage(String expectedMessage) {
        return wait.until(driver -> {
            List<WebElement> messages = driver.findElements(VALIDATION_MESSAGES);
            return messages.stream().anyMatch(message ->
                    message.isDisplayed() && message.getText().equals(expectedMessage));
        });
    }

    public boolean isCalculationDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(CALCULATION_RESULT))
                .isDisplayed();
    }

    private void selectRandomNonEmptyOption(By locator) {
        Select select = new Select(find(locator));
        List<WebElement> options = select.getOptions().stream()
                .filter(option -> !option.getAttribute("value").isBlank())
                .toList();
        WebElement option = options.get(ThreadLocalRandom.current().nextInt(options.size()));
        select.selectByValue(option.getAttribute("value"));
    }

    private void setDateOfBirth(String dateOfBirth) {
        WebElement input = find(DATE_OF_BIRTH);
        ((JavascriptExecutor) d).executeScript(
                "const input = arguments[0];"
                        + "const setter = Object.getOwnPropertyDescriptor("
                        + "HTMLInputElement.prototype, 'value').set;"
                        + "setter.call(input, arguments[1]);"
                        + "input.dispatchEvent(new Event('input', { bubbles: true }));"
                        + "input.dispatchEvent(new Event('change', { bubbles: true }));",
                input, dateOfBirth);
    }
}
