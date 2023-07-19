package com.canthideinbush.utils.commands.builder;

import com.canthideinbush.utils.DefMessages;
import com.canthideinbush.utils.ObjectBuilder;
import com.canthideinbush.utils.Reflector;
import com.canthideinbush.utils.commands.*;
import com.canthideinbush.utils.storing.ArgParser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public abstract class EditCommand<S> extends InternalCommand implements ABArgumentCompletion {

    private final BuilderCommand<S, ?> parent;
    protected List<TabCompleter> completion = prepareCompletion();

    public EditCommand(BuilderCommand<S, ?> parent) {
        this.parent = parent;
    }

    @Override
    protected List<String> getAliases() {
        return Collections.singletonList("e");
    }

    @Override
    public boolean execute(Player sender, String[] args) {
        ArgParser parser = new ArgParser(args, getArgIndex());

        if (!parser.hasNext()) {
            sendConfigErrorMessage(sender, DefMessages.ARGS_INSUFFICIENT);
            return false;
        }

        S obj = parent.findById(parser.next());

        if (obj == null) {
            sendConfigErrorMessage(sender, getMessagePath("object-not-found"));
            return false;
        }

        Class<? extends ObjectBuilder<?>> builder = parent.getBuilderFor(obj);

        if (builder == null) {
            sendConfigErrorMessage(sender, getMessagePath("builder-not-found"));
            return false;
        }

        parent.setBuilder(sender, Reflector.newInstance(builder, new Class[]{obj.getClass()}, obj));
        parent.addEditor(sender);

        sendConfigSuccessMessage(sender, getMessagePath("success"));

        return true;
    }

    @ABCompleter(index = 0)
    protected List<String> completeObjects() {
        return parent.getIdCompletion();
    }

    @DefaultConfigMessage(forN = "success")
    private static final String SUCCESS = "Rozpoczeto edycje!";

    @DefaultConfigMessage(forN = "object-not-found")
    private static final String OBJECT_NOT_FOUND = "Nie odnaleziono obiektu o podanym id!";

    @DefaultConfigMessage(forN = "builder-not-found")
    private static final String BUILDER_NOT_FOUND = "Nie odnaleziono generatora obiektu!";

    @Override
    public String getName() {
        return "edit";
    }


    @Override
    public List<String> complete(String[] args, CommandSender sender) {
        return ABComplete(args, sender);
    }

    @Override
    public List<TabCompleter> getCompletion() {
        return completion;
    }
}
