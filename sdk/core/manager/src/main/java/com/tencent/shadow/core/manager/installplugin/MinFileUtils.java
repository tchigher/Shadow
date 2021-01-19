package com.tencent.shadow.core.manager.installplugin;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 没有使用完整的commons-io是因为要控制方法数
 */
public class MinFileUtils {

    private static final Logger mLogger = LoggerFactory.getLogger(MinFileUtils.class);

    /**
     * 获取文件的 MD5
     *
     * @param file 文件
     * @return MD5
     */
    public static String getMD5(
            File file
    ) {
        String value;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            MappedByteBuffer byteBuffer = fileInputStream.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bigInteger = new BigInteger(1, md5.digest());
            value = bigInteger.toString(16);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (null != fileInputStream) {
                try {
                    fileInputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
        return value;
    }

    /**
     * Cleans a directory without deleting it.
     *
     * @param directory directory to clean
     * @throws IOException              in case cleaning is unsuccessful
     * @throws IllegalArgumentException if {@code directory} does not exist or is not a directory
     */
    public static void cleanDir(
            final File directory
    ) throws IOException {
        final File[] files = verifiedListFiles(directory);

        IOException exception = null;
        for (final File file : files) {
            try {
                forceDelete(file);
            } catch (final IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

    /**
     * Deletes a file. If file is a directory, delete it and all sub-directories.
     * A directory to be deleted does not have to be empty.
     *
     * @param file file or directory to delete, must not be {@code null}
     * @throws NullPointerException  if the directory is {@code null}
     * @throws FileNotFoundException if the file was not found
     * @throws IOException           in case deletion is unsuccessful
     */
    private static void forceDelete(
            final File file
    ) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            final boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }
                final String message =
                        "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }

    /**
     * Deletes a directory recursively.
     *
     * @param directory directory to delete
     * @throws IOException              in case deletion is unsuccessful
     * @throws IllegalArgumentException if {@code directory} does not exist or is not a directory
     */
    private static void deleteDirectory(
            final File directory
    ) throws IOException {
        if (!directory.exists()) {
            return;
        }

        cleanDir(directory);

        if (!directory.delete()) {
            final String message =
                    "Unable to delete directory " + directory + ".";
            throw new IOException(message);
        }
    }

    /**
     * Lists files in a directory, asserting that the supplied directory satisfies exists and is a directory
     *
     * @param directory The directory to list
     * @return The files in the directory, never null.
     * @throws IOException if an I/O error occurs
     */
    private static File[] verifiedListFiles(
            File directory
    ) throws IOException {
        if (!directory.exists()) {
            final String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            final String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        final File[] files = directory.listFiles();
        if (files == null) { // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }
        return files;
    }

    public static void writeOutZipEntry(
            ZipFile zipFile,
            ZipEntry zipEntry,
            File outputDir,
            String outputFileName
    ) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = zipFile.getInputStream(zipEntry);
            writeOutInputStream(outputDir, outputFileName, inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public static void writeOutInputStream(
            File outputDir,
            String outputFileName,
            InputStream inputStream
    ) throws IOException {
        BufferedOutputStream bufferedOutputStream = null;
        try {
            File outputFile = new File(outputDir, outputFileName);
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            byte[] bytes = new byte[8192];
            int n;
            while ((n = bufferedInputStream.read(bytes, 0, 8192)) >= 0) {
                bufferedOutputStream.write(bytes, 0, n);
            }
        } finally {
            if (bufferedOutputStream != null) {
                bufferedOutputStream.close();
            }
        }
    }

}
