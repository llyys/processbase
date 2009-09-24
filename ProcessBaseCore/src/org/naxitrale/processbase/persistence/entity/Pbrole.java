/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.persistence.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 
 */
@Entity
@Table(name = "PBROLES", schema = "PROCESSBASE")
public class Pbrole implements java.io.Serializable {

    private long id;
    private String rolename;
    private String pbtype;
    private Set<Pbuser> pbusers = new HashSet<Pbuser>(0);
    private Set<Pbgroup> pbgroups = new HashSet<Pbgroup>(0);
    private Set<String> processes = new HashSet<String>(0);

    public Pbrole() {
    }

    public Pbrole(long id, String rolename, String pbtype) {
        this.id = id;
        this.rolename = rolename;
        this.pbtype = pbtype;
    }

    public Pbrole(long id, String rolename, String pbtype, Set<Pbuser> pbusers, Set<Pbgroup> pbgroups) {
        this.id = id;
        this.rolename = rolename;
        this.pbtype = pbtype;
        this.pbusers = pbusers;
        this.pbgroups = pbgroups;
    }

    @Id
    @Column(name = "ID", unique = true, nullable = false, precision = 10, scale = 0)
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "ROLENAME", nullable = false)
    public String getRolename() {
        return this.rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    @Column(name = "PBTYPE", nullable = false, length = 10)
    public String getPbtype() {
        return this.pbtype;
    }

    public void setPbtype(String pbtype) {
        this.pbtype = pbtype;
    }

    @ManyToMany(targetEntity = org.naxitrale.processbase.persistence.entity.Pbgroup.class,
    cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "PBROLEGROUPS",
    joinColumns = @JoinColumn(name = "PBROLE_ID"),
    inverseJoinColumns = @JoinColumn(name = "PBGROUP_ID"))
    public Set<Pbgroup> getPbgroups() {
        return pbgroups;
    }

    public void setPbgroups(Set<Pbgroup> pbgroups) {
        this.pbgroups = pbgroups;
    }

    @ManyToMany(targetEntity = org.naxitrale.processbase.persistence.entity.Pbuser.class,
    cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "PBUSERROLES",
    joinColumns = @JoinColumn(name = "PBROLE_ID"),
    inverseJoinColumns = @JoinColumn(name = "PBUSER_ID"))
    public Set<Pbuser> getPbusers() {
        return pbusers;
    }

    public void setPbusers(Set<Pbuser> pbusers) {
        this.pbusers = pbusers;
    }

//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Set<String> getProcesses() {
        return this.processes;
    }

    public void setProcesses(Set<String> processes) {
        this.processes = processes;
    }
}


