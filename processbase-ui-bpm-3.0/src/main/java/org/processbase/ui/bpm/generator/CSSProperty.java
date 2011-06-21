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

package org.processbase.ui.bpm.generator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author abychkov
 */
public class CSSProperty {
    private String name;
    private Size width;
    private Size height;
    private String align;

    CSSProperty(String name, String group) {
	this.name = name;
	parseProperties(group);
    }
    
    private void parseProperties( CharSequence prop ) {
	Pattern p = Pattern.compile("\\s*([\\w-]+)\\s*:\\s*([^;\\s]+)\\s*;\\s*",
		Pattern.CASE_INSENSITIVE & Pattern.MULTILINE );
	Matcher m = p.matcher( prop );
	while( m.find() ){
	    String propName = m.group(1);
	    String propValue = m.group(2);
	    if( propName.equalsIgnoreCase("width") ){
		this.width = new Size(propValue);
	    } else if( propName.equalsIgnoreCase("height") ){
		this.height = new Size(propValue);
	    } else if( propName.equalsIgnoreCase("text-align") ){
		this.align = propValue;
	    }
	}
    }
    
    

    /**
     * @return the name
     */
    public String getName() {
	return name;
    }

    /**
     * @return the width
     */
    public final Size getWidth() {
	return width;
    }

    /**
     * @return the heigth
     */
    public final Size getHeigth() {
	return height;
    }

    /**
     * @return the align
     */
    public String getAlign() {
	return align;
    }
}
