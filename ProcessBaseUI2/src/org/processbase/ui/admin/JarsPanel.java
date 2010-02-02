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

import com.vaadin.data.Item;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window.CloseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.util.Constants;
import org.processbase.ui.template.TableExecButton;
import org.processbase.ui.template.TablePanel;
import org.processbase.util.ProcessBaseClassLoader;

/**
 *
 * @author mgubaidullin
 */
public class JarsPanel extends TablePanel implements
        Button.ClickListener,
        Upload.SucceededListener,
        Upload.FailedListener,
        Upload.Receiver {

    private Upload upload = new Upload("", (Upload.Receiver) this);
    private File file;
    private String filename;
    private FileOutputStream fos = null;
    private String fileExt;
    public static String FILE_JAR = "FILE_JAR";
    private String fileType = null;

    public JarsPanel() {
        super();
        upload.setButtonCaption(messages.getString("btnUpload"));
        upload.setImmediate(true);
        upload.addListener((Upload.SucceededListener) this);
        upload.addListener((Upload.FailedListener) this);
        buttonBar.addComponent(upload, 1);
        buttonBar.setComponentAlignment(upload, Alignment.MIDDLE_LEFT);
        initTableUI();
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
            File folder = new File(Constants.UI_LIBS_PATH);
            File[] files = folder.listFiles();
            for (File f : files) {
                Item woItem = table.addItem(f);
                woItem.getItemProperty("name").setValue(f.getName());
                woItem.getItemProperty("size").setValue(new Long(f.length()));
                woItem.getItemProperty("actions").setValue(new TableExecButton(messages.getString("btnDelete"), "icons/document-delete.png", f, this, Constants.ACTION_DELETE));
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
            if (event.getButton() instanceof TableExecButton) {
                TableExecButton execBtn = (TableExecButton) event.getButton();
                if (execBtn.getAction().equals(Constants.ACTION_DELETE)) {
                    ProcessBaseClassLoader.free();
                    File f = (File) execBtn.getTableValue();
                    f.delete();
                    ProcessBaseClassLoader.reset();
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

    public void uploadSucceeded(SucceededEvent event) {
        try {
            if (this.fileType.equals(FILE_JAR)) {
                fos.close();
                ProcessBaseClassLoader.reset();
                ProcessBaseClassLoader.getCurrent().addFile(Constants.UI_LIBS_PATH + File.separator + this.filename);
                refreshTable();
                showWarning(messages.getString("jarUploaded") + ": " + filename);
            } else {
                showError(messages.getString("fileIsNotJar") + ": " + filename);
            }
        } catch (IOException ex) {
            Logger.getLogger(JarsPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }
    }

    public void uploadFailed(FailedEvent event) {
        showError(event.getReason().getMessage());
    }

    public OutputStream receiveUpload(
            String filename, String MIMEType) {
        try {
            this.filename = filename;
            String[] fileNameParts = filename.split("\\.");
            this.fileExt = fileNameParts.length > 0 ? fileNameParts[fileNameParts.length - 1] : null;
            if (fileExt.equalsIgnoreCase("jar")) {
                this.fileType = FILE_JAR;
                ProcessBaseClassLoader.free();
                file = new File(Constants.UI_LIBS_PATH + File.separator + this.filename);
                if (file.exists()) {
                    Logger.getLogger(JarsPanel.class.getName()).log(Level.SEVERE, "file delete = " + file.delete());
                }
                fos = new FileOutputStream(file, false);
                return fos;
            }
        } catch (final java.io.FileNotFoundException e) {
            Logger.getLogger(JarsPanel.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        return null;
    }
}
