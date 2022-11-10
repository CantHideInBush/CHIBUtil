package com.canthideinbush.utils;

import org.bukkit.block.BlockFace;

public class AngleMath {

    //Credit to https://bukkit.org/members/bergerkiller.96957/

    public static final BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
    public static final BlockFace[] radial = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };

    /**
     * Gets the horizontal Block Face from a given yaw angle<br>
     * This includes the NORTH_WEST faces
     *
     * @param yaw angle
     * @return The Block Face of the angle
     */
    public static BlockFace yawToFace(float yaw) {
        return yawToFace(yaw, true);
    }

    /**
     * Gets the horizontal Block Face from a given yaw angle
     *
     * @param yaw angle
     * @param useSubCardinalDirections setting, True to allow NORTH_WEST to be returned
     * @return The Block Face of the angle
     */
    public static BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
        if (useSubCardinalDirections) {
            return radial[Math.round(yaw / 45f) & 0x7];
        } else {
            return axis[Math.round(yaw / 90f) & 0x3];
        }
    }

    //Credit end



    public static double roundTo360(double angle) {
        return angle > 360 ? angle - 360 : angle < 0 ? 360 - angle : angle;
    }

}
