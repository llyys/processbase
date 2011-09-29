package org.processbase.commands.documents;

import java.util.List;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Session;
import org.ow2.bonita.env.Environment;
import org.ow2.bonita.facade.exception.DocumentNotFoundException;
import org.ow2.bonita.services.Document;
import org.ow2.bonita.services.DocumentationManager;
import org.ow2.bonita.services.Folder;
import org.ow2.bonita.services.impl.CMISDocumentManager;
import org.ow2.bonita.util.BonitaRuntimeException;
import org.ow2.bonita.util.Command;
import org.ow2.bonita.util.EnvTool;
import org.processbase.engine.bam.db.HibernateUtil;
import org.processbase.engine.bam.metadata.MetaDim;

public class DeleteDocumentCommand implements Command<Void> {

    
	private final String documetName;

    public DeleteDocumentCommand(String documetName) {
		this.documetName = documetName;
        
    }

    public Void execute(Environment e) throws Exception {
    	DocumentationManager documentatinManager = EnvTool.getDocumentationManager();
    	List<Folder> folders = documentatinManager.getFolders(documetName);
    	/*
    	Session session2=((CMISDocumentManager) documentatinManager).getSession();
    	for (Folder f : folders) {
    		try{
      	      ObjectId objectId = session2.createObjectId(f.getId());
				CmisObject object = session2.getObject(objectId);
				object.delete(true);
				
      	    } catch (Throwable e){
      	      throw new BonitaRuntimeException("can't delete folder "+folder.getName()+" with id "+folder.getId()+"\n"+e.getMessage());
      	    }	
		}*/
    	
		if (folders != null) {
			for (Folder f : folders) {
				delFolder(documentatinManager, f);
			}
		}
		return null;
    }
    
    private void delFolder(DocumentationManager documentatinManager, Folder f) throws Exception{
    	for (Document d: documentatinManager.getChildrenDocuments(f.getId()))
		{
			documentatinManager.deleteDocument(d.getId(), true);			
		}
    	for(Folder childFolder:documentatinManager.getChildrenFolder(f.getId())){
    		delFolder(documentatinManager, childFolder);
    		documentatinManager.deleteFolder(childFolder);
    	}
    }
    
}
