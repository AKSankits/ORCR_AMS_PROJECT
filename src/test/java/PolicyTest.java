import hooks.Hooks;
import listeners.TestListener;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.*;

import java.lang.reflect.Method;

@Listeners(TestListener.class)
public class PolicyTest extends Hooks {

    HomePage homePage;

    static final String EMAIL    = "rajesh@insurance.com";
    static final String PASSWORD = "Insure@123";

    @BeforeMethod
    public void setUp(Method method) throws Exception {
        d = new ChromeDriver();
        d.manage().window().maximize();
        homePage = new HomePage(d);
        homePage.open();
        super.startRecording(method);
    }

    @Test(priority = 1)
    public void verifyPageTitle() {
        Assert.assertEquals(homePage.getTitle(), "PolicyDekho | TesterRank");
    }

    @Test(priority = 2)
    public void verifyPageURL() {
        Assert.assertTrue(homePage.getCurrentUrl().contains("testerrank.com/insurance"));
    }

    @Test(priority = 3)
    public void verifyLoginWithWrongPassword() {
        Assert.assertEquals(homePage.getTitle(), "WrongTitle | ShouldFail",
                "Intentional failure — demonstrates FAIL status in report");
    }

    @Test(priority = 4)
    public void verifyPolicyRenewal() {
        throw new SkipException("Policy Renewal feature is under maintenance — skipping this test");
    }

    @Test(priority = 5)
    public void verifyLogoutButton() {
        homePage.clickLogout();
        Assert.assertTrue(d.getCurrentUrl().contains("insurance"), "Logout failed");
    }

    @Test(priority = 6)
    public void verifyRaiseSupportTicket() {
        homePage.clickSupport();
        new LoginPage(d).login(EMAIL, PASSWORD);
        homePage.clickSupport();
        new SupportPage(d).raiseTicket("Policy Renewal Assistance",
                "I need assistance with renewing my insurance policy.");
    }

    @Test(priority = 7)
    public void verifyBuyNewPolicy() {
        BuyPolicyPage buyPage = new BuyPolicyPage(d);
        homePage.clickBuyPolicy();
        buyPage.selectVehicleInsurance();
        buyPage.selectPlanRow();
        new LoginPage(d).login(EMAIL, PASSWORD);
        buyPage.clickContinue1();
        buyPage.fillApplicantDetails();
        buyPage.fillVehicleDetails();
        buyPage.fillNomineeDetails();
        buyPage.payAndSubmit();
        Assert.assertTrue(buyPage.isDashboardReached(), "Dashboard redirect failed");
    }
}
