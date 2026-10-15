package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import utils.TestDataUtils;

public class BuyPolicyPage extends BasePage {

    // Locator constants
    private static final By VEHICLE_INSURANCE_LABEL = By.xpath("//label[normalize-space()='Vehicle Insurance']");
    private static final By PLAN_ROW                = By.xpath("//tr[@id='planRow0']//td[@class='px-4 py-3']");
    private static final By CONTINUE_BTN_1          = By.id("continueBtn1");
    private static final By APPLICANT_NAME          = By.id("applicantName");
    private static final By APPLICANT_DOB           = By.id("applicantDob");
    private static final By GENDER_FEMALE           = By.xpath("//label[normalize-space()='Female']");
    private static final By APPLICANT_PAN           = By.id("applicantPan");
    private static final By APPLICANT_AADHAAR       = By.id("applicantAadhaar");
    private static final By APPLICANT_MOBILE        = By.id("applicantMobile");
    private static final By APPLICANT_EMAIL         = By.id("applicantEmail");
    private static final By CONTINUE_BTN_2          = By.id("continueBtn2");
    private static final By REG_NUMBER              = By.id("regNumber");
    private static final By MAKE_MODEL              = By.id("makeModel");
    private static final By CONTINUE_BTN_3          = By.id("continueBtn3");
    private static final By NOMINEE_NAME            = By.id("nomineeName");
    private static final By NOMINEE_DOB             = By.id("nomineeDob");
    private static final By NOMINEE_AADHAAR         = By.id("nomineeAadhaar");
    private static final By CONTINUE_BTN_4          = By.id("continueBtn4");
    private static final By UPI_OPTION              = By.xpath("//fieldset[1]//div[1]//label[3]");
    private static final By UPI_ID_INPUT            = By.id("upiId");
    private static final By PAY_SUBMIT_BTN          = By.id("paySubmitBtn");
    private static final By GO_TO_DASHBOARD_BTN     = By.id("goToDashboard");

    public BuyPolicyPage(ChromeDriver d) { super(d); }

    public void selectVehicleInsurance() {
        click(VEHICLE_INSURANCE_LABEL).click();
    }

    public void selectPlanRow() {
        click(PLAN_ROW).click();
    }

    public void clickContinue1() {
        click(CONTINUE_BTN_1).click();
    }

    public void fillApplicantDetails() {
        find(APPLICANT_NAME).sendKeys(TestDataUtils.NAME);
        find(APPLICANT_DOB).sendKeys(TestDataUtils.DOB);
        click(GENDER_FEMALE).click();
        find(APPLICANT_PAN).sendKeys(TestDataUtils.PAN);
        find(APPLICANT_AADHAAR).sendKeys(TestDataUtils.AADHAAR);
        find(APPLICANT_MOBILE).sendKeys(TestDataUtils.MOBILE);
        find(APPLICANT_EMAIL).sendKeys(TestDataUtils.EMAIL);
        randomSelect("occupation");
        randomSelect("annualIncome");
        click(CONTINUE_BTN_2).click();
    }

    public void fillVehicleDetails() {
        randomSelect("vehicleType");
        find(REG_NUMBER).sendKeys(TestDataUtils.REG_NUMBER);
        randomSelect("mfgYear");
        find(MAKE_MODEL).sendKeys(TestDataUtils.MODEL);
        click(CONTINUE_BTN_3).click();
    }

    public void fillNomineeDetails() {
        find(NOMINEE_NAME).sendKeys(TestDataUtils.NOMINEE_NAME);
        randomSelect("nomineeRelation");
        find(NOMINEE_DOB).sendKeys(TestDataUtils.NOMINEE_DOB);
        find(NOMINEE_AADHAAR).sendKeys(TestDataUtils.NOMINEE_AADHAAR);
        click(CONTINUE_BTN_4).click();
    }

    public void payAndSubmit() {
        click(UPI_OPTION).click();
        find(UPI_ID_INPUT).sendKeys(TestDataUtils.UPI_ID);
        click(PAY_SUBMIT_BTN).click();
        click(GO_TO_DASHBOARD_BTN).click();
    }

    // Returns true when redirected to dashboard — used for assertion
    public boolean isDashboardReached() {
        return waitForUrlContains("insurance");
    }

    private void randomSelect(String id) {
        Select sel = new Select(find(By.id(id)));
        sel.selectByIndex(TestDataUtils.randomIndex(sel.getOptions().size()));
    }
}
