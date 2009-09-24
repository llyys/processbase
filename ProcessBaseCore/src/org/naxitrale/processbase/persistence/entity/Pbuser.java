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


import java.util.Date;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 */
@Entity
@Table(name = "PBUSERS", schema = "PROCESSBASE")
public class Pbuser implements java.io.Serializable {

    private long id;
    private Pborg pborgs;
    private String firstname;
    private String middlename;
    private String lastname;
    private Date birthdate;
    private String username;
    private String email;
    private String position;
    private String language;
    private String password;
    private String pbtype;
    private Set<Pborg> pborgses = new HashSet<Pborg>(0);
    private Set<Pbgroup> pbgroups = new HashSet<Pbgroup>(0);
    private Set<Pbrole> pbroles = new HashSet<Pbrole>(0);

    public Pbuser() {
    }

    public Pbuser(long id, String firstname, String lastname, String username, String email, String password, String pbtype) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.pbtype = pbtype;
    }

    public Pbuser(long id, Pborg pborgs, String firstname, String middlename, String lastname, Date birthdate, String username, String email, String position, String language, String password, String pbtype, Set<Pborg> pborgses, Set<Pbrole> pbroles, Set<Pbgroup> pbgroups) {
        this.id = id;
        this.pborgs = pborgs;
        this.firstname = firstname;
        this.middlename = middlename;
        this.lastname = lastname;
        this.birthdate = birthdate;
        this.username = username;
        this.email = email;
        this.position = position;
        this.language = language;
        this.password = password;
        this.pbtype = pbtype;
        this.pborgses = pborgses;
        this.pbgroups = pbgroups;
        this.pbroles = pbroles;
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
    @JoinColumn(name = "PBORG_ID")
    public Pborg getPborgs() {
        return this.pborgs;
    }

    public void setPborgs(Pborg pborgs) {
        this.pborgs = pborgs;
    }

    @Column(name = "FIRSTNAME", nullable = false)
    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Column(name = "MIDDLENAME")
    public String getMiddlename() {
        return this.middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    @Column(name = "LASTNAME", nullable = false)
    public String getLastname() {
        return this.lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "BIRTHDATE", length = 7)
    public Date getBirthdate() {
        return this.birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    @Column(name = "USERNAME", nullable = false)
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "EMAIL", nullable = false)
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "POSITION", length = 1000)
    public String getPosition() {
        return this.position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Column(name = "LANGUAGE")
    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Column(name = "PASSWORD", nullable = false, length = 1000)
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "PBTYPE", nullable = false, length = 10)
    public String getPbtype() {
        return this.pbtype;
    }

    public void setPbtype(String pbtype) {
        this.pbtype = pbtype;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "pbusers")
    public Set<Pborg> getPborgses() {
        return this.pborgses;
    }

    public void setPborgses(Set<Pborg> pborgses) {
        this.pborgses = pborgses;
    }

    @ManyToMany(targetEntity = org.naxitrale.processbase.persistence.entity.Pbgroup.class,
    cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "PBUSERGROUPS",
    joinColumns = @JoinColumn(name = "PBUSER_ID"),
    inverseJoinColumns = @JoinColumn(name = "PBGROUP_ID"))
    public Set<Pbgroup> getPbgroups() {
        return pbgroups;
    }

    public void setPbgroups(Set<Pbgroup> pbgroups) {
        this.pbgroups = pbgroups;
    }

    @ManyToMany(targetEntity = org.naxitrale.processbase.persistence.entity.Pbrole.class,
    cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "PBUSERROLES",
    joinColumns = @JoinColumn(name = "PBUSER_ID"),
    inverseJoinColumns = @JoinColumn(name = "PBROLE_ID"))
    public Set<Pbrole> getPbroles() {
        return pbroles;
    }

    public void setPbroles(Set<Pbrole> pbroles) {
        this.pbroles = pbroles;
    }

    @Override
    public String toString() {
        return this.lastname + " " + this.firstname + " (" + this.username + ")";
    }
}


