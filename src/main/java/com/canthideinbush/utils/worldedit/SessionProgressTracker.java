package com.canthideinbush.utils.worldedit;

import com.canthideinbush.utils.UtilsProvider;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class SessionProgressTracker {
    private final SessionProgress progress;
    private Consumer<SessionProgress> consumer;
    private final UtilsProvider provider;

    public SessionProgress getProgress() {
        return progress;
    }



    public SessionProgressTracker(UtilsProvider provider, Clipboard clipboard, EditSession session, Consumer<SessionProgress> consumer, long period, boolean async) {
        this.provider = provider;
        this.progress = new SessionProgress(clipboard, session);
        this.consumer = consumer;
        runTask(async, period);
    }

    public SessionProgressTracker(UtilsProvider provider, Clipboard clipboard, EditSession session, Consumer<SessionProgress> consumer, long period, boolean async, ReferenceTable referenceTable) {
        this.provider = provider;
        this.progress = new SessionProgress(clipboard, session, referenceTable);
        this.consumer = consumer;
        runTask(async, period);
    }

    private ReferenceTracker referenceTracker = null;

    public void createReferenceTracker(Consumer<ReferenceTable> consumer) {
        referenceTracker = new ReferenceTracker(getProgress(), consumer);
        referenceTracker.start();
    }

    public ReferenceTracker getReferenceTracker() {
        return referenceTracker;
    }

    private BukkitTask task;

    public BukkitTask getTask() {
        return task;
    }

    private void runTask(boolean async, long period) {
        BukkitRunnable bRunnable = new BukkitRunnable() {
            public void run() {
                try {
                    consumer.accept(progress);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (progress.isComplete()) {
                    cancel();
                    if (referenceTracker != null) {
                        referenceTracker.stop();
                    }
                }
            }
        };
        task = async ? bRunnable.runTaskTimerAsynchronously(provider.getPlugin(), 1, period)
                : bRunnable.runTaskTimer(provider.getPlugin(), 1, period);
    }


    public Consumer<SessionProgress> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<SessionProgress> consumer) {
        this.consumer = consumer;
    }
}
