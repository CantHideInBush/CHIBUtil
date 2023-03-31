package com.canthideinbush.utils.commands;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public interface ABArgumentCompletion {





    default List<HashMap<String, Method>> prepareCompletion() {
        List<HashMap<String, Method>> completion = new ArrayList<>();

        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(ABCompleter.class)) {
                ABCompleter completer = method.getAnnotation(ABCompleter.class);
                HashMap<String, Method> map;
                if (completion.size() <= completer.index()) {
                    map = new HashMap<>();
                    completion.add(map);
                } else map = completion.get(completer.index());
                map.put(completer.arg(), method);
            }

        }
        return completion;
    }

    List<HashMap<String, Method>> getCompletion();


    int getArgIndex();

    default List<String> ABComplete(String[] args) {
        int index = args.length - getArgIndex() + 1;
        if (index < 0 || getCompletion().size() <= index) return Collections.emptyList();
        HashMap<String, Method> map = getCompletion().get(index);
        if (!map.containsKey(args[args.length - 2])) return  Collections.emptyList();
        try {
            return (List<String>) map.get(args[args.length - 2]).invoke(this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }






}
