package com.canthideinbush.utils.commands.builder;

import com.canthideinbush.utils.ObjectBuilder;
import com.canthideinbush.utils.commands.DefaultConfigMessage;
import com.canthideinbush.utils.commands.InternalCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class CompleteCommand extends InternalCommand {

    private final BuilderCommand<?, ?> parent;
    public CompleteCommand(BuilderCommand<?, ?> parent) {
        this.parent = parent;
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
    private final String NOT_COMPLETE = "Brakujace opcje: ";

    @DefaultConfigMessage(forN = "success")
    private final String SUCCESS = "Utworzono nowy generator!";

    @Override
    public String getName() {
        return "complete";
    }
}
