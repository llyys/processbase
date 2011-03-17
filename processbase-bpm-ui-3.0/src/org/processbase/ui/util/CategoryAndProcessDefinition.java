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
package org.processbase.ui.util;

import org.ow2.bonita.facade.runtime.Category;
import org.ow2.bonita.light.LightProcessDefinition;

/**
 *
 * @author marat
 */
public class CategoryAndProcessDefinition {

    private Category category;
    private LightProcessDefinition processDef;

    public CategoryAndProcessDefinition(Category category, LightProcessDefinition processDef) {
        this.category = category;
        this.processDef = processDef;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LightProcessDefinition getProcessDef() {
        return processDef;
    }

    public void setProcessDef(LightProcessDefinition processDef) {
        this.processDef = processDef;
    }

    

}
