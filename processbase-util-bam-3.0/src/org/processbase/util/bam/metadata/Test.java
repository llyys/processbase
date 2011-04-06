/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.processbase.util.bam.metadata;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.Mapping;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.type.LongType;
import org.processbase.util.bam.BAMConstants;
import org.hibernate.type.Type;

/**
 *
 * @author marat
 */
public class Test {

    public static void main(String[] args) throws SQLException {
        try {
Calendar calendar = new GregorianCalendar();
                calendar.setTimeInMillis(System.currentTimeMillis());
                System.out.println(calendar.get(Calendar.YEAR));
                System.out.println(calendar.get(Calendar.MONTH)+1);

                double month = calendar.get(Calendar.MONTH)+1;
                System.out.println(Math.ceil(month / 4));
               
                System.out.println(calendar.get(Calendar.WEEK_OF_YEAR));
                System.out.println(calendar.get(Calendar.DAY_OF_MONTH));
                System.out.println(calendar.get(Calendar.DAY_OF_WEEK ) -1);

                 System.out.println("============================");
                for (int i = 1; i < 13 ; i++){
                    month = i;
                    System.out.println(Math.ceil(month / 3));
                }

//            SequenceGenerator sg = new SequenceGenerator();
//            HibernateUtil hutil = new HibernateUtil();
//            System.out.println(HibernateUtil.getConfiguration().getProperties().getProperty("hibernate.connection.datasource"));
//            Connection conn = configuration.buildSettings().getConnectionProvider().getConnection();
//        Dialect dialect = Dialect.getDialect(HibernateUtil.getConfiguration().getProperties());
//        sg.configure(new LongType(), HibernateUtil.getConfiguration().getProperties(), dialect);
//            System.out.print(sg.sqlCreateStrings(dialect)[0]);
//             System.out.print(sg.sqlCreateStrings(dialect)[1]);
//              System.out.print(sg.sqlCreateStrings(dialect)[2]);
//            HibernateUtil hutil = new HibernateUtil();
//            hutil.dropSchema();
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
