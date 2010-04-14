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

import com.liferay.portal.model.User;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.portlet.PortletSession;
import org.processbase.bpm.BPMModule;

/**
 *
 * @author mgubaidullin
 */
public class WorkPanel extends VerticalLayout implements Button.ClickListener, Window.CloseListener {

    protected BPMModule bpmModule = null;
    protected ResourceBundle messages = null;
    protected HorizontalLayout horizontalLayout = new HorizontalLayout();
    protected ButtonBar buttonBar = new ButtonBar();
    protected Button refreshBtn = null;
    private PortletApplicationContext2 portletApplicationContext2;

    public WorkPanel(PortletApplicationContext2 portletApplicationContext2) {
        super();
        try {
            this.portletApplicationContext2 = portletApplicationContext2;
            this.messages = ResourceBundle.getBundle("resources/MessagesBundle", getCurrentLocale());
            this.bpmModule = new BPMModule(this.getCurrentUser().getScreenName());
            refreshBtn = new Button(this.messages.getString("btnRefresh"), this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // prepare help button
        buttonBar.addComponent(refreshBtn);
        buttonBar.setComponentAlignment(refreshBtn, Alignment.MIDDLE_LEFT);

        horizontalLayout.setSizeFull();
//        horizontalLayout.setStyleName(Reindeer.LAYOUT_WHITE);

        setSizeFull();
        addComponent(buttonBar);
        addComponent(horizontalLayout);
        setExpandRatio(horizontalLayout, 1);
        setMargin(false);
        setStyleName(Reindeer.LAYOUT_WHITE);
    }

    public void buttonClick(ClickEvent event) {
    }

    public void windowClose(CloseEvent e) {
    }

    public void showError(String errorMessage) {
        ((PbWindow) getWindow()).showError(errorMessage);
    }

    public void showInformation(String infoMessage) {
        ((PbWindow) getWindow()).showInformation(infoMessage);
    }

    public void showWarning(String warningMessage) {
        ((PbWindow) getWindow()).showWarning(warningMessage);
    }

    public void showMessageWindow(String message, int windowStyle) {
        if (getWindow() instanceof PbWindow) {
            ((PbWindow) getApplication().getMainWindow()).showMessageWindow(message, windowStyle);
        }
    }

    public boolean showConfirmMessageWindow(String message, int windowStyle) {
        boolean result = false;
        showMessageWindow(message, windowStyle);
//        result = this.result;
        return result;
    }

    public User getCurrentUser() {
        return ((User) portletApplicationContext2.getPortletSession().getAttribute("PROCESSBASE_USER", PortletSession.APPLICATION_SCOPE));
    }

    public Locale getCurrentLocale() {
        return (Locale) portletApplicationContext2.getPortletSession().getAttribute("org.apache.struts.action.LOCALE", PortletSession.APPLICATION_SCOPE);
    }

    public PortletApplicationContext2 getPortletApplicationContext2() {
        return portletApplicationContext2;
    }

    public PbWindow getPbWindow(){
        return (PbWindow) this.getWindow();
    }
    
}
