/**
 * Copyright (C) 2011 PROCESSBASE Ltd.
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

package org.processbase.ui.core.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 *
 * @author marat
 */
public class ProcessBaseUIClassLoader extends URLClassLoader {

    public ProcessBaseUIClassLoader(URL[] urls) {
        super(urls, ProcessBaseUIClassLoader.class.getClassLoader());
    }

    public void addFile(String path) throws MalformedURLException {
        String urlPath = "jar:file:" + path + "!/";
        addURL(new URL(urlPath));
    }

    public void printPackages() {
        Package[] x = this.getPackages();
        for (int i = 0; i < x.length; i++) {
            System.out.println("Package = " + x[i].getName());
        }
    }

    public Package[] getP() {
        return this.getPackages();
    }
}
