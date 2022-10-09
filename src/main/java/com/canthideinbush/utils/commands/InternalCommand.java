package com.canthideinbush.utils.commands;

import com.canthideinbush.utils.CHIBPlugin;
import com.canthideinbush.utils.Reflector;
import com.canthideinbush.utils.UtilsProvider;
import com.canthideinbush.utils.chat.ChatUtils;
import com.canthideinbush.utils.managers.Keyed;
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
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class InternalCommand implements TabCompleter, CommandExecutor, Keyed<Class<? extends InternalCommand>> {




    public InternalCommand() {
        register(this.getPlugin());
    }

    public InternalCommand(CHIBPlugin plugin) {
        this.register(plugin);
    }

    public abstract String getName();

    @Override
    public Class<? extends  InternalCommand> getKey() {
        return this.getClass();
    }

    public abstract InternalCommand getParentCommand();

    public CHIBPlugin getPlugin() {
        return getParentCommand().getPlugin();
    }


    public abstract String getPermission();

    public String getAbsolutePermission() {
        if (getParentCommand() != null) return  getParentCommand().getAbsolutePermission();
        return getPermission();
    }

    public UtilsProvider getUtilsProvider() {
        return getParentCommand().getUtilsProvider();
    }

    public int getArgIndex() {
        if (getParentCommand() == null) return 0;
        return getParentCommand().getArgIndex() + 1;
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

    public abstract List<String> complete(String[] args);

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return complete(args);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return execute(commandSender, args);
    }




    public void register(CHIBPlugin plugin) {
        CHIBCommandsRegistry.instance.register(this);
        PluginCommand pluginCommand = Reflector.newInstance(PluginCommand.class, new Class[]{String.class, Plugin.class}, this.getName(), plugin);
        assert pluginCommand != null;
        pluginCommand.setExecutor(this);
        pluginCommand.setTabCompleter(this);
        pluginCommand.setPermission(this.getAbsolutePermission());
        Bukkit.getCommandMap().register(this.getName(), pluginCommand);
    }
}
