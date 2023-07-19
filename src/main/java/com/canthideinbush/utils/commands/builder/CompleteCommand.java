package com.canthideinbush.utils.commands.builder;

import com.canthideinbush.utils.ObjectBuilder;
import com.canthideinbush.utils.commands.DefaultConfigMessage;
import com.canthideinbush.utils.commands.InternalCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public abstract class CompleteCommand extends InternalCommand {

    private final BuilderCommand<?, ?> parent;
    public CompleteCommand(BuilderCommand<?, ?> parent) {
        this.parent = parent;
        saveDefaultConfigMessages();
    }

    @Override
    protected List<String> getAliases() {
        return Collections.singletonList("c");
    }

    @Override
    public boolean execute(Player sender, String[] args) {
        if (!parent.isBuilding(sender)) {
            sendConfigErrorMessage(sender, parent.getSubCommand("with").
                    getMessagePath("not-building"));
            return false;
        }

        ObjectBuilder<?> builder = parent.getBuilder(sender);
        if (!builder.isComplete()) {
            StringBuilder stringBuilder = new StringBuilder(
                    getPlugin().getUtilsProvider().getChatUtils()
                            .getMessage(getMessagePath("not-complete"))
            );
            stringBuilder.append(ChatColor.GOLD);
            for (String missing : builder.missingOptions()) {
                stringBuilder.append("\n- ").append(missing);
            }
            getPlugin().getUtilsProvider().getChatUtils().sendMessage(sender, stringBuilder.toString(), ChatColor.RED);
            return false;
        }

        parent.complete(sender);
        sendConfigSuccessMessage(sender, getMessagePath("success"));

        return true;
    }

    @DefaultConfigMessage(forN = "not-complete")
    private static final String NOT_COMPLETE = "Brakujace opcje: ";

    @DefaultConfigMessage(forN = "success")
    private static final String SUCCESS = "Utworzono nowy obiekt!";



    @Override
    public String getName() {
        return "complete";
    }
}
