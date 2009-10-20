/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.admin;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.Constants;
import org.processbase.ui.processes.UploadPackageWindow;
import org.processbase.ui.template.MessageWindow;
import org.processbase.ui.template.TableExecButton;
import org.processbase.ui.template.TablePanel;

/**
 *
 * @author mgubaidullin
 */
public class JarsPanel extends TablePanel implements Button.ClickListener {

    Button deployBtn = new Button(messages.getString("btnUpload"), this);
    private String libPath = System.getProperty("processbase.ui.libs");

    public JarsPanel() {
        super();
        buttonBar.addComponent(deployBtn, 0);
        initTableUI();
        refreshTable();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("name", String.class, null, messages.getString("tableCaptionFilename"), null, null);
        table.addContainerProperty("size", Long.class, null, messages.getString("tableCaptionFilesize"), null, null);
        table.setColumnWidth("size", 75);
        table.addContainerProperty("actions", TableExecButton.class, null, messages.getString("tableCaptionActions"), null, null);
        table.setColumnWidth("actions", 75);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            File folder = new File(libPath);
            File[] files = folder.listFiles();
            for (File file : files) {
                Item woItem = table.addItem(file);
                woItem.getItemProperty("name").setValue(file.getName());
                woItem.getItemProperty("size").setValue(new Long(file.length()));
//                woItem.getItemProperty("actions").setValue(new TableExecButton(messages.getString("btnDelete"), "icons/Delete.png", file, this, Constants.ACTION_DELETE));
            }
            table.setSortContainerPropertyId("name");
            table.setSortAscending(true);
            table.sort();
        } catch (Exception ex) {
            Logger.getLogger(JarsPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        try {
            if (event.getButton().equals(deployBtn)) {
                UploadPackageWindow uploadPackageWindow = new UploadPackageWindow();
                uploadPackageWindow.addListener((Window.CloseListener) this);
                getApplication().getMainWindow().addWindow(uploadPackageWindow);
            } else if (event.getButton() instanceof TableExecButton) {
                TableExecButton execBtn = (TableExecButton) event.getButton();
                if (execBtn.getAction().equals(Constants.ACTION_DELETE) //
//                        && showConfirmMessageWindow(messages.getString("areYouSure"), MessageWindow.CONFIRM_STYLE)
                        ) {
                    File file = (File) execBtn.getTableValue();
                    file.delete();
                    refreshTable();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(JarsPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }
    }

    @Override
    public void windowClose(CloseEvent e) {
        super.windowClose(e);
    }
}

