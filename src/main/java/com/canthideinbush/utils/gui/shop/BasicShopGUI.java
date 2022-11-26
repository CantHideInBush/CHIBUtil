package com.canthideinbush.utils.gui.shop;

import com.canthideinbush.utils.CHIBPlugin;
import com.canthideinbush.utils.CHIBUtils;
import com.canthideinbush.utils.gui.BasicGUI;
import com.canthideinbush.utils.storing.ABSave;
import com.canthideinbush.utils.storing.YAMLElement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


@SerializableAs("BasicShopGUI")
public abstract class BasicShopGUI extends BasicGUI implements ABSave {


    public BasicShopGUI(int size) {
        this.size = size;
    }

    public BasicShopGUI(Map<String, Object> map) {
        deserializeFromMap(map);
    }


    @YAMLElement
    public HashMap<Integer, ItemStack> contents = new HashMap<>();

    @YAMLElement
    public HashMap<Integer, String> playerCommands = new HashMap<>();

    @YAMLElement
    public HashMap<Integer, String> consoleCommands = new HashMap<>();

    @YAMLElement
    public HashMap<Integer, Double> price = new HashMap<>();

    @YAMLElement
    public int size;

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean initialize() {
        if (CHIBUtils.vaultEconomy == null) return false;
        createInventory();
        contents.forEach((slot, item) -> inventory.setItem(slot, item));
        return true;
    }

    @Override
    public void handleClickEvent(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Player player = (Player) event.getWhoClicked();
        if (clickedInventory != null && clickedInventory.equals(inventory)) {
            int slot = event.getSlot();
            if (price.get(slot) != null) {
                if (!CHIBUtils.vaultEconomy.has(player, price.get(slot))) {
                    getPlugin().getUtilsProvider().chat.sendConfigMessage("common.funds-insufficient", player, ChatColor.RED);
                }
                else {
                    CHIBUtils.vaultEconomy.withdrawPlayer(player, price.get(slot));
                    player.getInventory().addItem(contents.get(slot));
                    getPlugin().getUtilsProvider().chat.sendConfigMessage("common.purchase-successful", player, ChatColor.GREEN, CHIBUtils.vaultEconomy.getBalance(player));
                }
            }
            if (playerCommands.containsKey(slot)) {
                player.performCommand(playerCommands.get(slot));
            }
            if (consoleCommands.containsKey(slot)) {
                Bukkit.getConsoleSender().sendMessage(consoleCommands.get(slot).replaceAll("%player%", player.getName()));
            }

            event.setCancelled(true);
        }
        else if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
            event.setCancelled(true);
        }
        else if (event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
            event.setCancelled(true);
        }





    }


    public abstract CHIBPlugin getPlugin();

    @Override
    public void handleCloseEvent(InventoryCloseEvent event) {

    }

    @Override
    public void handleOpenEvent(InventoryOpenEvent event) {

    }

    public void setPricedItem(int slot, ItemStack itemStack, double price) {
        contents.put(slot, itemStack);
        this.price.put(slot, price);
    }

    public void removePricedItem(int slot) {
        price.remove(slot);
        contents.remove(slot);
    }

    public void setPlayerCommand(int slot, String command) {
        playerCommands.put(slot, command);
    }

    public void removePlayerCommand(int slot) {
        playerCommands.remove(slot);
    }
    public void setConsoleCommand(int slot, String command) {
        consoleCommands.put(slot, command);
    }

    public void removeConsoleCommand(int slot) {
        consoleCommands.remove(slot);
    }



    @Override
    public @NotNull Map<String, Object> serialize() {
        updateContents();
        return ABSave.super.serialize();
    }

    private void updateContents() {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) != null) {
                contents.put(i, inventory.getItem(i));
            }
            else contents.remove(i);
        }
    }

    @Override
    public void handleDragEvent(InventoryDragEvent event) {
        Set<Integer> slots = event.getRawSlots();


        //Cancels drag event if it includes GUI inventory
        if (slots.stream().anyMatch(s -> s <= size - 1)) event.setCancelled(true);
    }
}
