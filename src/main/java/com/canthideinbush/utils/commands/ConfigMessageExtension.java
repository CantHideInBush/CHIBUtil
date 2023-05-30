package com.canthideinbush.utils.commands;

import org.bukkit.command.CommandSender;

public interface ConfigMessageExtension {


    Class<? extends InternalCommand> getCommandClass();

    default String getMessage(String path) {
        return CHIBCommandsRegistry.get(getCommandClass()).getMessagePath(path);
    }

    default void sendErrorMessage(CommandSender sender, String path) {
        CHIBCommandsRegistry.get(getCommandClass()).sendConfigErrorMessage(sender, path);
    }
    default void sendErrorMessage(CommandSender sender, String path, Object... args) {
        CHIBCommandsRegistry.get(getCommandClass()).sendConfigErrorMessage(sender, path, args);
    }
    default void sendSuccessMessage(CommandSender sender, String path) {
        CHIBCommandsRegistry.get(getCommandClass()).sendConfigSuccessMessage(sender, path);
    }
    default void sendSuccessMessage(CommandSender sender, String path, Object... args) {
        CHIBCommandsRegistry.get(getCommandClass()).sendConfigSuccessMessage(sender, path, args);
    }

}
