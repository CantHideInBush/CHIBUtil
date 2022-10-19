package com.canthideinbush.utils.storing;

import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigMerger {


    private final YamlConfiguration merge;
    private final YamlConfiguration into;

    public ConfigMerger(YamlConfiguration merge, YamlConfiguration into) {
        this.merge = merge;
        this.into = into;
    }


    public void merge() {
        for (String key : merge.getKeys(true)) {
            if (!into.contains(key)) {
                into.set(key, merge.get(key));
            }
        }
        System.out.println(into.saveToString());
    }





}
