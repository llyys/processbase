package org.processbase.bonita.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.ow2.bonita.facade.def.InternalProcessDefinition;
import org.ow2.bonita.facade.def.element.AttachmentDefinition;
import org.ow2.bonita.facade.def.element.impl.AttachmentDefinitionImpl;
import org.ow2.bonita.facade.exception.DocumentAlreadyExistsException;
import org.ow2.bonita.facade.exception.DocumentNotFoundException;
import org.ow2.bonita.facade.exception.DocumentationCreationException;
import org.ow2.bonita.facade.exception.FolderAlreadyExistsException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.impl.FacadeUtil;
import org.ow2.bonita.facade.impl.SearchResult;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.search.DocumentCriterion;
import org.ow2.bonita.search.DocumentSearchBuilder;
import org.ow2.bonita.search.index.DocumentIndex;
import org.ow2.bonita.services.CmisUserProvider;
import org.ow2.bonita.services.Document;
import org.ow2.bonita.services.DocumentationManager;
import org.ow2.bonita.services.Folder;
import org.ow2.bonita.services.LargeDataRepository;
import org.ow2.bonita.services.impl.AuthAwareCookieManager;
import org.ow2.bonita.services.impl.DocumentImpl;
import org.ow2.bonita.util.EnvTool;
import org.ow2.bonita.util.Misc;
import org.ow2.bonita.util.xml.XStreamUtil;
import org.processbase.bonita.services.impl.filedocument.FileDocument;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.bonita.process.BarResource;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.ObjectId;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.thoughtworks.xstream.XStream;

public class FileDocumentManager implements DocumentationManager{
	private static final String LENGHT = "Lenght";
	private static final String FILE_ID = "FileId";
	private static final String NAME = "Name";
	private static final String DOCUMENT_ID = "ID";
	private static ResourceBundle  mimeProperties=null;
	private static final String DOCUMENT = "document";
	private static final String PROCESS_INSTANCE_UUID = "ProcessInstanceUUID";
	private static final String AUTHOR = "author";
	private static final String PROCESS_DEFINITION_UUID = "ProcessDefinitionUUID";
	private static final String DOCUMENTS = "documents";
	private String pathOfRootFolder;
	private File rootFolder;
	private DB db=null;
	private static Mongo mongo=null;
	
	public FileDocumentManager(String host, String database) {
		 if(mongo==null)
		 {
			 try {
				mongo=new Mongo(host);
				db = mongo.getDB( database );
				mimeProperties= ResourceBundle.getBundle("mime");
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MongoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
		 }
	}
	
	
	public Document createDocument(String name, ProcessDefinitionUUID definitionUUID, ProcessInstanceUUID instanceUUID, String fileName, String contentMimeType, byte[] fileContent) throws DocumentationCreationException, DocumentAlreadyExistsException {
		try {
			
			GridFS gfs = new GridFS(db, "FILE_TABLE"); 
			if(fileContent==null)
				fileContent=new byte[0];
			
			BasicDBObject doc = new BasicDBObject();
			DBCollection table = db.getCollection(DOCUMENTS);
			
			doc.put(LENGHT, fileContent.length);
			doc.put(NAME, name);
			
			if(definitionUUID!=null)
				doc.put(PROCESS_DEFINITION_UUID, definitionUUID.toString());
				
			
			String instance = instanceUUID != null ? instanceUUID.toString() : "DEFINITION_LEVEL_DOCUMENT";
			doc.put(PROCESS_INSTANCE_UUID, instance);
			
			//String folderId=definitionUUID.toString()+"/"+instance;
			
			Document document = new DocumentImpl(name, null, EnvTool.getUserId(), new Date(), new Date(), true, true,null, null, fileName, contentMimeType, fileContent.length, definitionUUID, instanceUUID);
			
			
			if(fileContent!=null && fileContent.length>0){
				GridFSInputFile file = gfs.createFile(fileContent);
				file.setFilename(fileName);
				file.setContentType(contentMimeType);
				file.save();
				doc.put(FILE_ID, file.getId());
			}
			
			table.insert(doc);

			ObjectId id = (ObjectId)doc.get( "_id" );
			document.setId(id.toString());
			
			doc.put(DOCUMENT, document.toString());
			
			table.save(doc);
			
			return document;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DocumentationCreationException("Can't connect to MongoDB, is MonboDB started?", ex);
        }
	        
	}



	public Document getDocument(String documentId) throws DocumentNotFoundException {
		   System.out.println("getDocument");
		   Document document = null;
		   try {
			   GridFS gfsPhoto = new GridFS(db, DOCUMENTS);
			   DBCollection table = db.getCollection(DOCUMENTS);
			   
			   DBObject doc = table.findOne(new ObjectId(documentId));
			   if(doc==null)
				   return null;
			   DocumentImpl document1 = DeserializeDocument(doc);
			   
			   
			return (Document) document1;			   
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	         return null;
	}


	/**
	 * @param doc
	 * @return
	 */
	private DocumentImpl DeserializeDocument(DBObject doc) {
		String documentXML = (String) doc.get(DOCUMENT);
		   XStream xstream = XStreamUtil.getDefaultXstream();
		   
		   DocumentImpl document1=new DocumentImpl((String) doc.get(NAME));
		   document1=(DocumentImpl)xstream.fromXML(documentXML, (Object)document1);
		return document1;
	}
	
	public List<Document> getDocuments(DBObject query) throws DocumentNotFoundException {
		System.out.println("getDocument");
		Document document = null;
		List<Document> docs=new ArrayList<Document>();
		try {
			
			DBCollection table = db.getCollection(DOCUMENTS);

			DBCursor gdocs = table.find(query);
			
			
			while (gdocs.hasNext()) {
				DBObject f = gdocs.next();				
				Document doc = DeserializeDocument(f);
				docs.add(doc);
			}			   
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return docs;
	}


	public byte[] getContent(Document document) throws DocumentNotFoundException {
		// TODO Auto-generated method stub
		 
		 DBObject dbObject = getDocumentFromMongoDB(document);
		 
		 Object docId = dbObject.get(FILE_ID);
		 if(docId==null)
			 return null;
         GridFS gfsPhoto = new GridFS(db, "FILE_TABLE");
         
		 GridFSDBFile doc = gfsPhoto.findOne((ObjectId) docId);
		 
		 ByteArrayOutputStream out=new ByteArrayOutputStream();
		try {
			doc.writeTo(out);
			return out.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	private DBObject getDocumentFromMongoDB(Document document) {
		DBCollection table = db.getCollection(DOCUMENTS);
		 String id = document.getId();
		 ObjectId oid=new ObjectId(id);
		
		 DBObject dbObject = table.findOne(oid);
		return dbObject;
	}

	
	public SearchResult search(DocumentSearchBuilder builder, int fromResult, int maxResults) {
	
		HashMap<DocumentIndex, DocumentCriterion> crits = new HashMap<DocumentIndex, DocumentCriterion>();
        for (Object o : builder.getQuery()) {
            if (o instanceof String) {
            } 
            else if (o instanceof DocumentCriterion) {
                DocumentCriterion dc = (DocumentCriterion) o;
                crits.put(dc.getField(), dc);
            }
        }
        ProcessInstanceUUID processInstanceUUID=null;
        ProcessDefinitionUUID processDefinitionUUID=null;
        
        String procDefString = crits.containsKey(DocumentIndex.PROCESS_DEFINITION_UUID) ? crits.get(DocumentIndex.PROCESS_DEFINITION_UUID).getValue().toString() : null;
        String procInstString = crits.containsKey(DocumentIndex.PROCESS_INSTANCE_UUID) ? crits.get(DocumentIndex.PROCESS_INSTANCE_UUID).getValue().toString() : "DEFINITION_LEVEL_DOCUMENT";
        
        String attachmentName = crits.containsKey(DocumentIndex.NAME) ? crits.get(DocumentIndex.NAME).getValue().toString() : null;

        List<Document> files = new ArrayList<Document>();
        try {
        	BasicDBObject query=new  BasicDBObject();
            if (attachmentName != null)             	 
                query.put(NAME, attachmentName);
            
            if(procInstString != null)
            {
            	query.put(PROCESS_INSTANCE_UUID, procInstString);
            	processInstanceUUID = new ProcessInstanceUUID(procInstString);
            }                        
            if(procDefString != null){
            	query.put(PROCESS_DEFINITION_UUID, procDefString);
            	processDefinitionUUID= new ProcessDefinitionUUID(procDefString);            	
            }
            
            files = getDocuments(query);
            if(files.size()==0 && attachmentName!=null){
            	//try load attachments from processdefinition and initialize.
            	//this happens when document repository does not have documents in it but 
            	
            	files=new ArrayList<Document>();
				//BarResource resource=BarResource.getBarResource(processDefinitionUUID);
            	
            	AttachmentInstnce attachemnt=getLargeDataRepositoryAttachment(processDefinitionUUID, attachmentName);
            	
            	if(attachemnt!=null){
            		String fileType=null;
            		String mimeType=null;
            		AttachmentDefinition attachmentDefinition = attachemnt.getAttachmentDefinition();
					if(attachmentDefinition.getFileName()!=null){
	            		fileType=attachmentDefinition.getFileName().substring(attachmentDefinition.getFileName().lastIndexOf('.')+1);
	            		mimeType=mimeProperties.getString(fileType);
            		}
            		Document doc = createDocument(attachmentDefinition.getName(), 
            				processDefinitionUUID, processInstanceUUID, attachmentDefinition.getFileName(), mimeType, attachemnt.getData());
            		
            		files.add(doc);
            	}
            }
            if (!files.isEmpty()) {
            	return new SearchResult(files, files.size());
            }

            return new SearchResult(files, 0);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
		return null;        
	}
	
	private AttachmentInstnce getLargeDataRepositoryAttachment(ProcessDefinitionUUID processDefinitionUUID, String attachmentName) {
		if(processDefinitionUUID==null)
			return null;
		List<String> attachmentCategories = Misc.getBusinessArchiveCategories(processDefinitionUUID);
		final LargeDataRepository ldr = EnvTool.getLargeDataRepository();
		
		try {
			InternalProcessDefinition process = FacadeUtil.getProcessDefinition(processDefinitionUUID);
			
			AttachmentDefinition attachmentdef = process.getAttachment(attachmentName);
			AttachmentInstnce attachment=new AttachmentInstnce();
			attachment.setAttachmentDefinition(attachmentdef);
			
			if(attachmentdef.getFilePath()!=null){
				byte[] data = ldr.getData(byte[].class,attachmentCategories, attachmentdef.getFilePath());
				attachment.setData(data);
			}
			return attachment;						
			
		} catch (ProcessNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   	return null;			
	}
	
	public Folder createFolder(String folderName) throws FolderAlreadyExistsException {
		throw new NotImplementedException("createFolder1");
	}

	public Folder createFolder(String folderName, String parentFolderId) throws FolderAlreadyExistsException {
		throw new NotImplementedException("createDocument2");
	}

	public Document createDocument(String name, ProcessDefinitionUUID definitionUUID, ProcessInstanceUUID instanceUUID) throws DocumentationCreationException, DocumentAlreadyExistsException {
		return createDocument(name, definitionUUID, instanceUUID, "null", "null", new byte[0]);
	}

	public Document createDocument(String name, String folderId, String fileName, String contentMimeType, byte[] fileContent) throws DocumentationCreationException, DocumentAlreadyExistsException {
		throw new NotImplementedException("createDocument3");
	}

	public Document createDocument(String name, ProcessDefinitionUUID definitionUUID, ProcessInstanceUUID instanceUUID, String author, Date versionDate) throws DocumentationCreationException, DocumentAlreadyExistsException {
		throw new NotImplementedException("createDocument4");
	}

	public Document createDocument(String name, ProcessDefinitionUUID definitionUUID, ProcessInstanceUUID instanceUUID, String author, Date versionDate, String fileName, String mimeType, byte[] content) throws DocumentationCreationException, DocumentAlreadyExistsException {
		throw new NotImplementedException("createDocument5");
	}
	public List<Folder> getFolders(String folderName) {
		throw new NotImplementedException("getFolders");
	}

	public Folder getRootFolder() {
		throw new NotImplementedException("getRootFolder");
	}

	public List<Document> getChildrenDocuments(String folderId) {
		throw new NotImplementedException("getChildrenDocuments");
	}

	public List<Folder> getChildrenFolder(String folderId) {
		// TODO Auto-generated method stub
		throw new NotImplementedException("cetChildrenFolder");
	}

	public List<Document> getVersionsOfDocument(String documentId)
			throws DocumentNotFoundException {
		List<Document> docs=new ArrayList<Document>();
		Document document = new DocumentImpl(documentId);
		document.setId(documentId);
		 DBObject dbObject = getDocumentFromMongoDB(document);
		 if(dbObject==null)
			 return docs;
		 Document doc = DeserializeDocument(dbObject);
		 docs.add(doc);
		return docs;
	}

	public String getDocumentPath(String documentId)
			throws DocumentNotFoundException {
		throw new NotImplementedException("getDocumentPath");
	}

	public Document createVersion(String documentId, boolean isMajorVersion)
			throws DocumentationCreationException {
		throw new NotImplementedException("create version4");
	}

	public Document createVersion(String documentId, boolean isMajorVersion,
			String author, Date versionDate)
			throws DocumentationCreationException {
		throw new NotImplementedException("create version3");
	}

	public Document createVersion(String documentId, boolean isMajorVersion,
			String fileName, String mimeType, byte[] content)
			throws DocumentationCreationException {
		
		GridFS gfs = new GridFS(db, "FILE_TABLE");
		DBCollection table = db.getCollection(DOCUMENTS);		   
		
		DBObject doc = table.findOne(new ObjectId(documentId));
		DocumentImpl impl = DeserializeDocument(doc);
		DocumentImpl impl2 = new DocumentImpl(impl.getName(), impl.getParentFolderId(), impl.getAuthor(), impl.getCreationDate(), new Date(), true, true, "1", "1", fileName, mimeType, content.length, impl.getProcessDefinitionUUID(), impl.getProcessInstanceUUID());
		impl2.setId(documentId);
		
		//doc.put(NAME, fileName);
		doc.put(LENGHT, content.length);
		doc.put(DOCUMENT, impl2.toString());
		
		if(content!=null && content.length>0){
			GridFSInputFile file = gfs.createFile(content);
			file.setFilename(fileName);
			file.setContentType(mimeType);
			
			DBObject metaData = file.getMetaData();			              	 
			metaData.put(PROCESS_INSTANCE_UUID, impl.getProcessInstanceUUID().toString());
            metaData.put(PROCESS_DEFINITION_UUID, impl.getProcessDefinitionUUID().toString());	            
            metaData.put(DOCUMENT, impl2.toString());
	            
			file.save();
			doc.put(FILE_ID, file.getId());
		}
		
		table.save(doc);
		return impl2;
	}

	public Document createVersion(String documentId, boolean isMajorVersion,
			String author, Date versionDate, String fileName, String mimeType,
			byte[] content) throws DocumentationCreationException {
		// TODO Auto-generated method stub
		throw new NotImplementedException("creteVersion");
		
	}

	
	public void clear() throws DocumentNotFoundException {
		// TODO Auto-generated method stub
		
	}

	public void updateDocumentContent(String documentId, String fileName,
			String mimeType, int size, byte[] content)
			throws DocumentNotFoundException {
		// TODO Auto-generated method stub
		throw new NotImplementedException("updateDocumentContent");
	}

	public void attachDocumentTo(ProcessDefinitionUUID processDefinitionUUID,
			String documentId) throws DocumentNotFoundException {
		// TODO Auto-generated method stub
		throw new NotImplementedException("attachDocumentTo");
	}

	public void attachDocumentTo(ProcessDefinitionUUID processDefinitionUUID,
			ProcessInstanceUUID processInstanceUUID, String documentId)
			throws DocumentNotFoundException {
		
		Document document = new DocumentImpl(documentId);
		document.setId(documentId);
		DBObject dbObject = getDocumentFromMongoDB(document);
		DocumentImpl impl = DeserializeDocument(dbObject);
		byte[] fileContent = getContent(impl);
		try {
			createDocument(impl.getName(), processDefinitionUUID, processInstanceUUID, impl.getContentFileName(), impl.getContentMimeType(), fileContent );
		} catch (DocumentAlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentationCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void deleteDocument(String documentId, boolean allVersions) throws DocumentNotFoundException {
		// TODO Auto-generated method stub
		throw new NotImplementedException("deleteDocument");
	}

	public void deleteFolder(Folder folder) {
		// TODO Auto-generated method stub
		throw new NotImplementedException("deleteFolder");
	}	
	private class AttachmentInstnce {
		
		private byte[] _data;
		private AttachmentDefinition attachmentDefinition;
		public void setData(byte[] _data) {
			this._data = _data;
		}

		public byte[] getData() {
			return _data;
		}

		public void setAttachmentDefinition(AttachmentDefinition attachmentDefinition) {
			this.attachmentDefinition = attachmentDefinition;
		}

		public AttachmentDefinition getAttachmentDefinition() {
			return attachmentDefinition;
		}
		
	}
}
