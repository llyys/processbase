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
package org.processbase.ui.panel;

import org.processbase.ui.Processbase;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;
import java.util.HashMap;
import org.processbase.ui.template.ButtonBar;
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
import org.processbase.ui.template.PbPanel;
import org.processbase.ui.template.TablePanel;

/**
 *
 * @author mgubaidullin
 */
public class BAMConfigurationPanel extends PbPanel
        implements Button.ClickListener, Window.CloseListener {

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

    public void initUI() {
        setMargin(false);

        prepareButtonBar();
        addComponent(buttonBar, 0);

        dimensionsPanel = new DimensionsPanel();
        panels.put(dimensionsBtn, dimensionsPanel);
        addComponent(dimensionsPanel, 1);
        setExpandRatio(dimensionsPanel, 1);
        dimensionsPanel.initUI();
        dimensionsPanel.refreshTable();

        factsPanel = new FactsPanel();
        panels.put(factsBtn, factsPanel);

        kpisPanel = new KPIsPanel();
        panels.put(kpisBtn, kpisPanel);

        schemesPanel = new SchemesPanel();
        panels.put(schemesBtn, schemesPanel);
    }

    private void setCurrentPanel(TablePanel tablePanel) {
        replaceComponent(getComponent(1), tablePanel);
        setExpandRatio(tablePanel, 1);
        if (!tablePanel.isInitialized()){
            tablePanel.initUI();
        }
        tablePanel.refreshTable();
    }

    private void prepareButtonBar() {
        // prepare dimensionsBtn button
        dimensionsBtn = new Button(((Processbase)getApplication()).getMessages().getString("dimensions"), this);
        dimensionsBtn.setStyleName("special");
        dimensionsBtn.setEnabled(false);
        buttonBar.addComponent(dimensionsBtn, 0);
        buttonBar.setComponentAlignment(dimensionsBtn, Alignment.MIDDLE_LEFT);

        // prepare factsBtn button
        factsBtn = new Button(((Processbase)getApplication()).getMessages().getString("facts"), this);
        factsBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(factsBtn, 1);
        buttonBar.setComponentAlignment(factsBtn, Alignment.MIDDLE_LEFT);

        // prepare kpisBtn button
        kpisBtn = new Button(((Processbase)getApplication()).getMessages().getString("kpis"), this);
        kpisBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(kpisBtn, 2);
        buttonBar.setComponentAlignment(kpisBtn, Alignment.MIDDLE_LEFT);

        // prepare schemesBtn button
        schemesBtn = new Button(((Processbase)getApplication()).getMessages().getString("schemes"), this);
        schemesBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(schemesBtn, 3);
        buttonBar.setComponentAlignment(schemesBtn, Alignment.MIDDLE_LEFT);

        // add expand label
        Label expandLabel = new Label("");
        buttonBar.addComponent(expandLabel, 4);
        buttonBar.setExpandRatio(expandLabel, 1);

        // prepare add button
        btnAdd = new Button(((Processbase)getApplication()).getMessages().getString("btnAdd"), this);
        buttonBar.addComponent(btnAdd, 5);
        buttonBar.setComponentAlignment(btnAdd, Alignment.MIDDLE_RIGHT);

        // prepare refresh button
        refreshBtn = new Button(((Processbase)getApplication()).getMessages().getString("btnRefresh"), this);
        buttonBar.addComponent(refreshBtn, 6);
        buttonBar.setComponentAlignment(refreshBtn, Alignment.MIDDLE_RIGHT);
        buttonBar.setWidth("100%");
    }

    private void enableButtons(){

    }
    
    public void buttonClick(ClickEvent event) {
        TablePanel panel = panels.get(event.getButton());
        if (event.getButton().equals(refreshBtn)) {
            ((TablePanel) getComponent(1)).refreshTable();
        } else if (event.getButton().equals(btnAdd)) {
            addBAMConfig();
        } else {
            activateButtons();
            event.getButton().setStyleName("special");
            event.getButton().setEnabled(false);
            setCurrentPanel(panel);
            if (getComponent(1) instanceof SchemesPanel) {
                btnAdd.setVisible(false);
            }
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
        btnAdd.setVisible(true);
    }

    public void windowClose(CloseEvent e) {
        ((TablePanel) getComponent(1)).refreshTable();
    }

    private void addBAMConfig() {
        if (getComponent(1) instanceof DimensionsPanel) {
            DimentionWindow ndw = new DimentionWindow(null);
            ndw.addListener((Window.CloseListener) this);
            getApplication().getMainWindow().addWindow(ndw);
            ndw.initUI();
        } else if (getComponent(1) instanceof FactsPanel) {
            FactWindow nfw = new FactWindow(null);
            nfw.addListener((Window.CloseListener) this);
            getApplication().getMainWindow().addWindow(nfw);
            nfw.initUI();
        } else if (getComponent(1) instanceof KPIsPanel) {
            KPIWindow nkw = new KPIWindow(null);
            nkw.addListener((Window.CloseListener) this);
            getApplication().getMainWindow().addWindow(nkw);
            nkw.initUI();
        }
    }
}
