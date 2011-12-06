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

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import net.htmlparser.jericho.*;

/**
 *
 * @author abychkov
 */
public class TableStyle {
    private int rows;
    private int columns;
    private HashMap<String, ComponentStyle> elements;
    private int[] _span;

    private HashMap<String, CSSProperty> classes;

    public TableStyle( CharSequence html, CharSequence css ){
	this.classes = new HashMap<String, CSSProperty>();
	parseCSS( css );
	this.elements = new HashMap<String, ComponentStyle>();
	parseXHTML( html );
    }

    public HashMap<String, CSSProperty> getClasses() {
        return classes;
    }

    public void setClasses(HashMap<String, CSSProperty> classes) {
        this.classes = classes;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public HashMap<String, ComponentStyle> getElements() {
        return elements;
    }

    public void setElements(HashMap<String, ComponentStyle> elements) {
        this.elements = elements;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }
    
    

    private void parseCSS( CharSequence in ){
	Pattern p = Pattern.compile("\\s*(\\.\\w+)\\s*[{]([^}]*)[}]",
		Pattern.CASE_INSENSITIVE & Pattern.MULTILINE );
	Matcher m = p.matcher( in );
	while( m.find() ){
	    this.classes.put( m.group(1), new CSSProperty( m.group(1), m.group(2) ) );
	}
    }

    private void parseXHTML(CharSequence html) {
	Source src = new Source(html);
	List<Element> tables = src.getAllElements(HTMLElementName.TABLE);
	for (Element e : tables) {
	    String _class = e.getAttributeValue("class");
	    if(_class!=null&&_class.equalsIgnoreCase("bonita_table_container")){
		parseBonitaTable( e );
	    }
	}
    }

    private void parseBonitaTable(Element e) {
	List<Element> _tr = e.getAllElements(HTMLElementName.TR);
	List<Element> _cg = e.getAllElements(HTMLElementName.COLGROUP);
	if( _tr != null ){ 
	    this.rows = _tr.size();
	}
	if( _cg != null ){ 
	    List<Element> _col = _cg.get(0).getChildElements();
	    if( _col != null ){
		this.columns = _col.size();
		this._span = new int[this.columns];
		for (int t : this._span) {
		    t = 0;
		}
	    }
	}
	int i = 0;
	for (Element el : _tr) {
	    parseBonitaTableTR( el, i );
	    i++;
	}
    }

    private void parseBonitaTableTR(Element e, int rPos ) {
	int cPos = 0;
	List<Element> _td = e.getAllElements(HTMLElementName.TD);
	for (Element cell : _td) {
	    String _hCell = cell.getAttributeValue("rowspan");
	    int hCell = Integer.parseInt( (_hCell==null)?"1":_hCell );
	    String _wCell = cell.getAttributeValue("colspan");
	    int wCell = Integer.parseInt( (_wCell==null)?"1":_wCell );
	    cPos = fixCellPosition( cPos, hCell, wCell );
	    String _class = inputClass(cell.getAttributeValue("class") );
	    String _name = null;
	    List<Element> _div = cell.getAllElements(HTMLElementName.DIV);
	    for (Element d : _div) {
		_name = d.getAttributeValue("id");
		break;
	    }

	    if(_name != null){
		ComponentStyle s = new ComponentStyle(null);
		s.setPosition( new Position(cPos,rPos,cPos+wCell-1,rPos+hCell-1) );
		s.setCss( classes.get(_class) );
		s.setName(_name);
		this.elements.put(s.getName(), s);
	    }

	    cPos += wCell;
	}
    }

    private int fixCellPosition(int cPos, int hCell, int wCell) {
	int vPos = cPos;
	if( this._span[vPos] != 0 ){
	    this._span[vPos]--;
	    vPos = fixCellPosition( vPos++, hCell, wCell );
	}
	if( hCell > 1 ){
	    for( int i = vPos; i < vPos + wCell; i++ ){
		this._span[i] += hCell - 1;
	    }
	}
	return vPos;
    }

    private String inputClass(String clss) {
	String _inp_class = null;
	if( clss != null ){
	    String[] _list_cls = clss.split("\\s+");
	    for (String s : _list_cls) {
		if( s.startsWith("widget_") ){
		    _inp_class = ".input" + s.substring(6);
		    break;
		}
	    }
	}
	return _inp_class;
    }


}
