package org.processbase.bonita.services.impl.filedocument;

import java.io.Serializable;
import java.util.Date;

import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.services.impl.DocumentImpl;

public class FileDocument extends DocumentImpl implements Serializable  {

	public FileDocument(String name){
		super(name);
	}
	
	public FileDocument(String name, String contentFileName, String contentMimeType, long contentSize) {
		super(name, contentFileName, contentMimeType, contentSize);
	}
	
	public FileDocument(String name, String folderId, String author,
			Date creationDate, Date lastModificationDate,
			boolean latestVersion, boolean majorVersion, String versionLabel,
			String versionSeriesId, String contentFileName,
			String contentMimeType, long contentSize,
			ProcessDefinitionUUID processDefinitionUUID,
			ProcessInstanceUUID processInstanceUUID) {
		super(name, folderId, author, creationDate, lastModificationDate,
				latestVersion, majorVersion, versionLabel, versionSeriesId,
				contentFileName, contentMimeType, contentSize, processDefinitionUUID,
				processInstanceUUID);
	}

}
