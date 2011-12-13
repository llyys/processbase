package ee.kovmen.data;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.mapping.Set;
import org.ow2.bonita.util.BonitaConstants;

import ee.kovmen.entities.Oigusakt;
import ee.kovmen.entities.Teenus;

public class LegislationData {
	private static SessionFactory sessionFactory;
	
	
	public static SessionFactory getSessionFactory() {
        if (sessionFactory!=null) return sessionFactory;
        try {
       	 String userHomeDir=BonitaConstants.getBonitaHomeFolder();
            File file=new File(userHomeDir+"/server/default/conf/bonita-journal.properties");
            FileInputStream fis = new FileInputStream(file);
            Properties properties = null;
			 
            if(properties==null)
            	properties=new Properties();
            
            properties.load(fis);
            fis.close();
           
            
            AnnotationConfiguration configuration=new AnnotationConfiguration()
            .addPackage("ee.kovmen.entities") 
            .addAnnotatedClass(Oigusakt.class)
            .addAnnotatedClass(Teenus.class)
            .mergeProperties(properties)
            .configure();
       	
           
           sessionFactory = configuration.buildSessionFactory();
           
       } catch (Throwable ex) {
           // Make sure you log the exception, as it might be swallowed
           System.err.println("Initial SessionFactory creation failed." + ex);
           throw new ExceptionInInitializerError(ex);
       }
		return sessionFactory;
    }
	
	public Session getSession(){
		return getSessionFactory().openSession();
	}
	
	private LegislationData(){
		
	}
	private static LegislationData instance;
	
	public static LegislationData getCurrent() {
		if(instance==null)
			instance = new LegislationData();
		return instance;
	}

	public List<Oigusakt> findAllLegislations() {
		return getSession()
		.createCriteria(Oigusakt.class)
		.list();
		
	}

}
