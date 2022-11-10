package com.canthideinbush.utils.storing.items;

import com.canthideinbush.utils.storing.ABSave;
import com.canthideinbush.utils.storing.YAMLElement;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SerializableAs("Item")
public class HRItem implements ABSave {

    public HRItem(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        this.name = meta.displayName();
        this.amount = itemStack.getAmount();
        this.lore = meta.lore();
        this.type = itemStack.getType();
        this.enchantments = meta.getEnchants();
    }



    private void setSpecificValues(ItemStack itemStack) {
        for (Class<?> c : itemStack.getItemMeta().getClass().getInterfaces()) {
            if (ItemMeta.class.isAssignableFrom(c)) {
                typeSpecificValues = TypeSpecificValues.getByClass((Class<? extends ItemMeta>) c);
                typeSpecificValues.load(itemStack.getItemMeta());
                break;
            }
        }
    }

    public HRItem(Map<String, Object> map) {
        deserializeFromMap(map);
    }

    @Override
    public boolean saveNullFields() {
        return false;
    }

    @YAMLElement
    TypeSpecificValues<?> typeSpecificValues;

    @YAMLElement
    Component name;

    @YAMLElement
    List<Component> lore = new ArrayList<>();

    @YAMLElement
    int amount = 1;

    @YAMLElement
    Material type = Material.AIR;

    @YAMLElement
    Map<Enchantment, Integer> enchantments = new HashMap<>();

    public ItemStack toItemStack() {
        ItemStack itemStack = new ItemStack(type, amount);
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(name);
        meta.lore(lore);
        enchantments.forEach((key, value) -> meta.addEnchant(key, value, true));
        itemStack.setItemMeta(meta);
        return itemStack;
    }


}
