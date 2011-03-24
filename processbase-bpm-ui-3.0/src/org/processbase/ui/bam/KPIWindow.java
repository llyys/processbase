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
package org.processbase.ui.bam;

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
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.processbase.bam.metadata.HibernateUtil;
import org.processbase.bam.metadata.MetaDim;
import org.processbase.bam.metadata.MetaFact;
import org.processbase.bam.metadata.MetaKpi;
import org.processbase.ui.Processbase;
import org.processbase.ui.template.ButtonBar;
import org.processbase.ui.template.PbWindow;

/**
 *
 * @author mgubaidullin
 */
public class KPIWindow extends PbWindow
        implements ClickListener {

    private MetaKpi metaKpi = null;
    private ButtonBar buttons = new ButtonBar();
    private Button closeBtn = new Button(Processbase.getCurrent().messages.getString("btnClose"), this);
    private Button saveBtn = new Button(Processbase.getCurrent().messages.getString("btnSave"), this);
    private TextField code = new TextField(Processbase.getCurrent().messages.getString("code"));
    private TextField name = new TextField(Processbase.getCurrent().messages.getString("name"));
    private TextField description = new TextField(Processbase.getCurrent().messages.getString("description"));
    private TwinColSelect dimensions = new TwinColSelect();
    private TwinColSelect facts = new TwinColSelect();
    private GridLayout layout = new GridLayout(2, 5);

    public KPIWindow(MetaKpi metaKpi) {
        super(metaKpi == null ? Processbase.getCurrent().messages.getString("newKPI")
                : Processbase.getCurrent().messages.getString("kpi") + " " + metaKpi.getCode());
        this.metaKpi = metaKpi;
    }

    public void exec() {
        try {
            setModal(true);
            setContent(layout);
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setStyleName(Reindeer.LAYOUT_WHITE);

            code.setWidth("270px");
            code.setMaxLength(20);
            code.setRequired(true);
            code.addValidator(new RegexpValidator("^[A-Z]\\w{1,15}$", Processbase.getCurrent().messages.getString("codeValidatorError")));
            layout.addComponent(code, 0, 0);
            name.setWidth("275px");
            name.setMaxLength(500);
            name.setRequired(true);
            layout.addComponent(name, 1, 0);
            description.setWidth("100%");
            description.setRequired(true);
            layout.addComponent(description, 0, 1, 1, 1);

            HibernateUtil hutil = new HibernateUtil();

            // preparing dimensions
            ArrayList<MetaDim> metaDims = hutil.getAllMetaDim();
            for (MetaDim metaDim : metaDims) {
                Item woItem = dimensions.addItem(metaDim);
                dimensions.setItemCaption(metaDim, metaDim.getCode() + " (" + metaDim.getName() + ")");
            }

            dimensions.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_EXPLICIT);
            dimensions.setWidth("570px");
            dimensions.setLeftColumnCaption(Processbase.getCurrent().messages.getString("dimensionsAvailable"));
            dimensions.setRightColumnCaption(Processbase.getCurrent().messages.getString("dimensionsSelected"));
            layout.addComponent(dimensions, 0, 2, 1, 2);

            // preparing facts
            ArrayList<MetaFact> metaFacts = hutil.getAllMetaFact();
            for (MetaFact metaFact : metaFacts) {
                Item woItem = facts.addItem(metaFact);
                facts.setItemCaption(metaFact, metaFact.getCode() + " (" + metaFact.getName() + ")");
            }

            facts.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_EXPLICIT);
            facts.setWidth("570px");
            facts.setLeftColumnCaption(Processbase.getCurrent().messages.getString("factsAvailable"));
            facts.setRightColumnCaption(Processbase.getCurrent().messages.getString("factsSelected"));
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
                if (metaKpi.getStatus().equals(MetaKpi.NOT_EDITABLE)){
                    code.setReadOnly(true);
                    name.setReadOnly(true);
                    description.setReadOnly(true);
                    dimensions.setReadOnly(true);
                    facts.setReadOnly(true);
                }
            } else {
                code.setValue("K"+String.format("%05d", new Integer(hutil.getAllMetaKpi().size()+1)));
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
                metaKpi.setOwner(Processbase.getCurrent().getPortalUser().getScreenName());
                metaKpi.getMetaDims().clear();
                if (dimensions.getValue() instanceof Set && !((Set) dimensions.getValue()).isEmpty()) {
                    metaKpi.getMetaDims().addAll((Set) dimensions.getValue());
                }
                metaKpi.getMetaFacts().clear();
                if (facts.getValue() instanceof Set && !((Set) facts.getValue()).isEmpty()) {
                    metaKpi.getMetaFacts().addAll((Set) facts.getValue());
                }
                HibernateUtil hutil = new HibernateUtil();
                ArrayList<MetaKpi> x = hutil.getMetaKpiByCode(metaKpi.getCode());
                if (x.isEmpty() || x.get(0).getId() > 0) {
                    hutil.updateMetaKpi(metaKpi);
                } else {
                    throw new Exception(Processbase.getCurrent().messages.getString("uniqueKpiCode"));
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
