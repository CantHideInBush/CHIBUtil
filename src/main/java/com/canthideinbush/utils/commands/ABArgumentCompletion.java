package com.canthideinbush.utils.commands;

import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ABArgumentCompletion {





    default List<TabCompleter> prepareCompletion() {
        List<TabCompleter> completion = new ArrayList<>();
        ABCompleter completer;
        Object inst = this;

        List<Class<?>> classes = new ArrayList<>();
        Class<?> c = this.getClass();
        while (!c.equals(Object.class)) {
            classes.add(c);
            c = c.getSuperclass();
        }
        for (Class<?> clazz : classes) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(ABCompleter.class)) {
                    completer = method.getAnnotation(ABCompleter.class);


                    completion.add(new TabCompleter(completer.index(), completer.arg(), (args) -> {
                        method.setAccessible(true);
                        try {
                            if (method.getParameterTypes().length > 0) {
                                return (List<String>) method.invoke(inst, (Object) args);
                            }
                            else {
                                return (List<String>) method.invoke(inst);
                            }
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
                    completion.add(new TabCompleter(completer.index(), completer.arg(), args -> {
                        try {
                            return Collections.singletonList(field.get(inst).toString());
                        } catch (
                                IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }, completer.permission(), completer.localPermission()));
                }
            }
        }


        return completion;
    }

    List<TabCompleter> getCompletion();

    default TabCompleter findCompleter(CommandSender sender, int index, String arg) {
        return getCompletion().stream().filter(tabCompleter ->
                ((Objects.equals(tabCompleter.getPermission(), "") || sender.hasPermission(tabCompleter.getPermission()))
                        && Objects.equals(tabCompleter.getLocalPermission(), "") || sender.hasPermission(getAbsolutePermission() + tabCompleter.getLocalPermission())
                )



                        && tabCompleter.getIndex() == index && (tabCompleter.getArg().equalsIgnoreCase("") || tabCompleter.getArg().equalsIgnoreCase(arg))).findAny().orElse(null);
    }


    int getArgIndex();

    String getAbsolutePermission();

    default List<String> ABComplete(String[] args, CommandSender sender) {
        int index = args.length - getArgIndex() - 1;
        if (index < 0 || getCompletion().size() <= index) return Collections.emptyList();
        TabCompleter completer = findCompleter(sender, index, args[args.length - 2]);
        if (completer == null) return  Collections.emptyList();
        return completer.getSupplier().apply(args);
    }






}
