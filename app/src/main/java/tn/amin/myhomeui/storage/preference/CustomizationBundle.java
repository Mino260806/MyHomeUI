package tn.amin.myhomeui.storage.preference;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class CustomizationBundle implements Serializable {
    public Set<Map<String, Object>> insertedItems = new HashSet<>();
    public Set<String> hiddenItems = new HashSet<>();
}
