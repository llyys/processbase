package org.processbase.util.ldap;

import java.util.ArrayList;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import org.processbase.util.Constants;

/**
 *
 * @author mgubaidullin
 */
public class Test {

    Hashtable env = new Hashtable();
    DirContext ctx = null;
    String userDN = null;
    String useruuid = null;
    String password = null;

    public static void main(String[] args) throws NamingException {
        Constants.loadConstants();
        LdapUtils ldapUtils = new LdapUtils("admin", null, "admin");
            ArrayList<User> jurists = ldapUtils.getUsers("Юрист");
            for (User user : jurists) {
                System.out.println(user.getUid());
                System.out.println(user.getGivenName());
                System.out.println(user.getSn());
            }
    }

    public void LdapUtils(String useruuid, String userDN, String password) throws NamingException {
        this.env.put(Context.INITIAL_CONTEXT_FACTORY, Constants.INITIAL_CONTEXT_FACTORY);
        this.env.put(Context.PROVIDER_URL, "ldap://" + Constants.LDAP_HOST + ":" + Constants.LDAP_PORT + "/");
        this.env.put(Context.SECURITY_AUTHENTICATION, "simple");
        if (useruuid != null) {
            this.useruuid = useruuid;
            this.userDN = Constants.DN_NAMIND_ATTRIBUTE + "=" + useruuid + "," + Constants.BASE_PEOPLE_DN;
        } else if (userDN != null) {
            this.userDN = userDN;
            this.useruuid = useruuid;
        }
        this.password = password;
        env.put(Context.SECURITY_PRINCIPAL, this.userDN);
        env.put(Context.SECURITY_CREDENTIALS, this.password);
        this.ctx = new InitialDirContext(this.env);
    }

   
}
