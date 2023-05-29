package com.canthideinbush.utils.commands.builder;

import com.canthideinbush.utils.ObjectBuilder;
import com.canthideinbush.utils.commands.InternalCommand;
import com.canthideinbush.utils.commands.ParentCommand;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.function.Consumer;

public abstract class BuilderCommand<S, T extends ObjectBuilder<S>> extends ParentCommand {

    private final HashMap<CommandSender, T> data = new HashMap<>();

    public BuilderCommand() {

    }

    public void defaultConstructor() {
        BuilderCommand<S, T> instance = this;
        subCommands.add(new StartCommand(this) {
            @Override
            public Class<? extends InternalCommand> getParentCommandClass() {
                return instance.getClass();
            }
        });
        subCommands.add(new WithCommand(this) {
            @Override
            public Class<? extends InternalCommand> getParentCommandClass() {
                return instance.getClass();
            }
        });

        subCommands.add(new CompleteCommand(this) {

            @Override
            public Class<? extends InternalCommand> getParentCommandClass() {
                return instance.getClass();
            }
        });



    }


    public abstract HashMap<String, Class<? extends ObjectBuilder<?>>> builders();


    public void complete(CommandSender sender) {
        completeAction(getBuilder(sender).build());
        data.remove(sender);

    }

    protected abstract void completeAction(S object);

    public void setBuilder(CommandSender player, ObjectBuilder<?> builder) {
        data.put(player, (T) builder);
    }

    public T getBuilder(CommandSender player) {
        return data.getOrDefault(player, null);
    }

    public boolean isBuilding(CommandSender player) {
        return data.containsKey(player);
    }

    @Override
    public String getName() {
        return "build";
    }
}
