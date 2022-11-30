package com.canthideinbush.utils;

import com.canthideinbush.utils.chat.ChatUtils;
import com.canthideinbush.utils.gui.GUIManager;
import com.canthideinbush.utils.storing.YAMLConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public abstract class CHIBPlugin extends JavaPlugin {

    protected UtilsProvider utilsProvider;

    public abstract String getPrefix();

    public abstract YAMLConfig getMessageConfig();

    public UtilsProvider getUtilsProvider() {
        return utilsProvider;
    }

    private GUIManager guiManager;
    public GUIManager getGuiManager() {
        return guiManager;
    }

    protected void CHIBInit() {
        this.utilsProvider = new UtilsProvider(this);
        this.guiManager = new GUIManager(this);
        createSchematicsFolder();
    }

    private void createSchematicsFolder() {
        new File(getDataFolder() + File.separator + "schematics").mkdir();
    }

    //Requires public static CHIBPlugin getInstance()
}
