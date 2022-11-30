package com.canthideinbush.utils.gui;

import com.canthideinbush.utils.CHIBPlugin;
import com.canthideinbush.utils.managers.KeyedStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.ArrayList;

public class GUIManager implements Listener {

    public GUIManager(CHIBPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private final ArrayList<GUI> registeredGUIs = new ArrayList<>();

    public void registerGUI(GUI gui) {
        if (gui.initialize())
        registeredGUIs.add(gui);
    }

    public void unregisterGUI(GUI gui) {
        registeredGUIs.remove(gui);
    }

    public ArrayList<GUI> getRegisteredGUIs() {
        return registeredGUIs;
    }


    public GUI getByPlayer(Player player) {
        return registeredGUIs.stream().filter(g -> g.viewers().contains(player)).findAny().orElse(null);
    }


    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        GUI gui;
        if ((gui = getByPlayer(player)) != null) {
            gui.handleClickEvent(event);
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        GUI gui;
        if ((gui = getByPlayer(player)) != null) {
            gui.handleOpenEvent(event);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        GUI gui;
        if ((gui = getByPlayer(player)) != null) {
            gui.handleCloseEvent(event);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        GUI gui;
        if ((gui = getByPlayer(player)) != null) {
            gui.handleDragEvent(event);
        }
    }


}
