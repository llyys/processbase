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
package org.processbase.ui.bpm.development;

import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.Processbase;
import org.processbase.ui.core.template.PbWindow;

/**
 *
 * @author mgubaidullin
 */
public class NewJarWindow extends PbWindow
        implements Upload.SucceededListener,
        Upload.FailedListener,
        Upload.Receiver {

    private Upload upload = new Upload("", (Upload.Receiver) this);
    private File file;
    private String filename;
    private String originalFilename;
    private String fileExt;
    public static String FILE_JAR = "FILE_JAR";
    private String fileType = null;

    public NewJarWindow() {
        super();
    }

    public void initUI() {
        try {
            setCaption(((Processbase) getApplication()).getMessages().getString("newProcessDefinition"));
            setModal(true);
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setStyleName(Reindeer.LAYOUT_WHITE);

            // prepare upload button
            upload.setButtonCaption(((Processbase) getApplication()).getMessages().getString("btnUpload"));
            upload.addListener((Upload.SucceededListener) this);
            upload.addListener((Upload.FailedListener) this);
            addComponent(upload);

            setWidth("350px");
            setResizable(false);
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    public void uploadSucceeded(SucceededEvent event) {
        try {
            byte[] readData = new byte[new Long(event.getLength()).intValue()];
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            int i = fis.read(readData);
            fis.close();
            if (this.fileType.equals(FILE_JAR)) {
                saveJar(originalFilename, readData);
                showWarning(((Processbase) getApplication()).getMessages().getString("jarUploaded") + ": " + originalFilename);
            }
            file.delete();
            close();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
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
        if (fileExt.equalsIgnoreCase("jar")) {
            this.fileType = FILE_JAR;
        }
        FileOutputStream fos = null;
        file = new File(this.filename);
        try {
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
        return fos;
    }

    private void saveJar(String name, byte[] data) throws IOException {
        File f = new File( name);
        System.out.println(File.listRoots()[0].getAbsolutePath());
        System.out.println(File.listRoots()[0].getPath());
        System.out.println(File.listRoots()[0].getCanonicalPath());
        System.out.println(File.listRoots()[0].getParent());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(data);
            fos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
