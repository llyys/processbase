package org.processbase.commands.documents;

import java.util.List;

import org.ow2.bonita.env.Environment;
import org.ow2.bonita.services.Document;
import org.ow2.bonita.services.DocumentationManager;
import org.ow2.bonita.services.Folder;
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
		if (folders != null) {
			for (Folder f : folders) {
				for (Document d: documentatinManager.getChildrenDocuments(f.getId()))
				{
					documentatinManager.deleteDocument(d.getId(), true);
				}				
				documentatinManager.deleteFolder(f);
			}
		}
		return null;
    }
}
