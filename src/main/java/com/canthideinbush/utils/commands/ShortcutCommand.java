package com.canthideinbush.utils.commands;

import com.canthideinbush.utils.CHIBPlugin;
import com.canthideinbush.utils.UtilsProvider;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class ShortcutCommand extends InternalCommand {


    @Override
    public boolean execute(CommandSender sender, String[] args) {

        String[] newArgs = Arrays.copyOf(commandArgs, commandArgs.length + args.length);
        System.arraycopy(args, 0, newArgs, commandArgs.length, args.length);

        return InternalCommand.getCommand(this.args[0]).execute(sender, newArgs);
    }

    private final String name;
    private final String[] args;
    private final String[] commandArgs;

    private final CHIBPlugin plugin;

    public ShortcutCommand(CHIBPlugin plugin, String name, String shortcut) {
        super(plugin, name);
        this.name = name;
        this.args = shortcut.split(" ");
        this.commandArgs = Arrays.copyOfRange(args, 1, args.length);
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CHIBPlugin getPlugin() {
        return plugin;
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String getAbsolutePermission() {
        return getCommand(this.args[0]).getAbsolutePermission();
    }

    @Override
    public UtilsProvider getUtilsProvider() {
        return plugin.getUtilsProvider();
    }

    @Override
    public Class<? extends InternalCommand> getParentCommandClass() {
        return null;
    }

    @Override
    public List<String> complete(String[] args) {
        String[] newArgs = new String[args.length + this.args.length - 1];
        System.arraycopy(this.args, 1, newArgs, 0, this.args.length - 1);
        System.arraycopy(args, 0, newArgs, this.args.length - 1, args.length);
        return InternalCommand.getCommand(this.args[0]).complete(newArgs);
    }
}
