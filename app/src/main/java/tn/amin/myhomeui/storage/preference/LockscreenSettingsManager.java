package tn.amin.myhomeui.storage.preference;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.paperdb.Book;

public final class LockscreenSettingsManager implements SettingsManager {
    public static final String PREF_NAME = ".lockscreen";

    private final Book book;

    public LockscreenSettingsManager(Book book) {
        this.book = book;
    }

    @Override
    public Book getBook() {
        return book;
    }

    HashSet<Map<String, Object>> customizations = null;

    public HashSet<Map<String, Object>> getInsertedItems() {
        return getInsertedItems(false);
    }

    public HashSet<Map<String, Object>> getInsertedItems(boolean forceReload) {
        synchronized (this) {
            if (forceReload || customizations == null){
                customizations = book.read("inserted_items", new HashSet<>());
            }
            return customizations;
        }
    }

    public void setInsertedItems(Set<Map<String, Object>> inserterdItems) {
        synchronized (this) {
            customizations = new HashSet<>(inserterdItems);
            book.write("inserted_items", inserterdItems);
        }
    }

    public void addInsertedItem(Map<String, Object> customization) {
        synchronized (this) {
            customizations = getInsertedItems();
            customizations.add(customization);
            book.write("inserted_items", customizations);
        }
    }
    public void removeInsertedItem(Map<String, Object> customization) {
        synchronized (this) {
            customizations = getInsertedItems();
            customizations.remove(customization);
            book.write("inserted_items", customizations);
        }
    }

    public void setHiddenLockscreenItems(Set<String> ids) {
        book.write("hidden_items", ids);
    }

    public Set<String> getHiddenLockscreenItems() {
        return book.read("hidden_items", Collections.emptySet());
    }

    public CustomizationBundle getBundle() {
        CustomizationBundle bundle = new CustomizationBundle();
        bundle.insertedItems = getInsertedItems();
        bundle.hiddenItems = getHiddenLockscreenItems();
        return bundle;
    }

    public void setBundle(CustomizationBundle bundle) {
        setInsertedItems(bundle.insertedItems);
        setHiddenLockscreenItems(bundle.hiddenItems);
    }
}
