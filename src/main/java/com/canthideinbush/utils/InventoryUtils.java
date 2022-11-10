package com.canthideinbush.utils;

import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class InventoryUtils {
    
    public static Integer getRandomEmptySlot(Inventory inventory) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        ArrayList<Integer> slots = new ArrayList<>();
        for (int i = 0; i < inventory.getSize(); i++) slots.add(i);
        int slot = slots.get(random.nextInt(0, slots.size()));
        slots.remove(slot);
        while (inventory.getItem(slot) != null) {
            if (slots.isEmpty()) return null;
            slot = slots.get(random.nextInt(0, slots.size()));
        }
        return slot;
    }
    
    
}
