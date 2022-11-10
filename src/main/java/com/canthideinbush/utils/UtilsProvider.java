package com.canthideinbush.utils;

import com.canthideinbush.utils.chat.ChatUtils;

public class UtilsProvider {

    private final CHIBPlugin plugin;


    public UtilsProvider(CHIBPlugin plugin) {
        this.plugin = plugin;
        this.chat = new ChatUtils(plugin);
        this.worldEdit = new WorldEditUtils(this);
    }



    public ChatUtils chat;
    public final WorldEditUtils worldEdit;

    public void setChatUtils(ChatUtils chatUtils) {
        this.chat = chatUtils;
    }

    public ChatUtils getChatUtils() {
        return chat;
    }

    public CHIBPlugin getPlugin() {
        return plugin;
    }
}
