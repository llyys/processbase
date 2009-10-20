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
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 
 */
@Entity
@Table(name = "PBORGS")
public class Pborg implements java.io.Serializable {

    private long id;
    private Pbuser pbusers;
    private Pborg pborgs;
    private String orgName;
    private Set<Pbuser> pbuserses = new HashSet<Pbuser>(0);
    private Set<Pborg> pborgses = new HashSet<Pborg>(0);

    public Pborg() {
    }

    public Pborg(long id, String orgName) {
        this.id = id;
        this.orgName = orgName;
    }

    public Pborg(long id, Pbuser pbusers, Pborg pborgs, String orgName, Set<Pbuser> pbuserses, Set<Pborg> pborgses) {
        this.id = id;
        this.pbusers = pbusers;
        this.pborgs = pborgs;
        this.orgName = orgName;
        this.pbuserses = pbuserses;
        this.pborgses = pborgses;
    }

    @Id
    @Column(name = "ID", unique = true, nullable = false, precision = 10, scale = 0)
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PBUSER_ID")
    public Pbuser getPbusers() {
        return this.pbusers;
    }

    public void setPbusers(Pbuser pbusers) {
        this.pbusers = pbusers;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PBORG_ID")
    public Pborg getPborgs() {
        return this.pborgs;
    }

    public void setPborgs(Pborg pborgs) {
        this.pborgs = pborgs;
    }

    @Column(name = "ORG_NAME", nullable = false, length = 1000)
    public String getOrgName() {
        return this.orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "pborgs")
    public Set<Pbuser> getPbuserses() {
        return this.pbuserses;
    }

    public void setPbuserses(Set<Pbuser> pbuserses) {
        this.pbuserses = pbuserses;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "pborgs")
    public Set<Pborg> getPborgses() {
        return this.pborgses;
    }

    public void setPborgses(Set<Pborg> pborgses) {
        this.pborgses = pborgses;
    }

    @Override
    public String toString() {
        return this.orgName;
    }
}


