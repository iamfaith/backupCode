package org.gradle.wrapper;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.gradle.wrapper.PathAssembler.LocalDistribution;

public class Install {
    public static final String DEFAULT_DISTRIBUTION_PATH = "wrapper/dists";
    private final IDownload download;
    private final ExclusiveFileAccessManager exclusiveFileAccessManager = new ExclusiveFileAccessManager(120000, 200);
    private final PathAssembler pathAssembler;

    public Install(IDownload download, PathAssembler pathAssembler) {
        this.download = download;
        this.pathAssembler = pathAssembler;
    }

    public File createDist(WrapperConfiguration configuration) throws Exception {
        final URI distributionUrl = configuration.getDistribution();
        LocalDistribution localDistribution = this.pathAssembler.getDistribution(configuration);
        final File distDir = localDistribution.getDistributionDir();
        final File localZipFile = localDistribution.getZipFile();
        return (File) this.exclusiveFileAccessManager.access(localZipFile, new Callable<File>() {
            public File call() throws Exception {
                File markerFile = new File(localZipFile.getParentFile(), localZipFile.getName() + ".ok");
                if (distDir.isDirectory() && markerFile.isFile()) {
                    return Install.this.getDistributionRoot(distDir, distDir.getAbsolutePath());
                }
                if (!localZipFile.isFile()) {
                    File tmpZipFile = new File(localZipFile.getParentFile(), localZipFile.getName() + ".part");
                    tmpZipFile.delete();
                    System.out.println("Downloading " + distributionUrl);
                    Install.this.download.download(distributionUrl, tmpZipFile);
                    tmpZipFile.renameTo(localZipFile);
                }
                for (File dir : Install.this.listDirs(distDir)) {
                    System.out.println("Deleting directory " + dir.getAbsolutePath());
                    Install.this.deleteDir(dir);
                }
                System.out.println("Unzipping " + localZipFile.getAbsolutePath() + " to " + distDir.getAbsolutePath());
                Install.this.unzip(localZipFile, distDir);
                File root = Install.this.getDistributionRoot(distDir, distributionUrl.toString());
                Install.this.setExecutablePermissions(root);
                markerFile.createNewFile();
                return root;
            }
        });
    }

    private File getDistributionRoot(File distDir, String distributionDescription) {
        List<File> dirs = listDirs(distDir);
        if (dirs.isEmpty()) {
            throw new RuntimeException(String.format("Gradle distribution '%s' does not contain any directories. Expected to find exactly 1 directory.", new Object[]{distributionDescription}));
        } else if (dirs.size() == 1) {
            return (File) dirs.get(0);
        } else {
            throw new RuntimeException(String.format("Gradle distribution '%s' contains too many directories. Expected to find exactly 1 directory.", new Object[]{distributionDescription}));
        }
    }

    private List<File> listDirs(File distDir) {
        List<File> dirs = new ArrayList();
        if (distDir.exists()) {
            for (File file : distDir.listFiles()) {
                if (file.isDirectory()) {
                    dirs.add(file);
                }
            }
        }
        return dirs;
    }

    private void setExecutablePermissions(File gradleHome) {
        if (!isWindows()) {
            File gradleCommand = new File(gradleHome, "bin/gradle");
            String errorMessage = null;
            try {
                Process p = new ProcessBuilder(new String[]{"chmod", "755", gradleCommand.getCanonicalPath()}).start();
                if (p.waitFor() == 0) {
                    System.out.println("Set executable permissions for: " + gradleCommand.getAbsolutePath());
                } else {
                    BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    Formatter stdout = new Formatter();
                    while (is.readLine() != null) {
                        stdout.format("%s%n", new Object[]{is.readLine()});
                    }
                    errorMessage = stdout.toString();
                }
            } catch (IOException e) {
                errorMessage = e.getMessage();
            } catch (InterruptedException e2) {
                errorMessage = e2.getMessage();
            }
            if (errorMessage != null) {
                System.out.println("Could not set executable permissions for: " + gradleCommand.getAbsolutePath());
                System.out.println("Please do this manually if you want to use the Gradle UI.");
            }
        }
    }

    private boolean isWindows() {
        if (System.getProperty("os.name").toLowerCase(Locale.US).indexOf("windows") > -1) {
            return true;
        }
        return false;
    }

    private boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String file : children) {
                if (!deleteDir(new File(dir, file))) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private void unzip(File zip, File dest) throws IOException {
        ZipFile zipFile = new ZipFile(zip);
        Enumeration entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            if (entry.isDirectory()) {
                new File(dest, entry.getName()).mkdirs();
            } else {
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(dest, entry.getName())));
                try {
                    copyInputStream(zipFile.getInputStream(entry), outputStream);
                    outputStream.close();
                } catch (Throwable th) {
                    zipFile.close();
                }
            }
        }
        zipFile.close();
    }

    private void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        while (true) {
            int len = in.read(buffer);
            if (len >= 0) {
                out.write(buffer, 0, len);
            } else {
                in.close();
                out.close();
                return;
            }
        }
    }
}
