package com.canthideinbush.utils;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.Clipboard;

import java.io.File;

public class WorldEditUtils {



    public static Clipboard findByName(String name) {
        File schemFile = new File(FileUtils.withDefaultParent(CHIBUtils.getInstance().getDataFolder(), CHIBUtils.getInstance().getConfig().getString("schematicsPath")) + File.separator + name);

    }



}
