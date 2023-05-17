package com.canthideinbush.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public interface ObjectBuilder<T> {

    T build();

    List<String> options();

    List<String> complete(CommandSender player, String option);

    String errorFor(String option, String value);

    void with(String option, String value);

    List<String> missingOptions();

    boolean isComplete();

}
