/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.help;

import org.processbase.ui.admin.*;
import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.MainWindow;
import org.processbase.ui.template.ButtonBar;
import org.processbase.ui.template.ByteArraySource;
import org.processbase.util.db.HibernateUtil;
import org.processbase.ui.template.PbWindow;
import org.processbase.util.XMLManager;
import org.processbase.util.db.PbActivityUi;
import org.processbase.util.db.PbHelp;

/**
 *
 * @author mgubaidullin
 */
public class HelpEditorWindow extends PbWindow implements
        Button.ClickListener,
        Upload.SucceededListener,
        Upload.FailedListener,
        Upload.Receiver {

    VerticalLayout layout = new VerticalLayout();
    private PbHelp pbHelp = null;
    private ButtonBar buttons = new ButtonBar();
    private Button cancelBtn = new Button(messages.getString("btnCancel"), this);
    private Button applyBtn = new Button(messages.getString("btnSave"), this);
    private Upload upload = new Upload("", (Upload.Receiver) this);
    private Button downloadBtn = new Button(messages.getString("btnDownload"), this);
    private File file;
    private String filename;
    private String originalFilename;
    private String fileExt;
    private final RichTextArea topEditor = new RichTextArea();
    private final RichTextArea blueEditor = new RichTextArea();
    private final RichTextArea blackEditor = new RichTextArea();
    

    public HelpEditorWindow(PbHelp pbHelp) {
        super();
        this.pbHelp = pbHelp;
    }

    public void exec() {
        try {
            setCaption(messages.getString("captionHelp") + " \"" + pbHelp.getUniqueUuid() + "\"");
            layout.setMargin(true);
            layout.setSpacing(true);
            setContent(layout);
            layout.setSizeUndefined();
            topEditor.setValue(new String(pbHelp.getTopText(), "UTF-8"));
            topEditor.setHeight("150px");
            layout.addComponent(topEditor);
            blueEditor.setValue(new String(pbHelp.getBlueText(), "UTF-8"));
            blueEditor.setHeight("150px");
            layout.addComponent(blueEditor);
            blackEditor.setValue(new String(pbHelp.getBlackText(), "UTF-8"));
            blackEditor.setHeight("150px");
            layout.addComponent(blackEditor);
            upload.setButtonCaption(messages.getString("btnUpload"));
            upload.setImmediate(true);
            upload.addListener((Upload.SucceededListener) this);
            upload.addListener((Upload.FailedListener) this);
            buttons.addComponent(upload);
            buttons.setComponentAlignment(upload, Alignment.TOP_RIGHT);
            buttons.addButton(downloadBtn);
            buttons.setComponentAlignment(downloadBtn, Alignment.TOP_RIGHT);
            buttons.addButton(applyBtn);
            buttons.setComponentAlignment(applyBtn, Alignment.TOP_RIGHT);
            buttons.addButton(cancelBtn);
            buttons.setComponentAlignment(cancelBtn, Alignment.TOP_RIGHT);
            buttons.setMargin(false);
            addComponent(buttons);
            setResizable(false);
            setModal(true);
        } catch (Exception ex) {
            Logger.getLogger(HelpEditorWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }
    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(applyBtn)) {
                pbHelp.setTopText(topEditor.getValue().toString().getBytes("UTF-8"));
                pbHelp.setBlueText(blueEditor.getValue().toString().getBytes("UTF-8"));
                pbHelp.setBlackText(blackEditor.getValue().toString().getBytes("UTF-8"));
                HibernateUtil hutil = new HibernateUtil();
                hutil.mergePbHelp(pbHelp);
                ((MainWindow) getApplication().getMainWindow()).getWorkPanel().getHelpPanel().setHelp(pbHelp);
                close();
            } else if (event.getButton().equals(downloadBtn)) {
                download();
            } else {
                close();
            }
        } catch (Exception ex) {
            Logger.getLogger(HelpEditorWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }
    }

    private void download() {
        try {
            ByteArraySource bas = new ByteArraySource(
                    XMLManager.createXML("org.processbase.util.db.PbHelp", pbHelp).getBytes("UTF-8"));
            StreamResource streamResource = new StreamResource(bas, pbHelp.getUniqueUuid() + "_help.xml", getApplication());
            streamResource.setCacheTime(50000); // no cache (<=0) does not work with IE8
            streamResource.setMIMEType("mime/xml");
            getWindow().getWindow().open(streamResource, "_new");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadSucceeded(SucceededEvent event) {
        try {
            byte[] readData = new byte[new Long(event.getLength()).intValue()];
            FileInputStream fis = new FileInputStream(file);
            int i = fis.read(readData);
            ArrayList<PbActivityUi> pbActivityUis = (ArrayList<PbActivityUi>) XMLManager.createObject(new String(readData, "UTF-8"));
            fis.close();
            file.delete();
        } catch (Exception ex) {
            Logger.getLogger(ProcessDefinitionsPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
            getWindow().showNotification(ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
        }
    }

    public void uploadFailed(FailedEvent event) {
        showError(event.getReason().getMessage());
    }

    public OutputStream receiveUpload(
            String filename, String MIMEType) {
        this.originalFilename = filename;
        this.filename = UUID.randomUUID().toString();
        FileOutputStream fos = null;
        try {
            file = new File(this.filename);
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            Logger.getLogger(ProcessDefinitionsPanel.class.getName()).log(Level.SEVERE, e.getMessage());
            return null;
        }
        return fos;
    }
}
