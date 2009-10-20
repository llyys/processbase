/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.processes;

import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.ProcessBase;
import org.processbase.bpm.AdminModule;
import org.processbase.ui.template.MessageWindow;
import org.processbase.ui.template.PbWindow;
import org.processbase.util.ProcessBaseClassLoader;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.exception.DeploymentException;

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
    private String filename;
    private String originalFilename;
    private String fileExt;
    public static String FILE_XPDL = "FILE_XPDL";
    public static String FILE_BAR = "FILE_BAR";
    public static String FILE_JAR = "FILE_JAR";
    private String fileType = null;

    public UploadPackageWindow() {
        super();
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
            if (this.fileType.equals(FILE_BAR)) {
                deployResult = adminModule.deployBar(readData);
            } else if (this.fileType.equals(FILE_XPDL)) {
                deployResult = adminModule.deployXpdl(readData);
            } else if (this.fileType.equals(FILE_JAR)) {
                FileOutputStream fos = new FileOutputStream(new File(System.getProperty("processbase.ui.libs"), this.originalFilename));
                fos.write(readData);
                fos.close();
                ((ProcessBase) getApplication()).processBaseClassLoader.addFile(System.getProperty("processbase.ui.libs") + File.separator + this.originalFilename);
            }
            fis.close();
            file.delete();
            //showNotification("Загружены : ", deployResult.keySet().toString(), Notification.TYPE_HUMANIZED_MESSAGE);
            close();
            getApplication().getMainWindow().removeWindow(this);
        } catch (IOException ex) {
            Logger.getLogger(UploadPackageWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            showMessageWindow(ex.getMessage(), MessageWindow.ERROR_STYLE);
        } catch (DeploymentException ex) {
            Logger.getLogger(UploadPackageWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            showMessageWindow(ex.getMessage(), MessageWindow.ERROR_STYLE);
        }
    }

    public void uploadFailed(FailedEvent event) {
        showError(event.getReason().getMessage());
    }

    public OutputStream receiveUpload(
            String filename, String MIMEType) {
        this.originalFilename = filename;
        this.filename = UUID.randomUUID().toString();
        String[] fileNameParts = originalFilename.split("\\.");
        this.fileExt = fileNameParts.length > 0 ? fileNameParts[fileNameParts.length - 1] : null;
        if (fileExt.equalsIgnoreCase("bar")) {
            this.fileType = FILE_BAR;
        } else if (fileExt.equalsIgnoreCase("xpdl")) {
            this.fileType = FILE_XPDL;
        } else if (fileExt.equalsIgnoreCase("jar")) {
            this.fileType = FILE_JAR;
        }
        FileOutputStream fos = null;
        file = new File(this.filename);
        try {
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            Logger.getLogger(UploadPackageWindow.class.getName()).log(Level.SEVERE, e.getMessage());
            return null;
        }
        return fos;
    }
}
