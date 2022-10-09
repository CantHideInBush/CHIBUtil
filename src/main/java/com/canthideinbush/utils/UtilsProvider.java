package com.canthideinbush.utils;

import com.canthideinbush.utils.chat.ChatUtils;

public class UtilsProvider {

    public ChatUtils chat;


    public void setChatUtils(ChatUtils chatUtils) {
        this.chat = chatUtils;
    }

    public ChatUtils getChatUtils() {
        return chat;
    }

}
