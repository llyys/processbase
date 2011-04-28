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

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author marat
 */
public class XMLTaskDefinition {

    private String name;
    private String label;
    private boolean byPassFormsGeneration = false;

    public XMLTaskDefinition(String name, String label) {
        this.name = name;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isByPassFormsGeneration() {
        return byPassFormsGeneration;
    }

    public void setByPassFormsGeneration(boolean byPassFormsGeneration) {
        this.byPassFormsGeneration = byPassFormsGeneration;
    }


    


}
