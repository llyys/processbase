/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.processbase.bam.db;

import java.lang.reflect.Field;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.Mapping;
import org.processbase.core.Constants;

/**
 *
 * @author marat
 */
public class Test {

    public static void main(String[] args) throws SQLException {
        try {
            HibernateUtil hutil = new HibernateUtil();
            hutil.dropSchema();
//            hutil.generateSchema();
//            MetaDim m1 = new MetaDim(1, "DIM01", "DIM01", "java.lang.String", Short.parseShort("10"));
//            MetaDim m2 = new MetaDim(2, "DIM02", "DIM02", "int", Short.parseShort("5"));
//            hutil.addMetaDim(m1);
//            hutil.addMetaDim(m2);
//
//           MetaKpi k = new MetaKpi();
//           k.setCode("KPI_0001");
//           k.setName("KPI_0001");
//           k.setOwner("test");
//           k.setStatus("EDITABLE");
//           k.getMetaDims().add(m1);
//           k.getMetaDims().add(m2);
//           hutil.addMetaKpi(k);
//
//           ScriptGenerator sg = new ScriptGenerator();
//           System.out.println(sg.getCreateTableScript(k, ScriptGenerator.CREATE_SCRIPT));
           
  
        } catch (HibernateException he) {
            System.out.println(he.getMessage());
        }



//        AnnotationConfiguration config = new AnnotationConfiguration();
//                config.configure();
//                new SchemaExport(configuration).drop(true, true);
        //        HibernateUtil hutil = new HibernateUtil();
        //        MetaDim m = new MetaDim();
        //        m.setCode("DIM1");
        //        m.setName("DIMENTION 1");
        //        m.setValueType("STRING");
        //        m = hutil.addMetaDim(m);
        //        hutil.deleteMetaDim(m);
    }
}
