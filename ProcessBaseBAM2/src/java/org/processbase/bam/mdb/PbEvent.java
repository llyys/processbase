package org.processbase.bam.mdb;
// Generated 26.02.2010 12:01:04 by Hibernate Tools 3.2.1.GA


import java.math.BigDecimal;
import java.util.Date;

/**
 * PbEvent generated by hbm2java
 */
public class PbEvent  implements java.io.Serializable {


     private long id;
     private String eventId;
     private Date timeStamp;
     private String serverId;
     private String processDefinitionId;
     private String processInstanceId;
     private String processName;
     private String activityDefinitionId;
     private String activityInstanceId;
     private String activityInstanceIter;
     private String activityName;
     private String currentState;
     private BigDecimal factValueNumeric;
     private Date factValueTimestamp;
     private String factValueString;
     private String kpiCode;
     private String kpiName;
     private String dim1Name;
     private BigDecimal dim1ValueNumeric;
     private Date dim1ValueTimestamp;
     private String dim1ValueString;
     private String dim2Name;
     private BigDecimal dim2ValueNumeric;
     private Date dim2ValueTimestamp;
     private String dim2ValueString;
     private String dim3Name;
     private BigDecimal dim3ValueNumeric;
     private Date dim3ValueTimestamp;
     private String dim3ValueString;
     private String dim4Name;
     private BigDecimal dim4ValueNumeric;
     private Date dim4ValueTimestamp;
     private String dim4ValueString;
     private String dim5Name;
     private BigDecimal dim5ValueNumeric;
     private Date dim5ValueTimestamp;
     private String dim5ValueString;
     private String dim6Name;
     private BigDecimal dim6ValueNumeric;
     private Date dim6ValueTimestamp;
     private String dim6ValueString;
     private String dim7Name;
     private BigDecimal dim7ValueNumeric;
     private Date dim7ValueTimestamp;
     private String dim7ValueString;
     private String dim8Name;
     private BigDecimal dim8ValueNumeric;
     private Date dim8ValueTimestamp;
     private String dim8ValueString;
     private String dim9Name;
     private BigDecimal dim9ValueNumeric;
     private Date dim9ValueTimestamp;
     private String dim9ValueString;
     private String dim10Name;
     private BigDecimal dim10ValueNumeric;
     private Date dim10ValueTimestamp;
     private String dim10ValueString;

    public PbEvent() {
    }

	
    public PbEvent(String eventId, Date timeStamp, String serverId, String processInstanceId, String activityInstanceId, String currentState, String kpiCode) {
        this.eventId = eventId;
        this.timeStamp = timeStamp;
        this.serverId = serverId;
        this.processInstanceId = processInstanceId;
        this.activityInstanceId = activityInstanceId;
        this.currentState = currentState;
        this.kpiCode = kpiCode;
    }
    public PbEvent(long id, String eventId, Date timeStamp, String serverId, String processDefinitionId, String processInstanceId, String processName, String activityDefinitionId, String activityInstanceId, String activityInstanceIter, String activityName, String currentState, BigDecimal factValueNumeric, Date factValueTimestamp, String factValueString, String kpiCode, String kpiName, String dim1Name, BigDecimal dim1ValueNumeric, Date dim1ValueTimestamp, String dim1ValueString, String dim2Name, BigDecimal dim2ValueNumeric, Date dim2ValueTimestamp, String dim2ValueString, String dim3Name, BigDecimal dim3ValueNumeric, Date dim3ValueTimestamp, String dim3ValueString, String dim4Name, BigDecimal dim4ValueNumeric, Date dim4ValueTimestamp, String dim4ValueString, String dim5Name, BigDecimal dim5ValueNumeric, Date dim5ValueTimestamp, String dim5ValueString, String dim6Name, BigDecimal dim6ValueNumeric, Date dim6ValueTimestamp, String dim6ValueString, String dim7Name, BigDecimal dim7ValueNumeric, Date dim7ValueTimestamp, String dim7ValueString, String dim8Name, BigDecimal dim8ValueNumeric, Date dim8ValueTimestamp, String dim8ValueString, String dim9Name, BigDecimal dim9ValueNumeric, Date dim9ValueTimestamp, String dim9ValueString, String dim10Name, BigDecimal dim10ValueNumeric, Date dim10ValueTimestamp, String dim10ValueString) {
       this.id = id;
       this.eventId = eventId;
       this.timeStamp = timeStamp;
       this.serverId = serverId;
       this.processDefinitionId = processDefinitionId;
       this.processInstanceId = processInstanceId;
       this.processName = processName;
       this.activityDefinitionId = activityDefinitionId;
       this.activityInstanceId = activityInstanceId;
       this.activityInstanceIter = activityInstanceIter;
       this.activityName = activityName;
       this.currentState = currentState;
       this.factValueNumeric = factValueNumeric;
       this.factValueTimestamp = factValueTimestamp;
       this.factValueString = factValueString;
       this.kpiCode = kpiCode;
       this.kpiName = kpiName;
       this.dim1Name = dim1Name;
       this.dim1ValueNumeric = dim1ValueNumeric;
       this.dim1ValueTimestamp = dim1ValueTimestamp;
       this.dim1ValueString = dim1ValueString;
       this.dim2Name = dim2Name;
       this.dim2ValueNumeric = dim2ValueNumeric;
       this.dim2ValueTimestamp = dim2ValueTimestamp;
       this.dim2ValueString = dim2ValueString;
       this.dim3Name = dim3Name;
       this.dim3ValueNumeric = dim3ValueNumeric;
       this.dim3ValueTimestamp = dim3ValueTimestamp;
       this.dim3ValueString = dim3ValueString;
       this.dim4Name = dim4Name;
       this.dim4ValueNumeric = dim4ValueNumeric;
       this.dim4ValueTimestamp = dim4ValueTimestamp;
       this.dim4ValueString = dim4ValueString;
       this.dim5Name = dim5Name;
       this.dim5ValueNumeric = dim5ValueNumeric;
       this.dim5ValueTimestamp = dim5ValueTimestamp;
       this.dim5ValueString = dim5ValueString;
       this.dim6Name = dim6Name;
       this.dim6ValueNumeric = dim6ValueNumeric;
       this.dim6ValueTimestamp = dim6ValueTimestamp;
       this.dim6ValueString = dim6ValueString;
       this.dim7Name = dim7Name;
       this.dim7ValueNumeric = dim7ValueNumeric;
       this.dim7ValueTimestamp = dim7ValueTimestamp;
       this.dim7ValueString = dim7ValueString;
       this.dim8Name = dim8Name;
       this.dim8ValueNumeric = dim8ValueNumeric;
       this.dim8ValueTimestamp = dim8ValueTimestamp;
       this.dim8ValueString = dim8ValueString;
       this.dim9Name = dim9Name;
       this.dim9ValueNumeric = dim9ValueNumeric;
       this.dim9ValueTimestamp = dim9ValueTimestamp;
       this.dim9ValueString = dim9ValueString;
       this.dim10Name = dim10Name;
       this.dim10ValueNumeric = dim10ValueNumeric;
       this.dim10ValueTimestamp = dim10ValueTimestamp;
       this.dim10ValueString = dim10ValueString;
    }
   
    public long getId() {
        return this.id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    public String getEventId() {
        return this.eventId;
    }
    
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    public Date getTimeStamp() {
        return this.timeStamp;
    }
    
    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
    public String getServerId() {
        return this.serverId;
    }
    
    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
    public String getProcessDefinitionId() {
        return this.processDefinitionId;
    }
    
    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }
    public String getProcessInstanceId() {
        return this.processInstanceId;
    }
    
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    public String getProcessName() {
        return this.processName;
    }
    
    public void setProcessName(String processName) {
        this.processName = processName;
    }
    public String getActivityDefinitionId() {
        return this.activityDefinitionId;
    }
    
    public void setActivityDefinitionId(String activityDefinitionId) {
        this.activityDefinitionId = activityDefinitionId;
    }
    public String getActivityInstanceId() {
        return this.activityInstanceId;
    }
    
    public void setActivityInstanceId(String activityInstanceId) {
        this.activityInstanceId = activityInstanceId;
    }
    public String getActivityInstanceIter() {
        return this.activityInstanceIter;
    }
    
    public void setActivityInstanceIter(String activityInstanceIter) {
        this.activityInstanceIter = activityInstanceIter;
    }
    public String getActivityName() {
        return this.activityName;
    }
    
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
    public String getCurrentState() {
        return this.currentState;
    }
    
    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }
    public BigDecimal getFactValueNumeric() {
        return this.factValueNumeric;
    }
    
    public void setFactValueNumeric(BigDecimal factValueNumeric) {
        this.factValueNumeric = factValueNumeric;
    }
    public Date getFactValueTimestamp() {
        return this.factValueTimestamp;
    }
    
    public void setFactValueTimestamp(Date factValueTimestamp) {
        this.factValueTimestamp = factValueTimestamp;
    }
    public String getFactValueString() {
        return this.factValueString;
    }
    
    public void setFactValueString(String factValueString) {
        this.factValueString = factValueString;
    }
    public String getKpiCode() {
        return this.kpiCode;
    }
    
    public void setKpiCode(String kpiCode) {
        this.kpiCode = kpiCode;
    }
    public String getKpiName() {
        return this.kpiName;
    }
    
    public void setKpiName(String kpiName) {
        this.kpiName = kpiName;
    }
    public String getDim1Name() {
        return this.dim1Name;
    }
    
    public void setDim1Name(String dim1Name) {
        this.dim1Name = dim1Name;
    }
    public BigDecimal getDim1ValueNumeric() {
        return this.dim1ValueNumeric;
    }
    
    public void setDim1ValueNumeric(BigDecimal dim1ValueNumeric) {
        this.dim1ValueNumeric = dim1ValueNumeric;
    }
    public Date getDim1ValueTimestamp() {
        return this.dim1ValueTimestamp;
    }
    
    public void setDim1ValueTimestamp(Date dim1ValueTimestamp) {
        this.dim1ValueTimestamp = dim1ValueTimestamp;
    }
    public String getDim1ValueString() {
        return this.dim1ValueString;
    }
    
    public void setDim1ValueString(String dim1ValueString) {
        this.dim1ValueString = dim1ValueString;
    }
    public String getDim2Name() {
        return this.dim2Name;
    }
    
    public void setDim2Name(String dim2Name) {
        this.dim2Name = dim2Name;
    }
    public BigDecimal getDim2ValueNumeric() {
        return this.dim2ValueNumeric;
    }
    
    public void setDim2ValueNumeric(BigDecimal dim2ValueNumeric) {
        this.dim2ValueNumeric = dim2ValueNumeric;
    }
    public Date getDim2ValueTimestamp() {
        return this.dim2ValueTimestamp;
    }
    
    public void setDim2ValueTimestamp(Date dim2ValueTimestamp) {
        this.dim2ValueTimestamp = dim2ValueTimestamp;
    }
    public String getDim2ValueString() {
        return this.dim2ValueString;
    }
    
    public void setDim2ValueString(String dim2ValueString) {
        this.dim2ValueString = dim2ValueString;
    }
    public String getDim3Name() {
        return this.dim3Name;
    }
    
    public void setDim3Name(String dim3Name) {
        this.dim3Name = dim3Name;
    }
    public BigDecimal getDim3ValueNumeric() {
        return this.dim3ValueNumeric;
    }
    
    public void setDim3ValueNumeric(BigDecimal dim3ValueNumeric) {
        this.dim3ValueNumeric = dim3ValueNumeric;
    }
    public Date getDim3ValueTimestamp() {
        return this.dim3ValueTimestamp;
    }
    
    public void setDim3ValueTimestamp(Date dim3ValueTimestamp) {
        this.dim3ValueTimestamp = dim3ValueTimestamp;
    }
    public String getDim3ValueString() {
        return this.dim3ValueString;
    }
    
    public void setDim3ValueString(String dim3ValueString) {
        this.dim3ValueString = dim3ValueString;
    }
    public String getDim4Name() {
        return this.dim4Name;
    }
    
    public void setDim4Name(String dim4Name) {
        this.dim4Name = dim4Name;
    }
    public BigDecimal getDim4ValueNumeric() {
        return this.dim4ValueNumeric;
    }
    
    public void setDim4ValueNumeric(BigDecimal dim4ValueNumeric) {
        this.dim4ValueNumeric = dim4ValueNumeric;
    }
    public Date getDim4ValueTimestamp() {
        return this.dim4ValueTimestamp;
    }
    
    public void setDim4ValueTimestamp(Date dim4ValueTimestamp) {
        this.dim4ValueTimestamp = dim4ValueTimestamp;
    }
    public String getDim4ValueString() {
        return this.dim4ValueString;
    }
    
    public void setDim4ValueString(String dim4ValueString) {
        this.dim4ValueString = dim4ValueString;
    }
    public String getDim5Name() {
        return this.dim5Name;
    }
    
    public void setDim5Name(String dim5Name) {
        this.dim5Name = dim5Name;
    }
    public BigDecimal getDim5ValueNumeric() {
        return this.dim5ValueNumeric;
    }
    
    public void setDim5ValueNumeric(BigDecimal dim5ValueNumeric) {
        this.dim5ValueNumeric = dim5ValueNumeric;
    }
    public Date getDim5ValueTimestamp() {
        return this.dim5ValueTimestamp;
    }
    
    public void setDim5ValueTimestamp(Date dim5ValueTimestamp) {
        this.dim5ValueTimestamp = dim5ValueTimestamp;
    }
    public String getDim5ValueString() {
        return this.dim5ValueString;
    }
    
    public void setDim5ValueString(String dim5ValueString) {
        this.dim5ValueString = dim5ValueString;
    }
    public String getDim6Name() {
        return this.dim6Name;
    }
    
    public void setDim6Name(String dim6Name) {
        this.dim6Name = dim6Name;
    }
    public BigDecimal getDim6ValueNumeric() {
        return this.dim6ValueNumeric;
    }
    
    public void setDim6ValueNumeric(BigDecimal dim6ValueNumeric) {
        this.dim6ValueNumeric = dim6ValueNumeric;
    }
    public Date getDim6ValueTimestamp() {
        return this.dim6ValueTimestamp;
    }
    
    public void setDim6ValueTimestamp(Date dim6ValueTimestamp) {
        this.dim6ValueTimestamp = dim6ValueTimestamp;
    }
    public String getDim6ValueString() {
        return this.dim6ValueString;
    }
    
    public void setDim6ValueString(String dim6ValueString) {
        this.dim6ValueString = dim6ValueString;
    }
    public String getDim7Name() {
        return this.dim7Name;
    }
    
    public void setDim7Name(String dim7Name) {
        this.dim7Name = dim7Name;
    }
    public BigDecimal getDim7ValueNumeric() {
        return this.dim7ValueNumeric;
    }
    
    public void setDim7ValueNumeric(BigDecimal dim7ValueNumeric) {
        this.dim7ValueNumeric = dim7ValueNumeric;
    }
    public Date getDim7ValueTimestamp() {
        return this.dim7ValueTimestamp;
    }
    
    public void setDim7ValueTimestamp(Date dim7ValueTimestamp) {
        this.dim7ValueTimestamp = dim7ValueTimestamp;
    }
    public String getDim7ValueString() {
        return this.dim7ValueString;
    }
    
    public void setDim7ValueString(String dim7ValueString) {
        this.dim7ValueString = dim7ValueString;
    }
    public String getDim8Name() {
        return this.dim8Name;
    }
    
    public void setDim8Name(String dim8Name) {
        this.dim8Name = dim8Name;
    }
    public BigDecimal getDim8ValueNumeric() {
        return this.dim8ValueNumeric;
    }
    
    public void setDim8ValueNumeric(BigDecimal dim8ValueNumeric) {
        this.dim8ValueNumeric = dim8ValueNumeric;
    }
    public Date getDim8ValueTimestamp() {
        return this.dim8ValueTimestamp;
    }
    
    public void setDim8ValueTimestamp(Date dim8ValueTimestamp) {
        this.dim8ValueTimestamp = dim8ValueTimestamp;
    }
    public String getDim8ValueString() {
        return this.dim8ValueString;
    }
    
    public void setDim8ValueString(String dim8ValueString) {
        this.dim8ValueString = dim8ValueString;
    }
    public String getDim9Name() {
        return this.dim9Name;
    }
    
    public void setDim9Name(String dim9Name) {
        this.dim9Name = dim9Name;
    }
    public BigDecimal getDim9ValueNumeric() {
        return this.dim9ValueNumeric;
    }
    
    public void setDim9ValueNumeric(BigDecimal dim9ValueNumeric) {
        this.dim9ValueNumeric = dim9ValueNumeric;
    }
    public Date getDim9ValueTimestamp() {
        return this.dim9ValueTimestamp;
    }
    
    public void setDim9ValueTimestamp(Date dim9ValueTimestamp) {
        this.dim9ValueTimestamp = dim9ValueTimestamp;
    }
    public String getDim9ValueString() {
        return this.dim9ValueString;
    }
    
    public void setDim9ValueString(String dim9ValueString) {
        this.dim9ValueString = dim9ValueString;
    }
    public String getDim10Name() {
        return this.dim10Name;
    }
    
    public void setDim10Name(String dim10Name) {
        this.dim10Name = dim10Name;
    }
    public BigDecimal getDim10ValueNumeric() {
        return this.dim10ValueNumeric;
    }
    
    public void setDim10ValueNumeric(BigDecimal dim10ValueNumeric) {
        this.dim10ValueNumeric = dim10ValueNumeric;
    }
    public Date getDim10ValueTimestamp() {
        return this.dim10ValueTimestamp;
    }
    
    public void setDim10ValueTimestamp(Date dim10ValueTimestamp) {
        this.dim10ValueTimestamp = dim10ValueTimestamp;
    }
    public String getDim10ValueString() {
        return this.dim10ValueString;
    }
    
    public void setDim10ValueString(String dim10ValueString) {
        this.dim10ValueString = dim10ValueString;
    }




}


