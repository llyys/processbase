package org.processbase.bonita.services.impl.db;

import com.thoughtworks.xstream.XStream;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.services.Document;
import org.ow2.bonita.util.xml.XStreamUtil;

import java.util.Date;

public class DbDocument implements  Document {

    private String name;
    private String folderId;
    private long oid;
    private String hash;
    private long contentSize;
    private ProcessDefinitionUUID processDefinitionUUID;
    private ProcessInstanceUUID processInstanceUUID;
    private String contentMimeType;
    private String contentFileName;
    private Date lastModificationDate;
    private boolean latestVersion;
    private boolean majorVersion;
    private String versionLabel;
    private String versionSeriesId;
    private String lastModifiedBy;
    private String author;
    private Date creationDate;
    private String id;
    private String parentFolderId;

    public DbDocument(String name, String folderId, String author, Date creationDate, Date lastModificationDate, boolean latestVersion, boolean majorVersion, String versionLabel, String versionSeriesId, String contentFileName, String contentMimeType, long contentSize, ProcessDefinitionUUID processDefinitionUUID, ProcessInstanceUUID processInstanceUUID) {

        this.name = name;
        this.folderId = folderId;
        this.author = author;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
        this.latestVersion = latestVersion;
        this.majorVersion = majorVersion;
        this.versionLabel = versionLabel;
        this.versionSeriesId = versionSeriesId;
        this.contentFileName = contentFileName;
        this.contentMimeType = contentMimeType;
        this.contentSize = contentSize;
        this.processDefinitionUUID = processDefinitionUUID;
        this.processInstanceUUID = processInstanceUUID;
    }

    public DbDocument() {

    }

    public long getOid() {
        return oid;
    }

    public void setOid(long oid) {
        this.oid = oid;
    }



    public static DbDocument deSerialize(String xml){
        XStream xstream = XStreamUtil.getDefaultXstream();
        DbDocument dbDocument=new DbDocument();
        return (DbDocument)xstream.fromXML(xml, dbDocument);
    }

    public String getHash() {
        return hash;
    }
    public void setHash(String hash) {
        this.hash=hash;
    }

    public void setContentSize(int contentSize) {
        this.contentSize = contentSize;
    }

    @Override
    public long getContentSize(){
        return this.contentSize;
    }

    @Override
    public ProcessInstanceUUID getProcessInstanceUUID() {
        return processInstanceUUID;
    }

    @Override
    public ProcessDefinitionUUID getProcessDefinitionUUID() {
        return processDefinitionUUID;
    }

    public void setContentMimeType(String contentMimeType) {
        this.contentMimeType = contentMimeType;
    }

    @Override
    public String getContentMimeType() {
        return contentMimeType;
    }

    public void setContentFileName(String contentFileName) {
        this.contentFileName = contentFileName;
    }

    @Override
    public String getContentFileName() {
        return contentFileName;
    }

    public void setLastModificationDate(Date lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    @Override
    public boolean isLatestVersion() {
        return latestVersion;
    }

    @Override
    public boolean isMajorVersion() {
        return majorVersion;
    }

    @Override
    public String getVersionLabel() {
        return versionLabel;
    }

    @Override
    public String getVersionSeriesId() {
        return versionSeriesId;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setId(String id) {
        this.id=id;
    }

    @Override
    public String getParentFolderId() {
        return parentFolderId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public void setProcessDefinitionUUID(ProcessDefinitionUUID processDefinitionUUID) {
        this.processDefinitionUUID = processDefinitionUUID;
    }

    public void setProcessInstanceUUID(ProcessInstanceUUID processInstanceUUID) {
        this.processInstanceUUID = processInstanceUUID;
    }

    public void setLatestVersion(boolean latestVersion) {
        this.latestVersion = latestVersion;
    }

    public void setMajorVersion(boolean majorVersion) {
        this.majorVersion = majorVersion;
    }

    public void setVersionLabel(String versionLabel) {
        this.versionLabel = versionLabel;
    }

    public void setVersionSeriesId(String versionSeriesId) {
        this.versionSeriesId = versionSeriesId;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        XStream xstream = XStreamUtil.getDefaultXstream();
        return xstream.toXML(this);
    }
}
