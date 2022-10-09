package com.canthideinbush.utils.chat;

import com.canthideinbush.utils.CHIBPlugin;
import com.canthideinbush.utils.CHIBUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class ChatUtils {

    private final String prefix;
    private final YamlConfiguration messageConfig;

    public ChatUtils(CHIBPlugin plugin) {
        this.prefix = plugin.getPrefix();
        this.messageConfig = plugin.getMessageConfig();
    }

    public String createMessage(String text, ChatColor color) {
        return prefix + color + " " + text;
    }

    public void sendMessage(CommandSender sender, String text, ChatColor color) {
        sender.sendMessage(createMessage(text, color));
    }

    public String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', messageConfig.getString(path, getDefaultMessage(path)));
    }

    private String getDefaultMessage(String path) {
        return CHIBUtils.getInstance().getMessageConfig().getString(path, "Undefined config message (%s)".formatted(path));
    }
}
