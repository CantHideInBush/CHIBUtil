package com.canthideinbush.utils.commands;


import com.canthideinbush.utils.managers.KeyedStorage;

import java.util.ArrayList;
import java.util.Collection;

public class CHIBCommandsRegistry implements KeyedStorage<InternalCommand> {

    public static final CHIBCommandsRegistry instance = new CHIBCommandsRegistry();

    private final ArrayList<InternalCommand> commands = new ArrayList<>();

    public static InternalCommand get(Class<? extends InternalCommand> key) {
        return instance.findByKey(key);
    }

    @Override
    public Collection<InternalCommand> getObjects() {
        return commands;
    }
}
