package org.processbase.util.db;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import org.processbase.util.Constants;

/**
 *
 * @author mgubaidullin
 */
public class Test {

    public static void main(String[] args) throws URISyntaxException, IOException {
        Constants.loadConstants();
        HibernateUtil hutil = new HibernateUtil();
        Map<String, Object> pbVars = hutil.findObjects("MortgageSamruk-1.2-MortgageSamruk-1.2$4461");
        for (String key : pbVars.keySet()) {
            System.out.println(key + " = " + pbVars.get(key));
        }
    }
}
