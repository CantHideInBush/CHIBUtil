package com.canthideinbush.utils.worldedit;

import com.canthideinbush.utils.UtilsProvider;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.extent.clipboard.Clipboard;

public class SessionProgress {

    private final EditSession session;
    private final int totalBlocks;

    private boolean complete = false;

    private ReferenceTable reference = null;

    public SessionProgress(Clipboard clipboard, EditSession session) {
        this.session = session;
        this.totalBlocks = clipboard.getDimensions().getX()
                            * clipboard.getDimensions().getZ()
                            * clipboard.getDimensions().getZ();
    }

    public SessionProgress(Clipboard clipboard, EditSession session, ReferenceTable reference) {
        this.session = session;
        this.totalBlocks = clipboard.getDimensions().getX()
                * clipboard.getDimensions().getZ()
                * clipboard.getDimensions().getZ();
        this.reference = reference;
    }


    public double getProgress() {
        if (complete) return 1;
        return session.getBlockChangeCount() / (double) totalBlocks;
    }

    public double getPercentageProgress() {
        double progress = reference != null ? reference.getReferenceProgress(getProgress()) : getProgress();
        progress = Math.round(progress * 100 * 100) / 100.0;
        return progress;
    }

    public boolean isComplete() {
        return complete || getProgress() == 1;
    }

    public EditSession getSession() {
        return session;
    }

    public void complete() {
        this.complete = true;
    }
}
