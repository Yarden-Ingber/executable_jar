import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.LogHandler;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import io.cloudbeat.testng.CbTestNGListener;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Listeners(CbTestNGListener.class)
public class EGTests {

    private static final String appName = "Execution grid";
    private static final String testName = "Execution grid tests";
    private static final String batchId = UUID.randomUUID().toString();
    private static BatchInfo batchInfo = new BatchInfo(JarArgsHandler.getBatchName());
    private static final LogHandler logHandler = new StdoutLogHandler(false);

    @Test
    public void vgRunner() throws Exception {
        CbTestNGListener.startStep("Setup driver");
        System.out.println("This is using the VG 3");
        batchInfo.setId(batchId);
        String url = JarArgsHandler.getWebsite();
        Eyes eyes = new Eyes(new VisualGridRunner());
        eyes.setLogHandler(logHandler);
        System.out.println("log 1");
        WebDriver driver = CbTestNGListener.createWebDriver(true);
        CbTestNGListener.endLastStep();
        try {
            CbTestNGListener.startStep("Open page");
            driver.get(url);
            eyes.setConfiguration(getConfiguration(eyes));
            driver = CbTestNGListener.wrapWebDriver(eyes.open(driver, appName, testName));
            driver.manage().window().maximize();
            CbTestNGListener.endLastStep();
            CbTestNGListener.step("Check window", () -> {
                eyes.check(Target.window().fully(true).withName(url));
                eyes.closeAsync();
            });
        } catch (Throwable t) {
            System.out.println("ERROR: session ID: " + ((RemoteWebDriver) driver).getSessionId());
            throw t;
        } finally {
            driver.quit();
        }
    }

    @Test
    public void classicRunner() throws MalformedURLException {
        CbTestNGListener.startStep("Setup driver");
        System.out.println("This is using the classic");
        System.out.println("api key is:" + CbTestNGListener.getEnv("APPLITOOLS_API_KEY"));
        batchInfo.setId(batchId);
        String url = JarArgsHandler.getWebsite();
        Eyes eyes = new Eyes(new ClassicRunner());
        eyes.setLogHandler(logHandler);
        WebDriver driver = CbTestNGListener.createWebDriver(true);
        CbTestNGListener.endLastStep();
        try {
            CbTestNGListener.startStep("Open page");
            driver.get(url);
            eyes.setConfiguration(getConfiguration(eyes));
            driver = CbTestNGListener.wrapWebDriver(eyes.open(driver, appName, testName));
            driver.manage().window().maximize();
            CbTestNGListener.endLastStep();
            CbTestNGListener.step("Check window", () -> {
                eyes.check(Target.window().fully(true).withName(url));
                eyes.closeAsync();
            });
        } catch (Throwable t) {
            System.out.println("ERROR: session ID: " + ((RemoteWebDriver) driver).getSessionId());
            throw t;
        } finally {
            driver.quit();
        }
    }

    private Configuration getConfiguration(Eyes eyes) {
        Configuration configuration = eyes.getConfiguration();
        configuration.setBatch(batchInfo);
        System.out.println("api key is:" + CbTestNGListener.getEnv("APPLITOOLS_API_KEY"));
        System.out.println("server irl is:" + CbTestNGListener.getEnv("APPLITOOLS_SERVER_URL"));
        configuration.setApiKey(CbTestNGListener.getEnv("APPLITOOLS_API_KEY"));
        configuration.setServerUrl(CbTestNGListener.getEnv("APPLITOOLS_SERVER_URL"));
        configuration.setSendDom(true);
        configuration.addBrowser(1366, 768, BrowserType.CHROME);
        configuration.setSaveDiffs(true);
        return configuration;
    }

}
