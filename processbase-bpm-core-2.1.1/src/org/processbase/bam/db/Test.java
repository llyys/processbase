/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.processbase.bam.db;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Mappings;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.engine.Mapping;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.TypeDef;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.type.TypeFactory;

/**
 *
 * @author marat
 */
public class Test {

    public static void main(String[] args) {

//       Configuration cfg = new Configuration()
//                .setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect")
//                .setProperty("hibernate.connection.driver_class", "oracle.jdbc.OracleDriver")
//                .setProperty("hibernate.connection.url", "jdbc:oracle:thin:@localhost:1521:maratdb")
//                .setProperty("hibernate.connection.username", "pbbam2")
//                .setProperty("hibernate.connection.password", "pbbam2");

            String d = "org.hibernate.dialect.Oracle10gDialect";
            ScriptGenerator g  = new ScriptGenerator();
            MetaKpi k = new MetaKpi();
            k.setCode("KPI_0001");

            k.getMetaDims().add(new MetaDim(1, "DIM01", "DIM01", "java.lang.String", Short.parseShort("10")));
            k.getMetaDims().add(new MetaDim(2, "DIM02", "DIM01", "int", Short.parseShort("10")));
            k.getMetaDims().add(new MetaDim(2, "DIM03", "DIM01", "long", Short.parseShort("10")));
            k.getMetaDims().add(new MetaDim(3, "DIM06", "DIM01", "java.util.Date", Short.parseShort("10")));
            
              k.getMetaFacts().add(new MetaFact(3, "FACT06", "DIM01"));
            System.out.println(g.getCreateTableScript(k, d, ScriptGenerator.CREATE_SCRIPT));


        AnnotationConfiguration config = new AnnotationConfiguration();
                config.configure();
                new SchemaExport(config).create(true, true);
        //        HibernateUtil hutil = new HibernateUtil();
        //        MetaDim m = new MetaDim();
        //        m.setCode("DIM1");
        //        m.setName("DIMENTION 1");
        //        m.setValueType("STRING");
        //        m = hutil.addMetaDim(m);
        //        hutil.deleteMetaDim(m);
    }

    protected Mapping getMapping() throws HibernateException {
        Mapping m = null;
        try {
            Configuration c = new Configuration();

            Field f = Configuration.class.getDeclaredField("mapping");
            f.setAccessible(true);
            m = (Mapping) f.get(this);
        } catch (Exception x) {
            throw new HibernateException("failed to reflect mapping field", x);
        }

        return m;
    }
}
