package org.processbase.util.db;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import kz.temirbank.ws.retail.client.ClientExt;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.processbase.util.Constants;

/**
 *
 * @author mgubaidullin
 */
public class Test {

    public static void main(String[] args) throws URISyntaxException, IOException {
        Constants.loadConstants();
        HibernateUtil hutil = new HibernateUtil();
         Map<String, Object> pbVars = hutil.findObjects("MortgageSamruk-1.2-MortgageSamruk-1.2$5621", "");

         ClientExt client = (ClientExt) pbVars.get("client");
         client.setMaritalStatus("DEV");
         client.setDateBirth(new Date());
         pbVars.put("client", client);

         hutil.saveObjects("MortgageSamruk-1.2-MortgageSamruk-1.2$5621", "TEST", pbVars);
//        Session session = hutil.getSessionFactory().openSession();
//        Transaction tx = null;
//            tx = session.beginTransaction();
//        List existPbObjects = (List) session.createQuery(
//                            "from PbObject as obj " +
//                            "where obj.proccessUuid = :proccessUuid " +
//                            "and obj.activityUuid = :activityUuid " +
//                            "and obj.varName = :varName").setString("proccessUuid", "")
//                            .setString("activityUuid", "").setString("varName", "").list();
        System.out.println(pbVars);
    }
}
