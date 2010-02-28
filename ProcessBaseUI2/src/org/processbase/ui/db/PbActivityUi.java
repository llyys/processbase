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
package org.processbase.ui.db;
// Generated 13.12.2009 22:19:20 by Hibernate Tools 3.2.1.GA

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * PbActivityUi generated by hbm2java
 */
@Entity
@Table(name = "PB_ACTIVITY_UI")
public class PbActivityUi implements java.io.Serializable {

    private long id;
    private String proccessUuid;
    private String activityUuid;
    private String uiClass;
    private String activityLabel;
    private String isMobile;
    private String mobileUiClass;
    private String isStart;

    public PbActivityUi() {
    }

    public PbActivityUi(String proccessUuid, String activityUuid) {
        this.proccessUuid = proccessUuid;
        this.activityUuid = activityUuid;
    }

    public PbActivityUi(String proccessUuid, String activityUuid, String uiClass, String activityLabel, String isStart) {
        this.proccessUuid = proccessUuid;
        this.activityUuid = activityUuid;
        this.uiClass = uiClass;
        this.activityLabel = activityLabel;
        this.isStart = isStart;
    }

    public PbActivityUi(String proccessUuid, String activityUuid, String uiClass, String activityLabel, String isMobile, String mobileUiClass, String isStart) {
        this.proccessUuid = proccessUuid;
        this.activityUuid = activityUuid;
        this.uiClass = uiClass;
        this.activityLabel = activityLabel;
        this.isMobile = isMobile;
        this.mobileUiClass = mobileUiClass;
        this.isStart = isStart;
    }

    @Id
    @Column(name = "ID", unique = true, nullable = false, precision = 16, scale = 0)
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "PROCCESS_UUID", nullable = false, length = 200)
    public String getProccessUuid() {
        return this.proccessUuid;
    }

    public void setProccessUuid(String proccessUuid) {
        this.proccessUuid = proccessUuid;
    }

    @Column(name = "ACTIVITY_UUID", nullable = false, length = 200)
    public String getActivityUuid() {
        return this.activityUuid;
    }

    public void setActivityUuid(String activityUuid) {
        this.activityUuid = activityUuid;
    }

    @Column(name = "UI_CLASS", length = 200)
    public String getUiClass() {
        return this.uiClass;
    }

    public void setUiClass(String uiClass) {
        this.uiClass = uiClass;
    }

    @Column(name = "ACTIVITY_LABEL", length = 200)
    public String getActivityLabel() {
        return this.activityLabel;
    }

    public void setActivityLabel(String activityLabel) {
        this.activityLabel = activityLabel;
    }

    @Column(name = "IS_MOBILE", length = 1)
    public String getIsMobile() {
        return isMobile;
    }

    public void setIsMobile(String isMobile) {
        this.isMobile = isMobile;
    }

    @Column(name = "MOBILE_UI_CLASS", length = 200)
    public String getMobileUiClass() {
        return mobileUiClass;
    }

    public void setMobileUiClass(String mobileUiClass) {
        this.mobileUiClass = mobileUiClass;
    }

    @Column(name = "IS_START", length = 1)
    public String getIsStart() {
        return isStart;
    }

    public void setIsStart(String isStart) {
        this.isStart = isStart;
    }


}

