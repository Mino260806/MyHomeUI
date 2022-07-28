package tn.amin.myhomeui.designer.fontpicker;

import android.graphics.fonts.Font;
import android.graphics.fonts.SystemFonts;

import java.io.File;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.paperdb.Book;
import io.paperdb.Paper;
import tn.amin.myhomeui.storage.StorageManager;
import tn.amin.myhomeui.util.FileUtil;
import tn.amin.myhomeui.util.LogUtil;

public class LazyFontList {
    public Boolean hasLoaded = false;
    private int selectedFontIndex = -1;
    private int selectedFontType = -1;

    ArrayList<File> systemFonts = new ArrayList<>();
    ArrayList<File> customFonts = new ArrayList<>();

    Book cacheBook = Paper.bookOn(StorageManager.getCacheDir().getAbsolutePath(), "fonts");

    public static final int SYSTEM_FONT = 0;
    public static final int CUSTOM_FONT = 1;

    public File set(int index, File element) {
        LogUtil.warn("Cannot set element manually in LazyFontList");
        return element;
    }

    public File get(int type, int index) {
        loadFonts();
        return getList(type).get(index);
    }

    private ArrayList<File> getList(int type) {
        switch (type) {
            case SYSTEM_FONT:
                return systemFonts;
            case CUSTOM_FONT:
                return customFonts;
            default:
                return null;
        }
    }

    public int getSelectedFontIndex() {
        return selectedFontIndex;
    }

    public int getSelectedFontType() {
        return selectedFontType;
    }

    public void setSelectedFont(File font) {
        if (font == null) return;
        loadFonts();
        if (hasLoaded) {
            if (systemFonts.contains(font)) {
                selectedFontType = SYSTEM_FONT;
                selectedFontIndex = systemFonts.indexOf(font);
            } else if (customFonts.contains(font)) {
                selectedFontType = CUSTOM_FONT;
                selectedFontIndex = customFonts.indexOf(font);
            }
        }
    }

    public void addCustomFont(File font) {
        customFonts.add(0, font);
        cacheBook.write("custom", customFonts);
    }

    public boolean removeCustomFont(File font) {
        // TODO check first if font is in use
        customFonts.remove(font);
        StorageManager.getFileFromUserFiles(font.getName(), "font").getAbsoluteFile().delete();
        cacheBook.write("custom", customFonts);
        return true;
    }

    public void loadFonts() {
        synchronized (this) {
            if (!hasLoaded) {
                hasLoaded = true;

                // Load from cache
                ArrayList<File> cachedCustomFonts = cacheBook.read("custom", new ArrayList<>());
                ArrayList<File> cachedSystemFonts = cacheBook.read("system", new ArrayList<>());
                if (cachedSystemFonts == null || cachedSystemFonts.isEmpty()) {
                    Set<File> fonts = SystemFontsCompat.getAvailableFonts();
                    systemFonts.addAll(fonts.stream().sorted((f1, f2) -> {
                        return getFontName(f1).compareToIgnoreCase(getFontName(f2));
                    }).distinct().filter((f) -> {
                        String fontName = getFontName(f).toLowerCase(Locale.ROOT);
                        return  !(fontName.endsWith("bold") || fontName.endsWith("italic"));
                    }).collect(Collectors.toList()));
                } else {
                    systemFonts.addAll(cachedSystemFonts);
                }
                if (cachedCustomFonts != null) {
                    customFonts.addAll(cachedCustomFonts.stream()
                            .filter(File::exists).collect(Collectors.toList()));
                }
                // Save to cache
                cacheBook.write("system", systemFonts);
            }
        }
    }

    private static String getFontName(File file) {
        return FileUtil.getBaseName(file);
    }

    public static String simplifyFontName(File file) {
        return getFontName(file).replaceAll("-[rR]egular", "");
    }
}
