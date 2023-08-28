package com.canthideinbush.utils.worldedit;

import com.canthideinbush.utils.CHIBUtils;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedHashMap;
import java.util.function.Consumer;

public class ReferenceTracker {


    private long startTime;
    private final SessionProgress progress;

    private final Consumer<ReferenceTable> consumer;

    private final LinkedHashMap<Double, Long> progressOverTime = new LinkedHashMap<>();
    public ReferenceTracker(SessionProgress progress, Consumer<ReferenceTable> consumer) {
        this.progress = progress;
        this.consumer = consumer;
    }

    private BukkitTask task;
    public void start() {
        startTime = System.currentTimeMillis();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                progressOverTime.put(progress.getProgress(), System.currentTimeMillis());
            }
        }.runTaskTimerAsynchronously(CHIBUtils.getInstance(), 0, 20);
    }

    public void stop() {
        task.cancel();
        ReferenceTable referenceTable = new ReferenceTable(progressOverTime, startTime,System.currentTimeMillis() - startTime);
        consumer.accept(referenceTable);
    }


}
