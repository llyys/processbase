package org.processbase.util.db;
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
    private String activityDescription;

    public PbActivityUi() {
    }

    public PbActivityUi(String proccessUuid, String activityUuid) {
        this.proccessUuid = proccessUuid;
        this.activityUuid = activityUuid;
    }

    public PbActivityUi(String proccessUuid, String activityUuid, String uiClass, String activityDescription) {
        this.proccessUuid = proccessUuid;
        this.activityUuid = activityUuid;
        this.uiClass = uiClass;
        this.activityDescription = activityDescription;
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

    @Column(name = "ACTIVITY_DESCRIPTION", length = 200)
    public String getActivityDescription() {
        return this.activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }
}


