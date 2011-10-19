package org.processbase.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.New;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.catalina.ServerFactory;
import org.apache.catalina.core.StandardServer;
import org.apache.http.auth.AUTH;
import org.ow2.bonita.facade.identity.ContactInfo;
import org.ow2.bonita.facade.identity.Membership;
import org.ow2.bonita.facade.identity.ProfileMetadata;
import org.ow2.bonita.facade.identity.User;

import com.google.gwt.dev.util.collect.HashMap;

public class IdAuthServlet extends HttpServlet{
	 @Override    
	    public void init() throws ServletException {
	        super.init();	        
	    }
	    
	    @Override    
	    protected void doGet(HttpServletRequest req, HttpServletResponse res)
	            throws ServletException, IOException {
	    	String sessId=req.getParameter("session_id");
	    	Connection dbConnection = null;
	    	req.getSession().invalidate();
			String dbQuery = "" +
		      "SELECT " +
		      "distinct a_asutus.asutus_id, " +
		      "sessions.personal_code, " +
		      "sessions.givenname, " +
		      "sessions.surname, " +
		      "a_asutus.nimetus, " +
		      "a_asutus.registrikood," +
		      "(SELECT asutused.isright('VOLIS','PEAKASUTAJA', CAST( a_asutus.asutus_id AS text), CAST(a_ametikoht.ametikoht_id AS text) )) AS peakasutaja " +
		      " FROM " +
		      "pwa.sessions AS sessions " +
		      "LEFT JOIN " +
		      "asutused.isik AS a_isik ON a_isik.kood = sessions.personal_code " +
		      "LEFT JOIN " +
		      "asutused.ametikoht_taitmine AS a_taitmine ON a_taitmine.i_id = a_isik.i_id " +
		      "LEFT JOIN asutused.ametikoht AS a_ametikoht ON a_ametikoht.ametikoht_id = a_taitmine.ametikoht_id " +
		      "LEFT JOIN asutused.asutus AS a_asutus ON a_asutus.asutus_id = a_ametikoht.asutus_id " +
		      "WHERE (channel = 'ID' OR channel = 'M-ID') AND valid_to > NOW() AND session_id = ? " +
		      "AND (a_taitmine.kuni >= NOW() OR a_taitmine.kuni IS NULL) ";
	        // Ensure that we have an open database connection
	        dbConnection = open();
	        if (dbConnection == null) {
	            return;
	        }
	        try {
	        	 PreparedStatement stmt = dbConnection.prepareStatement(dbQuery);
	        	 stmt.setString(1, sessId);
	        	 ResultSet rs = stmt.executeQuery();
	        	 
	        	 while (rs.next()) {
	        		 
	        		 BonitaUser user=new BonitaUser(rs.getString(2), rs.getString(3), rs.getString(4));
	        		 req.getSession(true).setAttribute(org.processbase.ui.core.ProcessbaseApplication.AUTH_KEY, user);
	        		 res.sendRedirect(getUrl3(req));
	        		 return;
	     		}
	            
	        } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
	        	close(dbConnection);
	        }
	    	req.getSession(true);
	    	res.sendRedirect(getUrl3(req));
	    	
	    }
	    
	    protected void close(Connection dbConnection) {
	        if (dbConnection == null)
	            return;
	        try {
	            if (!dbConnection.getAutoCommit()) {
	                dbConnection.commit();
	            }            
	        } catch (SQLException e) {
	        }

	        try {
	            dbConnection.close();
	        } catch (SQLException e) {
	        }

	    }
		
	    /**
	     * Open the specified database connection.
	     *
	     * @return Connection to the database
	     */
	    protected Connection open() {

	        try {
	        	Context ctx = new InitialContext();
	            if(ctx == null ) 
	                throw new Exception("Boom - No Context");

	            DataSource ds = (DataSource)ctx.lookup("java:comp/env/aar");
		    return ds.getConnection();
	        } catch (Exception e) {
	           System.out.println(e.getMessage());	           
	        }  
	        return null;
	    }
	    
	    public static String getUrl3(HttpServletRequest req) {
	        String scheme = req.getScheme();             // http
	        String serverName = req.getServerName();     // hostname.com
	        int serverPort = req.getServerPort();        // 80
	        String contextPath = req.getContextPath();   // /mywebapp
	        String servletPath = req.getServletPath();   // /servlet/MyServlet
	        String pathInfo = req.getPathInfo();         // /a/b;c=123
	        String queryString = req.getQueryString();          // d=789

	        // Reconstruct original requesting URL
	        return scheme+"://"+serverName+":"+serverPort+contextPath;
	        /*String url = scheme+"://"+serverName+":"+serverPort+contextPath+servletPath;
	        if (pathInfo != null) {
	            url += pathInfo;
	        }
	        if (queryString != null) {
	            url += "?"+queryString;
	        }
	        return url;*/
	    }
		
}
