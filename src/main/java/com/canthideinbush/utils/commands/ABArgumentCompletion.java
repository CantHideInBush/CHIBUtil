package com.canthideinbush.utils.commands;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public interface ABArgumentCompletion {






    default List<List<String>> prepareCompletion() {
        List<List<String>> completion = new ArrayList<>();

        for (Annotation annotation : this.getClass().getDeclaredAnnotations())

        return completion;
    }






}
