package org.gradle.wrapper;

import java.io.File;
import java.math.BigInteger;
import java.net.URI;
import java.security.MessageDigest;

public class PathAssembler {
    public static final String GRADLE_USER_HOME_STRING = "GRADLE_USER_HOME";
    public static final String PROJECT_STRING = "PROJECT";
    private File gradleUserHome;

    public class LocalDistribution {
        private final File distDir;
        private final File distZip;

        public LocalDistribution(File distDir, File distZip) {
            this.distDir = distDir;
            this.distZip = distZip;
        }

        public File getDistributionDir() {
            return this.distDir;
        }

        public File getZipFile() {
            return this.distZip;
        }
    }

    public PathAssembler(File gradleUserHome) {
        this.gradleUserHome = gradleUserHome;
    }

    public LocalDistribution getDistribution(WrapperConfiguration configuration) {
        String baseName = getDistName(configuration.getDistribution());
        String rootDirName = rootDirName(removeExtension(baseName), configuration);
        return new LocalDistribution(new File(getBaseDir(configuration.getDistributionBase()), configuration.getDistributionPath() + "/" + rootDirName), new File(getBaseDir(configuration.getZipBase()), configuration.getZipPath() + "/" + rootDirName + "/" + baseName));
    }

    private String rootDirName(String distName, WrapperConfiguration configuration) {
        String urlHash = getMd5Hash(configuration.getDistribution().toString());
        return String.format("%s/%s", new Object[]{distName, urlHash});
    }

    private String getMd5Hash(String string) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(string.getBytes());
            return new BigInteger(1, messageDigest.digest()).toString(32);
        } catch (Exception e) {
            throw new RuntimeException("Could not hash input string.", e);
        }
    }

    private String removeExtension(String name) {
        int p = name.lastIndexOf(".");
        return p < 0 ? name : name.substring(0, p);
    }

    private String getDistName(URI distUrl) {
        String path = distUrl.getPath();
        int p = path.lastIndexOf("/");
        return p < 0 ? path : path.substring(p + 1);
    }

    private File getBaseDir(String base) {
        if (base.equals("GRADLE_USER_HOME")) {
            return this.gradleUserHome;
        }
        if (base.equals(PROJECT_STRING)) {
            return new File(System.getProperty("user.dir"));
        }
        throw new RuntimeException("Base: " + base + " is unknown");
    }
}
