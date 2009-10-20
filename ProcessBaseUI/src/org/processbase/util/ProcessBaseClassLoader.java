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

    public ProcessBaseClassLoader() {
        super(((URLClassLoader)ClassLoader.getSystemClassLoader()).getURLs(), ProcessBaseClassLoader.class.getClassLoader());
    }

    public void addFile(String path) throws MalformedURLException {
        String urlPath = "jar:file:" + path + "!/";
        addURL(new URL(urlPath));
    }

    public void printPackages() {
        Package[] x = this.getPackages();
        for (int i = 0; i < x.length; i++) {
            Logger.getLogger("-----").log(Level.SEVERE, "Package = " + x[i].getName());
//            System.out.println("Package = " + x[i].getImplementationTitle());

        }
    }
}
