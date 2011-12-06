package org.processbase.bonita.services.impl.filedocument;

import java.io.File;

import org.ow2.bonita.services.Folder;

public class FileFolder implements Folder{
	private File folder;
	public FileFolder(File folder){
		this.folder=folder;
	}
	public String getId() {
		// TODO Auto-generated method stub
		return this.folder.getAbsolutePath();
	}

	public String getName() {
		return this.folder.getName();
	}

	public void setId(String id) {
		// TODO Auto-generated method stub
		
	}

	public String getParentFolderId() {
		this.folder.getParentFile().getAbsolutePath();
		return null;
	}

}
