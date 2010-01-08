package org.processbase.mobile.client;

import de.enough.polish.xml.XmlDomNode;

/**
 *
 * @author mgubaidullin
 */
public class Task {

    private String uuid;
    private String createdDate;
    private String caption;
    private boolean mobile;
    private String desc;
    private XmlDomNode form;

    public Task(String uuid, String createdDate, String caption, boolean mobile, String desc) {
        this.uuid = uuid;
        this.createdDate = createdDate;
        this.caption = caption;
        this.mobile = mobile;
        this.desc = desc;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public XmlDomNode getForm() {
        return form;
    }

    public void setForm(XmlDomNode form) {
        this.form = form;
    }

    
}
