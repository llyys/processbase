/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.util.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 */
@Entity
@Table(name = "PB_ATTACHMENTS")
public class PbAttachment implements java.io.Serializable {

    private String id;
    private String customId;
    private String proccessUuid;
    private String activityUuid;
    private String fileName;
    private Long fileSize;
    private String fileMimeType;
    private String fileDesc;
    private byte[] fileBody;

    public PbAttachment() {
    }

    public PbAttachment(String id) {
        this.id = id;
    }

    public PbAttachment(String id, String customId, String proccessUuid, String activityUuid, String fileName, Long fileSize, String fileMimeType, byte[] fileBody) {
        this.id = id;
        this.customId = customId;
        this.proccessUuid = proccessUuid;
        this.activityUuid = activityUuid;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileMimeType = fileMimeType;
        this.fileBody = fileBody;
    }

    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 100)
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "CUSTOM_ID", length = 2000)
    public String getCustomId() {
        return this.customId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    @Column(name = "PROCCESS_UUID", length = 2000)
    public String getProccessUuid() {
        return this.proccessUuid;
    }

    public void setProccessUuid(String proccessUuid) {
        this.proccessUuid = proccessUuid;
    }

    @Column(name = "ACTIVITY_UUID", length = 2000)
    public String getActivityUuid() {
        return this.activityUuid;
    }

    public void setActivityUuid(String activityUuid) {
        this.activityUuid = activityUuid;
    }

    @Column(name = "FILE_NAME", length = 2000)
    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Column(name = "FILE_SIZE", precision = 10, scale = 0)
    public Long getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Column(name = "FILE_MIME_TYPE", length = 2000)
    public String getFileMimeType() {
        return this.fileMimeType;
    }

    public void setFileMimeType(String fileMimeType) {
        this.fileMimeType = fileMimeType;
    }

    @Column(name = "FILE_BODY")
    public byte[] getFileBody() {
        return this.fileBody;
    }

    public void setFileBody(byte[] fileBody) {
        this.fileBody = fileBody;
    }

    public String getFileDesc() {
        return fileDesc;
    }

    public void setFileDesc(String fileDesc) {
        this.fileDesc = fileDesc;
    }

    
}


