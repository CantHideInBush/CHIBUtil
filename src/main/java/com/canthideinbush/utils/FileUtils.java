package com.canthideinbush.utils;

import java.io.File;

public class FileUtils {
    public static File withDefaultParent(File file, String path) {
        if (path.startsWith(File.separator) || path.contains(":")) {
            return new File(path);
        }
        return new File(file + File.separator + path);
    }
}
