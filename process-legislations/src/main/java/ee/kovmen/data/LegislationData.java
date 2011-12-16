package ee.kovmen.data;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.hibernate.FlushMode;
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

import ee.kovmen.entities.KovLegislation;
import ee.kovmen.entities.Oigusakt;
import ee.kovmen.entities.Teenus;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class LegislationData {
	private static SessionFactory sessionFactory;
	private static AnnotationConfiguration configure;
	private HibernateTemplate hibernateTemplate; 
	
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
		Session session = getSessionFactory().getCurrentSession();
		if(!session.isOpen())
			session=getSessionFactory().openSession();
		return session;
	}
	
	public HibernateTemplate getHibernate(){
		if(hibernateTemplate==null)
			hibernateTemplate=new HibernateTemplate(getSessionFactory());
		
		return hibernateTemplate;
	}
	
	
	
	
	private LegislationData(){
		
	}
	private static LegislationData instance;
	
	public static LegislationData getCurrent() {
		if(instance==null)
			instance = new LegislationData();
		return instance;
	}

	public List<KovLegislation> findAllLegislations() {
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		List list = session
		.createCriteria(KovLegislation.class)
		.list();
		tx.commit();
		return list;
		
	}
	
	public void DeleteLegislation(KovLegislation akt){
		Session sess=getSession();
		Transaction tx=null;
		try {
			tx = sess.beginTransaction();
			akt=(KovLegislation) sess.get(KovLegislation.class, akt.getId());
			
			sess.delete(akt);			
			tx.commit();
		} catch (Exception e) {
			 tx.rollback();
			throw new RuntimeException(e);
		}
		finally{
			if(sess.isOpen())
				sess.close();
		}
	}
	
	public void SaveLegislation(KovLegislation akt) {
		Session sess=getSession();
		Transaction tx=null;
		try {
			
			tx = sess.beginTransaction();
			if(akt.getId()==null)
				sess.save(akt);
			else
				sess.update(akt);
			
			tx.commit();
		} catch (Exception e) {
			 tx.rollback();
			throw new RuntimeException(e);
		}
		finally{
			if(sess.isOpen())
				sess.close();
		}
		
	}

}
