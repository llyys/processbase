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
package org.processbase.ui.core.template;

import org.processbase.ui.core.BPMModule;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;

/**
 *
 * @author mgubaidullin
 */
public class WorkPanel extends VerticalLayout {

    protected HorizontalLayout horizontalLayout = new HorizontalLayout();
    private boolean initialized = false;
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(WorkPanel.class);
    public WorkPanel() {
        super();
        horizontalLayout.setSizeFull();
        setSizeFull();
        addComponent(horizontalLayout);
        setExpandRatio(horizontalLayout, 1);
        setMargin(false, true, false, true);
    }

    public void initUI(){
        initialized = true;
    }

    public void windowClose(CloseEvent e) {
    }

    public void showError(String errorMessage) {
        ((PbWindow) getWindow()).showError(errorMessage);
    }

    public void showInformation(String infoMessage) {
        ((PbWindow) getWindow()).showInformation(infoMessage);
    }

    public void showImportantInformation(String infoMessage) {
        ((PbWindow) getWindow()).showImportantInformation(infoMessage);
    }

    public void showWarning(String warningMessage) {
        ((PbWindow) getWindow()).showWarning(warningMessage);
    }

    public PbWindow getPbWindow(){
        return (PbWindow) this.getWindow();
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

	public void Show() {
		// TODO Auto-generated method stub
		
	}

	public void Hide() {
		// TODO Auto-generated method stub
		
	}

    
    
}
