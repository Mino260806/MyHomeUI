package tn.amin.myhomeui.util;

import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

import tn.amin.myhomeui.BuildConfig;

public class FileUtil {
    public static String getBaseName(File file) {
        String fileName =  file.getName();
        if (fileName.indexOf(".") > 0) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        } else {
            return fileName;
        }
    }

    public static String getExtension(File file) {
        String fileName =  file.getName();
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            return fileName.substring(index + 1);
        } else {
            return "";
        }
    }


    public static File getUniqueFile(File file) {
        if (!file.exists()) return file;
        String baseFileName = FileUtil.getBaseName(file);
        String extension = FileUtil.getExtension(file);
        String[] split = baseFileName.split("-");
        int lastIndex = split.length-1;
        if (split[lastIndex].matches("\\d+"))
            split[lastIndex] = Integer.toString(Integer.parseInt(split[lastIndex]) + 1);
        else split[lastIndex] = split[lastIndex] + "-1";
        return getUniqueFile(new File(file.getParentFile(), String.join("-", split) + "." + extension));
    }

    public static void listFilesRecursively(File root, Predicate<File> predicate) {
        if (root.isFile()) {
            predicate.test(root);
            return;
        }
        File[] listFiles = root.listFiles();
        if (listFiles == null) return;
        for (File file: listFiles) {
            if (predicate.test(file)) listFilesRecursively(file, predicate);
        }
    }

    public static void clearDirectory(File directory) {
        deleteRecursive(directory, true);
    }
    private static void deleteRecursive(File fileOrDirectory, boolean keepRoot) {
        if (fileOrDirectory.isDirectory()) {
            File[] listFiles = fileOrDirectory.listFiles();
            if (listFiles == null)
                return;
            for (File child : listFiles)
                deleteRecursive(child, false);
        }

        if (!keepRoot)
            fileOrDirectory.delete();
    }

    public static void copy(InputStream is, FileOutputStream fos) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            FileUtils.copy(is, fos);
        } else {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
    }
}
