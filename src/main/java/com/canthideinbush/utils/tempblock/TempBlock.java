package com.canthideinbush.utils.tempblock;

import com.canthideinbush.utils.managers.Keyed;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Stairs;

public class TempBlock implements Keyed<Block> {

    private final Block block;
    private final BlockState oldState;

    private final long revertStamp;

    protected TempBlock(Block block, BlockData newData, long time) {
        this.oldState = block.getState();
        block.setBlockData(newData);
        this.block = block;
        long timestamp = System.currentTimeMillis();
        this.revertStamp = timestamp + time;
        TempBlockManager.getInstance().register(this);
    }

    public static void setMaterial(Block block, Material material, long time) {
        TempBlockManager.getInstance().register(new TempBlock(block, Bukkit.createBlockData(material), time));
    }

    public static  void setData(Block block, BlockData data, long time) {
        TempBlockManager.getInstance().register(new TempBlock(block, data, time));
    }


    public void revert() {
        oldState.update(true, false);
    }

    public boolean shouldRevert() {
        return System.currentTimeMillis() >= revertStamp;
    }




    @Override
    public Block getKey() {
        return block;
    }
}
