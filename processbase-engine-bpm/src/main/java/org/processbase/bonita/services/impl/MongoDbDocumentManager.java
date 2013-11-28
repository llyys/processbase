package org.processbase.bonita.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.ow2.bonita.facade.exception.DocumentAlreadyExistsException;
import org.ow2.bonita.facade.exception.DocumentNotFoundException;
import org.ow2.bonita.facade.exception.DocumentationCreationException;
import org.ow2.bonita.facade.impl.SearchResult;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.search.DocumentCriterion;
import org.ow2.bonita.search.DocumentSearchBuilder;
import org.ow2.bonita.search.index.DocumentIndex;
import org.ow2.bonita.services.Document;
import org.ow2.bonita.services.impl.DocumentImpl;
import org.ow2.bonita.util.EnvTool;
import org.ow2.bonita.util.xml.XStreamUtil;

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

public class MongoDbDocumentManager extends AbstractDocumentationManager{
    private static final String LENGHT = "Lenght";
	private static final String FILE_ID = "FileId";
	private static final String NAME = "Name";
	private static final String DOCUMENT_ID = "ID";
    private static final String DOCUMENT = "document";
	private static final String PROCESS_INSTANCE_UUID = "ProcessInstanceUUID";
	private static final String AUTHOR = "author";
	private static final String PROCESS_DEFINITION_UUID = "ProcessDefinitionUUID";
	private static final String DOCUMENTS = "documents";
    private String pathOfRootFolder;
	private File rootFolder;
	private DB db=null;
	private static Mongo mongo=null;
	
	public MongoDbDocumentManager(String host, String database) {
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
			boolean isNew=false;
			GridFS gfs = new GridFS(db, "FILE_TABLE"); 
			if(fileContent==null)
				fileContent=new byte[0];
			
			BasicDBObject doc = (BasicDBObject) tryFindDockument(definitionUUID, instanceUUID, name);
			if(doc==null)
				{
					isNew=true;
					doc=new BasicDBObject();
				}
			DBCollection table = db.getCollection(DOCUMENTS);
			
			doc.put(LENGHT, fileContent.length);
			doc.put(NAME, name);
			
			if(definitionUUID!=null)
				doc.put(PROCESS_DEFINITION_UUID, definitionUUID.toString());
				
			
			String instance = instanceUUID != null ? instanceUUID.toString() : DEFINITION_LEVEL_DOCUMENT;
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
	
	private DBObject tryFindDockument(ProcessDefinitionUUID processDefinitionUUID, ProcessInstanceUUID processInstanceUUID, String documentName){
		BasicDBObject query=new  BasicDBObject();
        if (documentName != null)             	 
            query.put(NAME, documentName);
    
        if(processInstanceUUID == null)
        	processInstanceUUID=new ProcessInstanceUUID(DEFINITION_LEVEL_DOCUMENT);
        
    	query.put(PROCESS_INSTANCE_UUID, processInstanceUUID.toString());
    	processInstanceUUID = new ProcessInstanceUUID(processInstanceUUID.toString());
                                
        if(processDefinitionUUID != null){
        	query.put(PROCESS_DEFINITION_UUID, processDefinitionUUID.toString());
        	processDefinitionUUID= new ProcessDefinitionUUID(processDefinitionUUID.toString());            	
        }
        DBCollection table = db.getCollection(DOCUMENTS);

		return table.findOne(query);
		
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
        String procInstString = crits.containsKey(DocumentIndex.PROCESS_INSTANCE_UUID) ? crits.get(DocumentIndex.PROCESS_INSTANCE_UUID).getValue().toString() : DEFINITION_LEVEL_DOCUMENT;
        
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
                files = getProcessDefinitionDocuments(processInstanceUUID, processDefinitionUUID, attachmentName);
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

    public Document createDocument(String name, ProcessDefinitionUUID definitionUUID, ProcessInstanceUUID instanceUUID) throws DocumentationCreationException, DocumentAlreadyExistsException {
        return createDocument(name, definitionUUID, instanceUUID, "null", "null", new byte[0]);
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
		System.out.println("deleteDocument");
        try {
		   
			DBCollection table = db.getCollection(DOCUMENTS);

			DBObject doc = table.findOne(new ObjectId(documentId));
			if (doc == null)
				return;
			Object fileId = doc.get(FILE_ID);
			table.remove(doc);

			if (fileId == null)
				return;
			GridFS gridFile = new GridFS(db, "FILE_TABLE");
			gridFile.remove((ObjectId) fileId);
					   
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}


}
