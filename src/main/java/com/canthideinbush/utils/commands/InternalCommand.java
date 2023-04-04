package com.canthideinbush.utils.commands;

import com.canthideinbush.utils.CHIBPlugin;
import com.canthideinbush.utils.Reflector;
import com.canthideinbush.utils.UtilsProvider;
import com.canthideinbush.utils.managers.Keyed;
import com.canthideinbush.utils.storing.ArgParser;
import com.canthideinbush.utils.storing.YAMLConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class InternalCommand implements TabCompleter, CommandExecutor, Keyed<Class<? extends InternalCommand>> {




    public InternalCommand() {
        register(this.getPlugin());
        saveDefaultConfigMessages();
    }

    public InternalCommand(CHIBPlugin plugin) {
        this.register(plugin);
        saveDefaultConfigMessages();
    }

    public InternalCommand(CHIBPlugin plugin, String name) {
        this.register(plugin, name);
        saveDefaultConfigMessages();
    }

    public abstract String getName();

    @Override
    public Class<? extends  InternalCommand> getKey() {
        return this.getClass();
    }

    public abstract Class<? extends InternalCommand> getParentCommandClass();

    public InternalCommand getParentCommand() {
        return CHIBCommandsRegistry.get(getParentCommandClass());
    }

    public CHIBPlugin getPlugin() {
        return getParentCommand().getPlugin();
    }


    public String getPermission() {
        return getName();
    }

    public String getAbsolutePermission() {
        if (getParentCommand() != null) return getParentCommand().getAbsolutePermission() + "." + getPermission();
        return getPermission();
    }

    public UtilsProvider getUtilsProvider() {
        return getParentCommand().getUtilsProvider();
    }

    public int getArgIndex() {
        if (getParentCommand() == null) return 0;
        return getParentCommand().getArgIndex() + getParentCommand().getArgCount();
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            return execute((Player) sender, args);
        }
        else if (sender instanceof ConsoleCommandSender) {
            return execute((ConsoleCommandSender) sender, args);
        }
        else sendConfigErrorMessage(sender, "invalid-command-sender");

        return true;
    }

    public boolean execute(Player sender, String[] args) {
        sendConfigErrorMessage(sender, "invalid-command-sender-player");
        return false;
    }

    public boolean execute(ConsoleCommandSender sender, String[] args) {
        sendConfigErrorMessage(sender, "invalid-command-sender-console");

        return false;
    }

    protected void sendConfigErrorMessage(CommandSender sender, String path) {
        getUtilsProvider().chat.sendMessage(sender, getUtilsProvider().chat.getMessage(path), ChatColor.RED);
    }
    protected void sendConfigSuccessMessage(CommandSender sender, String path) {
        getUtilsProvider().getChatUtils().sendMessage(sender, getUtilsProvider().chat.getMessage(path), ChatColor.GREEN);
    }

    protected void sendConfigErrorMessage(CommandSender sender, String path, Object... args) {
        getUtilsProvider().chat.sendMessage(sender, getUtilsProvider().chat.getMessage(path).formatted(args), ChatColor.RED);
    }
    protected void sendConfigSuccessMessage(CommandSender sender, String path, Object... args) {
        getUtilsProvider().getChatUtils().sendMessage(sender, getUtilsProvider().chat.getMessage(path).formatted(args), ChatColor.GREEN);
    }

    public List<String> complete(String[] args) {
        return Collections.emptyList();
    }

    public List<String> complete(String[] args, CommandSender sender) {
        return  complete(args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return complete(args, commandSender);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!commandSender.hasPermission(getAbsolutePermission())) {
            sendConfigErrorMessage(commandSender, "permissions-insufficient", getAbsolutePermission());
            return false;
        }
        return execute(commandSender, args);
    }

    public int getArgCount() {
        return 1;
    }






    public void register(CHIBPlugin plugin) {
        CHIBCommandsRegistry.instance.register(this);
        if (getParentCommand() == null) {
            PluginCommand pluginCommand = Reflector.newInstance(PluginCommand.class, new Class[]{String.class, Plugin.class}, this.getName(), plugin);
            assert pluginCommand != null;
            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
            //pluginCommand.setPermission(this.getAbsolutePermission());
            //pluginCommand.permissionMessage(LegacyComponentSerializer.legacyAmpersand().deserialize());
            Bukkit.getCommandMap().register(this.getName(), pluginCommand);
        }
    }

    public void register(CHIBPlugin plugin, String name) {
        CHIBCommandsRegistry.instance.register(this);
        if (getParentCommand() == null) {
            PluginCommand pluginCommand = Reflector.newInstance(PluginCommand.class, new Class[]{String.class, Plugin.class}, name, plugin);
            assert pluginCommand != null;
            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
            //pluginCommand.setPermission(this.getAbsolutePermission());
            //pluginCommand.permissionMessage(LegacyComponentSerializer.legacyAmpersand().deserialize());
            Bukkit.getCommandMap().register(name, pluginCommand);
        }
    }


    public static String getPermission(String command, String[] args) {
        ArgParser parser = new ArgParser(args);
        InternalCommand c = CHIBCommandsRegistry.getParentCommand(command);
        if (c == null) return "";
        while (parser.hasNext() && c instanceof ParentCommand && ((ParentCommand) c).getSubCommand(parser.next()) != null) {
            c = ((ParentCommand) c).getSubCommand(parser.previous());
        }
        return c.getAbsolutePermission();
    }

    public static InternalCommand getCommand(String command) {
        return CHIBCommandsRegistry.getParentCommand(command);
    }

    public static List<String> getCompletion(String command, String[] args) {
        InternalCommand c = CHIBCommandsRegistry.getParentCommand(command);
        if (c == null) return Collections.emptyList();
        ArrayList<String> tabArgs = new ArrayList<>(List.of(args));
        tabArgs.add(0, command);
        return c.complete(tabArgs.toArray(new String[]{}));
    }

    public String getMessagesPath() {
        if (getParentCommand() != null) {
            return getParentCommand().getMessagesPath() + "." + getName();
        }
        else return "command." + getName();
    }

    protected void saveDefaultConfigMessages() {
        YAMLConfig config = this.getPlugin().getMessageConfig();
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(DefaultConfigMessage.class)) {
                field.setAccessible(true);
                DefaultConfigMessage dcm = field.getAnnotation(DefaultConfigMessage.class);
                String path = getMessagesPath() + "." + dcm.forN();
                if (!config.isSet(path)) {
                    try {
                        config.set(path, field.get(this).toString());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public String getMessagePath(String message) {
        return getMessagesPath() + "." + message;
    }

}
