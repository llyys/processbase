/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.template;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.util.db.HibernateUtil;
import org.processbase.util.db.PbAttachment;

/**
 *
 * @author mgubaidullin
 */
public class AttachmentBar extends VerticalLayout {

    public AttachmentBar() {
        super();
        this.setSpacing(true);
    }

    public void load(String processUUID, String activityUUID) {
        Logger.getLogger(AttachmentBar.class.getName()).log(Level.SEVERE, "processUUID = " + processUUID);
        Logger.getLogger(AttachmentBar.class.getName()).log(Level.SEVERE, "activityUUID = " + activityUUID);
        HibernateUtil hutil = new HibernateUtil();
        ArrayList<PbAttachment> pbAttachments = hutil.findProcessPbAttachments(processUUID);
        for (PbAttachment pbAttachment : pbAttachments) {
            Logger.getLogger(AttachmentBar.class.getName()).log(Level.SEVERE, "pbAttachments = " + pbAttachments);
            this.addComponent(new AttachmentFileRow(pbAttachment));
        }
        this.addComponent(new AttachmentFileRow(null));
    }

    public void newUpload() {
        this.addComponent(new AttachmentFileRow(null));
    }

    public void save(String processUUID, String activityUUID) {
        HibernateUtil hutil = new HibernateUtil();
        ArrayList<PbAttachment> pbAttachments = new ArrayList<PbAttachment>();
        for (Iterator iter = getComponentIterator(); iter.hasNext();) {
            Component a = (Component) iter.next();
            if (a instanceof AttachmentFileRow
                    && !((AttachmentFileRow) a).isSaved()
                    && ((AttachmentFileRow) a).getPbAttachments().getFileSize() != null) {
                PbAttachment file = ((AttachmentFileRow) a).getPbAttachments();
                file.setId(UUID.randomUUID().toString());
                file.setProccessUuid(processUUID);
                file.setActivityUuid(activityUUID);
                pbAttachments.add(file);
            }
        }
        if (pbAttachments.size() > 0) {
            hutil.mergeProcessPbAttachments(pbAttachments);
        }
    }
}
