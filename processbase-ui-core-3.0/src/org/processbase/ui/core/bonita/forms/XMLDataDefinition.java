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
package org.processbase.ui.core.bonita.forms;

/**
 *
 * @author marat
 */
public class XMLDataDefinition {

    private String id;
    private String name;
    private String type;
    private String defaultValue;
    private String scope;
    public static final String PROCESS_VARIABLE = "PROCESS_VARIABLE";
    public static final String ACTIVITY_VARIABLE = "ACTIVITY_VARIABLE";

    public XMLDataDefinition(String id, String name, String type, String scope) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.scope = scope;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    

    @Override
    public String toString() {
        return "XMLDataDefinition id=" + id + ", name=" + name + ", type=" + type + ", scope =" + scope;
    }
}
