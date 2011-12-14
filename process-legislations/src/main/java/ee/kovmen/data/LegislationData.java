package ee.kovmen.data;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.engine.Mapping;
import org.hibernate.mapping.Set;
import org.ow2.bonita.util.BonitaConstants;

import ee.kovmen.entities.Oigusakt;
import ee.kovmen.entities.Teenus;

public class LegislationData {
	private static SessionFactory sessionFactory;
	private static AnnotationConfiguration configure;
	
	
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
           
            
            AnnotationConfiguration configuration=new AnnotationConfiguration();
            /*configuration.addPackage("ee.kovmen.entities");
            configuration.addAnnotatedClass(Oigusakt.class);
            configuration.addAnnotatedClass(Teenus.class);
            */
            configure = configuration.configure();
            /*
            for (Entry<Object, Object> p : properties.entrySet()) {
				configure.setProperty(p.getKey().toString(), p.getValue().toString());
			}
            */
            
       	
           
           sessionFactory = configure.buildSessionFactory();
           
       } catch (Exception ex) {
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
	
	public void SaveLegislation(Oigusakt akt) {
		try {
			Transaction transaction = getSession().beginTransaction();
			getSession().saveOrUpdate(akt);
			transaction.commit();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

}
