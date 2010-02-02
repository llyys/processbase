/**
 * Copyright (C) 2010 PROCESSBASE Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

    public static void free() {
        synchronized (LOCK) {
            try {
                current.finalize();
                current = null;
                System.gc();
            } catch (Throwable ex) {
                Logger.getLogger(ProcessBaseClassLoader.class.getName()).log(Level.SEVERE, ex.getMessage());
            }
        }
    }

    public static void reset() {
        synchronized (LOCK) {
            current = new ProcessBaseClassLoader();
        }
    }

    public boolean isUiLoaded() {
        return uiLoaded;
    }

    public void loadUIClasses() {
        try {
            File folder = new File(Constants.UI_LIBS_PATH);
            File[] files = folder.listFiles();
            for (File file : files) {
                current.addFile(Constants.UI_LIBS_PATH + File.separator + file.getName());
            }
        } catch (Exception ex) {
            Logger.getLogger(ProcessBaseClassLoader.class.getName()).log(Level.SEVERE, ex.getMessage());
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
