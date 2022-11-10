package com.canthideinbush.utils.tempblock;

import com.canthideinbush.utils.CHIBUtils;
import com.canthideinbush.utils.managers.KeyedStorage;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class TempBlockManager implements KeyedStorage<TempBlock> {

    private final ArrayList<TempBlock> tempBlocks = new ArrayList<>();
    private final ArrayList<TempBlock> toAdd = new ArrayList<>();
    private final ArrayList<TempBlock> toRemove = new ArrayList<>();

    private static TempBlockManager instance;

    public TempBlockManager() {
        instance = this;

        new BukkitRunnable() {
            @Override
            public void run() {

                Iterator<TempBlock> tempBlockIterator = tempBlocks.iterator();
                TempBlock tempBlock;
                while (tempBlockIterator.hasNext()) {
                    tempBlock = tempBlockIterator.next();
                    if (tempBlock.shouldRevert() || toRemove.contains(tempBlock)) {
                        tempBlock.revert();
                        tempBlockIterator.remove();
                    }
                }
                toRemove.clear();
                tempBlocks.addAll(toAdd);
                toAdd.clear();
            }
        }.runTaskTimer(CHIBUtils.getInstance(), 0, 1);
    }

    @Override
    public void register(TempBlock tempBlock) {
        this.toAdd.add(tempBlock);
        if (findByKey(tempBlock.getKey()) != null) {
            unregister(findByKey(tempBlock.getKey()));
        }
    }

    @Override
    public void unregister(TempBlock tempBlock) {
        this.toRemove.add(tempBlock);
    }

    @Override
    public Collection<TempBlock> getObjects() {
        return tempBlocks;
    }

    public static TempBlockManager getInstance() {
        return instance;
    }

    public void revertAll() {
        tempBlocks.forEach(TempBlock::revert);
    }
}
