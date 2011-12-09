package ee.kovmen.data;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.mapping.Set;

import ee.kovmen.entities.Oigusakt;
import ee.kovmen.entities.Teenus;

public class LegislationData {
	private static final SessionFactory sessionFactory;
	static {
        try {
            AnnotationConfiguration cnf = new AnnotationConfiguration();
            cnf.setProperty(Environment.DRIVER, "org.hsqldb.jdbcDriver");
            cnf.setProperty(Environment.URL, "jdbc:hsqldb:mem:Workout");
            cnf.setProperty(Environment.USER, "sa");
            cnf.setProperty(Environment.DIALECT, PostgreSQLDialect.class.getName());
            cnf.setProperty(Environment.SHOW_SQL, "true");
            cnf.setProperty(Environment.HBM2DDL_AUTO, "create-drop");
            cnf.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS,"thread");
            
            cnf.addAnnotatedClass(Oigusakt.class);
            cnf.addAnnotatedClass(Teenus.class);
            
            sessionFactory = cnf.buildSessionFactory();
            
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
	}
	
	public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
	
	private LegislationData(){
		instance=new LegislationData();
	}
	private static LegislationData instance;
	
	public static LegislationData getCurrent() {
		if(instance==null)
			new LegislationData();
		return instance;
	}

	public List<Oigusakt> findAllLegislations() {
		// TODO Auto-generated method stub
		return null;
	}

}
