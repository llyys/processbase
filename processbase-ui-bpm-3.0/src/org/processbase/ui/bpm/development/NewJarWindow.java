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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
import java.lang.reflect.Type;
import java.net.URI;
import java.util.LinkedList;
import java.util.Properties;
import java.util.UUID;
import org.processbase.ui.core.BPMModule;
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
            setCaption("Add new Tabsheet module jar");
            setModal(true);
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setStyleName(Reindeer.LAYOUT_WHITE);

            // prepare upload button
            upload.setButtonCaption(((Processbase) getApplication()).getPbMessages().getString("btnUpload"));
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
                saveJarInfo(originalFilename);
                showWarning(((Processbase) getApplication()).getPbMessages().getString("jarUploaded") + ": " + originalFilename);
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
        try {
            Properties p = System.getProperties();
            String instanceRoot = p.getProperty("com.sun.aas.instanceRootURI");
            String fileSeparator = p.getProperty("file.separator");

            File f = new File(new URI(instanceRoot + Constants.CUSTOM_UI_JAR_PATH + fileSeparator + name));
            FileOutputStream fos = null;
            fos = new FileOutputStream(f);
            fos.write(data);
            fos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveJarInfo(String name) {
        try {
            BPMModule bpm = ((Processbase) getApplication()).getBpmModule();
            // save metadata
            GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
            Gson gson = gb.create();
            Type collectionType = new TypeToken<LinkedList<String>>() {
            }.getType();
            LinkedList<String> jarList = new LinkedList<String>();
            String metaDataString = bpm.getMetaData("PROCESSBASE_UI_JAR_LIST");
            if (metaDataString != null) {
                LinkedList<String> savedJarList = gson.fromJson(metaDataString, collectionType);
                if (!savedJarList.isEmpty()) {
                    jarList.addAll(savedJarList);
                }
            }
            if (!jarList.contains(name)) {
                jarList.add(name);
            }
            metaDataString = gson.toJson(jarList, collectionType);
            bpm.addMetaData("PROCESSBASE_UI_JAR_LIST", metaDataString);
            // create rule
//            Rule rule = bpm.createRule(name, name, name, RuleType.CUSTOM);
//            Set<CustomUUID> uis = new HashSet<CustomUUID>(1);
//            uis.add(new CustomUUID(name));
//            bpm.addExceptionsToRuleByUUID(rule.getUUID(), uis);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
