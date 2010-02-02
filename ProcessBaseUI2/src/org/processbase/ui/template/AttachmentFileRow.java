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
package org.processbase.ui.template;

import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.ProcessBase;
import org.processbase.util.db.PbAttachment;

/**
 *
 * @author mgubaidullin
 */
public class AttachmentFileRow extends HorizontalLayout
        implements Button.ClickListener, Upload.SucceededListener, Upload.FailedListener, Upload.Receiver {

    private PbAttachment pbAttachments;
    protected ResourceBundle messages = ResourceBundle.getBundle("resources/MessagesBundle", ((ProcessBase) getApplication()).getCurrent().getLocale());
    private Button btnDelete = new Button(messages.getString("btnDelete"), this);
    private Label emptyLabel = new Label(" ");
    private Button btnFileDownload = new Button("", this);
    private Upload upload = new Upload("", this);
    private File file;
    private String MIMEType;
    private String filename;
    private boolean saved = false;
    private TextField fileDesc = new TextField("Описание");
    private ComboBox fileType = new ComboBox("Тип");

    public AttachmentFileRow(PbAttachment pbAttachments) {
        super();
        if (pbAttachments != null) {
            this.pbAttachments = pbAttachments;
            this.saved = true;
        } else {
            this.pbAttachments = new PbAttachment();
            this.saved = false;
        }
        initUI();
    }

    private void initUI() {
        setSizeFull();
        this.setSpacing(true);
        emptyLabel.setWidth("20px");
        btnFileDownload.setStyleName(Button.STYLE_LINK);
        btnDelete.setStyleName(Button.STYLE_LINK);
        if (pbAttachments.getFileSize() == null) {
            upload.setButtonCaption(messages.getString("btnUpload"));
            upload.addListener((Upload.SucceededListener) this);
            upload.addListener((Upload.FailedListener) this);
            upload.setImmediate(true);
            this.addComponent(fileType);
            this.addComponent(fileDesc);
            this.addComponent(upload);
            setComponentAlignment(upload, Alignment.MIDDLE_CENTER);
            this.setExpandRatio(upload, 1);
        } else {
            btnFileDownload.setCaption(pbAttachments.getFileName());
            this.filename = pbAttachments.getFileName();
            addComponent(btnFileDownload);
            setComponentAlignment(btnFileDownload, Alignment.MIDDLE_LEFT);
            setExpandRatio(btnFileDownload, 1);
        }

    }

    private void download() {
        try {
            ByteArraySource bas = new ByteArraySource(pbAttachments.getFileBody());
            StreamResource streamResource = new StreamResource(bas, pbAttachments.getFileName(), getApplication());
            streamResource.setCacheTime(50000); // no cache (<=0) does not work with IE8
            streamResource.setMIMEType(pbAttachments.getFileMimeType());
            getWindow().getWindow().open(streamResource, "_new");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void delete() {
        ((Layout) this.getParent()).removeComponent(this);
    }

    private void afterDownload() {
        this.removeComponent(upload);
        btnFileDownload.setCaption(filename);
        btnFileDownload.setEnabled(false);
        addComponent(btnFileDownload);
        setComponentAlignment(btnFileDownload, Alignment.MIDDLE_LEFT);
        this.addComponent(emptyLabel);
        this.addComponent(btnDelete);
        this.setExpandRatio(emptyLabel, 1);
        ((Layout) this.getParent()).addComponent(new AttachmentFileRow(null));
    }

    public void uploadSucceeded(SucceededEvent event) {
        try {
            pbAttachments = new PbAttachment();
            pbAttachments.setFileName(filename);
            pbAttachments.setFileSize(event.getLength());
            pbAttachments.setFileMimeType(MIMEType);
            byte[] readData = new byte[new Long(event.getLength()).intValue()];
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            int i = fis.read(readData);
            fis.close();
            pbAttachments.setFileBody(readData);
            afterDownload();
            file.delete();
        } catch (Exception ex) {
            this.addComponent(new Label(ex.getMessage().toString()));
        }
    }

    public void uploadFailed(FailedEvent event) {
        addComponent(new Label("Ошибка при загрузке файла " + event.getFilename() + " " + event.getReason().getMessage()));
    }

    public OutputStream receiveUpload(String filename, String MIMEType) {
        this.MIMEType = MIMEType;
        this.filename = filename;
        FileOutputStream fos = null;
        file = new File(filename);
        try {
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            Logger.getLogger(AttachmentFileRow.class.getName()).log(Level.SEVERE, e.getMessage());
            return null;
        }
        return fos;
    }

    public Button getBtnDelete() {
        return btnDelete;
    }

    public Upload getUpload() {
        return upload;
    }

    public PbAttachment getPbAttachments() {
        return pbAttachments;
    }

    public boolean isSaved() {
        return saved;
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton().equals(btnDelete)) {
            delete();
        } else if (event.getButton().equals(btnFileDownload)) {
            download();
        }
    }
}
