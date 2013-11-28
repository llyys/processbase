package org.processbase.bonita.services.impl.filedocument;

import java.io.File;
import java.io.Serializable;

import org.ow2.bonita.services.Folder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="FileFolder")
public class FileFolder implements Folder, Serializable {
    @Id
    String id;

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
