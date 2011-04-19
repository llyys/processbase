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
package org.processbase.ui.bpm.identity;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import org.ow2.bonita.facade.identity.ProfileMetadata;
import org.processbase.ui.core.Processbase;
import org.processbase.ui.core.template.ButtonBar;
import org.processbase.ui.core.template.PbWindow;

/**
 *
 * @author mgubaidullin
 */
public class MetadataWindow extends PbWindow implements ClickListener {

    private ProfileMetadata metadata = null;
    private ButtonBar buttons = new ButtonBar();
    private Button cancelBtn;
    private Button applyBtn;
    private TextField metadataName;
    private TextField metadataLabel;

    public MetadataWindow(ProfileMetadata metadata) {
        super();
        this.metadata = metadata;
    }

    public void initUI() {
        try {
            if (metadata == null) {
                setCaption(((Processbase) getApplication()).getPbMessages().getString("newMetadata"));
            } else {
                setCaption(((Processbase) getApplication()).getPbMessages().getString("metadata"));
            }
            setModal(true);
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setStyleName(Reindeer.LAYOUT_WHITE);

            cancelBtn = new Button(((Processbase) getApplication()).getPbMessages().getString("btnCancel"), this);
            applyBtn = new Button(((Processbase) getApplication()).getPbMessages().getString("btnSave"), this);
            metadataName = new TextField(((Processbase) getApplication()).getPbMessages().getString("metadataName"));
            metadataLabel = new TextField(((Processbase) getApplication()).getPbMessages().getString("metadataLabel"));

            metadataName.setWidth("270px");
            addComponent(metadataName);
            metadataLabel.setWidth("270px");
            addComponent(metadataLabel);

            if (metadata != null) {
                metadataName.setValue(metadata.getName());
                metadataLabel.setValue(metadata.getLabel());
            }


            buttons.addButton(applyBtn);
            buttons.setComponentAlignment(applyBtn, Alignment.MIDDLE_RIGHT);
            buttons.setExpandRatio(applyBtn, 1);
            buttons.addButton(cancelBtn);
            buttons.setComponentAlignment(cancelBtn, Alignment.MIDDLE_RIGHT);
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
            if (event.getButton().equals(applyBtn)) {
                if (metadata == null) {
                    ((Processbase) getApplication()).getBpmModule().addProfileMetadata(metadataName.getValue().toString(), metadataLabel.getValue().toString());
                } else {
                    ((Processbase) getApplication()).getBpmModule().updateProfileMetadataByUUID(metadata.getUUID(), metadataName.getValue().toString(), metadataLabel.getValue().toString());
                }
                close();
            } else {
                close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }
}
