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

/**
 *
 * @author mgubaidullin
 */
public class User extends Entity {

    private String givenName = null;
    private String sn = null;
    private String uid = null;
    private String password;
    private String mail;
    private boolean bpmAdmin;
    private boolean aclAdmin;
    private boolean dashboardAdmin;
    private boolean helpAdmin;
    private ArrayList<Group> groups = new ArrayList<Group>();

    public User() {
        super();
    }

    public User(String cn, String dn) {
        super("inetOrgPerson", cn, dn);
    }

    public User(String uid, String sn, String givenName, String cn, String dn, String password) {
        super("inetOrgPerson", cn, dn);
        this.givenName = givenName;
        this.sn = sn;
        this.uid = uid;
        this.password = password;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isAclAdmin() {
        return aclAdmin;
    }

    public void setAclAdmin(boolean aclAdmin) {
        this.aclAdmin = aclAdmin;
    }

    public boolean isBpmAdmin() {
        return bpmAdmin;
    }

    public void setBpmAdmin(boolean bpmAdmin) {
        this.bpmAdmin = bpmAdmin;
    }

    public boolean isDashboardAdmin() {
        return dashboardAdmin;
    }

    public void setDashboardAdmin(boolean dashboardAdmin) {
        this.dashboardAdmin = dashboardAdmin;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }

    public ArrayList<String> getGroupsDn() {
        ArrayList<String> result = new ArrayList<String>();
        for (Group group : this.groups){
            result.add(group.dn);
        }
        return result;
    }

    public boolean isHelpAdmin() {
        return helpAdmin;
    }

    public void setHelpAdmin(boolean helpAdmin) {
        this.helpAdmin = helpAdmin;
    }

    

    public void clear(){
        this.groups.clear();
        this.aclAdmin = false;
        this.bpmAdmin = false;
        this.dashboardAdmin = false;
        this.helpAdmin = false;
    }
}
