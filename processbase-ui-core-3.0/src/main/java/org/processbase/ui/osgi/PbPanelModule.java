/**
 * Copyright (C) 2011 PROCESSBASE Ltd.
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
package org.processbase.ui.osgi;

import java.util.Locale;
import org.processbase.ui.core.template.PbPanel;

/**
 *
 * @author marat
 */
public abstract class PbPanelModule extends PbPanel {

    public PbPanelModule() {

    } 
    
    private String[] roles;

    public String getName(){
        return this.getClass().getCanonicalName();
    }

    public abstract String getTitle(Locale locale);

//    public abstract PbPanel createComponent();

    protected void setModuleService(PbPanelModuleService service) {
        service.registerModule(this);
    }

    protected void unsetModuleService(PbPanelModuleService service) {
        service.unregisterModule(this);
    }

	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	public String[] getRoles() {
		return roles;
	}
}
