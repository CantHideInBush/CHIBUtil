package com.canthideinbush.utils;

import com.canthideinbush.utils.chat.ChatUtils;
import com.canthideinbush.utils.storing.YAMLConfig;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CHIBPlugin extends JavaPlugin {

    protected UtilsProvider utilsProvider;

    public abstract String getPrefix();

    public abstract YAMLConfig getMessageConfig();

    public UtilsProvider getUtilsProvider() {
        return utilsProvider;
    }

    protected void CHIBInit() {
        this.utilsProvider = new UtilsProvider(this);
    }

    //Requires public static CHIBPlugin getInstance()
}
