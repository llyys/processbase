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

/**
 *
 * @author marat
 */
public class XMLFormDefinition {

    String type;
    String id;
    String name;
    String label;
    String pageLabel;
    Integer nColumn = new Integer(1);
    Integer nLine = new Integer(1);
    String width;
    String height;
    ArrayList<XMLWidgetsDefinition> widgets;

    public XMLFormDefinition() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getnColumn() {
        return nColumn;
    }

    public void setnColumn(Integer nColumn) {
        this.nColumn = nColumn;
    }

    public Integer getnLine() {
        return nLine;
    }

    public void setnLine(Integer nLine) {
        this.nLine = nLine;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPageLabel() {
        return pageLabel;
    }

    public void setPageLabel(String pageLabel) {
        this.pageLabel = pageLabel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<XMLWidgetsDefinition> getWidgets() {
        return widgets;
    }

    public void addWidgets(XMLWidgetsDefinition widgets) {
        this.widgets.add(widgets);
        if (widgets.getLine() != null && widgets.getLine() >= this.getnLine()) {
            this.nLine = widgets.getLine() + 1;
        }
    }

    public void setWidgets(ArrayList<XMLWidgetsDefinition> widgets) {
        this.widgets = widgets;
        for (XMLWidgetsDefinition w : widgets) {
            if (w.getLine() != null && w.getLine() >= this.getnLine()) {
                this.nLine = w.getLine() + 1;
            }
        }
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }
}
