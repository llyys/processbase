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
        PbSection section = hutil.findPbSection("MortgageSamruk-1.2-MortgageSamruk-1.2");
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        Transaction tx = null;
//        try {
//            tx = session.beginTransaction();
//            PbProcessSection queryResult = (PbProcessSection) session.createQuery(
//                    "from PbProcessSection ps where ps.proccessUuid = :processUUID" ).setString("processUUID", "MortgageSamruk-1.2-MortgageSamruk-1.2").uniqueResult();
//            section = queryResult.getPbSection();
//            System.out.println("1 " + section);
//            tx.commit();
//            System.out.println("2 " + section);
//        } finally {
//            if (tx.isActive()) {
//                tx.rollback();
//            }
//            session.close();
//        }
        System.out.println("4 " + section);
    }
}
