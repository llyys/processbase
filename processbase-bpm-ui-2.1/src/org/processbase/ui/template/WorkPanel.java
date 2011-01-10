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
package org.processbase.ui.template;

import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;

/**
 *
 * @author mgubaidullin
 */
public class WorkPanel extends VerticalLayout {

    protected HorizontalLayout horizontalLayout = new HorizontalLayout();
    
    public WorkPanel() {
        super();
        horizontalLayout.setSizeFull();
//        horizontalLayout.setStyleName(Reindeer.LAYOUT_WHITE);
        setSizeFull();
        addComponent(horizontalLayout);
        setExpandRatio(horizontalLayout, 1);
        setMargin(false);
        setStyleName(Reindeer.LAYOUT_WHITE);
    }




    public WorkPanel(PortletApplicationContext2 portletApplicationContext2) {
        super();
//        try {
//            this.portletApplicationContext2 = portletApplicationContext2;
//            this.messages = ResourceBundle.getBundle("resources/MessagesBundle", getCurrentLocale());
//            this.bpmModule = new BPMModule(this.getCurrentUser().getScreenName());
//            refreshBtn = new Button(this.messages.getString("btnRefresh"), this);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        // prepare help button
//        buttonBar.addComponent(refreshBtn);
//        buttonBar.setComponentAlignment(refreshBtn, Alignment.MIDDLE_LEFT);

        horizontalLayout.setSizeFull();
//        horizontalLayout.setStyleName(Reindeer.LAYOUT_WHITE);

        setSizeFull();
//        addComponent(buttonBar);
        addComponent(horizontalLayout);
        setExpandRatio(horizontalLayout, 1);
        setMargin(false);
        setStyleName(Reindeer.LAYOUT_WHITE);
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
    
}
