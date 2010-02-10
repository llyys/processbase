/**
 * Copyright (C) 2010 PROCESSBASE Ltd.
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
package org.processbase.util.db;

import java.util.ArrayList;
import java.util.Iterator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * @author mgubaidullin
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            // Create the SessionFactory from standard (hibernate.cfg.xml) 
            // config file.
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void mergeProcessUi(String processUUID, ArrayList<PbActivityUi> pbActivityUis) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<PbActivityUi> oldPbActivityUis = (ArrayList<PbActivityUi>) session.createQuery("from PbActivityUi as ui where ui.proccessUuid = :proccessUuid").setString("proccessUuid", processUUID).list();
            for (PbActivityUi act : oldPbActivityUis) {
                session.delete(act);
            }
            for (PbActivityUi act : pbActivityUis) {
                if (act.getMobileUiClass() != null && !act.getMobileUiClass().isEmpty()) {
                    act.setIsMobile("T");
                } else {
                    act.setIsMobile("F");
                }
                session.save(act);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void addProcessUiEmpty(String processUUID, String processDescription, ArrayList<PbActivityUi> activities) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            for (PbActivityUi act : activities) {
                session.save(act);
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

    public void mergeProcessPbAttachments(ArrayList<PbAttachment> toMerge, ArrayList<PbAttachment> toDelete) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            for (PbAttachment atch : toMerge) {
                session.merge(atch);
            }
            for (PbAttachment atch : toDelete) {
                session.delete(atch);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void deletePbProcessess(ArrayList<String> processInstanceUUIDs) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            for (String processUUID : processInstanceUUIDs) {

                ArrayList<PbActivityUi> result2 = (ArrayList<PbActivityUi>) session.createQuery("from PbActivityUi as ui where ui.proccessUuid = :proccessUuid").setString("proccessUuid", processUUID).list();
                for (Iterator iter = result2.iterator(); iter.hasNext();) {
                    PbActivityUi pbActivityUi = (PbActivityUi) iter.next();
                    session.delete(pbActivityUi);
                }

                ArrayList<PbAttachment> result4 = (ArrayList<PbAttachment>) session.createQuery("from PbAttachment as ui where ui.proccessUuid = :proccessUuid").setString("proccessUuid", processUUID).list();
                for (Iterator iter = result4.iterator(); iter.hasNext();) {
                    PbAttachment pbAttachment = (PbAttachment) iter.next();
                    session.delete(pbAttachment);
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

    public void deletePbProcess(String processUUID) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<PbActivityUi> result2 = (ArrayList<PbActivityUi>) session.createQuery("from PbActivityUi as ui where ui.proccessUuid = :proccessUuid").setString("proccessUuid", processUUID).list();
            for (Iterator iter = result2.iterator(); iter.hasNext();) {
                PbActivityUi pbActivityUi = (PbActivityUi) iter.next();
                session.delete(pbActivityUi);
            }
            ArrayList<PbAttachment> result4 = (ArrayList<PbAttachment>) session.createQuery("from PbAttachment as ui where ui.proccessUuid = :proccessUuid").setString("proccessUuid", processUUID).list();
            for (Iterator iter = result4.iterator(); iter.hasNext();) {
                PbAttachment pbAttachment = (PbAttachment) iter.next();
                session.delete(pbAttachment);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void deletePbAttachments(ArrayList<PbAttachment> attachments) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            for (PbAttachment obj : attachments) {
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
