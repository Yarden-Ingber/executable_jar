import org.testng.TestNG;
import org.testng.collections.Lists;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        JarArgsHandler.handleArgs(args);
        try {
            TestNG testng = new TestNG();
            List<String> suites = Lists.newArrayList();
            suites.add(JarArgsHandler.getTestNgSuiteFile());
            testng.setTestSuites(suites);
            testng.run();
        } finally {
            System.exit(0);
        }
    }

}
