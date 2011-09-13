package org.processbase.cmis;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.auth.OrganizationAuthenticatorImpl;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.Credential;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
import org.exoplatform.services.security.PasswordCredential;
import org.exoplatform.services.security.UsernameCredential;

public class LiferayAuthenticator implements Authenticator {

	  public static final String GROUPNAME_ADMINISTRATORS = "administrators";
	  public Authenticator defaultAuth;
	  
	  /**
	   * 
	   */
	  public LiferayAuthenticator(OrganizationService organizationService) {
	    defaultAuth = new OrganizationAuthenticatorImpl(organizationService);
	  }
	  
	  /*
	   * (non-Javadoc)
	   * 
	   * @see
	   * org.exoplatform.services.security.Authenticator#validateUser(org.exoplatform
	   * .services.security.Credential[])
	   */
	  public String validateUser(Credential[] credentials) throws LoginException, Exception {
	    String user = null;
	    String passwordHash = null;
	    PasswordCredential pswCrd = null;
	    for (Credential cred : credentials) {
	      if (cred instanceof UsernameCredential) {
	        user = ((UsernameCredential) cred).getUsername();
	      }
	      if (cred instanceof PasswordCredential) {
	        pswCrd = (PasswordCredential) cred;
	        passwordHash = pswCrd.getPassword();
	      }
	    }
	    if (user == null || "".equals(user)) {
	      throw new LoginException("No user specified");
	    } else {
	      if (passwordHash == null || "".equals(passwordHash)) {
	        throw new LoginException("No password specified for user: " + user);
	      }
	    }
	    int index;
	    if ((index = user.lastIndexOf('#')) > -1 && user.length()>index) {
	      String username = user.substring(0, index);
	      String bonitaUser = user.substring(index+1);
	      Credential[] cmisCredentials = new Credential[]{new UsernameCredential(username),pswCrd};
	      
	      defaultAuth.validateUser(cmisCredentials);//will throw login exception
	      return bonitaUser;
	    } else{
	      return defaultAuth.validateUser(credentials);
	    }

	    
	  }

	  /*
	   * (non-Javadoc)
	   * 
	   * @see
	   * org.exoplatform.services.security.Authenticator#createIdentity(java.lang
	   * .String)
	   */
	  public Identity createIdentity(String userId) throws Exception {
	    Set<String> roles = new HashSet<String>(1);
	    roles.add(GROUPNAME_ADMINISTRATORS);
	    return new Identity(userId, new HashSet<MembershipEntry>(), roles);//not every ones should be admin
	  }

}
