package com.canthideinbush.utils.commands;

import java.util.List;
import java.util.function.Supplier;

public class TabCompleter {

    private final String arg;
    private final int index;
    private final Supplier<List<String>> supplier;
    private final String permission;

    public TabCompleter(int index, String arg, Supplier<List<String>> supplier) {
        this.arg = arg;
        this.index = index;
        this.supplier = supplier;
        this.permission = null;
    }
    public TabCompleter(int index, String arg, Supplier<List<String>> supplier, String permission) {
        this.arg = arg;
        this.index = index;
        this.supplier = supplier;
        this.permission = permission;
    }

    public String getArg() {
        return arg;
    }

    public int getIndex() {
        return index;
    }

    public String getPermission() {
        return permission;
    }

    public Supplier<List<String>> getSupplier() {
        return supplier;
    }
}
