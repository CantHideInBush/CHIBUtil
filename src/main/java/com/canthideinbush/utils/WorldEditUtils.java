package com.canthideinbush.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;

public class WorldEditUtils {

    private final UtilsProvider provider;

    public WorldEditUtils(UtilsProvider provider) {
        this.provider = provider;
    }




    public Clipboard findByName(String name) {
        File schemFile = new File(FileUtils.withDefaultParent(Bukkit.getPluginManager().getPlugin("WorldEdit").getDataFolder(), WorldEdit.getInstance().getConfiguration().saveDir) + File.separator + name);
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
        Clipboard clipboard = findByName(name);
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
        this.provider.getPlugin().getLogger().log(Level.INFO, offset.toString());
        this.provider.getPlugin().getLogger().log(Level.INFO, clipboard.getDimensions().toString());

        BlockVector3 newMin = BukkitAdapter.adapt(location).toVector().toBlockPoint().add(offset);

        CuboidRegion region = new CuboidRegion(
                newMin,
                BlockVector3.at(newMin.getBlockX() - 1, newMin.getBlockY() - 1, newMin.getBlockZ() - 1).add(clipboard.getDimensions()));
        BlockArrayClipboard swapped = new BlockArrayClipboard(region);
        this.provider.getPlugin().getLogger().log(Level.INFO, swapped.getOrigin().toString());
        this.provider.getPlugin().getLogger().log(Level.INFO, swapped.getRegion().getMaximumPoint().toString());

        pasteAt(location, clipboard);



        return swapped;
    }




}
