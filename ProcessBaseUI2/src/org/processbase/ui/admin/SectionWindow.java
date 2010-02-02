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
package org.processbase.ui.admin;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.ui.template.PbFieldFactory;
import org.processbase.ui.template.PbWindow;
import org.processbase.util.db.HibernateUtil;
import org.processbase.util.db.PbSection;

/**
 *
 * @author mgubaidullin
 */
public class SectionWindow extends PbWindow implements ClickListener {

    private PbSection section = null;
    private HorizontalLayout buttons = new HorizontalLayout();
    private Button cancelBtn = new Button(messages.getString("btnCancel"), this);
    private Button saveBtn = new Button(messages.getString("btnSave"), this);
    private boolean isNew = true;
    private Vector order = new Vector();
    private Form form = new Form();

    public SectionWindow(PbSection section) {
        super();
        this.section = section;
        if (this.section != null) {
            this.isNew = false;
        }
    }

    public void exec() {
        try {
            if (isNew) {
                section = new PbSection();
            }
            setCaption(messages.getString("Section"));
            order.add("sectionName");
            order.add("sectionDesc");
            form.setItemDataSource(new BeanItem(section));
            form.setFormFieldFactory(new PbFieldFactory(messages));
            form.setVisibleItemProperties(order);

            form.setWriteThrough(false);
            form.setInvalidCommitted(false);
            setModal(true);
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setSizeUndefined();
            buttons.setSpacing(true);
            buttons.addComponent(saveBtn);
            buttons.addComponent(cancelBtn);
            form.getLayout().addComponent(buttons);
            addComponent(form);
            setResizable(false);
        } catch (Exception ex) {
            Logger.getLogger(SectionWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }

    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(cancelBtn)) {
                form.discard();
            } else {
                form.commit();
                HibernateUtil hutil = new HibernateUtil();
                hutil.mergePbSection(section);
            }
            close();
        } catch (Exception ex) {
            Logger.getLogger(SectionWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }

    }
}
