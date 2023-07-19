package com.canthideinbush.utils.commands;

import com.canthideinbush.utils.CHIBPlugin;
import com.canthideinbush.utils.UtilsProvider;
import com.canthideinbush.utils.storing.ArgParser;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class ParentCommand extends InternalCommand {

    public Collection<InternalCommand> getSubcommands() {return subCommands;}

    protected final ArrayList<InternalCommand> subCommands = new ArrayList<>();


    private CHIBPlugin plugin;
    private final UtilsProvider utilsProvider;

    public ParentCommand(CHIBPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        this.utilsProvider = plugin.getUtilsProvider();
    }

    public ParentCommand() {
        super();
        this.utilsProvider = getParentCommand().getUtilsProvider();
    }


    public void saveDefaultConfigMessages() {
        for (InternalCommand command : getSubcommands()) {
            command.saveDefaultConfigMessages();
        }
        super.saveDefaultConfigMessages();
    }




    @Override
    public CHIBPlugin getPlugin() {
        if (getParentCommand() != null) {
            return getParentCommand().getPlugin();
        }
        return plugin;
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {

        ArgParser parser = new ArgParser(args, getArgIndex());

        InternalCommand subCommand;

        if (!parser.hasNext()) sendConfigErrorMessage(sender, "command-arguments-insufficient");
        else if ((subCommand = getSubCommand(parser.current())) == null) sendConfigErrorMessage(sender, "command-arguments-unknown", parser.next());
        else {
            if (!hasPermission(sender, subCommand.getAbsolutePermission())) {
                sendConfigErrorMessage(sender, "permissions-insufficient", subCommand.getAbsolutePermission());
                return false;
            }
            return subCommand.execute(sender, args);
        }
        return false;
    }

    public InternalCommand getSubCommand(String name) {
        return getSubcommands().stream().filter(c -> c.getName().equals(name) || c.getAliases().contains(name)).findAny().orElse(null);
    }

    @Override
    public UtilsProvider getUtilsProvider() {
        return utilsProvider;
    }

    @Override
    public List<String> complete(String[] args, CommandSender sender) {
        InternalCommand subCommand;
        if (args.length == getArgIndex() + getArgCount()) {
            ArrayList<String> completion = new ArrayList<>(getSubcommands().stream().filter(command -> command.hasPermission(sender, command.getAbsolutePermission())).map(InternalCommand::getName).toList());
            getSubcommands().stream().filter(command -> command.hasPermission(sender, command.getAbsolutePermission())).map(InternalCommand::getAliases).forEach(completion::addAll);
            return completion;
        }
        else if ((subCommand = getSubCommand(args[getArgIndex() + getArgCount() - 1])) != null) {
            return subCommand.complete(args, sender);
        }
        return Collections.emptyList();
    }
}
