package org.processbase.ui.core.util;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by Lauri on 3.11.13.
 */
public class BonitaClassloader extends URLClassLoader {
    public BonitaClassloader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }
}
