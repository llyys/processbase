/**
 * Copyright (C) 2011 PROCESSBASE
 * PROCESSBASE Ltd, Almaty, Kazakhstan
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
package org.processbase.ui.portlet;

import com.liferay.portal.model.User;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;
import java.util.HashMap;
import java.util.Locale;
import javax.portlet.PortletSession;
import org.processbase.ui.template.ButtonBar;
import org.processbase.ui.template.PbWindow;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import org.processbase.ui.bam.DimensionsPanel;
import org.processbase.ui.bam.DimentionWindow;
import org.processbase.ui.bam.FactWindow;
import org.processbase.ui.bam.FactsPanel;
import org.processbase.ui.bam.KPIWindow;
import org.processbase.ui.bam.KPIsPanel;
import org.processbase.ui.bam.SchemesPanel;
import org.processbase.ui.template.TablePanel;

/**
 *
 * @author mgubaidullin
 */
public class BAMPortlet extends PbPortlet
        implements Button.ClickListener, Window.CloseListener {

    private PbWindow bamWindow;
    private VerticalLayout mainLayout = new VerticalLayout();
    private ButtonBar buttonBar = new ButtonBar();
    private DimensionsPanel dimensionsPanel;
    private FactsPanel factsPanel;
    private KPIsPanel kpisPanel;
    private SchemesPanel schemesPanel;
    private Button refreshBtn = null;
    private Button btnAdd = null;
    private Button kpisBtn = null;
    private Button schemesBtn = null;
    private Button dimensionsBtn = null;
    private Button factsBtn = null;
    private HashMap<Button, TablePanel> panels = new HashMap<Button, TablePanel>();

    @Override
    public void init() {
        super.init();
        this.setTheme("processbase");
        prepareMainWindow();
    }

    private void prepareMainWindow() {

        mainLayout.setMargin(false);

        bamWindow = new PbWindow("BAM Portlet");
        bamWindow.setContent(mainLayout);
        bamWindow.setSizeFull();

        this.setMainWindow(bamWindow);

        prepareButtonBar();
        mainLayout.addComponent(buttonBar, 0);

        dimensionsPanel = new DimensionsPanel();
        panels.put(dimensionsBtn, dimensionsPanel);
        mainLayout.addComponent(dimensionsPanel, 1);
        dimensionsPanel.refreshTable();

        factsPanel = new FactsPanel();
        panels.put(factsBtn, factsPanel);

        kpisPanel = new KPIsPanel();
        panels.put(kpisBtn, kpisPanel);

        schemesPanel = new SchemesPanel();
        panels.put(schemesBtn, schemesPanel);

    }

    private void setCurrentPanel(TablePanel tablePanel) {
        mainLayout.replaceComponent(mainLayout.getComponent(1), tablePanel);
        tablePanel.refreshTable();
    }

    private void prepareButtonBar() {
        // prepare dimensionsBtn button
        dimensionsBtn = new Button(this.messages.getString("dimensions"), this);
        dimensionsBtn.setStyleName("special");
        dimensionsBtn.setEnabled(false);
        buttonBar.addComponent(dimensionsBtn, 0);
        buttonBar.setComponentAlignment(dimensionsBtn, Alignment.MIDDLE_LEFT);

        // prepare factsBtn button
        factsBtn = new Button(this.messages.getString("facts"), this);
        factsBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(factsBtn, 1);
        buttonBar.setComponentAlignment(factsBtn, Alignment.MIDDLE_LEFT);

        // prepare kpisBtn button
        kpisBtn = new Button(this.messages.getString("kpis"), this);
        kpisBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(kpisBtn, 2);
        buttonBar.setComponentAlignment(kpisBtn, Alignment.MIDDLE_LEFT);

        // prepare schemesBtn button
        schemesBtn = new Button(this.messages.getString("schemes"), this);
        schemesBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(schemesBtn, 3);
        buttonBar.setComponentAlignment(schemesBtn, Alignment.MIDDLE_LEFT);

        // add expand label
        Label expandLabel = new Label("");
        buttonBar.addComponent(expandLabel, 4);
        buttonBar.setExpandRatio(expandLabel, 1);


        // prepare add button
        btnAdd = new Button(this.messages.getString("btnAdd"), this);
        buttonBar.addComponent(btnAdd, 5);
        buttonBar.setComponentAlignment(btnAdd, Alignment.MIDDLE_RIGHT);


        // prepare refresh button
        refreshBtn = new Button(this.messages.getString("btnRefresh"), this);
        buttonBar.addComponent(refreshBtn, 6);
        buttonBar.setComponentAlignment(refreshBtn, Alignment.MIDDLE_RIGHT);

        buttonBar.setStyleName("white");
        buttonBar.setWidth("100%");
//        buttonBar.setHeight("48px");
        buttonBar.setMargin(false, true, false, true);
        buttonBar.setSpacing(true);
    }

    public User getCurrentUser() {
        return ((User) portletApplicationContext2.getPortletSession().getAttribute("PROCESSBASE_USER", PortletSession.APPLICATION_SCOPE));
    }

    public Locale getCurrentLocale() {
        return (Locale) portletApplicationContext2.getPortletSession().getAttribute("org.apache.struts.action.LOCALE", PortletSession.APPLICATION_SCOPE);
    }

    public void buttonClick(ClickEvent event) {
        TablePanel panel = panels.get(event.getButton());
        if (event.getButton().equals(refreshBtn)) {
            ((TablePanel) mainLayout.getComponent(1)).refreshTable();
        } else if (event.getButton().equals(btnAdd)) {
            addBAMConfig();
        } else {
            activateButtons();
            event.getButton().setStyleName("special");
            event.getButton().setEnabled(false);
            setCurrentPanel(panel);
        }

    }

    private void activateButtons() {
        kpisBtn.setStyleName(Reindeer.BUTTON_LINK);
        kpisBtn.setEnabled(true);
        dimensionsBtn.setStyleName(Reindeer.BUTTON_LINK);
        dimensionsBtn.setEnabled(true);
        schemesBtn.setStyleName(Reindeer.BUTTON_LINK);
        schemesBtn.setEnabled(true);
        factsBtn.setStyleName(Reindeer.BUTTON_LINK);
        factsBtn.setEnabled(true);
    }

    public void windowClose(CloseEvent e) {
        ((TablePanel) mainLayout.getComponent(1)).refreshTable();
    }

    private void addBAMConfig() {
        if (mainLayout.getComponent(1) instanceof DimensionsPanel) {
            DimentionWindow ndw = new DimentionWindow(null);
            ndw.exec();
            ndw.addListener((Window.CloseListener) this);
            getMainWindow().addWindow(ndw);
        } else if (mainLayout.getComponent(1) instanceof FactsPanel) {
            FactWindow nfw = new FactWindow(null);
            nfw.exec();
            nfw.addListener((Window.CloseListener) this);
            getMainWindow().addWindow(nfw);
        } else if (mainLayout.getComponent(1) instanceof KPIsPanel) {
            KPIWindow nkw = new KPIWindow(null);
            nkw.exec();
            nkw.addListener((Window.CloseListener) this);
            getMainWindow().addWindow(nkw);
//        } else if (mainLayout.getComponent(1) instanceof MetadataPanel) {
//            MetadataWindow nmw = new MetadataWindow(null);
//            nmw.exec();
//            nmw.addListener((Window.CloseListener) this);
//            getMainWindow().addWindow(nmw);
        }
    }
}
