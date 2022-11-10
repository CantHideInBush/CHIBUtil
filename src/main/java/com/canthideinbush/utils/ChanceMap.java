package com.canthideinbush.utils;

import com.canthideinbush.utils.storing.ABSave;
import com.canthideinbush.utils.storing.YAMLElement;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


@SerializableAs("ChanceMap")
public class ChanceMap<T> implements ABSave  {

    public ChanceMap() {}

    public ChanceMap(Map<T, Double> map) {
        chanceMap.putAll(map);
        updateActualChanceMap();
    }


    public static ChanceMap<?> deserialize(Map<String, Object> map) {
        ChanceMap<?> chanceMap = new ChanceMap<>();
        chanceMap.deserializeFromMap(map);
        chanceMap.updateActualChanceMap();
        return chanceMap;
    }


    @YAMLElement
    Map<T, Double> chanceMap = new HashMap<>();

    LinkedHashMap<Double, T> actualChanceMap = new LinkedHashMap<>();

    private void updateActualChanceMap() {
        actualChanceMap.clear();
        double counter = 100;
        for (Map.Entry<T, Double> entry : chanceMap.entrySet()) {
            counter -= entry.getValue();
            if (counter < 0) {
                throw new IllegalStateException("Sum of chances in ChanceMap can't be greater than 100%");
            }
            actualChanceMap.put(counter, entry.getKey());
        }
    }

    public void add(T value, Double chance) {
        chanceMap.put(value, chance);
        updateActualChanceMap();
    }

    public void remove(T value) {
        chanceMap.remove(value);
        updateActualChanceMap();
    }


    public void clear() {
        chanceMap.clear();
        actualChanceMap.clear();
    }
    public T getRandom() {
        double random = ThreadLocalRandom.current().nextDouble(0, 100);
        for (Map.Entry<Double, T> entry : actualChanceMap.entrySet()) {
            if (random >= entry.getKey()) return entry.getValue();
        }
        return null;
    }


    @Override
    public String toString() {
        return "ChanceMap{" +
                "chanceMap=" + chanceMap +
                ", actualChanceMap=" + actualChanceMap +
                '}';
    }
}
