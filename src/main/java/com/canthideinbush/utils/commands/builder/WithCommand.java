package com.canthideinbush.utils.commands.builder;

import com.canthideinbush.utils.ObjectBuilder;
import com.canthideinbush.utils.commands.DefaultConfigMessage;
import com.canthideinbush.utils.commands.InternalCommand;
import com.canthideinbush.utils.storing.ArgParser;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public abstract class WithCommand extends InternalCommand {

    private final BuilderCommand<?, ?> parent;
    public WithCommand(BuilderCommand<?, ?> parent) {
        this.parent = parent;
    }

    @Override
    public boolean execute(Player sender, String[] args) {


        if (!parent.isBuilding(sender)) {
            sendConfigErrorMessage(sender, getMessagePath("not-building"));
            return false;
        }

        ArgParser parser = new ArgParser(args, getArgIndex());
        if (!parser.hasNext()) {
            sendConfigErrorMessage(sender, "command-arguments-insufficient");
            return false;
        }

        ObjectBuilder<?> builder = parent.getBuilder(sender);
        String option = parser.next();

        if (!builder.options().contains(option.toLowerCase())) {
            sendConfigErrorMessage(sender, getMessagePath("option-not-found"));
            return false;
        }

        if (!parser.hasNext()) {
            sendConfigErrorMessage(sender, "command-arguments-insufficient");
            return false;
        }

        String value = parser.next();
        String error;
        if ((error = builder.errorFor(option, value)) != null) {
            getPlugin().getUtilsProvider().getChatUtils().sendMessage(sender,
                    error,
                    ChatColor.RED
            );
            return false;
        }

        builder.with(option, value);



        sendConfigSuccessMessage(sender, getMessagePath("success"));


        return true;
    }

    @Override
    public List<String> complete(String[] args, CommandSender sender) {

        if (parent.isBuilding(sender)) {
            ObjectBuilder<?> builder = parent.getBuilder(sender);
            if (args.length == getArgIndex() + 1) {
                return builder.options();
            }
            else if (args.length == getArgIndex() + 2) {
                String option = args[getArgIndex()];
                return builder.complete(sender, option);
            }
        }


        return Collections.emptyList();
    }

    @DefaultConfigMessage(forN = "not-building")
    private static final String NOT_BUILDING = "Nie jestes w trakcie tworzenia generatora!";

    @DefaultConfigMessage(forN = "option-not-found")
    private static final String OPTION_NOT_FOUND = "Wprowadzono niepoprawna opcje!";

    @DefaultConfigMessage(forN = "success")
    private static final String SUCCESS = "Pomyslnie dodano wartosc!";

    @Override
    public String getName() {
        return "with";
    }




}
