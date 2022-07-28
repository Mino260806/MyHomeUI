package tn.amin.myhomeui.serializer.factory.callback;

import androidx.annotation.Nullable;

import java.util.HashMap;

// TODO DOCS
public class CallbackMap<K, V> extends HashMap<K, V> {
    private final HashMap<K, GetterSetter> mCallbacks = new HashMap<>();

    public void addCallback(K key, V defaultValue , SetterCallback<V> setter, GetterCallback<V> getter) {
        putAndCallback(key, defaultValue);
        mCallbacks.put(key, new GetterSetter(setter, getter));
    }

    public void addCallback(K key, V defaultValue , SetterCallback<V> setter) {
        putAndCallback(key, defaultValue);
        mCallbacks.put(key, new GetterSetter(setter));
    }

    public void fireSetter(K key) {
        GetterSetter callback = mCallbacks.get(key);
        if (callback != null)
            callback.setter.onValueSet(get(key));
    }

    public V fireGetter(K key, V storedValue) {
        GetterSetter callback = mCallbacks.get(key);
        if (callback != null && callback.getter != null && callback.getter.needToFire(storedValue)) {
            V value = callback.getter.onValueGet();
            put((K) key, value);
            return value;
        }
        return storedValue;
    }

    @Nullable
    public V putAndCallback(K key, V value) {
        V oldValue = put(key, value);
        fireSetter(key);
        return oldValue;
    }

    @Nullable
    @Override
    public V get(@Nullable Object key) {
        V value = super.get(key);
        value = fireGetter((K) key, value);
        return value;
    }

    public interface SetterCallback<V> { void onValueSet(V value); }
    public interface GetterCallback<V> {
        V onValueGet();
        boolean needToFire(V value);
    }

    private class GetterSetter {
        final SetterCallback<V> setter;
        final GetterCallback<V> getter;

        private GetterSetter(SetterCallback<V> setter, GetterCallback<V> getter) {
            this.setter = setter;
            this.getter = getter;
        }

        private GetterSetter(SetterCallback<V> setter) {
            this(setter, null);
        }
    }
}
