package tn.amin.myhomeui.storage.preference;

import android.content.SharedPreferences;

import io.paperdb.Book;

public final class DesignerSettingsManager implements SettingsManager {
    public static final String PREF_NAME = ".designer";

    private final Book book;

    public DesignerSettingsManager(Book book) {
        this.book = book;
    }

    @Override
    public Book getBook() {
        return book;
    }

    public boolean getIsFirstLaunch() {
        //noinspection ConstantConditions
        return book.read("firstLaunch", true);
    }

    public void validateLaunch() {
        book.write("firstLaunch", false);
    }

    public boolean knowsHowToInsert() {
        return Boolean.TRUE.equals(book.read("knows_how_to_insert", false));
    }

    public void setKnowsHowToInsert(boolean value) {
        book.write("knows_how_to_insert", value);
    }

    public boolean knowsHowToCustomizeInsertedItem() {
        return Boolean.TRUE.equals(book.read("knows_how_to_customize_inserted", false));
    }

    public void setKnowsHowToCustomizeInsertedItem(boolean value) {
        book.write("knows_how_to_customize_inserted", value);
    }
}
