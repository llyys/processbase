/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.util.db;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.hibernate.Session;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.processbase.util.XMLManager;
import org.processbase.util.ldap.User;

/**
 * @author mgubaidullin
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            // Create the SessionFactory from standard (hibernate.cfg.xml) 
            // config file.
            sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
//            Configuration cfg = new Configuration()
//                    .addClass(org.processbase.util.db.PbActivityUi.class)
//                    .addClass(org.processbase.util.db.PbAttachment.class)
//                    .addClass(org.processbase.util.db.PbHelp.class)
//                    .addClass(org.processbase.util.db.PbProcessAcl.class)
//                    .setProperty("hibernate.connection.datasource","jdbc/ProcessBase")
//                    ;
//
//            sessionFactory = cfg.configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Log the exception. 
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public ArrayList<PbProcessAcl> findPbProcessAcl(String processUUID) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<PbProcessAcl> result = (ArrayList<PbProcessAcl>) session.createQuery("from PbProcessAcl as acl where acl.proccessUuid = :proccessUuid").setString("proccessUuid", processUUID).list();
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public ArrayList<PbProcessAcl> findPbProcessAcl(User user) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<PbProcessAcl> result = new ArrayList<PbProcessAcl>();
            if (user.getGroupsDn().size() > 0) {
                result.addAll((ArrayList<PbProcessAcl>) session.createCriteria(PbProcessAcl.class).add(Restrictions.in("groupDn", user.getGroupsDn())).list());
            }
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void deletePbProcessAcl(String processUUID, String groupDN) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            PbProcessAcl result = (PbProcessAcl) session.createQuery("from PbProcessAcl as acl where acl.proccessUuid = :proccessUuid and acl.groupDn=:groupDn").setString("proccessUuid", processUUID).setString("groupDn", groupDN).list().get(0);
            session.delete(result);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void deletePbProcess(String processUUID) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<PbProcessAcl> result = (ArrayList<PbProcessAcl>) session.createQuery("from PbProcessAcl as acl where acl.proccessUuid = :proccessUuid").setString("proccessUuid", processUUID).list();
            for (Iterator iter = result.iterator(); iter.hasNext();) {
                PbProcessAcl pbProcessAcl = (PbProcessAcl) iter.next();
                session.delete(pbProcessAcl);
            }
            ArrayList<PbActivityUi> result2 = (ArrayList<PbActivityUi>) session.createQuery("from PbActivityUi as ui where ui.proccessUuid = :proccessUuid").setString("proccessUuid", processUUID).list();
            for (Iterator iter = result2.iterator(); iter.hasNext();) {
                PbActivityUi pbActivityUi = (PbActivityUi) iter.next();
                session.delete(pbActivityUi);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void addPbProcessAcl(String processUUID, String groupDN) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            PbProcessAcl result = new PbProcessAcl(UUID.randomUUID().toString(), processUUID, groupDN);
            session.merge(result);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void mergeProcessUi(ArrayList<PbActivityUi> pbActivityUis) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            for (PbActivityUi act : pbActivityUis) {
                session.merge(act);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void addProcessUiEmpty(String processUUID, String processDescription, HashMap<String, String> activities) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(new PbActivityUi(UUID.randomUUID().toString(), processUUID, processUUID, processDescription));
            for (String actUUID : activities.keySet()) {
                session.save(new PbActivityUi(UUID.randomUUID().toString(), processUUID, actUUID, activities.get(actUUID)));
            }
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public ArrayList<PbActivityUi> findProcessUis(String processUUID) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<PbActivityUi> result = (ArrayList<PbActivityUi>) session.createQuery("from PbActivityUi as ui where ui.proccessUuid = :proccessUuid").setString("proccessUuid", processUUID).list();
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public PbActivityUi findPbActivityUi(String activityUUID) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            PbActivityUi result = (PbActivityUi) session.createQuery("from PbActivityUi as ui where ui.activityUuid = :activityUuid").setString("activityUuid", activityUUID).list().get(0);
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public ArrayList<PbAttachment> findProcessPbAttachments(String processUUID) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<PbAttachment> result = (ArrayList<PbAttachment>) session.createQuery("from PbAttachment where proccessUuid = :proccessUuid").setString("proccessUuid", processUUID).list();
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void mergeProcessPbAttachments(ArrayList<PbAttachment> pbAttachments) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            for (PbAttachment atch : pbAttachments) {
                session.merge(atch);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public PbHelp findPbHelp(String uniqueUUID) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List queryResult = session.createQuery("from PbHelp as help where help.uniqueUuid = :uniqueUuid").setString("uniqueUuid", uniqueUUID).list();
            if (queryResult.size() > 0) {
                PbHelp result = (PbHelp) queryResult.get(0);
                tx.commit();
                return result;
            } else {
                tx.commit();
                return null;
            }
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void mergePbHelp(PbHelp pbHelp) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.merge(pbHelp);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void mergePbObject(PbObject pbObject) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.merge(pbObject);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void saveObjects(String processInstanceUUID, String activityUUID, Map<String, Object> pbVars) throws UnsupportedEncodingException {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            for (String key : pbVars.keySet()) {
                Object object = pbVars.get(key);
                if (activityUUID == null) { // new process
                    // process level
                    PbObject pbObjectOfProcess = new PbObject(UUID.randomUUID().toString(), processInstanceUUID,
                            processInstanceUUID, key, object.getClass().getName(),
                            XMLManager.createXML(object.getClass().getName(), object).getBytes("UTF-8"));
                    session.save(pbObjectOfProcess);
                    // activity level (field activityUUID = null)
                    PbObject pbObjectActivity = new PbObject(UUID.randomUUID().toString(), processInstanceUUID,
                            activityUUID, key, object.getClass().getName(),
                            XMLManager.createXML(object.getClass().getName(), object).getBytes("UTF-8"));
                    session.save(pbObjectActivity);
                } else { // existing process
                    // process level
                    List existPbObjects = (List) session.createQuery(
                            "from PbObject as obj " +
                            "where obj.proccessUuid = :proccessUuid " +
                            "and obj.activityUuid = :activityUuid " +
                            "and obj.varName = :varName").setString("proccessUuid", processInstanceUUID).setString("activityUuid", processInstanceUUID).setString("varName", key).list();
                    PbObject pbObjectProcessLevel = null;
                    if (existPbObjects.size() > 0) { // object exists in process
                        pbObjectProcessLevel = (PbObject) existPbObjects.get(0);
                    } else { // object doesn't exist in process
                        pbObjectProcessLevel = new PbObject(UUID.randomUUID().toString());
                    }
                    pbObjectProcessLevel.setProccessUuid(processInstanceUUID);
                    pbObjectProcessLevel.setActivityUuid(processInstanceUUID);
                    pbObjectProcessLevel.setVarName(key);
                    pbObjectProcessLevel.setClassName(object.getClass().getName());
                    pbObjectProcessLevel.setObjectBody(XMLManager.createXML(object.getClass().getName(), object).getBytes("UTF-8"));
                    session.merge(pbObjectProcessLevel);

                    // step level
                    existPbObjects = (List) session.createQuery(
                            "from PbObject as obj " +
                            "where obj.proccessUuid = :proccessUuid " +
                            "and obj.activityUuid = :activityUuid " +
                            "and obj.varName = :varName").setString("proccessUuid", processInstanceUUID).setString("activityUuid", activityUUID).setString("varName", key).list();
                    PbObject pbObjectActivityLevel = null;
                    if (existPbObjects.size() > 0) { // object exists in process
                        pbObjectActivityLevel = (PbObject) existPbObjects.get(0);
                    } else { // object doesn't exist in process
                        pbObjectActivityLevel = new PbObject(UUID.randomUUID().toString());
                    }
                    pbObjectActivityLevel.setProccessUuid(processInstanceUUID);
                    pbObjectActivityLevel.setActivityUuid(activityUUID);
                    pbObjectActivityLevel.setVarName(key);
                    pbObjectActivityLevel.setClassName(object.getClass().getName());
                    pbObjectActivityLevel.setObjectBody(XMLManager.createXML(object.getClass().getName(), object).getBytes("UTF-8"));
                    session.merge(pbObjectActivityLevel);
                }
            }
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public Map<String, Object> findObjects(String processInstanceUUID, String activityUuid) throws UnsupportedEncodingException {
        Map<String, Object> pbVars = new HashMap<String, Object>();
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<PbObject> queryResult = (ArrayList<PbObject>) session.createQuery(
                    "from PbObject as obj where obj.proccessUuid = :proccessUuid" +
                    " and obj.activityUuid = :activityUuid ").setString("proccessUuid", processInstanceUUID).setString("activityUuid", activityUuid).list();
            for (PbObject pbObject : queryResult) {
                pbVars.put(pbObject.getVarName(), XMLManager.createObject(new String(pbObject.getObjectBody(), "UTF-8")));
            }
            tx.commit();
            return pbVars;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public ArrayList<PbObject> findPbObjects(String processInstanceUUID) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<PbObject> queryResult = (ArrayList<PbObject>) session.createQuery(
                    "from PbObject as obj where obj.proccessUuid = :proccessUuid").setString("proccessUuid", processInstanceUUID).list();
            tx.commit();
            return queryResult;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void deletePbObjects(String processInstanceUUID) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<PbObject> queryResult = (ArrayList<PbObject>) session.createQuery(
                    "from PbObject as obj where obj.proccessUuid = :proccessUuid").setString("proccessUuid", processInstanceUUID).list();
            for (PbObject obj : queryResult) {
                session.delete(obj);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void deletePbObjects(ArrayList<String> processInstanceUUIDs) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<PbObject> queryResult = new ArrayList<PbObject>();

            if (processInstanceUUIDs.size() > 0) {
                queryResult.addAll((ArrayList<PbObject>) session.createCriteria(PbObject.class).add(Restrictions.in("proccessUuid", processInstanceUUIDs)).list());
            }
            for (PbObject obj : queryResult) {
                session.delete(obj);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }
}
