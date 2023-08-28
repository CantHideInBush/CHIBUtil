package com.canthideinbush.utils.worldedit;

import com.canthideinbush.utils.storing.ABSave;
import com.canthideinbush.utils.storing.YAMLElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReferenceTable implements ABSave {


    @YAMLElement
    private LinkedHashMap<Double, Double> referenceProgressMap = new LinkedHashMap<>();

    ArrayList<Double> ordered;
    public ReferenceTable(LinkedHashMap<Double, Long> progressOverTime, long startTime, long totalTime) {
        for (Map.Entry<Double, Long> e : progressOverTime.entrySet()) {
            long elapsed = e.getValue() - startTime;
            double percentage = elapsed / (double) totalTime;
            referenceProgressMap.put(e.getKey(), percentage);
        }
        referenceProgressMap.put(1.0, 1.0);


        ordered = new ArrayList<>(referenceProgressMap.keySet());
    }

    public ReferenceTable(Map<String, Object> map) {
        deserializeFromMap(map);
        ordered = new ArrayList<>(referenceProgressMap.keySet());
    }


    public double getReferenceProgress(double progress) {
        for (double currentReference : ordered) {
            if (progress <= currentReference) {
                return referenceProgressMap.get(currentReference);
            }
        }
        return 1;
    }



}
