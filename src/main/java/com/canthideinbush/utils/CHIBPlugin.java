package com.canthideinbush.utils;

import com.canthideinbush.utils.commands.ShortcutCommand;
import com.canthideinbush.utils.storing.YAMLConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public abstract class CHIBPlugin extends JavaPlugin {

    protected UtilsProvider utilsProvider;

    protected YAMLConfig shortcutCommandsConfig;

    public abstract String getPrefix();

    public abstract YAMLConfig getMessageConfig();

    public YAMLConfig getShortcutCommandsConfig() {
        return shortcutCommandsConfig;
    }

    public UtilsProvider getUtilsProvider() {
        return utilsProvider;
    }

    protected void CHIBInit() {
        this.utilsProvider = new UtilsProvider(this);
        this.shortcutCommandsConfig = new YAMLConfig(this, "shortcut_commands", true);
        registerShortcutCommands();
        createSchematicsFolder();
    }

    private void createSchematicsFolder() {
        new File(getDataFolder() + File.separator + "schematics").mkdir();
    }

    private void registerShortcutCommands() {
        for (String key : shortcutCommandsConfig.getKeys(false)) {
            if (key == null || key.equals("")) continue;
            new ShortcutCommand(this, key, shortcutCommandsConfig.getString(key));
        }
    }

    //Requires public static CHIBPlugin getInstance()
}
