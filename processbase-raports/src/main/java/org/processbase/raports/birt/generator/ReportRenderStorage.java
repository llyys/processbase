package org.processbase.raports.birt.generator;

import org.eclipse.birt.report.engine.api.IReportDocumentInfo;

public class ReportRenderStorage {

	   private IReportDocumentInfo docInfo;
       private int lastCheckpoint;
       private String viewerID;
       private boolean renderComplete;
       private String reportName = "";
       
       public String getReportName() {
               return reportName;
       }
       public void setReportName(String reportName) {
               this.reportName = reportName;
       }
       public boolean isRenderComplete() {
               return renderComplete;
       }
       public void setRenderComplete(boolean renderComplete) {
               this.renderComplete = renderComplete;
       }
       public String getViewerID() {
               return viewerID;
       }
       public void setViewerID(String viewerID) {
               this.viewerID = viewerID;
       }
       public IReportDocumentInfo getDocInfo() {
               return docInfo;
       }
       public void setDocInfo(IReportDocumentInfo docInfo) {
               this.docInfo = docInfo;
       }
       public int getLastCheckpoint() {
               return lastCheckpoint;
       }
       public void setLastCheckpoint(int lastCheckpoint) {
               this.lastCheckpoint = lastCheckpoint;
       }

}

