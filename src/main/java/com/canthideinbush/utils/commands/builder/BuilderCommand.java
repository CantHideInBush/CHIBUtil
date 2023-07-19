package com.canthideinbush.utils.commands.builder;

import com.canthideinbush.utils.ObjectBuilder;
import com.canthideinbush.utils.commands.InternalCommand;
import com.canthideinbush.utils.commands.ParentCommand;
import org.bukkit.command.CommandSender;

import java.util.*;

public abstract class BuilderCommand<S, T extends ObjectBuilder<S>> extends ParentCommand {

    protected final HashMap<CommandSender, T> data = new HashMap<>();
    protected final Set<CommandSender> editors = new HashSet<>();

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

        subCommands.add(new EditCommand<>(this) {
            @Override
            public Class<? extends InternalCommand> getParentCommandClass() {
                return instance.getClass();
            }
        });

    }


    public abstract HashMap<String, Class<? extends ObjectBuilder<?>>> builders();

    public abstract Class<? extends T> getBuilderFor(S object);

    public abstract S findById(String id);

    public abstract List<String> getIdCompletion();


    public void complete(CommandSender sender) {
        completeAction(sender, getBuilder(sender).build());
        data.remove(sender);
        editors.remove(sender);
    }

    protected void completeAction(CommandSender sender, S object) {
        completeAction(object);
    }

    protected void completeAction(S object) {

    }

    public boolean isEditing(CommandSender sender) {
        return editors.contains(sender);
    }

    public void addEditor(CommandSender sender) {
        editors.add(sender);
    }

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
