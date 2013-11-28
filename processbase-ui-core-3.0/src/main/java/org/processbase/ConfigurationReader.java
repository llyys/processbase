package org.processbase;

import org.ow2.bonita.util.BonitaConstants;
import org.ow2.bonita.util.Misc;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Created by Lauri on 23.11.13.
 */
public class ConfigurationReader {
    public static Properties loadProperties(String value){
        Misc.checkArgsNotNull(value);
        Properties properties = new Properties();
        String property = "property:";
        String path = value;
        if (value.startsWith(property)) {
            path = System.getProperty(value.substring(property.length()));
        } else if (value.startsWith("${" + BonitaConstants.HOME + "}")) {
            path = path.replace("${" + BonitaConstants.HOME + "}", System.getProperty(BonitaConstants.HOME));
        }

        FileInputStream in = null;
        try {
            in = new FileInputStream(path);

            properties.load(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }
}
