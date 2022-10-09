package com.canthideinbush.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Comparator;
import java.util.Set;

public class WorldGuardUtils {


    public static void getRegion(World world,String id) {
        WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).getRegion(id);
    }

    public static Set<ProtectedRegion> getRegionsInLocation(Location location) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(location.getWorld())).getApplicableRegions(BlockVector3.at(location.getX(), location.getY(), location.getZ())).getRegions();
    }

    public static ProtectedRegion getHighestPriorityRegion(Location location) {
        return getRegionsInLocation(location).stream().max(Comparator.comparingInt(ProtectedRegion::getPriority)).orElse(null);
    }

}