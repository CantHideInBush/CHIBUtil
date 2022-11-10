package com.canthideinbush.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class WorldEditUtils {

    private final UtilsProvider provider;

    public WorldEditUtils(UtilsProvider provider) {
        this.provider = provider;
    }


    public Clipboard findByName(String name) {
        File schemFile = new File(FileUtils.withDefaultParent(provider.getPlugin().getDataFolder(), provider.getPlugin().getConfig().getString("schematicsPath", "schematics")) + File.separator + name);
        System.out.println(schemFile);
        ClipboardFormat format = ClipboardFormats.findByFile(schemFile);
        try {
            if (format != null) {
                return format.getReader(new FileInputStream(schemFile)).read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void pasteAt(Location location, String name) {
        Clipboard clipboard = findByName(name);
        EditSession session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(location.getWorld()));
        Operation operation = new ClipboardHolder(clipboard).createPaste(session)
                .to(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
                .build();
        try {
            Operations.complete(operation);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
        session.close();
    }



}
