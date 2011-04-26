/**
 * Copyright (C) 2011 PROCESSBASE Ltd.
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

package org.processbase.engine.bam.metadata;

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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * MetaFact
 */
@Entity
@Table(name = "META_FACT", uniqueConstraints =
@UniqueConstraint(columnNames = "CODE"))
public class MetaFact implements java.io.Serializable {

    private long id;
    private String code;
    private String name;
    private Set<MetaKpi> metaKpis = new HashSet<MetaKpi>(0);

    public MetaFact() {
    }

    public MetaFact(long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public MetaFact(long id, String code, String name, Set<MetaKpi> metaKpis) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.metaKpis = metaKpis;
    }

    @Id
    @Column(name = "ID", unique = true, nullable = false, precision = 16, scale = 0)
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "CODE", nullable = false, length = 16)
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "NAME", nullable = false, length = 200)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "META_KPI_FACT", schema = "PBBAM2", joinColumns = {
        @JoinColumn(name = "FACT_ID", nullable = false, updatable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "KPI_ID", nullable = false, updatable = false)})
    public Set<MetaKpi> getMetaKpis() {
        return this.metaKpis;
    }

    public void setMetaKpis(Set<MetaKpi> metaKpis) {
        this.metaKpis = metaKpis;
    }
}
