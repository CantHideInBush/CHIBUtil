package com.canthideinbush.utils.commands;


import com.canthideinbush.utils.Reflector;
import com.canthideinbush.utils.managers.KeyedStorage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.permissions.Permission;

import java.util.*;

public class CHIBCommandsRegistry implements KeyedStorage<InternalCommand> {

    public static final CHIBCommandsRegistry instance = new CHIBCommandsRegistry();

    private static final ArrayList<InternalCommand> commands = new ArrayList<>();

    public static InternalCommand get(Class<? extends InternalCommand> key) {
        return instance.findByKey(key);
    }

    public static InternalCommand getParentCommand(String name) {
        return commands.stream().filter(c -> c.getName().equals(name)).findAny().orElse(null);
    }

    @Override
    public void unregister(InternalCommand command) {
        KeyedStorage.super.unregister(command);
    }

    public void bukkitUnregister(InternalCommand command) {
        if (command.getParentCommandClass() == null) {
            Command bukkitCommand = Bukkit.getCommandMap().getCommand(command.getName());
            if (bukkitCommand != null) bukkitCommand.unregister(Bukkit.getCommandMap());
        }
        for (Permission p : command.getAdditionalPermissions()) {
            Bukkit.getPluginManager().removePermission(p);
        }
        Bukkit.getPluginManager().removePermission(command.getAbsolutePermission());
    }

    @Override
    public Collection<InternalCommand> getObjects() {
        return commands;
    }
}
