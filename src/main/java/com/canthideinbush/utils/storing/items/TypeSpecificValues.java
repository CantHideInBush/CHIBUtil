package com.canthideinbush.utils.storing.items;

import com.canthideinbush.utils.Reflector;
import com.canthideinbush.utils.storing.ABSave;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public abstract class TypeSpecificValues<T extends ItemMeta> implements ABSave {

    private static final HashMap<Class<? extends ItemMeta>, Class<? extends TypeSpecificValues<?>>> map = new HashMap<>();


    public TypeSpecificValues(Map<String, Object> map) {
        deserializeFromMap(map);
    }

    static {

        map.put(AxolotlBucketMeta.class, AxolotlBucketValues.class);


    }



    public abstract void load(ItemMeta meta);
    public abstract void add(ItemMeta meta);

    public static TypeSpecificValues<?> getByClass(Class<? extends ItemMeta> c) {
        return Reflector.newInstance(map.get(c), new Class[]{});
    }

}
