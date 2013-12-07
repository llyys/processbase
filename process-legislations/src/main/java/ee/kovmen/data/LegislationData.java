package ee.kovmen.data;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.h2.util.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.ow2.bonita.util.BonitaConstants;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.springframework.orm.hibernate3.HibernateTemplate;

import ee.kovmen.entities.KovLegislation;

public class LegislationData {
	
//	private static SessionFactory sessionFactory;
//	private static AnnotationConfiguration configure;
//	private HibernateTemplate hibernateTemplate; 
	
	private static Map<String, SessionFactory> sessionFactoryMap = new HashMap<String, SessionFactory>();
	
	private static Map<String, HibernateTemplate> hibernateTemplateMap = new HashMap<String, HibernateTemplate>();
	
	public static SessionFactory getSessionFactory() {
		
		String currentDomain = ProcessbaseApplication.getCurrent()
				.getBpmModule().getCurrentDomain();
        if(StringUtils.isNullOrEmpty(currentDomain) || "null".equalsIgnoreCase(currentDomain))
            currentDomain= Constants.BONITA_DOMAIN;
		SessionFactory sessionFactory =  sessionFactoryMap.get(currentDomain);
		if (sessionFactory == null) {
			try {
				String userHomeDir = BonitaConstants.getBonitaHomeFolder();
				File file = new File(userHomeDir + "/server/" + currentDomain
						+ "/conf/bonita-journal.properties");

				FileInputStream in = new FileInputStream(file);

				Properties properties = new Properties();
				properties.load(in);

				in.close();

				AnnotationConfiguration configuration = new AnnotationConfiguration();
				
				configuration
						.setProperties(properties);

				AnnotationConfiguration configure = configuration.configure();
				sessionFactory = configure.buildSessionFactory();
				
				sessionFactoryMap.put(currentDomain, sessionFactory);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
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
		
		String currentDomain = ProcessbaseApplication.getCurrent()
				.getBpmModule().getCurrentDomain();

		HibernateTemplate hibernateTemplate = hibernateTemplateMap
				.get(currentDomain);
		if (hibernateTemplate == null) {
			hibernateTemplate = new HibernateTemplate(getSessionFactory());
			hibernateTemplateMap.put(currentDomain, hibernateTemplate);
		}
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
