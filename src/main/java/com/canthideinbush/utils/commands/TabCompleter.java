package com.canthideinbush.utils.commands;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class TabCompleter {

    private final String arg;
    private final int index;
    private final Function<String[], List<String>> supplier;
    private final String permission;

    private final String localPermission;

    public TabCompleter(int index, String arg, Function<String[], List<String>> supplier) {
        this.arg = arg;
        this.index = index;
        this.supplier = supplier;
        this.permission = null;
        this.localPermission = null;
    }
    public TabCompleter(int index, String arg, Function<String[], List<String>> supplier, String permission) {
        this.arg = arg;
        this.index = index;
        this.supplier = supplier;
        this.permission = permission;
        this.localPermission = null;
    }

    public TabCompleter(int index, String arg, Function<String[], List<String>> supplier, String permission, String localPermission) {
        this.arg = arg;
        this.index = index;
        this.supplier = supplier;
        this.permission = permission;
        this.localPermission = localPermission;
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

    public Function<String[], List<String>> getSupplier() {
        return supplier;
    }

    public String getLocalPermission() {
        return localPermission;
    }
}
