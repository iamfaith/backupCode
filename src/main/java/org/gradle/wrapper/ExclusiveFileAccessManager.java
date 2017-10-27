package org.gradle.wrapper;

import java.io.Closeable;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.Callable;

public class ExclusiveFileAccessManager {
    public static final String LOCK_FILE_SUFFIX = ".lck";
    private final int pollIntervalMs;
    private final int timeoutMs;

    public ExclusiveFileAccessManager(int timeoutMs, int pollIntervalMs) {
        this.timeoutMs = timeoutMs;
        this.pollIntervalMs = pollIntervalMs;
    }

    public <T> T access(File exclusiveFile, Callable<T> task) {
        FileLock lock;
        RandomAccessFile randomAccessFile;
        Exception e;
        File lockFile = new File(exclusiveFile.getParentFile(), exclusiveFile.getName() + LOCK_FILE_SUFFIX);
        lockFile.getParentFile().mkdirs();
        RandomAccessFile randomAccessFile2 = null;
        FileChannel channel = null;
        try {
            long startAt = System.currentTimeMillis();
            lock = null;
            randomAccessFile = null;
            while (lock == null) {
                try {
                    if (System.currentTimeMillis() >= ((long) this.timeoutMs) + startAt) {
                        break;
                    }
                    randomAccessFile2 = new RandomAccessFile(lockFile, "rw");
                    channel = randomAccessFile2.getChannel();
                    lock = channel.tryLock();
                    if (lock == null) {
                        maybeCloseQuietly(channel);
                        maybeCloseQuietly(randomAccessFile2);
                        Thread.sleep((long) this.pollIntervalMs);
                        randomAccessFile = randomAccessFile2;
                    } else {
                        randomAccessFile = randomAccessFile2;
                    }
                } catch (Exception e2) {
                    e = e2;
                    randomAccessFile2 = randomAccessFile;
                } catch (Throwable th) {
                    Throwable th2 = th;
                    randomAccessFile2 = randomAccessFile;
                }
            }
            if (lock == null) {
                throw new RuntimeException("Timeout of " + this.timeoutMs + " reached waiting for exclusive access to file: " + exclusiveFile.getAbsolutePath());
            }
            T call = task.call();
            lock.release();
            maybeCloseQuietly(channel);
            channel = null;
            maybeCloseQuietly(randomAccessFile);
            maybeCloseQuietly(null);
            maybeCloseQuietly(null);
            return call;
        } catch (Exception e3) {
            e = e3;
        } catch (Throwable th3) {
            lock.release();
            maybeCloseQuietly(channel);
            channel = null;
            maybeCloseQuietly(randomAccessFile);
            randomAccessFile2 = null;
        }
        try {
            if (e instanceof RuntimeException) {
                throw ((RuntimeException) e);
            }
            throw new RuntimeException(e);
        } catch (Throwable th4) {
            th2 = th4;
            maybeCloseQuietly(channel);
            maybeCloseQuietly(randomAccessFile2);
            throw th2;
        }
    }

    private static void maybeCloseQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
            }
        }
    }
}
