package com.canthideinbush.utils;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class Reflector {

    public static boolean containsField(String field, Class<?> c) {
        try {
            c.getDeclaredField(field);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    public static <T> Object getValue(String field, T object) {
        Field f;
        try {
            f = object.getClass().getDeclaredField(field);
            f.setAccessible(true);
            return f.get(object);
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

    public static <T> Class<T> getCollectionType(Collection<T> c) {
        ParameterizedType parameterizedType = (ParameterizedType) c.getClass().getGenericSuperclass();

        return (Class<T>) ((TypeVariable)parameterizedType.getActualTypeArguments()[0]).getBounds()[0];
    }
}


