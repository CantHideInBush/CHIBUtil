package com.canthideinbush.utils.commands;

import org.bukkit.command.CommandSender;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ABArgumentCompletion {





    default List<TabCompleter> prepareCompletion() {
        List<TabCompleter> completion = new ArrayList<>();
        ABCompleter completer;
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(ABCompleter.class)) {
                completer = method.getAnnotation(ABCompleter.class);

                Object inst = this;
                completion.add(new TabCompleter(completer.index(), completer.arg(), () -> {
                    method.setAccessible(true);
                    try {
                        return (List<String>) method.invoke(inst);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }, completer.permission()));
            }
        }
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(ABCompleter.class)) {
                completer = field.getAnnotation(ABCompleter.class);
                field.setAccessible(true);
                completion.add(new TabCompleter(completer.index(), completer.arg(), new Supplier<>() {
                    @Override
                    public List<String> get() {
                        try {
                            return Collections.singletonList(field.get(this).toString());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, completer.permission()));
            }
        }
        return completion;
    }

    List<TabCompleter> getCompletion();

    default TabCompleter findCompleter(CommandSender sender, int index, String arg) {
        return getCompletion().stream().filter(tabCompleter ->
                sender.hasPermission(tabCompleter.getPermission()) && tabCompleter.getIndex() == index && tabCompleter.getArg().equalsIgnoreCase(arg)).findAny().orElse(null);
    }


    int getArgIndex();

    default List<String> ABComplete(String[] args, CommandSender sender) {
        int index = args.length - getArgIndex() - 1;
        if (index < 0 || getCompletion().size() <= index) return Collections.emptyList();
        TabCompleter completer = findCompleter(sender, index, args[args.length - 2]);
        if (completer == null) return  Collections.emptyList();
        return completer.getSupplier().get();
    }






}
