package com.canthideinbush.utils.managers;

import java.util.Collection;

public interface KeyedStorage<T extends Keyed<?>> {

    Collection<T> getObjects();

    default void register(T t) {
        if (t == null) return;
        getObjects().add(t);
    }

    default void unregister(T t) {
        getObjects().remove(t);
    }

    default T findByKey(Object key) {
        for (T t : getObjects()) {
            if (t.getKey().equals(key)) return t;
        }

        return null;
    }

}
