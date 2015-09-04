package com.github.otbproject.otbproject.bot.nullbot;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Wrapper around {@link Collections#emptyMap()} which implements
 * {@link ConcurrentMap}
 *
 * @param <K> Type of key mappings
 * @param <V> Type of value mappings
 */
class EmptyConcurrentMap<K, V> implements ConcurrentMap<K, V> {
    private final Map<K, V> emptyMap = Collections.emptyMap();

    @Override
    public int size() {
        return emptyMap.size();
    }

    @Override
    public boolean isEmpty() {
        return emptyMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return emptyMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return emptyMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return emptyMap.get(key);
    }

    @Override
    public V put(K key, V value) {
        return emptyMap.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return emptyMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        emptyMap.putAll(m);
    }

    @Override
    public void clear() {
        emptyMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return emptyMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return emptyMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return emptyMap.entrySet();
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return emptyMap.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return emptyMap.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return emptyMap.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        return emptyMap.replace(key, value);
    }
}
