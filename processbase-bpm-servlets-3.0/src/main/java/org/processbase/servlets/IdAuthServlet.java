package org.processbase.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.naming.Context;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.catalina.ServerFactory;
import org.apache.catalina.core.StandardServer;

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
	        	 PreparedStatement stmt = dbConnection.prepareStatement("");
	        	 stmt.setString(1, sessId);
	        	 ResultSet rs = stmt.executeQuery();
	        	 while (rs.next()) {
	        		 req.getSession(true).setAttribute("user", sessId);
	        		 Map<String, String> user=new HashMap<String,String>();
	        		 user.put("isikukood", rs.getString(3));
	        		 user.put("nimi", rs.getString(4));
	        		 user.put("perenimi", rs.getString(5));
	        		 user.put("asutus", rs.getString(6));
	        		 res.sendRedirect("/SmartBPM");
	        		 //AarPrincipal principal= new AarPrincipal(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6));
	        		 //return principal;
	     			/*String role = rs.getString(1);
	     			if (role != null) {
	     				list.add(role.trim());
	     			}*/
	     		}
	            
	        } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
	        	close(dbConnection);
	        }
	    	req.getSession(true).setAttribute("username", sessId);
	    	res.sendRedirect(req.getRemoteAddr());
	       
	    }
	    
	    protected void close(Connection dbConnection) {

	        // Do nothing if the database connection is already closed
	        if (dbConnection == null)
	            return;

	        // Commit if not auto committed
	        try {
	            if (!dbConnection.getAutoCommit()) {
	                dbConnection.commit();
	            }            
	        } catch (SQLException e) {
	            //containerLog.error("Exception committing connection before closing:", e);
	        }

	        // Close this database connection, and log any errors
	        try {
	            dbConnection.close();
	        } catch (SQLException e) {
	            //containerLog.error(sm.getString("dataSourceRealm.close"), e); // Just log it here
	        }

	    }
		
	    /**
	     * Open the specified database connection.
	     *
	     * @return Connection to the database
	     */
	    protected Connection open() {

	        try {
	            Context context = null;
	            
	                StandardServer server = 
	                    (StandardServer) ServerFactory.getServer();
	                context = server.getGlobalNamingContext();
	            DataSource dataSource = (DataSource)context.lookup("aar");
		    return dataSource.getConnection();
	        } catch (Exception e) {
	            // Log the problem for posterity
	           
	        }  
	        return null;
	    }
		
}
