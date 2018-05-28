package com.common.tool.solr.util;

import java.io.File;

import lombok.extern.slf4j.Slf4j;

/**
 * 删除文件夹
 *
 * @author Administrator
 */
@Slf4j
public class DeleteDirUtil {

    /**
     * @param dir
     * @return
     */
    public static boolean process(File dir) {
        try {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    boolean success = process(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
            return dir.delete();
        } catch (Exception e) {
            log.error("delete file fail");
            e.printStackTrace();
            return false;
        }
    }

}
