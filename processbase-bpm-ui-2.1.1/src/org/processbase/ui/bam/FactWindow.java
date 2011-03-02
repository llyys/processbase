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
import java.util.Iterator;
import org.processbase.bam.db.HibernateUtil;
import org.processbase.bam.db.MetaFact;
import org.processbase.ui.portlet.PbPortlet;
import org.processbase.ui.template.ButtonBar;
import org.processbase.ui.template.PbWindow;

/**
 *
 * @author mgubaidullin
 */
public class FactWindow extends PbWindow
        implements ClickListener {

    private MetaFact metaFact = null;
    private ButtonBar buttons = new ButtonBar();
    private Button closeBtn = new Button(PbPortlet.getCurrent().messages.getString("btnClose"), this);
    private Button saveBtn = new Button(PbPortlet.getCurrent().messages.getString("btnSave"), this);
    private TextField code = new TextField(PbPortlet.getCurrent().messages.getString("code"));
    private TextField name = new TextField(PbPortlet.getCurrent().messages.getString("name"));

    public FactWindow(MetaFact metaFact) {
        super(metaFact == null ? PbPortlet.getCurrent().messages.getString("newFact")
                : PbPortlet.getCurrent().messages.getString("fact") + metaFact.getCode());
        this.metaFact = metaFact;
    }

    public void exec() {
        try {
            setModal(true);
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setStyleName(Reindeer.LAYOUT_WHITE);

            code.setWidth("265px");
            code.setMaxLength(20);
            code.setRequired(true);
            code.addValidator(new RegexpValidator("^[A-Z]\\w{1,15}$", PbPortlet.getCurrent().messages.getString("codeValidatorError")));
            addComponent(code);
            name.setWidth("265px");
            name.setMaxLength(500);
            name.setRequired(true);
            addComponent(name);

            if (metaFact != null) {
                code.setValue(metaFact.getCode());
                name.setValue(metaFact.getName());
            } else {
                HibernateUtil hutil = new HibernateUtil();
                code.setValue("F"+String.format("%05d", new Integer(hutil.getAllMetaFact().size()+1)));
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
            setWidth("300px");
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
                HibernateUtil hutil = new HibernateUtil();
                if (hutil.getMetaFactByCode(metaFact.getCode()).isEmpty()) {
                    hutil.addMetaFact(metaFact);
                } else {
                    throw new Exception(PbPortlet.getCurrent().messages.getString("uniqueFactCode"));
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
