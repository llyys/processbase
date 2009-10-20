/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.acl.persistence;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * @author mgubaidullin
 */
@Entity
@Table(name = "PBGROUPS")
public class Pbgroup implements java.io.Serializable {

    private long id;
    private String groupname;
    private String groupemail;
    private String pbtype;
    private Set<Pbuser> pbusers = new HashSet<Pbuser>(0);
    private Set<Pbrole> pbroles = new HashSet<Pbrole>(0);

    public Pbgroup() {
    }

    public Pbgroup(long id, String groupname, String pbtype) {
        this.id = id;
        this.groupname = groupname;
        this.pbtype = pbtype;
    }

    public Pbgroup(long id, String groupname, String groupemail, String pbtype, Set<Pbuser> pbusers, Set<Pbrole> pbroles) {
        this.id = id;
        this.groupname = groupname;
        this.groupemail = groupemail;
        this.pbtype = pbtype;
        this.pbroles = pbroles;
        this.pbusers = pbusers;
    }

    @Id
    @Column(name = "ID", unique = true, nullable = false, precision = 10, scale = 0)
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "GROUPNAME", nullable = false)
    public String getGroupname() {
        return this.groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    @Column(name = "GROUPEMAIL")
    public String getGroupemail() {
        return this.groupemail;
    }

    public void setGroupemail(String groupemail) {
        this.groupemail = groupemail;
    }

    @Column(name = "PBTYPE", nullable = false, length = 10)
    public String getPbtype() {
        return this.pbtype;
    }

    public void setPbtype(String pbtype) {
        this.pbtype = pbtype;
    }

    @ManyToMany(targetEntity = org.processbase.acl.persistence.Pbuser.class,
    cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "PBUSERGROUPS",
    joinColumns = @JoinColumn(name = "PBGROUP_ID"),
    inverseJoinColumns = @JoinColumn(name = "PBUSER_ID"))
    public Set<Pbuser> getPbusers() {
        return pbusers;
    }

    public void setPbusers(Set<Pbuser> pbusers) {
        this.pbusers = pbusers;
    }

    @ManyToMany(targetEntity = org.processbase.acl.persistence.Pbrole.class,
    cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "PBROLEGROUPS",
    joinColumns = @JoinColumn(name = "PBGROUP_ID"),
    inverseJoinColumns = @JoinColumn(name = "PBROLE_ID"))
    public Set<Pbrole> getPbroles() {
        return pbroles;
    }

    public void setPbroles(Set<Pbrole> pbroles) {
        this.pbroles = pbroles;
    }

    @Override
    public String toString(){
        return this.groupname;
    }
}


