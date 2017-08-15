package newTest;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;
import org.openqa.selenium.Keys;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import perfecto.Utils;

import java.io.IOException;
import java.util.ArrayList;


public class NewTestClass {
    RemoteWebDriver driver;
    PerfectoExecutionContext perfectoExecutionContext;
    ReportiumClient reportiumClient;


    @Parameters({"platformName", "platformVersion", "browserName", "browserVersion", "screenResolution"})
    @BeforeTest
    public void beforeTest(String platformName, String platformVersion, String browserName, String browserVersion, String screenResolution) throws IOException {
        driver = Utils.getRemoteWebDriver(platformName, platformVersion, browserName, browserVersion, screenResolution);
        PerfectoExecutionContext perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withProject(new Project("My Project", "1.0"))
                .withJob(new Job("My Job", 45))
                .withContextTags("tag1")
                .withWebDriver(driver)
                .build();
        reportiumClient = new ReportiumClientFactory().createPerfectoReportiumClient(perfectoExecutionContext);
    }

    @Test
    public void test() {
        try {
            reportiumClient.testStart("Perfecto Test", new TestContext("tag2", "tag3"));
            System.out.println("Yay");

            // open the first tab
            driver.get("http://www.google.com");

// open the second tab

            driver.executeScript("window.parent = window.open('http://www.amazon.com');");
            driver.executeScript("window.parent = window.open('http://www.bn.com');");
            ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
            String handleName = tabs.get(0);
            driver.switchTo().window(handleName);
            Thread.sleep(2000);
            driver.findElementById("lst-ib").sendKeys("Perfecto Mobile");
            driver.findElementById("lst-ib").sendKeys(Keys.ENTER);
            Thread.sleep(2000);

            handleName = tabs.get(2);
            driver.switchTo().window(handleName);
            Thread.sleep(2000);
            driver.findElementById("twotabsearchtextbox").sendKeys("air balloon");
            driver.findElementById("twotabsearchtextbox").sendKeys(Keys.ENTER);
            Thread.sleep(2000);


            handleName = tabs.get(1);
            driver.switchTo().window(handleName);
            Thread.sleep(2000);
            driver.findElementById("searchBarBN").sendKeys("Beauty and the beast");
            driver.findElementById("searchBarBN").sendKeys(Keys.ENTER);
            Thread.sleep(2000);

            // finally rotate through the tabs
            for (String handle:tabs){
                driver.switchTo().window(handle);
                Thread.sleep(2000);
            }



            reportiumClient.testStop(TestResultFactory.createSuccess());
        } catch (Exception e) {
            //reportiumClient.testStop(TestResultFactory.createFailure(e.getMessage(), e));
            e.printStackTrace();
        }
    }

    @AfterTest
    public void afterTest() {
        try {
            // Retrieve the URL of the Single Test Report, can be saved to your execution summary and used to download the report at a later point
            String reportURL = reportiumClient.getReportUrl();
            System.out.println("Report URL:" + reportURL);
            // For documentation on how to export reporting PDF, see https://github.com/perfectocode/samples/wiki/reporting
            // String reportPdfUrl = (String)(driver.getCapabilities().getCapability("reportPdfUrl"));

            driver.close();
            System.out.println("Report: " + reportURL);


            // In case you want to download the report or the report attachments, do it here.
            // PerfectoLabUtils.downloadAttachment(driver, "video", "C:\\test\\report\\video", "flv");
            // PerfectoLabUtils.downloadAttachment(driver, "image", "C:\\test\\report\\images", "jpg");

        } catch (Exception e) {
            e.printStackTrace();
        }

        driver.quit();
    }
}