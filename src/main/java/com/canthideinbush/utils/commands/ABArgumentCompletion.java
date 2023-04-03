package com.canthideinbush.utils.commands;

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





    default List<HashMap<String, Supplier<List<String>>>> prepareCompletion() {
        List<HashMap<String, Supplier<List<String>>>> completion = new ArrayList<>();
        HashMap<String, Supplier<List<String>>> map;
        ABCompleter completer;
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(ABCompleter.class)) {
                completer = method.getAnnotation(ABCompleter.class);
                if (completion.size() <= completer.index()) {
                    map = new HashMap<>();
                    completion.add(map);
                } else map = completion.get(completer.index());
                Object inst = this;
                map.put(completer.arg(), new Supplier<>() {
                    @Override
                    public List<String> get() {
                        method.setAccessible(true);
                        try {
                            return (List<String>) method.invoke(inst);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(ABCompleter.class)) {
                completer = field.getAnnotation(ABCompleter.class);
                if (completion.size() <= completer.index()) {
                    map = new HashMap<>();
                    completion.add(map);
                } else map = completion.get(completer.index());
                field.setAccessible(true);
                map.put(completer.arg(), new Supplier<>() {
                    @Override
                    public List<String> get() {
                        try {
                            return Collections.singletonList(field.get(this).toString());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }
        return completion;
    }

    List<HashMap<String, Supplier<List<String>>>> getCompletion();


    int getArgIndex();

    default List<String> ABComplete(String[] args) {
        int index = args.length - getArgIndex() - 1;
        if (index < 0 || getCompletion().size() <= index) return Collections.emptyList();
        HashMap<String, Supplier<List<String>>> map = getCompletion().get(index);
        if (args.length - 2 < 0 || !map.containsKey(args[args.length - 2])) return  Collections.emptyList();
        Supplier<List<String>> method = map.get(args[args.length - 2]);
        return method.get();
    }






}
