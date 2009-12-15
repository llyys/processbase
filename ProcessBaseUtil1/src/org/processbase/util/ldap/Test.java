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
        Test test = new Test();
        test.LdapUtils("admin", null, "admin");
        ArrayList<User> g = test.getUsers("Юрист");
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

    public ArrayList<User> getUsers(String groupName) throws NamingException {
        ArrayList<User> result = new ArrayList<User>();
        NamingEnumeration<SearchResult> groupsSearch = ctx.search(Constants.BASE_GROUP_DN, "cn=" + groupName, new SearchControls());
        NamingEnumeration userIDs = groupsSearch.next().getAttributes().get("uniqueMember").getAll();
        for (; userIDs.hasMore();) {
             NamingEnumeration<SearchResult> sr = ctx.search(Constants.BASE_PEOPLE_DN, userIDs.next().toString().replaceAll(","+Constants.BASE_PEOPLE_DN, ""), new SearchControls());
            SearchResult ldapUser = sr.next();
            User user = new User(ldapUser.getAttributes().get("uid").toString(),
                    ldapUser.getAttributes().get("sn").toString(),
                    ldapUser.getAttributes().get("givenname").toString(),
                    ldapUser.getAttributes().get("cn").toString(),
                    "", "");
        }
        ctx.close();
        return result;
    }
}
