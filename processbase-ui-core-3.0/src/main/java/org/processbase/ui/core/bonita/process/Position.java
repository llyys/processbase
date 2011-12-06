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

package org.processbase.ui.core.bonita.process;

/**
 *
 * @author abychkov
 */
public class Position {
    private int from_c = -1;
    private int from_r = -1;
    private int to_c = 1;
    private int to_r = 1;

    public Position( int column, int row ){
	this.from_c = column;
	this.from_r = row;
    }

    public Position( int fCol, int fRow, int tCol, int tRow ){
	this.from_c = fCol;
	this.from_r = fRow;
	this.to_c = tCol;
	this.to_r = tRow;
    }

    /**
     * @return the column
     */
    public int getFColumn() {
	return from_c;
    }

    /**
     * @param column the column to set
     */
    public void setFColumn(int column) {
	this.from_c = column;
    }

    /**
     * @return the row
     */
    public int getFRow() {
	return from_r;
    }

    /**
     * @param fRow the row to set
     */
    public void setFRow(int fRow) {
	this.from_r = fRow;
    }

    /**
     * @return the height
     */
    public int getTRow() {
	return to_r;
    }

    /**
     * @param tRow the height to set
     */
    public void setTRow(int tRow) {
	this.to_r = tRow;
    }

    /**
     * @return the width
     */
    public int getTColumn() {
	return to_c;
    }

    /**
     * @param tCol the width to set
     */
    public void setTCol(int tCol) {
	this.to_c = tCol;
    }
}
