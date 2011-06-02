/**
 * Copyright (C) 2011 PROCESSBASE Ltd.
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
package org.processbase.engine.bam.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.processbase.engine.bam.BAMConstants;
import org.processbase.engine.bam.metadata.MetaDim;
import org.processbase.engine.bam.metadata.MetaFact;
import org.processbase.engine.bam.metadata.MetaKpi;

/**
 * Hibernate Utility class 
 *
 * @author marat
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;
    private static final Configuration configuration;

    static {
        try {
            if (!BAMConstants.LOADED) {
                BAMConstants.loadConstants();
            }
            /*configuration = new Configuration();
            configuration.setProperty("hibernate.dialect", BAMConstants.BAM_DB_DIALECT);
            configuration.setProperty("hibernate.connection.datasource", BAMConstants.BAM_DB_POOLNAME);
            configuration.addClass(org.processbase.engine.bam.metadata.MetaDim.class);
            configuration.addClass(org.processbase.engine.bam.metadata.MetaFact.class);
            configuration.addClass(org.processbase.engine.bam.metadata.MetaKpi.class);
            sessionFactory = configuration.buildSessionFactory();*/
            configuration=new AnnotationConfiguration()
            .addAnnotatedClass(org.processbase.engine.bam.metadata.MetaDim.class)
            .addAnnotatedClass(org.processbase.engine.bam.metadata.MetaFact.class)
            .addAnnotatedClass(org.processbase.engine.bam.metadata.MetaKpi.class)
            .setProperties(BAMConstants.hibernateProperties());
            sessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    public DatabaseMetaData getDatabaseMetadata() {
        Connection conn = null;
        DatabaseMetaData result = null;
        try {
            conn = configuration.buildSettings().getConnectionProvider().getConnection();
            result = conn.getMetaData();
        } catch (Exception ex) {
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, ex.getMessage());
            }
        }
        return result;
    }

    public Object getID() {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = session.createSQLQuery("SELECT PB_SEQUENCE.NEXTVAL from Dual");
            Object result = q.uniqueResult();
            return result;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            session.close();
        }

    }

    public void executeScripts(ArrayList<String> scripts) throws Exception {
        Connection conn = configuration.buildSettings().getConnectionProvider().getConnection();
        for (String script : scripts) {
            PreparedStatement ps = conn.prepareStatement(script);
            ps.execute();
        }
        if (!conn.isClosed()) {
            conn.close();
        }
    }

    public void validateSchema() throws Exception {
        Connection conn = configuration.buildSettings().getConnectionProvider().getConnection();
        Dialect dialect = Dialect.getDialect(configuration.getProperties());
        DatabaseMetadata metadata = new DatabaseMetadata(conn, dialect);
        configuration.validateSchema(dialect, metadata);
        if (!conn.isClosed()) {
            conn.close();
        }
    }

    public void generateSchema() {
        new SchemaExport(configuration).create(true, true);
    }

    public void dropSchema() {
        new SchemaExport(configuration).drop(true, true);
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

    public ArrayList<MetaKpi> getMetaKpiByStatus(String status) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<MetaKpi> result = (ArrayList<MetaKpi>) session.createQuery("from MetaKpi as metaKpi where metaKpi.status = :status").setString("status", status).list();
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
            ArrayList<MetaDim> result = (ArrayList<MetaDim>) session.createQuery("from MetaDim as metaDim where metaDim.code = :code").setString("code", code).list();
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
            ArrayList<MetaFact> result = (ArrayList<MetaFact>) session.createQuery("from MetaFact as metaFact where metaFact.code = :code").setString("code", code).list();
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
            ArrayList<MetaKpi> result = (ArrayList<MetaKpi>) session.createQuery("from MetaKpi as metaKpi where metaKpi.code = :code").setString("code", code).list();
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
