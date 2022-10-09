package com.canthideinbush.utils;

import com.canthideinbush.utils.chat.ChatUtils;
import com.canthideinbush.utils.storing.YAMLConfig;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CHIBUtils extends CHIBPlugin {


    private YAMLConfig config;
    private YAMLConfig messageConfig;

    private UtilsProvider utilsProvider;


    private static CHIBUtils instance;

    public static CHIBUtils getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        config = new YAMLConfig(this, "config", true);
        messageConfig = new YAMLConfig(this, "default_messages", true);

        utilsProvider = new UtilsProvider();
        utilsProvider.setChatUtils(new ChatUtils(this));

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

}
