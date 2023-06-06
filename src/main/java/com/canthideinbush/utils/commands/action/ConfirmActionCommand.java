package com.canthideinbush.utils.commands.action;

import com.canthideinbush.utils.commands.DefaultConfigMessage;
import com.canthideinbush.utils.commands.InternalCommand;
import com.canthideinbush.utils.commands.ParentCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.function.Consumer;

public abstract class ConfirmActionCommand extends InternalCommand implements Listener {


    public ConfirmActionCommand() {
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        getActionMap().remove(event.getPlayer());
        getTimeMap().remove(event.getPlayer());
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Consumer<CommandSender> consumer;
        if ((consumer = getActionMap().get(sender)) == null) {
            sendConfigErrorMessage(sender, getMessagePath("no-action"));
            return false;
        }
        if (hasTimedOut(sender)) {
            sendConfigErrorMessage(sender, getMessagePath("timed-out"));
            getActionMap().remove(sender);
            getTimeMap().remove(sender);
            return false;
        }
        consumer.accept(sender);
        getActionMap().remove(sender);
        getTimeMap().remove(sender);
        return true;
    }

    @DefaultConfigMessage(forN = "no-action")
    private static final String a = "Nie mozesz wykonac zadnej akcji!";
    @DefaultConfigMessage(forN = "timed-out")
    private static final String b = "Ta akcja jest przedawniona!";


    @Override
    public String getName() {
        return "confirm";
    }



    abstract protected HashMap<CommandSender, Consumer<CommandSender>> getActionMap();
    abstract protected HashMap<CommandSender, Long> getTimeMap();


    public void setAction(CommandSender sender, Consumer<CommandSender> consumer) {
        getActionMap().put(sender, consumer);
        getTimeMap().put(sender, System.currentTimeMillis());
    }

    public boolean hasTimedOut(CommandSender sender) {
        return System.currentTimeMillis() - getTimeMap().get(sender) >= getTimeout();
    }

    protected long getTimeout() {
        return 1000 * 30;
    }

}
