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
package org.processbase.acl;

import java.io.File;
import java.io.FileInputStream;
import javax.naming.NamingException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.ow2.bonita.definition.RoleMapper;
import org.ow2.bonita.facade.QueryAPIAccessor;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;

/**
 *
 * @author mgubaidullin
 */
public class ProcessBaseRoleMapper implements RoleMapper {

    public static Properties properties = new Properties();
    public Hashtable env = null;
    public DirContext ctx = null;

    public Set<String> searchMembers(QueryAPIAccessor queryAPIAccessor, ProcessInstanceUUID piUUID, String rolename) throws Exception {
        HashSet<String> result = new HashSet<String>();
        return result;
    }

    public void loadProperties() {
        try {
            File file = new File("processbase1.properties");
            FileInputStream fis = new FileInputStream(file);
            properties.loadFromXML(fis);
            fis.close();
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    public void connect() throws NamingException {
        env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://" + properties.getProperty("LDAP_HOST") + ":" + properties.getProperty("LDAP_PORT") + "/");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, properties.getProperty("LDAP_ADMIN_USERNAME"));
        env.put(Context.SECURITY_CREDENTIALS, properties.getProperty("LDAP_ADMIN_PASSWORD"));
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        ctx = new InitialDirContext(this.env);
    }
}
