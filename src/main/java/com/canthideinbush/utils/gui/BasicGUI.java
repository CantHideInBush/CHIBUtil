package com.canthideinbush.utils.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.List;

public abstract class BasicGUI implements GUI {




    protected Inventory inventory;

    public Inventory getInventory() {
        return inventory;
    }

    public abstract int getSize();





    protected void createInventory() {
        this.inventory = Bukkit.createInventory(null, getSize(), Component.text(""));
    }


    @Override
    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public void close(Player player) {
        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
    }

    @Override
    public List<HumanEntity> viewers() {
        return inventory.getViewers();
    }

}
