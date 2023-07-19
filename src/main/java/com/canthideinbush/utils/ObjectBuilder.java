package com.canthideinbush.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface ObjectBuilder<T> {

    T build();

    List<String> options();


    /**
     * @param option option
     * @param sender sender
     * @param value value
     */
    default List<String> complete(CommandSender sender, String option, String value) {
        return Collections.emptyList();
    }

    default List<String> complete(CommandSender sender, String option, String[] values) {
        return complete(sender, option, values[0]);
    }

    /**
     *
     * @param option option
     * @param value value
     */
    default String errorFor(String option, String value) {return null;};

    default String errorFor(String option, String[] values) {
        return errorFor(option, values[0]);
    }


    /**
     *
     * @param option option
     * @param value value
     */
    default void with(String option, String value) {}

    default void with(String option, String[] values) {
        with(option, values[0]);
    }

    /**
     *
     * @param values values
     * @param completion provided completion
     */
    default List<String> formatCompletion(List<String> completion, String option, String[] values) {
        String value;
        if (values.length == 0) value = "";
        else value = values[values.length - 1];
        if (!options().contains(option.toLowerCase())) return completion;
        else if (value != null && !value.equals("")) {
            return completion.stream().filter(s -> s.toLowerCase().startsWith(value.toLowerCase())).collect(Collectors.toList());
        }
        return completion;
    }

    List<String> missingOptions();

    boolean isComplete();

}
