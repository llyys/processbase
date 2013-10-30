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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.terminal.Sizeable;

/**
 *
 * @author abychkov
 */
public class Size {

    public static final int STYLE_SIZE_PX = 1;
    public static final int STYLE_SIZE_PT = 2;
    public static final int STYLE_SIZE_PR = 3;
    public static final String[] STYLE_SIZE = {"", "px", "pt", "%"};
    private Long size;
    private int unit;

    public Size(final Long size, int unit) {
	if (size == null || size < 0 || unit < 1 || unit > 3) {
	    throw new IllegalArgumentException();
	}
	this.size = size;
	this.unit = unit;
    }

    public Size(final String s) {
	Pattern p = Pattern.compile("\\s*(\\d+)\\s*(px|pt|%)",
		Pattern.CASE_INSENSITIVE & Pattern.MULTILINE);
	Matcher m = p.matcher(s);
	String tUnit;
	int tUnitIndex;
	if (!m.matches() || m.groupCount() != 2) {
	    throw new IllegalArgumentException();
	} else {
	    this.size = Long.parseLong(m.group(1));
	    this.unit = -1;
	    tUnit = m.group(2).toLowerCase();
	    for (tUnitIndex = 0; tUnitIndex < STYLE_SIZE.length; tUnitIndex++) {
		if (tUnit.equals(STYLE_SIZE[tUnitIndex])) {
		    this.unit = tUnitIndex;
		    break;
		}
	    }
	    if (this.unit == -1) {
		throw new IllegalArgumentException();
	    }
	}
    }

    @Override
    public String toString() {
	return new StringBuilder()
		.append(size.toString())
		.append(STYLE_SIZE[unit])
		.toString();
    }

    ;

    /**
     * @return the size
     */
    public final Long getSize() {
	return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(final Long size) {
	this.size = size;
    }

    /**
     * @return the unit
     */
    public int getUnit() {
	return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(int unit) {
	this.unit = unit;
    }
    
	public int getSizableUnit() {
		int u = Sizeable.UNITS_PIXELS;
		switch (unit) {
		case STYLE_SIZE_PR:
			u = Sizeable.UNITS_PERCENTAGE;
			break;
		case STYLE_SIZE_PT:
			u = Sizeable.UNITS_POINTS;
			break;
		case STYLE_SIZE_PX:
			u = Sizeable.UNITS_PIXELS;
			break;
		default:
			break;
		}
		return u;
	}
    
}
