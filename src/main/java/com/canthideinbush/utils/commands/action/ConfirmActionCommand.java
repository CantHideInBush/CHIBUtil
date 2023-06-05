package com.canthideinbush.utils.commands.action;

import com.canthideinbush.utils.commands.InternalCommand;
import com.canthideinbush.utils.commands.ParentCommand;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.function.Consumer;

public abstract class ConfirmActionCommand extends InternalCommand {






    @Override
    public boolean execute(CommandSender sender, String[] args) {


        return true;
    }

    public ConfirmActionCommand(ParentCommand command) {
        parent = command.getClass();
    }

    @Override
    public String getName() {
        return "confirm";
    }

    private final Class<? extends ParentCommand> parent;

    @Override
    public Class<? extends InternalCommand> getParentCommandClass() {
        return parent;
    }


    abstract protected HashMap<CommandSender, Consumer<CommandSender>> getActionMap();
}
