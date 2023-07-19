package com.canthideinbush.utils;

import com.canthideinbush.utils.commands.CHIBCommandsRegistry;
import com.canthideinbush.utils.commands.InternalCommand;
import com.canthideinbush.utils.commands.ShortcutCommand;
import com.canthideinbush.utils.chat.ChatUtils;
import com.canthideinbush.utils.gui.GUIManager;
import com.canthideinbush.utils.storing.YAMLConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    private GUIManager guiManager;
    public GUIManager getGuiManager() {
        return guiManager;
    }

    protected void CHIBInit() {
        this.utilsProvider = new UtilsProvider(this);
        this.shortcutCommandsConfig = new YAMLConfig(this, "shortcut_commands", true);
        registerShortcutCommands();
        this.guiManager = new GUIManager(this);
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

    @Override
    public void onDisable() {
        disableCommands();
    }



    private void disableCommands() {
        List<InternalCommand> forUnregister = new ArrayList<>();
        for (InternalCommand command : CHIBCommandsRegistry.instance.getObjects()) {
            if (command.getPlugin().equals(this)) {
                CHIBCommandsRegistry.instance.bukkitUnregister(command);
            }
            forUnregister.add(command);
        }
        forUnregister.forEach(CHIBCommandsRegistry.instance::unregister);
    }

}
