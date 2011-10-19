package org.processbase.bonita.services.impl;

import java.util.List;

import org.ow2.bonita.facade.exception.DocumentNotFoundException;
import org.ow2.bonita.facade.impl.SearchResult;
import org.ow2.bonita.search.DocumentSearchBuilder;
import org.ow2.bonita.services.CmisUserProvider;
import org.ow2.bonita.services.Document;
import org.ow2.bonita.services.impl.CMISDocumentManager;


public class CachedCMISDocumentManager extends CMISDocumentManager{

	private static final String CMIS = "CMIS";

	public CachedCMISDocumentManager(String binding, String url, String repositoryId, CmisUserProvider userProvider, String pathOfRootFolder) {
		super(binding, url, repositoryId, userProvider, pathOfRootFolder);		
	}
	public CachedCMISDocumentManager(final String binding, final String url, final String repositoryId, final CmisUserProvider userProvider) {
		    this(binding,url,repositoryId,userProvider,"/");
	}
	@Override
	public Document getDocument(final String documentId) throws DocumentNotFoundException {
		final CachedCMISDocumentManager that=this;
		try {
			return CacheUtil.getOrCache(CMIS, "getDocument"+documentId, new ICacheDelegate<Document>() {
				public Document execute() throws Exception {
					return that._getDocument(documentId);		
				}
			});
		} catch (Exception e) {
			if(e instanceof DocumentNotFoundException)
				throw (DocumentNotFoundException)e;
			else
				throw new DocumentNotFoundException("CACHEDOC", e);
		}		
	}
	
	
	
	@Override
	public void deleteDocument(final String documentId, final boolean allVersions) throws DocumentNotFoundException {
		CacheUtil.remove(CMIS, "getDocument"+documentId);
	    super.deleteDocument(documentId, allVersions);
	}  
	
	@Override
	public List<org.ow2.bonita.services.Folder> getFolders(final String folderName) {
		final CachedCMISDocumentManager that=this;
		try {
			return CacheUtil.getOrCache(CMIS, "getFolders"+folderName, new ICacheDelegate<List<org.ow2.bonita.services.Folder>>() {
				public List<org.ow2.bonita.services.Folder> execute() throws Exception {
					return that._getFolders(folderName);		
				}
			});
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return null;
		
	}
	
	public List<org.ow2.bonita.services.Folder> _getFolders(final String folderName) {
		return super.getFolders(folderName);
		
	}
	
	public Document _getDocument(final String documentId) throws DocumentNotFoundException {
		return super.getDocument(documentId);
	}
}
