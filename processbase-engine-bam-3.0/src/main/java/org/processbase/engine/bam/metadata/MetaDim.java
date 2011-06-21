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
 * MetaDim 
 */

public class MetaDim implements java.io.Serializable {

    private long id;
    private String code;
    private String name;
    private String valueType;
    private Short valueLength;
    private Set<MetaKpi> metaKpis = new HashSet<MetaKpi>(0);

    public MetaDim() {
    }

    public MetaDim(long id, String code, String name, String valueType, Short valueLength) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.valueType = valueType;
        this.valueLength = valueLength;
    }

    public MetaDim(long id, String code, String name, String valueType, Short valueLength, Set<MetaKpi> metaKpis) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.valueType = valueType;
        this.valueLength = valueLength;
        this.metaKpis = metaKpis;
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

    public String getValueType() {
        return this.valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public Short getValueLength() {
        return valueLength;
    }

    public void setValueLength(Short valueLength) {
        this.valueLength = valueLength;
    }
//
    public Set<MetaKpi> getMetaKpis() {
        return this.metaKpis;
    }

    public void setMetaKpis(Set<MetaKpi> metaKpis) {
        this.metaKpis = metaKpis;
    }
}
