public class JarArgsHandler {

    private static String testNgSuiteFile = "testng.xml";
    private static boolean isUsingEgClient = false;
    private static String website = "https://www.google.com/";
    private static final String version = "1.0";

    public static String getTestNgSuiteFile() {
        return testNgSuiteFile;
    }

    public static void setTestNgSuiteFile(String testNgSuiteFile) {
        JarArgsHandler.testNgSuiteFile = testNgSuiteFile;
    }

    public static boolean isIsUsingEgClient() {
        return isUsingEgClient;
    }

    public static void setIsUsingEgClient(boolean isUsingEgClient) {
        JarArgsHandler.isUsingEgClient = isUsingEgClient;
    }

    public static String getWebsite() {
        return website;
    }

    public static void setWebsite(String website) {
        JarArgsHandler.website = website;
    }

    static void handleArgs(String[] args) {
        for (int i=0; i<args.length; i++) {
            if (Inputs.testNgSuiteFile.isEqual(args[i])) {
                setTestNgSuiteFile(args[i+1]);
            } else if (Inputs.usingEgClient.isEqual(args[i])) {
                setIsUsingEgClient(true);
            } else if (Inputs.website.isEqual(args[i])) {
                setWebsite(args[i+1]);
            } else if (args.length < 2 && Inputs.help.isEqual(args[i])) {
                System.out.println("\noptions:\n" +
                        "  -s, --suite     Path to the TestNG.xml test suite file. Default is testng.xml in the same directory.\n" +
                        "  -e, --eg-client     Flag to run tests through the eg-client. Default is false.\n" +
                        "  -w, --website     The url the test will call eyes.check() on. Default is https://www.google.com/.\n" +
                        "  -v, --version     Print the version of this JAR" +
                        "  -h, --help     Print help file.\n");
                System.exit(0);
            } else if (args.length < 2 && Inputs.version.isEqual(args[i])) {
                System.out.println(version);
                System.exit(0);
            } else if (args.length > 1 && (Inputs.version.isEqual(args[i]) || Inputs.help.isEqual(args[i]))) {
                String argsString = "";
                for (String arg : args) {
                    argsString = argsString + arg + " ";
                }
                System.out.println("Unsupported option " + argsString);
                System.exit(0);
            }
        }
    }


    public enum Inputs {
        testNgSuiteFile("--suite", "-s"), usingEgClient("--eg-client", "-e"), website("--website", "-w"),
        help("--help", "-h"), version("--version", "-v");

        public String input;
        public String shortInput;

        Inputs(String name, String shortName) {
            input = name;
            shortInput = shortName;
        }

        public boolean isEqual(String userInput){
            return userInput.equals(this.input) || userInput.equals(this.shortInput);
        }
    }

}
