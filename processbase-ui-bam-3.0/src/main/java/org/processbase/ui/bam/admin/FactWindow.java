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
package org.processbase.ui.bam.admin;

import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import java.util.ArrayList;
import java.util.Iterator;
import org.processbase.engine.bam.command.AddMetaFact;
import org.processbase.engine.bam.command.GetAllMetaFact;
import org.processbase.engine.bam.command.GetMetaFactByCode;
import org.processbase.ui.core.template.ButtonBar;
import org.processbase.ui.core.template.PbWindow;
import org.processbase.engine.bam.metadata.MetaFact;
import org.processbase.ui.core.ProcessbaseApplication;

/**
 *
 * @author mgubaidullin
 */
public class FactWindow extends PbWindow
        implements ClickListener {

    private MetaFact metaFact = null;
    private ButtonBar buttons = new ButtonBar();
    private Button closeBtn;
    private Button saveBtn;
    private TextField code;
    private TextField name;

    public FactWindow(MetaFact metaFact) {
        super();
        this.metaFact = metaFact;
    }

    public void initUI() {
        try {
            if (metaFact == null) {
                setCaption(ProcessbaseApplication.getCurrent().getPbMessages().getString("newFact"));
            } else {
                setCaption(ProcessbaseApplication.getCurrent().getPbMessages().getString("fact") + metaFact.getCode());
            }
            setModal(true);
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setStyleName(Reindeer.LAYOUT_WHITE);

            closeBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnClose"), this);
            saveBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnSave"), this);
            code = new TextField(ProcessbaseApplication.getCurrent().getPbMessages().getString("code"));
            name = new TextField(ProcessbaseApplication.getCurrent().getPbMessages().getString("name"));

            code.setWidth("265px");
            code.setMaxLength(20);
            code.setRequired(true);
            code.addValidator(new RegexpValidator("^[A-Z]\\w{1,15}$", ProcessbaseApplication.getCurrent().getPbMessages().getString("codeValidatorError")));
            addComponent(code);
            name.setWidth("265px");
            name.setMaxLength(500);
            name.setRequired(true);
            addComponent(name);

            if (metaFact != null) {
                code.setValue(metaFact.getCode());
                name.setValue(metaFact.getName());
            } else {
                ArrayList<MetaFact> metaFacts = ProcessbaseApplication.getCurrent().getBpmModule().execute(new GetAllMetaFact());
                code.setValue("F" + String.format("%05d", new Integer(metaFacts.size() + 1)));
            }

            buttons.addButton(saveBtn);
            buttons.setComponentAlignment(saveBtn, Alignment.MIDDLE_RIGHT);
            buttons.setExpandRatio(saveBtn, 1);
            buttons.addButton(closeBtn);
            buttons.setComponentAlignment(closeBtn, Alignment.MIDDLE_RIGHT);
            buttons.setMargin(false);
            buttons.setHeight("30px");
            buttons.setWidth("100%");
            addComponent(buttons);
            setWidth("310px");
            setResizable(false);
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(saveBtn)) { 
                commit();
                metaFact = new MetaFact();
                metaFact.setCode(code.getValue().toString());
                metaFact.setName(name.getValue().toString());
                ArrayList<MetaFact> metaFacts = ProcessbaseApplication.getCurrent().getBpmModule().execute(new GetMetaFactByCode(metaFact.getCode()));
                if (metaFacts.isEmpty()) {
                     ProcessbaseApplication.getCurrent().getBpmModule().execute(new AddMetaFact(metaFact));
                } else {
                    throw new Exception(ProcessbaseApplication.getCurrent().getPbMessages().getString("uniqueFactCode"));
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
