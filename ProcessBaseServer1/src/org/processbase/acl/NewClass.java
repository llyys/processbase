/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.processbase.acl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author mgubaidullin
 */
public class NewClass {

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.setProperty("aaaa", "2222");
        properties.setProperty("bbb", "2222");
        properties.storeToXML(new FileOutputStream(new File("hello.properties")), "", "UTF-8");
    }
}
