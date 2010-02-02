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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
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
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
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
            ArrayList<PbProcessSection> result3 = (ArrayList<PbProcessSection>) session.createQuery("from PbProcessSection as ui where ui.proccessUuid = :proccessUuid").setString("proccessUuid", processUUID).list();
            for (Iterator iter = result3.iterator(); iter.hasNext();) {
                PbProcessSection pbProcessSection = (PbProcessSection) iter.next();
                session.delete(pbProcessSection);
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

    public void addPbProcessAcl(String processUUID, String groupDN) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            PbProcessAcl result = new PbProcessAcl(processUUID, groupDN);
            session.merge(result);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
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

    public void deletePbProcessess(ArrayList<String> processInstanceUUIDs) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            for (String processUUID : processInstanceUUIDs) {
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
                ArrayList<PbProcessSection> result3 = (ArrayList<PbProcessSection>) session.createQuery("from PbProcessSection as ui where ui.proccessUuid = :proccessUuid").setString("proccessUuid", processUUID).list();
                for (Iterator iter = result3.iterator(); iter.hasNext();) {
                    PbProcessSection pbProcessSection = (PbProcessSection) iter.next();
                    session.delete(pbProcessSection);
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

    public ArrayList<PbSection> findPbSections() {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<PbSection> queryResult = (ArrayList<PbSection>) session.createQuery(
                    "from PbSection as obj").list();
            tx.commit();
            return queryResult;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void deletePbSection(PbSection section) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.delete(section);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public ArrayList<PbProcessSection> findPbProcessSections() {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<PbProcessSection> queryResult = (ArrayList<PbProcessSection>) session.createQuery(
                    "from PbProcessSection as obj").list();
            tx.commit();
            return queryResult;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public PbSection findPbSection(String processUUID) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        PbSection section = null;
        try {
            tx = session.beginTransaction();
            PbProcessSection queryResult = (PbProcessSection) session.createQuery(
                    "from PbProcessSection ps where ps.proccessUuid = :processUUID").setString("processUUID", processUUID).uniqueResult();
            if (queryResult != null) {
                section = queryResult.getPbSection();
                section.toString();
            }
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
        return section;
    }

    public void setPbProcessSection(String processUUID, PbSection section) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            PbProcessSection queryResult = (PbProcessSection) session.createQuery(
                    "from PbProcessSection as ps where ps.proccessUuid = :processUUID").setString("processUUID", processUUID).uniqueResult();
            if (queryResult != null) {
                queryResult.setPbSection(section);
                session.merge(queryResult);
            } else {
                PbProcessSection newps = new PbProcessSection(section, processUUID);
                session.save(newps);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void mergePbSection(PbSection pbSection) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.merge(pbSection);
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
