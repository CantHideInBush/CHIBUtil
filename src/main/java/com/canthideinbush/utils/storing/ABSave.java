package com.canthideinbush.utils.storing;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public interface ABSave extends ConfigurationSerializable {


    @Override
    default @NotNull Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        serializeToMap(map);
        return map;
    }


    default boolean saveNullFields() {
        return true;
    }

    default void serializeToMap(Map<String, Object> map) {
        for (Field f : this.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(YAMLElement.class)) {
                f.setAccessible(true);
                try {
                    Object obj = f.get(this);
                    if (saveNullFields() || obj != null) {
                        map.put(f.getName(), YAMLConfig.serialize(f.getType(), obj));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        for (Method m : this.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(StoreResult.class)) {
                m.setAccessible(true);
                try {
                    Object obj = m.invoke(this);
                    if (saveNullFields() || obj != null) {
                        map.put(m.getAnnotation(StoreResult.class).field(), obj);
                    }
                } catch (
                        InvocationTargetException |
                        IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    default void deserializeFromMap(Map<String, Object> map) {
        for (Field f : this.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(YAMLElement.class)) {
                f.setAccessible(true);
                try {
                    if (map.containsKey(f.getName()))
                        f.set(this, YAMLConfig.deserialize(f.getType(), f.getName(), map.get(f.getName())));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }




}
