/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.ui.admin;

import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.naxitrale.processbase.bpm.AdminModule;
import org.naxitrale.processbase.ui.template.MessageWindow;
import org.naxitrale.processbase.ui.template.PbWindow;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.exception.DeploymentException;
import org.ow2.bonita.facade.exception.PackageNotFoundException;
import org.ow2.bonita.facade.uuid.PackageDefinitionUUID;

/**
 *
 * @author mgubaidullin
 */
public class UploadPackageWindow extends PbWindow implements
        Upload.SucceededListener,
        Upload.FailedListener,
        Upload.Receiver {

    private Upload upload = new Upload("", this);
    private AdminModule adminModule = new AdminModule();
    private File file;
    private String MIMEType;
    private String filename;
    private PackageDefinitionUUID pdUUID = null;

    public UploadPackageWindow(PackageDefinitionUUID pdUUID) {
        super();
        this.pdUUID = pdUUID;
        initUI();
    }

    private void initUI() {
        setModal(true);
        setCaption(messages.getString("btnUpload"));
        VerticalLayout layout = (VerticalLayout) this.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeUndefined();
        upload.setButtonCaption(messages.getString("btnUpload"));
        upload.addListener((Upload.SucceededListener) this);
        upload.addListener((Upload.FailedListener) this);
        this.addComponent(upload);
        this.setResizable(false);
    }

    public void uploadSucceeded(SucceededEvent event) {
        try {
            byte[] readData = new byte[new Long(event.getLength()).intValue()];
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            int i = fis.read(readData);

            Map<String, ProcessDefinition> deployResult = new HashMap();
            String[] fileNameParts = this.filename.split("\\.");
            String fileExt = fileNameParts.length > 0 ? fileNameParts[fileNameParts.length - 1] : null;
            if (pdUUID == null && fileExt.equalsIgnoreCase("bar")) {
                deployResult = adminModule.deployBar(readData);
            } else if (pdUUID == null && fileExt.equalsIgnoreCase("xpdl")) {
                deployResult = adminModule.deployXpdl(readData);
            }
            fis.close();
            //showNotification("Загружены : ", deployResult.keySet().toString(), Notification.TYPE_HUMANIZED_MESSAGE);
            close();
            getApplication().getMainWindow().removeWindow(this);
        } catch (IOException ex) {
            showMessageWindow(ex.getMessage(), MessageWindow.ERROR_STYLE);
        } catch (DeploymentException ex) {
            showMessageWindow(ex.getMessage(), MessageWindow.ERROR_STYLE);
        }
    }

    public void uploadFailed(FailedEvent event) {
        showError(event.getReason().getMessage());
    }

    public OutputStream receiveUpload(
            String filename, String MIMEType) {
        this.MIMEType = MIMEType;
        this.filename = filename;
        FileOutputStream fos = null;
        file = new File(filename);
        try {
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            Logger.getLogger(UploadPackageWindow.class.getName()).log(Level.SEVERE, e.getMessage());
            return null;
        }
        return fos;
    }
}
