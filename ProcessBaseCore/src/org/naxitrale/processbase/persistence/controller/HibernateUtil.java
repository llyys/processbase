/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.persistence.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.naxitrale.processbase.persistence.entity.Pbgroup;
import org.naxitrale.processbase.persistence.entity.Pborg;
import org.naxitrale.processbase.persistence.entity.Pbrole;
import org.naxitrale.processbase.persistence.entity.Pbuser;
import org.naxitrale.processbase.util.PasswordService;

/**
 *
 * @author mgubaidullin
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public List<Pbuser> findPbusersByUsername(String username) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List result = session.createQuery(
                    "from Pbuser as Pbuser where Pbuser.username = ?").setString(0, username).list();
            tx.commit();
            return (List<Pbuser>) result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public boolean isUserInRole(String userName, String roleName) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List users = session.createSQLQuery("SELECT 1 " +
                    " FROM PROCESSBASE.PBUSERS u, PROCESSBASE.PBROLES r, PROCESSBASE.PBUSERROLES ur " +
                    " WHERE u.id = ur.pbuser_id " +
                    " AND ur.pbrole_id = r.ID " +
                    " AND r.ROLENAME = :roleName and u.username = :userName " +
                    " UNION " +
                    " SELECT 1 " +
                    " FROM PROCESSBASE.PBUSERS u, PROCESSBASE.PBROLES r, PROCESSBASE.PBROLEGROUPS gr, PROCESSBASE.PBUSERGROUPS ug " +
                    " WHERE u.id = ug.pbuser_id " +
                    " AND ug.pbgroup_id = gr.pbgroup_id " +
                    " AND gr.pbrole_id = r.ID " +
                    " AND r.rolename = :roleName and u.username = :userName").setString("userName", userName).setString("roleName", roleName).list();
            tx.commit();
            return users.size() == 0 ? false : true;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public List<String> findUsernamesByRoleName(String rolename) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<String> result = (List<String>) session.createSQLQuery("SELECT u.username " +
                    "FROM PROCESSBASE.PBUSERS u, PROCESSBASE.PBROLES r, PROCESSBASE.PBUSERROLES ur " +
                    "WHERE u.id = ur.pbuser_id " +
                    "AND ur.pbrole_id = r.ID " +
                    "AND r.rolename = :roleName " +
                    "UNION " +
                    "SELECT u.username " +
                    "FROM PBUSERS u, PROCESSBASE.PBROLES r, PROCESSBASE.PBROLEGROUPS gr, PROCESSBASE.PBUSERGROUPS ug " +
                    "WHERE u.id = ug.pbuser_id " +
                    "AND ug.pbgroup_id = gr.pbgroup_id " +
                    "AND gr.pbrole_id = r.ID " +
                    "AND r.rolename = :roleName ").setString("roleName", rolename).list();
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public List<String> findUsernamesByOrgUnit(String orgUnitName) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<String> result = session.createSQLQuery("select u.username " +
                    "from PROCESSBASE.PBUSERS u " +
                    "where u.pborg_id in " +
                    "(select o.id " +
                    "from Pborgs o " +
                    "CONNECT BY PRIOR o.id = o.pborg_id " +
                    "START WITH o.org_name = :orgName)").setString("orgName", orgUnitName).list();
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public List<Pbrole> findAllPbroles(String pbtype) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<Pbrole> result = (List<Pbrole>) session.createQuery("from Pbrole as role where role.pbtype = :pbtype").setString("pbtype", pbtype).list();
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public List<Pborg> findAllOrgUnits() {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<Pborg> result = (List<Pborg>) session.createSQLQuery(
                    "select {Pborg.*} from PROCESSBASE.PBORGS {Pborg} " +
                    "CONNECT BY PRIOR id = pborg_id " +
                    "START WITH pborg_id is null").addEntity("Pborg", Pborg.class).list();
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public List<Pbgroup> findAllGroups(String pbtype) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<Pbgroup> result = (List<Pbgroup>) session.createQuery("from Pbgroup as group where group.pbtype = :pbtype").setString("pbtype", pbtype).list();
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public List<Pbuser> findAllPbusers(String pbtype) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List result = session.createQuery("from Pbuser as user where user.pbtype = :pbtype").setString("pbtype", pbtype).list();
            tx.commit();
            return (List<Pbuser>) result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void merge(Object object) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.merge(object);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void mergeUser(Pbuser user) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Pbuser currentUser = (Pbuser) session.load(Pbuser.class, user.getId());
            if (!user.getPassword().equals(currentUser.getPassword())) {
                user.setPassword(PasswordService.encrypt(user.getPassword()));
            }
            session.merge(user);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, ex.getMessage());
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void mergePborg(Pborg org) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Pborg parent = (Pborg) session.load(Pborg.class, org.getPborgs().getId());
            org.setPborgs(parent);
            session.merge(org);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void delete(Object object) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.delete(object);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void addUserToGroup(Pbgroup g, Pbuser user) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Pbgroup group = (Pbgroup) session.load(Pbgroup.class, g.getId());
            group.getPbusers().add(user);
            session.flush();
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void addUserToRole(Pbrole r, Pbuser user) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Pbrole role = (Pbrole) session.load(Pbrole.class, r.getId());
            role.getPbusers().add(user);
            session.flush();
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void addProcessToRole(Pbrole r, String ProcessDefinitionUUID) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Pbrole role = (Pbrole) session.load(Pbrole.class, r.getId());
            role.getProcesses().add(ProcessDefinitionUUID);
            session.flush();
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void deleteProcessFromRole(Pbrole r, String ProcessDefinitionUUID) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Pbrole role = (Pbrole) session.load(Pbrole.class, r.getId());
            role.getProcesses().remove(ProcessDefinitionUUID);
            session.save(role);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void addGroupToRole(Pbrole r, Pbgroup group) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Pbrole role = (Pbrole) session.load(Pbrole.class, r.getId());
            role.getPbgroups().add(group);
            session.flush();
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void deleteUserFromGroup(Pbgroup g, Pbuser u) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Pbgroup group = (Pbgroup) session.load(Pbgroup.class, g.getId());
            Pbuser user = (Pbuser) session.load(Pbuser.class, u.getId());
            group.getPbusers().remove(user);
            session.save(group);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void deleteUserFromRole(Pbrole r, Pbuser u) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Pbrole role = (Pbrole) session.load(Pbrole.class, r.getId());
            Pbuser user = (Pbuser) session.load(Pbuser.class, u.getId());
            role.getPbusers().remove(user);
            session.save(role);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void deleteGroupFromRole(Pbrole r, Pbgroup g) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Pbrole role = (Pbrole) session.load(Pbrole.class, r.getId());
            Pbgroup group = (Pbgroup) session.load(Pbgroup.class, g.getId());
            role.getPbgroups().remove(group);
            session.save(role);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public Set<Pbuser> getUsersByGroup(Pbgroup g) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        Set<Pbuser> result = new HashSet<Pbuser>();
        try {
            tx = session.beginTransaction();
            Pbgroup group = (Pbgroup) session.load(Pbgroup.class, g.getId());
            result.addAll(group.getPbusers());
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public Set<Pbuser> getUsersByOrg(Pborg o) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        Set<Pbuser> result = new HashSet<Pbuser>();
        try {
            tx = session.beginTransaction();
            Pborg org = (Pborg) session.load(Pborg.class, o.getId());
            result.addAll(org.getPbuserses());
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public List<Pbuser> getUsersNotInGroup(Pbgroup g) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List result = session.createQuery("from Pbuser as user where user.pbtype = :pbtype").setString("pbtype", "APP").list();
            Pbgroup group = (Pbgroup) session.load(Pbgroup.class, g.getId());
            result.removeAll(group.getPbusers());
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public Set<Pbuser> getUsersByRole(Pbrole r) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        Set<Pbuser> result = new HashSet<Pbuser>();
        try {
            tx = session.beginTransaction();
            Pbrole role = (Pbrole) session.load(Pbrole.class, r.getId());
            result.addAll(role.getPbusers());
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public Set<Pbgroup> getGroupsByRole(Pbrole r) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        Set<Pbgroup> result = new HashSet<Pbgroup>();
        try {
            tx = session.beginTransaction();
            Pbrole role = (Pbrole) session.load(Pbrole.class, r.getId());
            result.addAll(role.getPbgroups());
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public List<Pbuser> getUsersNotInRole(Pbrole r) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List result = session.createQuery("from Pbuser as user where user.pbtype = :pbtype").setString("pbtype", "APP").list();
            Pbrole role = (Pbrole) session.load(Pbrole.class, r.getId());
            result.removeAll(role.getPbusers());
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public List<Pbgroup> getGroupsNotInRole(Pbrole r) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<Pbgroup> result = session.createQuery("from Pbgroup as group where group.pbtype = :pbtype").setString("pbtype", "APP").list();
            Pbrole role = (Pbrole) session.load(Pbrole.class, r.getId());
            result.removeAll(role.getPbgroups());
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public Set<String> getRoleProcesses(Pbrole r) {
        Set<String> result = new HashSet<String>();
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Pbrole role = (Pbrole) session.load(Pbrole.class, r.getId());
            result.addAll(role.getProcesses());
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
            return result;
        }
    }

    public Set<String> getUserProcesses(Pbuser u) {
        Set<String> result = new HashSet<String>();
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            result.addAll(session.createSQLQuery("select p.processid " +
                    " from PROCESSBASE.pbusers u, PROCESSBASE.pbroles r, PROCESSBASE.pbuserroles ur, PROCESSBASE.pbroleprocesses p" +
                    " where u.id = ur.pbuser_id and r.id = ur.pbrole_id and p.pbrole_id = r.id and u.id = :userId" +
                    " union " +
                    " select p.processid " +
                    " from PROCESSBASE.pbusers u, PROCESSBASE.pbrolegroups rg, PROCESSBASE.pbusergroups ug, PROCESSBASE.pbroleprocesses p" +
                    " where u.id = ug.pbuser_id and ug.pbgroup_id = rg.pbgroup_id and p.pbrole_id = rg.pbrole_id and u.id = :userId").setLong("userId", u.getId()).list());
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
            return result;
        }
    }
}
