/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mgubaidullin
 */
public class ProcessBaseClassLoader extends URLClassLoader {

    private static ProcessBaseClassLoader current = new ProcessBaseClassLoader();
    public static final Object LOCK = new Object();
    private boolean uiLoaded = false;

    public ProcessBaseClassLoader() {
        super(((URLClassLoader) ProcessBaseClassLoader.class.getClassLoader()).getURLs(), ProcessBaseClassLoader.class.getClassLoader());
    }

    public static ProcessBaseClassLoader getCurrent() {
        if (!current.uiLoaded) {
            current.loadUIClasses();
        }
        return current;
    }

    public static void free()  {
        synchronized (LOCK) {
            try {
                current.finalize();
                current = null;
            } catch (Throwable ex) {
                Logger.getLogger(ProcessBaseClassLoader.class.getName()).log(Level.SEVERE, ex.getMessage());
            }
        }
    }

    public static void reset() {
        synchronized (LOCK) {
            current = new ProcessBaseClassLoader();
            System.gc();
        }
    }

    public boolean isUiLoaded() {
        return uiLoaded;
    }

    public void loadUIClasses() {
        File folder = new File(Constants.UI_LIBS_PATH);
        File[] files = folder.listFiles();
        for (File file : files) {
            try {
                current.addFile(Constants.UI_LIBS_PATH + File.separator + file.getName());
            } catch (Exception ex) {
                Logger.getLogger(ProcessBaseClassLoader.class.getName()).log(Level.SEVERE, ex.getMessage());
            }
        }
//        current.uiLoaded = true;
    }

    public void addFile(String path) throws MalformedURLException {
        String urlPath = "jar:file:" + path + "!/";
        addURL(new URL(urlPath));
    }

    public void printPackages() {
        Package[] x = this.getPackages();
        for (int i = 0; i < x.length; i++) {
            Logger.getLogger("-----").log(Level.SEVERE, "Package = " + x[i].getName());
        }
    }

    public Package[] getP() {
        return this.getPackages();
    }
}
