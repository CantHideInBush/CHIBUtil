package com.canthideinbush.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import net.minecraft.world.level.block.Block;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.*;
import java.util.logging.Level;

public class WorldEditUtils {

    private final UtilsProvider provider;


    WorldEditPlugin worldEdit;
    public WorldEditUtils(UtilsProvider provider) {
        this.provider = provider;
        worldEdit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
    }




    public Clipboard findByName(String name) {
        return findByName(name, true);
    }


    public Clipboard findByName(String name, boolean useWorldEdit) {
        File schemFile;
        if (useWorldEdit) {
            schemFile = new File(FileUtils.withDefaultParent(worldEdit.getDataFolder(), WorldEdit.getInstance().getConfiguration().saveDir) + File.separator + name);
        }
        else schemFile = new File(name);


        ClipboardFormat format = ClipboardFormats.findByFile(schemFile);
        try {
            if (format != null) {
                return format.getReader(new FileInputStream(schemFile)).read();
            }
        } catch (IOException e) {
            provider.getPlugin().getLogger().log(Level.WARNING, "Schematic file " + name + " not found!");
        }
        return null;
    }


    public void pasteAt(Location location, String name) {
        pasteAt(location, name, true);
    }

    public void pasteAt(Location location, String name, boolean useWorldEdit) {
        Clipboard clipboard = findByName(name, useWorldEdit);
        pasteAt(location, clipboard);
    }

    public void pasteAt(Location location, Clipboard clipboard) {
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


    public void inversePasteAt(Location location, String name) {
        Clipboard clipboard = findByName(name);


        BlockVector3 origin = clipboard.getMinimumPoint();
        BlockVector3 dimensions = clipboard.getDimensions();
        for (int x = origin.getX(); x < origin.getX() + dimensions.getX(); x++) {
            for (int y = origin.getY(); y < origin.getY() + dimensions.getY(); y++) {
                for (int z = origin.getZ(); z < origin.getZ() + dimensions.getZ(); z++) {
                    BlockVector3 pos = BlockVector3.at(x, y, z);
                    if (!clipboard.getBlock(pos).getBlockType().equals(BlockTypes.AIR)) {
                        try {
                            assert BlockTypes.AIR != null;
                            clipboard.setBlock(pos, BlockTypes.AIR.getDefaultState());
                        } catch (WorldEditException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }


        EditSession session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(location.getWorld()));
        Operation operation = new ClipboardHolder(clipboard).createPaste(session)
                .ignoreAirBlocks(false)
                .to(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
                .build();
        try {
            Operations.complete(operation);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
        session.close();
    }

    public Clipboard swapAt(Location location, String name) {
        Clipboard clipboard = findByName(name);

        BlockVector3 offset = clipboard.getRegion().getMinimumPoint().subtract(clipboard.getOrigin());

        BlockVector3 vectorLocation = BukkitAdapter.adapt(location).toVector().toBlockPoint();

        BlockVector3 newMin = vectorLocation.add(offset);



        CuboidRegion region = new CuboidRegion(
                newMin,
                BlockVector3.at(newMin.getBlockX() - 1, newMin.getBlockY() - 1, newMin.getBlockZ() - 1).add(clipboard.getDimensions()));
        BlockArrayClipboard swapped = new BlockArrayClipboard(region);
        swapped.setOrigin(vectorLocation);
        ForwardExtentCopy copy = new ForwardExtentCopy(BukkitAdapter.adapt(location.getWorld()), region, swapped, newMin);

        try {
            Operations.complete(copy);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }

        pasteAt(location, clipboard);


        return swapped;
    }


    public void saveClipboard(Clipboard clipboard, String name) {
        saveClipboard(clipboard, name, true);
    }

    public void saveClipboard(Clipboard clipboard, String name, boolean useWorldEdit) {
        File file;
        if (useWorldEdit) {
            file = FileUtils.withDefaultParent(worldEdit.getDataFolder(), WorldEdit.getInstance().getConfiguration().saveDir + File.separator + name);
        }
        else file = new File(name);
        try {
            file.createNewFile();
            ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file));
            writer.write(clipboard);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }




}
