package com.canthideinbush.utils.storing;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public interface ABSave extends ConfigurationSerializable {


    @Override
    default @NotNull Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        serializeToMap(map);
        return map;
    }

    default void serializeToMap(Map<String, Object> map) {
        for (Field f : this.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(YAMLElement.class)) {
                f.setAccessible(true);
                try {
                    map.put(f.getName(), YAMLConfig.serialize(f.get(this)));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    default void deserializeFromMap(Map<String, Object> map) {
        for (Field f : this.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(YAMLElement.class)) {
                f.setAccessible(true);
                try {
                    f.set(this, YAMLConfig.deserialize(f.getType(), map.get(f.getName())));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }




}
