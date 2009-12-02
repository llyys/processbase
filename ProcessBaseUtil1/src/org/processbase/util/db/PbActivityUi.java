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
@Table(name = "PB_ACTIVITY_UI")
public class PbActivityUi implements java.io.Serializable {

    private String id;
    private String proccessUuid;
    private String activityUuid;
    private String activityDescription;
    private String uiClass;

    public PbActivityUi() {
    }

    public PbActivityUi(String id, String proccessUuid, String activityUuid, String activityDescription) {
        this.id = id;
        this.proccessUuid = proccessUuid;
        this.activityUuid = activityUuid;
        this.activityDescription = activityDescription;
    }

    public PbActivityUi(String id, String proccessUuid, String activityUuid, String uiClass, String activityDescription) {
        this.id = id;
        this.proccessUuid = proccessUuid;
        this.activityUuid = activityUuid;
        this.uiClass = uiClass;
        this.activityDescription = activityDescription;
    }

    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 100)
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "PROCCESS_UUID", nullable = false, length = 2000)
    public String getProccessUuid() {
        return this.proccessUuid;
    }

    public void setProccessUuid(String proccessUuid) {
        this.proccessUuid = proccessUuid;
    }

    @Column(name = "ACTIVITY_UUID", nullable = false, length = 2000)
    public String getActivityUuid() {
        return this.activityUuid;
    }

    public void setActivityUuid(String activityUuid) {
        this.activityUuid = activityUuid;
    }

    @Column(name = "ACTIVITY_DESCRIPTION", nullable = false, length = 2000)
    public String getActivityDescription() {
        return this.activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    @Column(name = "UI_CLASS", length = 2000)
    public String getUiClass() {
        return this.uiClass;
    }

    public void setUiClass(String uiClass) {
        this.uiClass = uiClass;
    }
}


