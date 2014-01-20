package org.processbase.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.enterprise.inject.New;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.security.cert.CertificateException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.codeborne.security.authenticator.SmartCardAuthenticator;
import com.codeborne.security.authenticator.SmartCardUserInfo;
import org.apache.catalina.ServerFactory;
import org.apache.catalina.core.StandardServer;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.AUTH;
import org.ow2.bonita.facade.identity.ContactInfo;
import org.ow2.bonita.facade.identity.Membership;
import org.ow2.bonita.facade.identity.ProfileMetadata;
import org.ow2.bonita.facade.identity.User;

import com.google.gwt.dev.util.collect.HashMap;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.PbUser;
import org.processbase.ui.core.ProcessbaseApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class IdAuthServlet extends HttpServlet{
	 @Override    
	    public void init() throws ServletException {
	        super.init();
	    }
	    
	    @Override    
	    protected void doGet(HttpServletRequest req, HttpServletResponse res)
	            throws ServletException, IOException {
            String domain = req.getParameter("domain");
            Properties props = Constants.getJournalProperties(domain);
            JdbcTemplate jdbc = Constants.getJdbcTemplate(props);

            String sessId=req.getParameter("sid");
            String loc=req.getParameter("loc");
//            if(!loc.contains("?"))
//                loc=loc+"?";
//            else
//                loc=loc+"&";
//
//            loc=loc+"reloadApplication";

            HttpSession sess= (HttpSession) getServletContext().getAttribute(sessId);
            if(sess==null)
                sess=req.getSession();
            sess.removeAttribute("com.vaadin.terminal.gwt.server.WebApplicationContext"); //reload whole application
            SmartCardUserInfo userInfo=null;
            try {
                userInfo = SmartCardAuthenticator.getUserInfoFromRequest(req);
            } catch (CertificateException e) {
                e.printStackTrace();
                sess.setAttribute(ProcessbaseApplication.AUTH_MESSAGE, e.getMessage());

                res.sendRedirect(loc);
            }
            String dbQuery = "" +
		      "SELECT user_uuid_, user_username_, user_first_name_, user_last_name_, user_password_ from bn_user where user_username_ = ?";

            PbUser user = new PbUser();
            SqlRowSet rs = jdbc.queryForRowSet(dbQuery, userInfo.personalCode);
            if (rs.next()) {
                 user.id=rs.getString(1);
                 user.username=rs.getString(2);
                 user.status="SUCCESS";

                 user.firstName=rs.getString(3);
                 user.lastName=rs.getString(4);
                 user.name=rs.getString(3)+" "+rs.getString(4);
                 user.password=rs.getString(5);
                 user.authMethod= PbUser.AuthMethod.smart_card;

                 sess.setAttribute(org.processbase.ui.core.ProcessbaseApplication.USER, user);
                 res.sendRedirect(loc);
                 return;
            }
            else
            {
                user.status="USER_NOT_FOUND";
                sess.setAttribute(ProcessbaseApplication.USER, user);
            }
	    	res.sendRedirect(loc);
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
