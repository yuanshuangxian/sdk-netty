package com.fqserver.lang.util.concurrent;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class AtomicLongMap<K> {
    private final ConcurrentHashMap<K, AtomicLong> map;

    private AtomicLongMap(ConcurrentHashMap<K, AtomicLong> map) {
        this.map = checkNotNull(map);
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling
     * method is not null.
     *
     * @param reference
     *            an object reference
     * @return the non-null reference that was validated
     * @throws NullPointerException
     *             if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    /**
     * Creates an {@code AtomicLongMap}.
     */
    public static <K> AtomicLongMap<K> create() {
        return new AtomicLongMap<K>(new ConcurrentHashMap<K, AtomicLong>());
    }

    /**
     * Creates an {@code AtomicLongMap} with the same mappings as the specified
     * {@code Map}.
     */
    public static <K> AtomicLongMap<K> create(Map<? extends K, ? extends Long> m) {
        AtomicLongMap<K> result = create();
        result.putAll(m);
        return result;
    }

    /**
     * Returns the value associated with {@code key}, or zero if there is no
     * value associated with {@code key}.
     */
    public long get(K key) {
        AtomicLong atomic = map.get(key);
        return atomic == null ? 0L : atomic.get();
    }

    /**
     * Increments by one the value currently associated with {@code key}, and
     * returns the new value.
     */
    public long incrAndGet(K key) {
        return addAndGet(key, 1);
    }

    /**
     * Decrements by one the value currently associated with {@code key}, and
     * returns the new value.
     */
    public long decrAndGet(K key) {
        return addAndGet(key, -1);
    }

    /**
     * Adds {@code delta} to the value currently associated with {@code key},
     * and returns the new value.
     */
    public long addAndGet(K key, long delta) {
        outer: for (;;) {
            AtomicLong atomic = map.get(key);
            if (atomic == null) {
                atomic = map.putIfAbsent(key, new AtomicLong(delta));
                if (atomic == null) {
                    return delta;
                }
                // atomic is now non-null; fall through
            }

            for (;;) {
                long oldValue = atomic.get();
                if (oldValue == 0L) {
                    // don't compareAndSet a zero
                    if (map.replace(key, atomic, new AtomicLong(delta))) {
                        return delta;
                    }
                    // atomic replaced
                    continue outer;
                }

                long newValue = oldValue + delta;
                if (atomic.compareAndSet(oldValue, newValue)) {
                    return newValue;
                }
                // value changed
            }
        }
    }

    /**
     * Increments by one the value currently associated with {@code key}, and
     * returns the old value.
     */
    public long getAndIncr(K key) {
        return getAndAdd(key, 1);
    }

    /**
     * Decrements by one the value currently associated with {@code key}, and
     * returns the old value.
     */
    public long getAndDecr(K key) {
        return getAndAdd(key, -1);
    }

    /**
     * Adds {@code delta} to the value currently associated with {@code key},
     * and returns the old value.
     */
    public long getAndAdd(K key, long delta) {
        outer: for (;;) {
            AtomicLong atomic = map.get(key);
            if (atomic == null) {
                atomic = map.putIfAbsent(key, new AtomicLong(delta));
                if (atomic == null) {
                    return 0L;
                }
                // atomic is now non-null; fall through
            }

            for (;;) {
                long oldValue = atomic.get();
                if (oldValue == 0L) {
                    // don't compareAndSet a zero
                    if (map.replace(key, atomic, new AtomicLong(delta))) {
                        return 0L;
                    }
                    // atomic replaced
                    continue outer;
                }

                long newValue = oldValue + delta;
                if (atomic.compareAndSet(oldValue, newValue)) {
                    return oldValue;
                }
                // value changed
            }
        }
    }

    /**
     * Associates {@code newValue} with {@code key} in this map, and returns the
     * value previously associated with {@code key}, or zero if there was no
     * such value.
     */
    public long put(K key, long newValue) {
        outer: for (;;) {
            AtomicLong atomic = map.get(key);
            if (atomic == null) {
                atomic = map.putIfAbsent(key, new AtomicLong(newValue));
                if (atomic == null) {
                    return 0L;
                }
                // atomic is now non-null; fall through
            }

            for (;;) {
                long oldValue = atomic.get();
                if (oldValue == 0L) {
                    // don't compareAndSet a zero
                    if (map.replace(key, atomic, new AtomicLong(newValue))) {
                        return 0L;
                    }
                    // atomic replaced
                    continue outer;
                }

                if (atomic.compareAndSet(oldValue, newValue)) {
                    return oldValue;
                }
                // value changed
            }
        }
    }

    /**
     * Copies all of the mappings from the specified map to this map. The effect
     * of this call is equivalent to that of calling {@code put(k, v)} on this
     * map once for each mapping from key {@code k} to value {@code v} in the
     * specified map. The behavior of this operation is undefined if the
     * specified map is modified while the operation is in progress.
     */
    public void putAll(Map<? extends K, ? extends Long> m) {
        for (Map.Entry<? extends K, ? extends Long> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Removes and returns the value associated with {@code key}. If {@code key}
     * is not in the map, this method has no effect and returns zero.
     */
    public long remove(K key) {
        AtomicLong atomic = map.get(key);
        if (atomic == null) {
            return 0L;
        }

        for (;;) {
            long oldValue = atomic.get();
            if (oldValue == 0L || atomic.compareAndSet(oldValue, 0L)) {
                // only remove after setting to zero, to avoid concurrent
                // updates
                map.remove(key, atomic);
                // succeed even if the remove fails, since the value was already
                // adjusted
                return oldValue;
            }
        }
    }

    /**
     * Removes all mappings from this map whose values are zero.
     *
     * <p>
     * This method is not atomic: the map may be visible in intermediate states,
     * where some of the zero values have been removed and others have not.
     */
    public void removeAllZeros() {
        Iterator<Map.Entry<K, AtomicLong>> entryIterator = map.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<K, AtomicLong> entry = entryIterator.next();
            AtomicLong atomic = entry.getValue();
            if (atomic != null && atomic.get() == 0L) {
                entryIterator.remove();
            }
        }
    }

    /**
     * Returns the sum of all values in this map.
     *
     * <p>
     * This method is not atomic: the sum may or may not include other
     * concurrent operations.
     */
    public long sum() {
        long sum = 0L;
        for (AtomicLong value : map.values()) {
            sum = sum + value.get();
        }
        return sum;
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     */
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    /**
     * Returns the number of key-value mappings in this map. If the map contains
     * more than {@code Integer.MAX_VALUE} elements, returns
     * {@code Integer.MAX_VALUE}.
     */
    public int size() {
        return map.size();
    }

    /**
     * Returns {@code true} if this map contains no key-value mappings.
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Removes all of the mappings from this map. The map will be empty after
     * this call returns.
     *
     * <p>
     * This method is not atomic: the map may not be empty after returning if
     * there were concurrent writes.
     */
    public void clear() {
        map.clear();
    }

    @Override
    public String toString() {
        return map.toString();
    }

    /*
     * ConcurrentMap operations which we may eventually add.
     * 
     * The problem with these is that remove(K, long) has to be done in two
     * phases by definition --- first decrementing to zero, and then removing.
     * putIfAbsent or replace could observe the intermediate zero-state. Ways we
     * could deal with this are:
     * 
     * - Don't define any of the ConcurrentMap operations. This is the current
     * state of affairs.
     * 
     * - Define putIfAbsent and replace as treating zero and absent identically
     * (as currently implemented below). This is a bit surprising with
     * putIfAbsent, which really becomes putIfZero.
     * 
     * - Allow putIfAbsent and replace to distinguish between zero and absent,
     * but don't implement remove(K, long). Without any two-phase operations it
     * becomes feasible for all remaining operations to distinguish between zero
     * and absent. If we do this, then perhaps we should add replace(key, long).
     * 
     * - Introduce a special-value private static final AtomicLong that would
     * have the meaning of removal-in-progress, and rework all operations to
     * properly distinguish between zero and absent.
     */

    /**
     * If {@code key} is not already associated with a value or if {@code key}
     * is associated with zero, associate it with {@code newValue}. Returns the
     * previous value associated with {@code key}, or zero if there was no
     * mapping for {@code key}.
     */
    long putIfAbsent(K key, long newValue) {
        for (;;) {
            AtomicLong atomic = map.get(key);
            if (atomic == null) {
                atomic = map.putIfAbsent(key, new AtomicLong(newValue));
                if (atomic == null) {
                    return 0L;
                }
                // atomic is now non-null; fall through
            }

            long oldValue = atomic.get();
            if (oldValue == 0L) {
                // don't compareAndSet a zero
                if (map.replace(key, atomic, new AtomicLong(newValue))) {
                    return 0L;
                }
                // atomic replaced
                continue;
            }

            return oldValue;
        }
    }

    /**
     * If {@code (key, expectedOldValue)} is currently in the map, this method
     * replaces {@code expectedOldValue} with {@code newValue} and returns true;
     * otherwise, this method returns false.
     *
     * <p>
     * If {@code expectedOldValue} is zero, this method will succeed if
     * {@code (key, zero)} is currently in the map, or if {@code key} is not in
     * the map at all.
     */
    boolean replace(K key, long expectedOldValue, long newValue) {
        if (expectedOldValue == 0L) {
            return putIfAbsent(key, newValue) == 0L;
        } else {
            AtomicLong atomic = map.get(key);
            return (atomic == null) ? false : atomic.compareAndSet(expectedOldValue, newValue);
        }
    }

    /**
     * If {@code (key, value)} is currently in the map, this method removes it
     * and returns true; otherwise, this method returns false.
     */
    boolean remove(K key, long value) {
        AtomicLong atomic = map.get(key);
        if (atomic == null) {
            return false;
        }

        long oldValue = atomic.get();
        if (oldValue != value) {
            return false;
        }

        if (oldValue == 0L || atomic.compareAndSet(oldValue, 0L)) {
            // only remove after setting to zero, to avoid concurrent updates
            map.remove(key, atomic);
            // succeed even if the remove fails, since the value was already
            // adjusted
            return true;
        }

        // value changed
        return false;
    }

    public AtomicLongMap<K> reset(K key) {
        putIfAbsent(key, 0);
        return this;
    }

}