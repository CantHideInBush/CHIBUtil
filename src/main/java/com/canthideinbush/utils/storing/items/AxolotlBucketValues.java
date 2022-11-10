package com.canthideinbush.utils.storing.items;

import com.canthideinbush.utils.storing.YAMLElement;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Axolotl;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class AxolotlBucketValues extends TypeSpecificValues<AxolotlBucketMeta> {



    public AxolotlBucketValues(Map<String, Object> map) {
        super(map);
    }

    @YAMLElement
    private Axolotl.Variant variant;



    @Override
    public void load(ItemMeta meta) {
        this.variant = ((AxolotlBucketMeta) meta).getVariant();
    }

    @Override
    public void add(ItemMeta meta) {
        ((AxolotlBucketMeta) meta).setVariant(variant);
    }
}
