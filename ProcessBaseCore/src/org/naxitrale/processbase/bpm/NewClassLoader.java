/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.bpm;

/**
 *
 * @author mgubaidullin
 */
public class NewClassLoader extends ClassLoader {

    private byte[] source = null;

    public NewClassLoader() {
        super(NewClassLoader.class.getClassLoader());
    }

    public NewClassLoader(byte[] b) {
        super(NewClassLoader.class.getClassLoader());
        source = b;
    }

    @Override
    public Class findClass(String name) {
//        Package[] x = this.getPackages();
//        for (int i = 0; i < x.length; i++) {
//            Logger.getLogger(NewClassLoader.class.getName()).log(Level.SEVERE, "Package = " + x[i].getName());
//        }
//        Logger.getLogger(NewClassLoader.class.getName()).log(Level.SEVERE, "name = " + name);
        byte[] b = getSource();
        return defineClass(name, b, 0, b.length);
    }

    public byte[] getSource() {
        return source;
    }

    public void setSource(byte[] source) {
        this.source = source;
    }
}
