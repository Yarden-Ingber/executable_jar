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
import io.cloudbeat.testng.CbTestNg;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Listeners(io.cloudbeat.testng.Plugin.class)
public class EGTests extends CbTestNg {

    private static final String batchName = "Execution grid tests";
    private static final String appName = "Execution grid";
    private static final String testName = "Execution grid tests";
    private static final String batchId = UUID.randomUUID().toString();


    private static EyesRunner classicRunner = new ClassicRunner();
    private static EyesRunner visualGridRunner = new VisualGridRunner();
    private static BatchInfo batchInfo = new BatchInfo(JarArgsHandler.getBatchName());
    private static final LogHandler logHandler = new StdoutLogHandler(false);

    @Test
    private void test() throws MalformedURLException {
        System.out.println("Batch ID: " + batchId);
        batchInfo.setId(batchId);
        String url = JarArgsHandler.getWebsite();
        ChromeOptions chromeOptions = new ChromeOptions();
        if (JarArgsHandler.isIsUsingTunnel() && JarArgsHandler.isIsUsingEgClient()) {
            chromeOptions.setCapability("applitools:tunnel", true);
        }
        if (JarArgsHandler.isIsUsingTunnel() && !JarArgsHandler.isIsUsingEgClient()) {
            chromeOptions.setCapability("browserName", "chrome");
            chromeOptions.setCapability("applitools:x-tunnel-id-0", System.getenv("TUNNEL_ID"));
            chromeOptions.setCapability("applitools:apiKey", System.getenv("APPLITOOLS_API_KEY"));
            chromeOptions.setCapability("applitools:eyesServerUrl", System.getenv("APPLITOOLS_SERVER_URL"));
            chromeOptions.addArguments("--headless");
        }
        WebDriver driver;
        if (JarArgsHandler.isIsUsingEgClient()) {
            driver = new RemoteWebDriver(new URL("http://localhost:8080/"), chromeOptions);
        } else {
            chromeOptions.setCapability("applitools:eyesServerUrl", System.getenv("APPLITOOLS_SERVER_URL"));
            chromeOptions.setCapability("applitools:apiKey", System.getenv("APPLITOOLS_API_KEY"));
            driver = new RemoteWebDriver(new URL("https://exec-wus.applitools.com/"), chromeOptions);
        }
        setupDriver(driver);
        Eyes eyes;
        if (JarArgsHandler.isIsUsingVGRunner()) {
            eyes = new Eyes(visualGridRunner);
        } else {
            eyes = new Eyes(classicRunner);
        }
        eyes.setLogHandler(logHandler);
        try {
            startStep("Step");
            driver.get(url);
            eyes.setConfiguration(getConfiguration(eyes));
            driver = eyes.open(driver, appName, testName);
            driver.manage().window().maximize();
            eyes.check(Target.window().fully(true).withName(url));
            eyes.closeAsync();
            endStep("Step");
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
        System.out.println("api key is:" + System.getenv("APPLITOOLS_API_KEY"));
        System.out.println("server irl is:" + System.getenv("APPLITOOLS_SERVER_URL"));
        configuration.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
        configuration.setServerUrl(System.getenv("APPLITOOLS_SERVER_URL"));
        configuration.setSendDom(true);
        configuration.addBrowser(1366, 768, BrowserType.CHROME);
        configuration.setSaveDiffs(true);
        return configuration;
    }

}
