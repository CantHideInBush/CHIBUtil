package com.canthideinbush.utils;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Reflector {

    public static <T> T getValue(String field, Class<T> type, T object) {
        Field f;
        try {
            f = object.getClass().getDeclaredField(field);
            f.setAccessible(true);
            return (T) f.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }




    public static <T> T newInstance(Class<T> t, Class<?>[] types, Object... args) {
        try {
            Constructor<T> constructor = t.getDeclaredConstructor(types);
            constructor.setAccessible(true);
            return (T) constructor.newInstance(args);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) null;
    }
}


