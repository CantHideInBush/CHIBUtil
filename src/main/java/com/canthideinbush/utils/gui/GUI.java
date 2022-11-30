package com.canthideinbush.utils.gui;

import com.canthideinbush.utils.managers.Keyed;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;

public interface GUI {



    void open(Player player);
    void close(Player player);

    List<HumanEntity> viewers();

    boolean initialize();


    void handleClickEvent(InventoryClickEvent event);

    void handleCloseEvent(InventoryCloseEvent event);

    void handleOpenEvent(InventoryOpenEvent event);

    void handleDragEvent(InventoryDragEvent event);

}
