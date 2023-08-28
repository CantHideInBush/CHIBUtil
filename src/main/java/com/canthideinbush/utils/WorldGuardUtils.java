package com.canthideinbush.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Comparator;
import java.util.Set;

public class WorldGuardUtils {


    public static ProtectedRegion getRegion(World world,String id) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).getRegion(id);
    }

    public static Set<ProtectedRegion> getRegionsInLocation(Location location) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(location.getWorld())).getApplicableRegions(BlockVector3.at(location.getX(), location.getY(), location.getZ())).getRegions();
    }

    public static ProtectedRegion getHighestPriorityRegion(Location location) {
        return getRegionsInLocation(location).stream().max(Comparator.comparingInt(ProtectedRegion::getPriority)).orElse(null);
    }

    public static void redefine(World world, ProtectedCuboidRegion region, CuboidRegion newArea) {
        RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
        ProtectedCuboidRegion newRegion = new ProtectedCuboidRegion(region.getId(), newArea.getMinimumPoint(), newArea.getMaximumPoint());
        newRegion.copyFrom(region);
        assert manager != null;
        manager.addRegion(newRegion);
    }



    public static void expand(World world, ProtectedCuboidRegion region, BlockVector3... vector3) {
        CuboidRegion newRegion = new CuboidRegion(region.getMinimumPoint(), region.getMaximumPoint());
        newRegion.expand(vector3);
        redefine(world, region, newRegion);
    }

    public static void contract(World world, ProtectedCuboidRegion region, BlockVector3... vector3) {
        CuboidRegion newRegion = new CuboidRegion(region.getMinimumPoint(), region.getMaximumPoint());
        newRegion.contract(vector3);
        redefine(world, region, newRegion);
    }


}