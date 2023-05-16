package com.canthideinbush.utils.commands.builder;

import com.canthideinbush.utils.ObjectBuilder;
import com.canthideinbush.utils.commands.ParentCommand;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.function.Consumer;

public abstract class BuilderCommand<S, T extends ObjectBuilder<S>> extends ParentCommand {

    private final HashMap<CommandSender, T> data = new HashMap<>();

    public abstract void complete(CommandSender sender, Consumer<S> consumer);

    public void setBuilder(CommandSender player, ObjectBuilder<?> builder) {
        data.put(player, (T) builder);
    }

    public T getBuilder(CommandSender player) {
        return data.getOrDefault(player, null);
    }

    public boolean isBuilding(CommandSender player) {
        return data.containsKey(player);
    }

}
