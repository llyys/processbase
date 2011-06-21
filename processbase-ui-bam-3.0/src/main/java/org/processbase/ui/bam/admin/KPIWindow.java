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
package org.processbase.ui.bam.admin;

import com.vaadin.data.Item;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.themes.Reindeer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.processbase.engine.bam.command.GetAllMetaDim;
import org.processbase.engine.bam.command.GetAllMetaFact;
import org.processbase.engine.bam.command.GetAllMetaKpi;
import org.processbase.engine.bam.command.GetMetaKpiByCode;
import org.processbase.engine.bam.command.UpdateMetaKpi;
import org.processbase.ui.core.template.ButtonBar;
import org.processbase.ui.core.template.PbWindow;
import org.processbase.engine.bam.metadata.MetaDim;
import org.processbase.engine.bam.metadata.MetaFact;
import org.processbase.engine.bam.metadata.MetaKpi;
import org.processbase.ui.core.ProcessbaseApplication;

/**
 *
 * @author mgubaidullin
 */
public class KPIWindow extends PbWindow
        implements ClickListener {

    private MetaKpi metaKpi = null;
    private ButtonBar buttons = new ButtonBar();
    private Button closeBtn;
    private Button saveBtn;
    private TextField code;
    private TextField name;
    private TextField description;
    private TwinColSelect dimensions = new TwinColSelect();
    private TwinColSelect facts = new TwinColSelect();
    private GridLayout layout = new GridLayout(2, 5);

    public KPIWindow(MetaKpi metaKpi) {
        super();
        this.metaKpi = metaKpi;
    }

    public void initUI() {
        try {
            if (metaKpi == null) {
                setCaption(ProcessbaseApplication.getCurrent().getPbMessages().getString("newKPI"));
            } else {
                setCaption(ProcessbaseApplication.getCurrent().getPbMessages().getString("kpi") + " " + metaKpi.getCode());
            }
            setModal(true);
            setContent(layout);
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setStyleName(Reindeer.LAYOUT_WHITE);

            closeBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnClose"), this);
            saveBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnSave"), this);
            code = new TextField(ProcessbaseApplication.getCurrent().getPbMessages().getString("code"));
            name = new TextField(ProcessbaseApplication.getCurrent().getPbMessages().getString("name"));
            description = new TextField(ProcessbaseApplication.getCurrent().getPbMessages().getString("description"));

            code.setWidth("270px");
            code.setMaxLength(20);
            code.setRequired(true);
            code.addValidator(new RegexpValidator("^[A-Z]\\w{1,15}$", ProcessbaseApplication.getCurrent().getPbMessages().getString("codeValidatorError")));
            layout.addComponent(code, 0, 0);
            name.setWidth("275px");
            name.setMaxLength(500);
            name.setRequired(true);
            layout.addComponent(name, 1, 0);
            description.setWidth("100%");
            description.setRequired(true);
            layout.addComponent(description, 0, 1, 1, 1);

            // preparing dimensions
            ArrayList<MetaDim> metaDims = ProcessbaseApplication.getCurrent().getBpmModule().execute(new GetAllMetaDim());
            for (MetaDim metaDim : metaDims) {
                Item woItem = dimensions.addItem(metaDim);
                dimensions.setItemCaption(metaDim, metaDim.getCode() + " (" + metaDim.getName() + ")");
            }

            dimensions.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_EXPLICIT);
            dimensions.setWidth("570px");
            dimensions.setLeftColumnCaption(ProcessbaseApplication.getCurrent().getPbMessages().getString("dimensionsAvailable"));
            dimensions.setRightColumnCaption(ProcessbaseApplication.getCurrent().getPbMessages().getString("dimensionsSelected"));
            layout.addComponent(dimensions, 0, 2, 1, 2);

            // preparing facts
            ArrayList<MetaFact> metaFacts = ProcessbaseApplication.getCurrent().getBpmModule().execute(new GetAllMetaFact());
            for (MetaFact metaFact : metaFacts) {
                Item woItem = facts.addItem(metaFact);
                facts.setItemCaption(metaFact, metaFact.getCode() + " (" + metaFact.getName() + ")");
            }

            facts.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_EXPLICIT);
            facts.setWidth("570px");
            facts.setLeftColumnCaption(ProcessbaseApplication.getCurrent().getPbMessages().getString("factsAvailable"));
            facts.setRightColumnCaption(ProcessbaseApplication.getCurrent().getPbMessages().getString("factsSelected"));
            layout.addComponent(facts, 0, 3, 1, 3);

            if (metaKpi != null) {
                code.setValue(metaKpi.getCode());
                name.setValue(metaKpi.getName());
                description.setValue(metaKpi.getDescription());
                if (!metaKpi.getMetaDims().isEmpty()) {
                    HashSet<MetaDim> preselected = new HashSet<MetaDim>();
                    for (Iterator<MetaDim> iter = metaKpi.getMetaDims().iterator(); iter.hasNext();) {
                        preselected.add(findDimension(iter.next().getId()));
                    }
                    dimensions.setValue(preselected);
                }
                if (!metaKpi.getMetaFacts().isEmpty()) {
                    HashSet<MetaFact> preselected = new HashSet<MetaFact>();
                    for (Iterator<MetaFact> iter = metaKpi.getMetaFacts().iterator(); iter.hasNext();) {
                        preselected.add(findFacts(iter.next().getId()));
                    }
                    facts.setValue(preselected);
                }
                if (metaKpi.getStatus().equals(MetaKpi.NOT_EDITABLE)) {
                    code.setReadOnly(true);
                    name.setReadOnly(true);
                    description.setReadOnly(true);
                    dimensions.setReadOnly(true);
                    facts.setReadOnly(true);
                }
            } else {
                ArrayList<MetaKpi> kpis = ProcessbaseApplication.getCurrent().getBpmModule().execute(new GetAllMetaKpi());
                code.setValue("K" + String.format("%05d", new Integer(kpis.size() + 1)));
            }

            buttons.addButton(saveBtn);
            buttons.setComponentAlignment(saveBtn, Alignment.MIDDLE_RIGHT);
            buttons.setExpandRatio(saveBtn, 1);
            buttons.addButton(closeBtn);
            buttons.setComponentAlignment(closeBtn, Alignment.MIDDLE_RIGHT);
            buttons.setMargin(false);
            buttons.setHeight("30px");
            buttons.setWidth("100%");
            layout.addComponent(buttons, 0, 4, 1, 4);
//            setWidth("600px");
            setResizable(false);
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    private MetaDim findDimension(long id) {
        for (Object metaDim : dimensions.getItemIds()) {
            if (((MetaDim) metaDim).getId() == id) {
                return (MetaDim) metaDim;
            }
        }
        return null;
    }

    private MetaFact findFacts(long id) {
        for (Object metaFact : facts.getItemIds()) {
            if (((MetaFact) metaFact).getId() == id) {
                return (MetaFact) metaFact;
            }
        }
        return null;
    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(saveBtn)) {
                commit();
                if (metaKpi == null) {
                    metaKpi = new MetaKpi();
                    metaKpi.setStatus(MetaKpi.EDITABLE);
                }
                metaKpi.setCode(code.getValue().toString());
                metaKpi.setName(name.getValue().toString());
                metaKpi.setDescription(description.getValue().toString());
                metaKpi.setOwner(ProcessbaseApplication.getCurrent().getUserName());
                metaKpi.getMetaDims().clear();
                if (dimensions.getValue() instanceof Set && !((Set) dimensions.getValue()).isEmpty()) {
                    metaKpi.getMetaDims().addAll((Set) dimensions.getValue());
                }
                metaKpi.getMetaFacts().clear();
                if (facts.getValue() instanceof Set && !((Set) facts.getValue()).isEmpty()) {
                    metaKpi.getMetaFacts().addAll((Set) facts.getValue());
                }
                ArrayList<MetaKpi> x = ProcessbaseApplication.getCurrent().getBpmModule().execute(new GetMetaKpiByCode(metaKpi.getCode()));
                if (x.isEmpty() || x.get(0).getId() > 0) {
                    ProcessbaseApplication.getCurrent().getBpmModule().execute(new UpdateMetaKpi(metaKpi));
                } else {
                    throw new Exception(ProcessbaseApplication.getCurrent().getPbMessages().getString("uniqueKpiCode"));
                }
                close();
            } else if (event.getButton().equals(closeBtn)) {
                close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    private void commit() {
        for (Iterator<Component> iter = getComponentIterator(); iter.hasNext();) {
            Component c = iter.next();
            if (c instanceof AbstractField) {
                ((AbstractField) c).commit();
            }
        }
    }
}
