/**
 * Copyright (C) 2010 PROCESSBASE Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.processbase.util.ldap;

import java.util.ArrayList;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
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
