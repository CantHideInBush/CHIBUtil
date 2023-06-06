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
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class InternalCommand implements TabCompleter, CommandExecutor, Keyed<Class<? extends InternalCommand>> {




    public InternalCommand() {
        register(this.getPlugin());
    }

    public InternalCommand(CHIBPlugin plugin) {
        this.register(plugin);
    }

    public InternalCommand(CHIBPlugin plugin, String name) {
        this.register(plugin, name);
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
        return complete(args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return complete(args, commandSender);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!hasPermission(commandSender, getAbsolutePermission())) {
            sendConfigErrorMessage(commandSender, "permissions-insufficient", getAbsolutePermission());
            return false;
        }
        return execute(commandSender, args);
    }

    public int getArgCount() {
        return 1;
    }

    public  boolean hasPermission(CommandSender sender, String permission) {
        if (sender.hasPermission(permission)) return true;
        Permission p;
        if ((p = Bukkit.getPluginManager().getPermission(permission)) != null) {
            for (String p1 : p.getChildren().keySet()) {
                if (hasPermission(sender, p1))
                    return true;
            }
        }
        return false;
    }






    public void register(CHIBPlugin plugin) {
        register(plugin, this.getName());
    }

    public void register(CHIBPlugin plugin, String name) {
        CHIBCommandsRegistry.instance.register(this);
        Permission permission = new Permission(getAbsolutePermission());
        if (getParentCommand() == null) {
            PluginCommand pluginCommand = Reflector.newInstance(PluginCommand.class, new Class[]{String.class, Plugin.class}, name, plugin);
            assert pluginCommand != null;
            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
            Bukkit.getCommandMap().register(name, pluginCommand);
        }
        else {
            permission.addParent(Bukkit.getPluginManager().getPermission(getParentCommand().getAbsolutePermission()), true);
        }


        Bukkit.getPluginManager().addPermission(permission);


        for (Permission additional : getAdditionalPermissions()) {
            Bukkit.getPluginManager().addPermission(additional);
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
        List<Class<?>> classes = new ArrayList<>(getAdditionalMessageClasses());
        Class<?> c = this.getClass();
        while (!c.equals(Object.class)) {
            classes.add(c);
            c = c.getSuperclass();
        }
        for (Class<?> clazz : classes) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(DefaultConfigMessage.class)) {
                    field.setAccessible(true);
                    DefaultConfigMessage dcm = field.getAnnotation(DefaultConfigMessage.class);
                    String path = getMessagesPath() + (clazz.isAssignableFrom(this.getClass()) ? "." : "." + clazz.getSimpleName() + ".") + dcm.forN();
                    if (!config.isSet(path)) {
                        try {
                            config.set(path, field.get(clazz.equals(this.getClass()) ? this : null).toString());
                        } catch (
                                IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    public String getMessagePath(String message) {
        return getMessagesPath() + "." + message;
    }

    protected List<Permission> getAdditionalPermissions() {
        return Collections.emptyList();
    }


    protected List<Class<?>> getAdditionalMessageClasses() {
        return Collections.emptyList();
    }

    protected List<String> getLabels() {
        return Collections.emptyList();
    }

}
