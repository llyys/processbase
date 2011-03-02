/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.processbase.bam.db;

import java.util.ArrayList;
import org.hibernate.Session;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * Hibernate Utility class with a convenient method to get Session Factory object.
 *
 * @author marat
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;
    private static final Configuration configuration;

    static {
        try {
            configuration = new Configuration()
                .setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect")
                .setProperty("hibernate.connection.datasource", "jdbc/pbbam2")
//                .setProperty("hibernate.connection.driver_class", "oracle.jdbc.OracleDriver")
//                .setProperty("hibernate.connection.url", "jdbc:oracle:thin:@localhost:1521:maratdb")
//                .setProperty("hibernate.connection.username", "pbbam2")
//                .setProperty("hibernate.connection.password", "pbbam2");
                 .addClass(org.processbase.bam.db.MetaDim.class)
                  .addClass(org.processbase.bam.db.MetaFact.class)
                   .addClass(org.processbase.bam.db.MetaKpi.class);
            sessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            // Log the exception. 
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    public void xxx(String sql){
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
//            session.createSQLQuery(null)
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void generateSchema(){
        new SchemaExport(configuration).create(true, true);
    }

    public ArrayList<MetaDim> getAllMetaDim() {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<MetaDim> result = (ArrayList<MetaDim>) session.createQuery("from MetaDim as metaDim").list();
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public ArrayList<MetaFact> getAllMetaFact() {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<MetaFact> result = (ArrayList<MetaFact>) session.createQuery("from MetaFact as metaFact").list();
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public ArrayList<MetaKpi> getAllMetaKpi() {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<MetaKpi> result = (ArrayList<MetaKpi>) session.createQuery("from MetaKpi as metaKpi").list();
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public ArrayList<MetaDim> getMetaDimByCode(String code) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<MetaDim> result = (ArrayList<MetaDim>)
                    session.createQuery("from MetaDim as metaDim where metaDim.code = :code").setString("code", code).list();
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public ArrayList<MetaFact> getMetaFactByCode(String code) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<MetaFact> result = (ArrayList<MetaFact>)
                    session.createQuery("from MetaFact as metaFact where metaFact.code = :code").setString("code", code).list();
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public ArrayList<MetaKpi> getMetaKpiByCode(String code) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<MetaKpi> result = (ArrayList<MetaKpi>)
                    session.createQuery("from MetaKpi as metaKpi where metaKpi.code = :code").setString("code", code).list();
            tx.commit();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public MetaDim addMetaDim(MetaDim metaDim) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            metaDim = (MetaDim) session.merge(metaDim);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
        return metaDim;
    }

    public MetaFact addMetaFact(MetaFact metaFact) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            metaFact = (MetaFact) session.merge(metaFact);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
        return metaFact;
    }

    public MetaKpi addMetaKpi(MetaKpi metaKpi) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            metaKpi = (MetaKpi) session.merge(metaKpi);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
        return metaKpi;
    }

    public void updateMetaKpi(MetaKpi metaKpi) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.merge(metaKpi);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void deleteMetaDim(MetaDim metaDim) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            MetaDim result = (MetaDim) session.createQuery("from MetaDim as metaDim where metaDim.id = :id").setLong("id", metaDim.getId()).list().get(0);
            session.delete(result);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void deleteMetaFact(MetaFact metaFact) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            MetaFact result = (MetaFact) session.createQuery("from MetaFact as metaFact where metaFact.id = :id").setLong("id", metaFact.getId()).list().get(0);
            session.delete(result);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }

    public void deleteMetaKpi(MetaKpi metaKpi) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            MetaKpi result = (MetaKpi) session.createQuery("from MetaKpi as metaKpi where metaKpi.id = :id").setLong("id", metaKpi.getId()).list().get(0);
            session.delete(result);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }
    }
}
