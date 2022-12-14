package com.canthideinbush.utils;

import com.canthideinbush.utils.gui.shop.BasicShopGUI;
import com.canthideinbush.utils.storing.items.AxolotlBucketValues;
import com.canthideinbush.utils.storing.items.HRItem;
import com.canthideinbush.utils.storing.YAMLConfig;
import com.canthideinbush.utils.tempblock.TempBlockManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class CHIBUtils extends CHIBPlugin {

    static {
        ConfigurationSerialization.registerClass(HRItem.class);
        ConfigurationSerialization.registerClass(AxolotlBucketValues.class);
        ConfigurationSerialization.registerClass(BasicShopGUI.class);
    }

    private YAMLConfig config;
    private YAMLConfig messageConfig;

    public static Economy vaultEconomy;



    private static CHIBUtils instance;

    public static CHIBUtils getInstance() {
        return instance;
    }

    private TempBlockManager tempBlockManager;

    @Override
    public void onEnable() {
        instance = this;

        if (!setupEconomy()) {
            getLogger().warning("Economy plugin not found! Basic shop gui's are disabled.");
        }

        config = new YAMLConfig(this, "config", true);
        messageConfig = new YAMLConfig(this, "default_messages", true);

        CHIBInit();

        this.tempBlockManager = new TempBlockManager();


    }

    @Override
    public void onDisable() {
        tempBlockManager.revertAll();
    }

    @Override
    public String getPrefix() {
        return "[CHIB]";
    }

    @Override
    public YAMLConfig getMessageConfig() {
        return messageConfig;
    }

    @NotNull
    @Override
    public YAMLConfig getConfig() {
        return config;
    }

    @Override
    public UtilsProvider getUtilsProvider() {
        return utilsProvider;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        vaultEconomy = rsp.getProvider();
        return true;
    }

}
