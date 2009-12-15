/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.util.ldap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import org.processbase.util.Constants;
//import org.processbase.ProcessBase;

/**
 *
 * @author mgubaidullin
 */
public class LdapUtils {

    Hashtable env = new Hashtable();
    DirContext ctx = null;
    String userDN = null;
    String useruuid = null;
    String password = null;

    public LdapUtils(String useruuid, String userDN, String password) throws NamingException {
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

//    public LdapUtils() throws NamingException {
//        this.env.put(Context.INITIAL_CONTEXT_FACTORY, Constants.INITIAL_CONTEXT_FACTORY);
//        this.env.put(Context.PROVIDER_URL, "ldap://" + Constants.LDAP_HOST + ":" + Constants.LDAP_PORT + "/");
//        this.env.put(Context.SECURITY_AUTHENTICATION, "simple");
//        this.useruuid = ProcessBase.getCurrent().getUser().getUid();
//        this.userDN = Constants.DN_NAMIND_ATTRIBUTE + "=" + this.useruuid + "," + Constants.BASE_PEOPLE_DN;
//        this.password = ProcessBase.getCurrent().getUser().getPassword();
//        env.put(Context.SECURITY_PRINCIPAL, this.userDN);
//        env.put(Context.SECURITY_CREDENTIALS, this.password);
//        this.ctx = new InitialDirContext(this.env);
//    }
    public User authenticate() throws NamingException, Exception {
        User user = null;
        try {
            NamingEnumeration<SearchResult> userSearch = ctx.search(Constants.BASE_PEOPLE_DN, Constants.DN_NAMIND_ATTRIBUTE + "=" + useruuid, new SearchControls());
            SearchResult userSR = userSearch.next();
            user = new User(userSR.getAttributes().get("cn").get().toString(), userSR.getNameInNamespace());
            user.setUid(useruuid);
            user.setDn(userDN);
            user.setPassword(password);
            if (userSR.getAttributes().get("givenname") != null) {
                user.setGivenName(userSR.getAttributes().get("givenname").get().toString());
            }
            if (userSR.getAttributes().get("sn") != null) {
                user.setSn(userSR.getAttributes().get("sn").get().toString());
            }
            if (userSR.getAttributes().get("mail") != null) {
                user.setMail(userSR.getAttributes().get("mail").get().toString());
            }
            updateUserGroups(user);
//        } catch (Exception e) {
//            throw new Exception(e.getMessage());
        } catch (NamingException ex) {
            Logger.getLogger(LdapUtils.class.getName()).log(Level.SEVERE, null, ex);

//        } catch (Exception e) {
//            throw new Exception(e.getMessage());
        } finally {
            ctx.close();
        }
        return user;
    }

    public void updateUserGroups(User user) throws Exception {
        try {
            user.clear();
            NamingEnumeration<SearchResult> groupsSearch = ctx.search(Constants.BASE_GROUP_DN, "uniquemember=" + user.dn, new SearchControls());
            for (; groupsSearch.hasMore();) {
                SearchResult sr = groupsSearch.next();
                Group group = new Group(sr.getAttributes().get("cn").get().toString(),
                        sr.getNameInNamespace(),
                        sr.getAttributes().get("description").get(0).toString(),
                        sr.getAttributes().get("uniqueMember"));
                user.getGroups().add(group);
                if (sr.getAttributes().get("cn").get().equals("BPMAdmin")) {
                    user.setBpmAdmin(true);
                }
                if (sr.getAttributes().get("cn").get().equals("ACLAdmin")) {
                    user.setAclAdmin(true);
                }
                if (sr.getAttributes().get("cn").get().equals("DashboardAdmin")) {
                    user.setDashboardAdmin(true);
                }
                if (sr.getAttributes().get("cn").get().equals("HelpAdmin")) {
                    user.setHelpAdmin(true);
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        } finally {
            ctx.close();
        }
    }

//    public ArrayList<String> getUserProcessDefinitions(String login, String password) throws NamingException {
//        ArrayList<String> result = new ArrayList<String>();
//        NamingEnumeration<SearchResult> groupsSearch = ctx.search(Constants.BASE_GROUP_DN, "uniquemember=" + userDN, new SearchControls());
//        for (; groupsSearch.hasMore();) {
//            SearchResult gsr = groupsSearch.next();
//            NamingEnumeration<SearchResult> processesSearch = ctx.search(Constants.BASE_PROCESS_DN, "uniquemember=" + gsr.getNameInNamespace(), new SearchControls());
//            for (; processesSearch.hasMore();) {
//                SearchResult psr = processesSearch.next();
//                result.add(psr.getAttributes().get("cn").get(0).toString());
//            }
//        }
//        ctx.close();
//        return result;
//    }
    public ArrayList<Group> getGroups() throws NamingException {
        ArrayList<Group> result = new ArrayList<Group>();
        NamingEnumeration<SearchResult> groupsSearch = ctx.search(Constants.BASE_GROUP_DN, "(cn=*)", new SearchControls());
        for (; groupsSearch.hasMore();) {
            SearchResult gsr = groupsSearch.next();
            Group group = new Group(gsr.getAttributes().get("cn").get().toString(),
                    gsr.getNameInNamespace(),
                    gsr.getAttributes().get("description").get(0).toString(),
                    gsr.getAttributes().get("uniqueMember"));
            result.add(group);
        }
        ctx.close();
        return result;
    }

    public void changePassword(String userdn, String newPassword) throws NamingException {
        ModificationItem[] mods = new ModificationItem[1];
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword", newPassword));
        ctx.modifyAttributes(userdn, mods);
        ctx.close();
    }

    public ArrayList<User> getUsers(String groupName) throws NamingException {
        ArrayList<User> result = new ArrayList<User>();
        NamingEnumeration<SearchResult> groupsSearch = ctx.search(Constants.BASE_GROUP_DN, "cn=" + groupName, new SearchControls());
        NamingEnumeration userIDs = groupsSearch.next().getAttributes().get("uniqueMember").getAll();
        for (; userIDs.hasMore();) {
            NamingEnumeration<SearchResult> sr = ctx.search(Constants.BASE_PEOPLE_DN, userIDs.next().toString().replaceAll("," + Constants.BASE_PEOPLE_DN, ""), new SearchControls());
            SearchResult ldapUser = sr.next();
            User user = new User(ldapUser.getAttributes().get("uid").get(0).toString(),
                    ldapUser.getAttributes().get("sn").get(0).toString(),
                    ldapUser.getAttributes().get("givenname").get(0).toString(),
                    ldapUser.getAttributes().get("cn").get(0).toString(),
                    "", "");
            result.add(user);
        }
        ctx.close();
        return result;
    }
//    public void deleteProcess(String processUUID, String login, String password) {
//        try {
//            ctx.destroySubcontext("cn=" + processUUID + "," + Constants.BASE_PROCESS_DN);
//            ctx.close();
//        } catch (NamingException ex) {
//            Logger.getLogger(LdapUtils.class.getName()).log(Level.SEVERE, ex.getMessage());
//        }
//    }
//    public void addProcess(String processUUID, String processDescription, String login, String password) throws NamingException {
//        BasicAttributes attributes = new BasicAttributes();
//        attributes.put("objectclass", "groupOfUniqueNames");
//        attributes.put("description", processDescription);
//        attributes.put("cn", processUUID);
//        System.out.println(attributes);
//        ctx.createSubcontext("cn=" + processUUID + "," + Constants.BASE_PROCESS_DN, attributes);
//        ctx.close();
//    }
}
