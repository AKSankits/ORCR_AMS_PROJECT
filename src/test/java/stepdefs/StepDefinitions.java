package stepdefs;

import io.cucumber.java.After;
import io.cucumber.java.en.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import pages.*;

public class StepDefinitions {

    private ChromeDriver d;
    private HomePage homePage;

    static final String EMAIL    = "rajesh@insurance.com";
    static final String PASSWORD = "Insure@123";

    @Given("the user is on the PolicyDekho home page")
    public void openHomePage() {
        d = new ChromeDriver();
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

    @After
    public void tearDown() {
        if (d != null) d.quit();
    }
}
