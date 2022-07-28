package tn.amin.myhomeui.designer.fontpicker;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import tn.amin.myhomeui.util.FileUtil;

public class SystemFontsCompat {
    private static String[] fontdirs = { "/system/fonts", "/system/font", "/data/fonts" };

    public static Set<File> getAvailableFonts() {
        HashSet<File> result = new HashSet<>();
        for (String fontdir: fontdirs) {
            FileUtil.listFilesRecursively(new File(fontdir), (file) -> {
                if (file.isFile())
                    result.add(file);
                return true;
            });
        }
        return result;
    }
}
