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

/**
 * MetaKpi 
 */

public class MetaKpi implements java.io.Serializable {

    private long id;
    private String code;
    private String name;
    private String description;
    private String owner;
    private String status;
    private Set<MetaDim> metaDims = new HashSet<MetaDim>(0);
    private Set<MetaFact> metaFacts = new HashSet<MetaFact>(0);
    public static final String EDITABLE = "EDITABLE";
    public static final String NOT_EDITABLE = "NOT_EDITABLE";

    public MetaKpi() {
    }

    public MetaKpi(long id, String code, String name, String owner, String status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.owner = owner;
        this.status = status;
    }

    public MetaKpi(long id, String code, String name, String description, String owner, Set<MetaDim> metaDims, Set<MetaFact> metaFacts) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.metaDims = metaDims;
        this.metaFacts = metaFacts;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<MetaDim> getMetaDims() {
        return this.metaDims;
    }

    public void setMetaDims(Set<MetaDim> metaDims) {
        this.metaDims = metaDims;
    }

    public Set<MetaFact> getMetaFacts() {
        return this.metaFacts;
    }

    public void setMetaFacts(Set<MetaFact> metaFacts) {
        this.metaFacts = metaFacts;
    }
}
