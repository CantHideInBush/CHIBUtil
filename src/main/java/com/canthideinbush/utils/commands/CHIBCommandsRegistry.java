package com.canthideinbush.utils.commands;


import com.canthideinbush.utils.managers.KeyedStorage;

import java.util.ArrayList;
import java.util.Collection;

public class CHIBCommandsRegistry implements KeyedStorage<InternalCommand> {

    public static final CHIBCommandsRegistry instance = new CHIBCommandsRegistry();

    private static final ArrayList<InternalCommand> commands = new ArrayList<>();

    public static InternalCommand get(Class<? extends InternalCommand> key) {
        return instance.findByKey(key);
    }

    public static InternalCommand getParentCommand(String name) {
        return commands.stream().filter(c -> c.getName().equals(name)).findAny().orElse(null);
    }

    @Override
    public Collection<InternalCommand> getObjects() {
        return commands;
    }
}
