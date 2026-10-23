package stepdefs;

import base.BaseTest;
import io.cucumber.java.en.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import pages.*;
import pages.todaysUpdate.AddNomineePage;
import pages.todaysUpdate.NomineesPage;
import pages.todaysUpdate.PremiumCalculatorPage;
import pages.todaysUpdate.RenewPolicyPage;

public class StepDefinitions {

    private ChromeDriver d;
    private HomePage homePage;
    private int initialNomineeCount;
    private String addedNomineeName;

    static final String EMAIL    = "rajesh@insurance.com";
    static final String PASSWORD = "Insure@123";

    @Given("the user is on the PolicyDekho home page")
    public void openHomePage() {
        d = new ChromeDriver();
        BaseTest.setCurrentDriver(d);
        d.manage().window().maximize();
        homePage = new HomePage(d);
        homePage.open();
    }

    @Then("the page title should be {string}")
    public void verifyTitle(String expectedTitle) {
        Assert.assertEquals(homePage.getTitle(), expectedTitle, "Page title mismatch");
    }

    @Then("the page URL should contain {string}")
    public void verifyUrl(String urlFragment) {
        Assert.assertTrue(homePage.getCurrentUrl().contains(urlFragment),
                "URL does not contain: " + urlFragment);
    }

    @When("the user clicks on Renew")
    public void clickRenew() { homePage.clickRenew(); }

    @When("the user logs in with valid credentials")
    public void login() {
        LoginPage loginPage = new LoginPage(d);
        loginPage.login(EMAIL, PASSWORD);
        Assert.assertTrue(loginPage.isLoginSuccessful(), "Login failed");
    }

    @When("the user clicks on Renew again")
    public void clickRenewAgain() { homePage.clickRenew(); }

    @When("the user clicks on the Renew Policy link")
    public void clickRenewPolicyLink() {
        new RenewPolicyPage(d).clickRenewPolicyLink();
    }

    @When("the user renews the first available policy")
    public void renewFirstAvailablePolicy() {
        new RenewPolicyPage(d).renewFirstPolicy();
    }

    @When("the user confirms the renewal")
    public void confirmPolicyRenewal() {
        new RenewPolicyPage(d).confirmRenewal();
    }

    @Then("the renewal confirmation should be displayed")
    public void verifyRenewalConfirmation() {
        Assert.assertTrue(new RenewPolicyPage(d).isRenewalSuccessful(),
                "Policy renewal confirmation was not displayed");
    }

    @When("the user opens the premium calculator")
    public void openPremiumCalculator() {
        new PremiumCalculatorPage(d).openCalculator();
    }

    @When("the user submits the calculator without entering details")
    public void submitCalculatorWithoutDetails() {
        new PremiumCalculatorPage(d).submitCalculator();
    }

    @When("the user enters random calculator details with an underage date of birth")
    public void enterCalculatorDetailsWithUnderageDob() {
        new PremiumCalculatorPage(d).fillRandomDetailsWithUnderageDateOfBirth();
    }

    @When("the user enters random valid calculator details")
    public void enterRandomValidCalculatorDetails() {
        new PremiumCalculatorPage(d).fillRandomValidDetails();
    }

    @When("the user calculates the premium")
    public void calculatePremium() {
        new PremiumCalculatorPage(d).submitCalculator();
    }

    @Then("the calculator should show the validation message {string}")
    public void verifyCalculatorValidationMessage(String expectedMessage) {
        Assert.assertTrue(new PremiumCalculatorPage(d).hasValidationMessage(expectedMessage),
                "Expected calculator validation message: " + expectedMessage);
    }

    @Then("the premium calculation result should be displayed")
    public void verifyPremiumCalculationResult() {
        Assert.assertTrue(new PremiumCalculatorPage(d).isCalculationDisplayed(),
                "Premium calculation result was not displayed");
    }

    @When("the user selects the first plan and confirms renewal")
    public void selectPlanAndConfirm() { new RenewPage(d).selectFirstPlanAndConfirm(); }

    @When("the user clicks on Logout")
    public void clickLogout() { homePage.clickLogout(); }

    @When("the user clicks on Support")
    public void clickSupport() { homePage.clickSupport(); }

    @When("the user clicks on Support again")
    public void clickSupportAgain() { homePage.clickSupport(); }

    @When("the user raises a ticket with subject {string} and description {string}")
    public void raiseTicket(String subject, String description) {
        new SupportPage(d).raiseTicket(subject, description);
    }

    @When("the user clicks on Buy Policy")
    public void clickBuyPolicy() { homePage.clickBuyPolicy(); }

    @When("the user selects Vehicle Insurance")
    public void selectVehicleInsurance() { new BuyPolicyPage(d).selectVehicleInsurance(); }

    @When("the user selects a plan")
    public void selectPlan() { new BuyPolicyPage(d).selectPlanRow(); }

    @When("the user continues from plan selection")
    public void clickContinue1() { new BuyPolicyPage(d).clickContinue1(); }

    @When("the user fills applicant details")
    public void fillApplicant() { new BuyPolicyPage(d).fillApplicantDetails(); }

    @When("the user fills vehicle details")
    public void fillVehicle() { new BuyPolicyPage(d).fillVehicleDetails(); }

    @When("the user fills nominee details")
    public void fillNominee() { new BuyPolicyPage(d).fillNomineeDetails(); }

    @When("the user pays and submits")
    public void payAndSubmit() { new BuyPolicyPage(d).payAndSubmit(); }

    @Then("the user should be redirected to the dashboard")
    public void verifyDashboardRedirect() {
        Assert.assertTrue(new BuyPolicyPage(d).isDashboardReached(), "Dashboard redirect failed");
    }

    @When("the user clicks on Nominees")
    public void clickNominees() {
        homePage.clickNominees();

        NomineesPage nomineesPage = new NomineesPage(d);
        initialNomineeCount = nomineesPage.getNomineeCount();
    }

    @When("the user deletes the first nominee")
    public void deleteFirstNominee() {
        new NomineesPage(d).deleteFirstNominee();
    }

    @When("the user makes room for a nominee")
    public void makeRoomForNominee() {
        if (initialNomineeCount >= 2) {
            int expectedCount = initialNomineeCount - 1;
            NomineesPage nomineesPage = new NomineesPage(d);
            nomineesPage.deleteFirstNominee();

            Assert.assertTrue(
                    nomineesPage.waitForNomineeCount(expectedCount),
                    "Expected nominee count to decrease from "
                            + initialNomineeCount + " to " + expectedCount
            );
            initialNomineeCount = expectedCount;
        }
    }

    @Then("the nominee count should decrease by one")
    public void verifyNomineeDeleted() {
        int expectedCount = initialNomineeCount - 1;

        boolean countUpdated = new NomineesPage(d)
                .waitForNomineeCount(expectedCount);

        Assert.assertTrue(
                countUpdated,
                "Expected nominee count to decrease from "
                        + initialNomineeCount + " to " + expectedCount
        );
    }

    @When("the user adds a nominee with random details")
    public void addRandomNominee() {
        addedNomineeName = new AddNomineePage(d).addRandomNominee();
    }

    @Then("the nominee count should increase by one")
    public void verifyNomineeAdded() {
        NomineesPage nomineesPage = new NomineesPage(d);
        int expectedCount = initialNomineeCount + 1;
        boolean countUpdated = nomineesPage.waitForNomineeCount(expectedCount);
        boolean nomineeVisible = new AddNomineePage(d).waitForNominee(addedNomineeName);

        Assert.assertTrue(countUpdated, "Expected nominee count to increase from "
                + initialNomineeCount + " to " + expectedCount);
        Assert.assertTrue(nomineeVisible, "New nominee was not saved: "
                + addedNomineeName);
    }

    @Then("the nominee count should return to its original value")
    public void verifyNomineeCountRestored() {
        NomineesPage nomineesPage = new NomineesPage(d);
        boolean countUpdated = nomineesPage.waitForNomineeCount(initialNomineeCount);
        boolean nomineeVisible = new AddNomineePage(d).waitForNominee(addedNomineeName);

        Assert.assertTrue(countUpdated, "Nominee count did not return to "
                + initialNomineeCount);
        Assert.assertTrue(nomineeVisible, "Replacement nominee was not saved: "
                + addedNomineeName);
    }
}
