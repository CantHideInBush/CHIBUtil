package com.canthideinbush.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public interface ObjectBuilder<T> {

    T build();

    List<String> options();


    /**
     *
     * @param option option
     * @param sender sender
     */
    List<String> complete(CommandSender sender, String option);

    /**
     *
     * @param value value
     * @param sender sender
     */
    default List<String> completeValue(CommandSender sender, String value) {
        return Collections.emptyList();
    }



    /**
     *
     * @param option option
     * @param value value
     */
    String errorFor(String option, String value);


    /**
     *
     * @param option option
     * @param value value
     */
    void with(String option, String value);

    List<String> missingOptions();

    boolean isComplete();

}
