/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.ui.dashboard;

/**
 *
 * @author mgubaidullin
 */
public class BarValue {

    private Number value = null;
    private Comparable rowKey = null;
    private Comparable columnKey = null;

    public BarValue(Number value, Comparable rowKey, Comparable columnKey) {
        this.value = value;
        this.columnKey = columnKey;
        this.rowKey = rowKey;
    }

    public Comparable getColumnKey() {
        return columnKey;
    }

    public void setColumnKey(Comparable columnKey) {
        this.columnKey = columnKey;
    }

    public Comparable getRowKey() {
        return rowKey;
    }

    public void setRowKey(Comparable rowKey) {
        this.rowKey = rowKey;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }
    
}
