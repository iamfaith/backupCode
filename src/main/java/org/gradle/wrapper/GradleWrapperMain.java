package org.gradle.wrapper;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;
import org.gradle.cli.CommandLineParser;
import org.gradle.cli.SystemPropertiesCommandLineConverter;

public class GradleWrapperMain {
    public static final String DEFAULT_GRADLE_USER_HOME = (System.getProperty("user.home") + "/.gradle");
    public static final String GRADLE_USER_HOME_ENV_KEY = "GRADLE_USER_HOME";
    public static final String GRADLE_USER_HOME_PROPERTY_KEY = "gradle.user.home";

    public static void main(String[] args) throws Exception {
        File wrapperJar = wrapperJar();
        File propertiesFile = wrapperProperties(wrapperJar);
        File rootDir = rootDir(wrapperJar);
        System.getProperties().putAll(parseSystemPropertiesFromArgs(args));
        addSystemProperties(rootDir);
        WrapperExecutor.forWrapperPropertiesFile(propertiesFile, System.out).execute(args, new Install(new Download("gradlew", wrapperVersion()), new PathAssembler(gradleUserHome())), new BootstrapMainStarter());
    }

    private static Map<String, String> parseSystemPropertiesFromArgs(String[] args) {
        SystemPropertiesCommandLineConverter converter = new SystemPropertiesCommandLineConverter();
        CommandLineParser commandLineParser = new CommandLineParser();
        converter.configure(commandLineParser);
        commandLineParser.allowUnknownOptions();
        return (Map) converter.convert(commandLineParser.parse(args));
    }

    private static void addSystemProperties(File rootDir) {
        System.getProperties().putAll(SystemPropertiesHandler.getSystemProperties(new File(gradleUserHome(), "gradle.properties")));
        System.getProperties().putAll(SystemPropertiesHandler.getSystemProperties(new File(rootDir, "gradle.properties")));
    }

    private static File rootDir(File wrapperJar) {
        return wrapperJar.getParentFile().getParentFile().getParentFile();
    }

    private static File wrapperProperties(File wrapperJar) {
        return new File(wrapperJar.getParent(), wrapperJar.getName().replaceFirst("\\.jar$", ".properties"));
    }

    private static File wrapperJar() {
        try {
            URI location = GradleWrapperMain.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            if (location.getScheme().equals("file")) {
                return new File(location.getPath());
            }
            throw new RuntimeException(String.format("Cannot determine classpath for wrapper Jar from codebase '%s'.", new Object[]{location}));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    static String wrapperVersion() {
        InputStream resourceAsStream;
        try {
            resourceAsStream = GradleWrapperMain.class.getResourceAsStream("/build-receipt.properties");
            if (resourceAsStream == null) {
                throw new RuntimeException("No build receipt resource found.");
            }
            Properties buildReceipt = new Properties();
            buildReceipt.load(resourceAsStream);
            String versionNumber = buildReceipt.getProperty("versionNumber");
            if (versionNumber == null) {
                throw new RuntimeException("No version number specified in build receipt resource.");
            }
            resourceAsStream.close();
            return versionNumber;
        } catch (Exception e) {
            throw new RuntimeException("Could not determine wrapper version.", e);
        } catch (Throwable th) {
            resourceAsStream.close();
        }
    }

    private static File gradleUserHome() {
        String gradleUserHome = System.getProperty(GRADLE_USER_HOME_PROPERTY_KEY);
        if (gradleUserHome != null) {
            return new File(gradleUserHome);
        }
        gradleUserHome = System.getenv("GRADLE_USER_HOME");
        if (gradleUserHome != null) {
            return new File(gradleUserHome);
        }
        return new File(DEFAULT_GRADLE_USER_HOME);
    }
}
