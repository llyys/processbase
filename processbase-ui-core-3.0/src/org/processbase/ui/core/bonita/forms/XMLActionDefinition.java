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
public class XMLActionDefinition {

    String type;
    String id;  
    String exprScript;
    String setVarScript;
    

    public XMLActionDefinition() {
    }

    public String getExprScript() {
        return exprScript;
    }

    public void setExprScript(String exprScript) {
        this.exprScript = exprScript;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSetVarScript() {
        return setVarScript;
    }

    public void setSetVarScript(String setVarScript) {
        this.setVarScript = setVarScript;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    
    
}
