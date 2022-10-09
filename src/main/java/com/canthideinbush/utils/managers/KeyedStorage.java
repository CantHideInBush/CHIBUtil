package com.canthideinbush.utils.managers;

import java.util.Collection;

public interface KeyedStorage<T extends Keyed<?>> {

    Collection<T> getObjects();

    default void register(T t) {
        getObjects().add(t);
    }

    default void unregister(T t) {
        getObjects().remove(t);
    }

    default T findByKey(Object key) {
        for (T t : getObjects()) {
            if (key.equals(t.getKey())) return t;
        }

        return null;
    }

}
