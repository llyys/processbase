/** 
 * Copyright (C) 2011 PROCESSBASE
 * PROCESSBASE Ltd
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.processbase.bonita.services.impl;

import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.ow2.bonita.facade.exception.DocumentAlreadyExistsException;
import org.ow2.bonita.facade.exception.DocumentNotFoundException;
import org.ow2.bonita.facade.exception.DocumentationCreationException;
import org.ow2.bonita.facade.exception.FolderAlreadyExistsException;
import org.ow2.bonita.facade.impl.SearchResult;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.search.DocumentCriterion;
import org.ow2.bonita.search.DocumentSearchBuilder;
import org.ow2.bonita.search.index.DocumentIndex;
import org.ow2.bonita.services.Document;
import org.ow2.bonita.services.DocumentationManager;
import org.ow2.bonita.services.Folder;
import org.ow2.bonita.services.impl.DocumentImpl;

/**
 *
 * @author marat
 */
public class LiferayDocumentationManager implements DocumentationManager {

    private LiferayDocumentLibrary documentLibrary;

    public LiferayDocumentationManager(final String companyMx, final String group, final String user) {
        try {
//            System.out.println("company = " + company);
//            System.out.println("group = " + group);
//            System.out.println("user = " + user);
            documentLibrary = new LiferayDocumentLibrary(companyMx, group, user);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Folder createFolder(String folderName) throws FolderAlreadyExistsException {
        System.out.println("createFolder1");
//        System.out.println("folderName = " + folderName);
        return null;
    }

    @Override
    public Folder createFolder(String folderName, String parentFolderId) throws FolderAlreadyExistsException {
        System.out.println("createFolder2");
//        System.out.println("folderName = " + folderName);
//        System.out.println("parentFolderId = " + parentFolderId);
        return null;
    }

    @Override
    public Document createDocument(String name, ProcessDefinitionUUID definitionUUID, ProcessInstanceUUID instanceUUID) throws DocumentationCreationException, DocumentAlreadyExistsException {
//        System.out.println("createDocument");
        return createDocument(name, definitionUUID, instanceUUID, "null", "null", new byte[0]);
    }

    @Override
    public Document createDocument(String name, ProcessDefinitionUUID definitionUUID, ProcessInstanceUUID instanceUUID, String fileName, String contentMimeType, byte[] fileContent) throws DocumentationCreationException, DocumentAlreadyExistsException {
//        System.out.println("createDocument2");
//        System.out.println("name = " + name);
//        System.out.println("definitionUUID = " + definitionUUID);
//        System.out.println("instanceUUID = " + instanceUUID);
//        System.out.println("fileName = " + fileName);
//        System.out.println("contentMimeType = " + contentMimeType);
        DocumentImpl doc = null;
        try {
            String definition = definitionUUID.toString();
            String instance = instanceUUID != null ? instanceUUID.toString() : "DEFINITION_LEVEL_DOCUMENT";
            DLFileEntry fileEntry = documentLibrary.addFile(definition, instance, name, fileName, fileContent);
            doc = new DocumentImpl(name);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return doc;
    }

    @Override
    public Document createDocument(String name, String folderId, String fileName, String contentMimeType, byte[] fileContent) throws DocumentationCreationException, DocumentAlreadyExistsException {
        System.out.println("createDocument3");
        return null;
    }

    @Override
    public Document createDocument(String name, ProcessDefinitionUUID definitionUUID, ProcessInstanceUUID instanceUUID, String author, Date versionDate) throws DocumentationCreationException, DocumentAlreadyExistsException {
        System.out.println("createDocument4");
        return null;
    }

    @Override
    public Document createDocument(String name, ProcessDefinitionUUID definitionUUID, ProcessInstanceUUID instanceUUID, String author, Date versionDate, String fileName, String mimeType, byte[] content) throws DocumentationCreationException, DocumentAlreadyExistsException {
        System.out.println("createDocument5");
        return null;
    }

    @Override
    public Document getDocument(String documentId) throws DocumentNotFoundException {
        System.out.println("getDocument");
        Document document = null;
        try {
            DLFileEntry file = documentLibrary.getFileEntry(Long.parseLong(documentId));
            ProcessDefinitionUUID pdUUID = new ProcessDefinitionUUID(file.getFolder().getParentFolder().getName());
            ProcessInstanceUUID piUUID = new ProcessInstanceUUID(file.getFolder().getName());
            
            document = new DocumentImpl(file.getTitle(), "folderId",
                    "author", new Date(), new Date(), true, true, file.getVersion(), "", file.getDescription(),
                    "mimetype", file.getSize(), pdUUID, piUUID);
            document.setId(String.valueOf(file.getFileEntryId()));
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
         return document;
    }

    @Override
    public void deleteDocument(String documentId, boolean allVersions) throws DocumentNotFoundException {
        System.out.println("deleteDocument");
    }

    @Override
    public void deleteFolder(Folder folder) {
        System.out.println("deleteFolder");
    }

    @Override
    public byte[] getContent(Document document) throws DocumentNotFoundException {
//        System.out.println("getContent");
//        System.out.println("document.name = " + document.getName());
//        System.out.println("document.id = " + document.getId());
//        System.out.println("document.contentfilename = " + document.getContentFileName());
//        System.out.println("document.getProcessDefinitionUUID = " + document.getProcessDefinitionUUID());
//        System.out.println("document.getProcessInstanceUUID = " + document.getProcessInstanceUUID());
//        String procDef = document.getProcessDefinitionUUID() != null ? document.getProcessDefinitionUUID().toString() : null;
//        String procInst = document.getProcessInstanceUUID() != null ? document.getProcessInstanceUUID().toString() : null;
        byte[] result = documentLibrary.getFileBody(Long.parseLong(document.getId()));
        return result;
    }

    @Override
    public List<Folder> getFolders(String folderName) {
        System.out.println("getFolders");
        return null;
    }

    @Override
    public Folder getRootFolder() {
        System.out.println("getRootFolder");
        return null;
    }

    @Override
    public List<Document> getChildrenDocuments(String folderId) {
        System.out.println("getChildrenDocuments");
        return null;
    }

    @Override
    public List<Folder> getChildrenFolder(String folderId) {
        System.out.println("getChildrenFolder");
        return null;
    }

    @Override
    public List<Document> getVersionsOfDocument(String documentId) throws DocumentNotFoundException {
        System.out.println("getVersionsOfDocument");
        return null;
    }

    @Override
    public String getDocumentPath(String documentId) throws DocumentNotFoundException {
        System.out.println("getDocumentPath");
        return null;
    }

    @Override
    public Document createVersion(String documentId, boolean isMajorVersion) throws DocumentationCreationException {
        System.out.println("createVersion");
        return null;
    }

    @Override
    public Document createVersion(String documentId, boolean isMajorVersion, String author, Date versionDate) throws DocumentationCreationException {
        System.out.println("createVersion2");
        return null;
    } 

    @Override
    public Document createVersion(String documentId, boolean isMajorVersion, String fileName, String mimeType, byte[] content) throws DocumentationCreationException {
        System.out.println("createVersion3");
//        System.out.println("documentId = " + documentId);
//        System.out.println("isMajorVersion = " + isMajorVersion);
//        System.out.println("fileName = " + fileName);
//        System.out.println("mimeType = " + mimeType);
//        System.out.println("content = " + content.length);
        Document document = null;
        try {
            DLFileEntry file = documentLibrary.getFileEntry(Long.valueOf(documentId).longValue());
            file.setDescription(fileName);
            DLFileEntry result = documentLibrary.updateFileEntry(file.getFolderId(), file.getName(), fileName, file.getTitle(), file.getDescription(),
                    "", true, file.getExtraSettings(), content);


            ProcessDefinitionUUID pdUUID = new ProcessDefinitionUUID(result.getFolder().getParentFolder().getName());
            ProcessInstanceUUID piUUID = new ProcessInstanceUUID(result.getFolder().getName());
            document = new DocumentImpl(result.getTitle(), "folderId",
                    "author", new Date(), new Date(), true, true, result.getVersion(), "", result.getDescription(),
                    "mimetype", result.getSize(), pdUUID, piUUID);
            document.setId(String.valueOf(result.getFileEntryId()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return document;
    }

    @Override
    public Document createVersion(String documentId, boolean isMajorVersion, String author, Date versionDate, String fileName, String mimeType, byte[] content) throws DocumentationCreationException {
        System.out.println("createVersion4");
        return null;
    }

    @Override
    public SearchResult search(DocumentSearchBuilder builder, int fromResult, int maxResults) {
        System.out.println("search");
////        File x = new File("builder.xml");
//        try {
//            FileOutputStream fos = new FileOutputStream(x);
//            fos.write(builder.toString().getBytes());
//            fos.close();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        HashMap<DocumentIndex, DocumentCriterion> crits = new HashMap<DocumentIndex, DocumentCriterion>();
        for (Object o : builder.getQuery()) {
            if (o instanceof String) {
            } else if (o instanceof DocumentCriterion) {
                DocumentCriterion dc = (DocumentCriterion) o;
                crits.put(dc.getField(), dc);
//                System.out.println("crit = " + dc.getField() + " = " + dc.getValue());
            }
        }
        String procDefString = crits.containsKey(DocumentIndex.PROCESS_DEFINITION_UUID) ? crits.get(DocumentIndex.PROCESS_DEFINITION_UUID).getValue().toString() : null;
        String procInstString = crits.containsKey(DocumentIndex.PROCESS_INSTANCE_UUID) ? crits.get(DocumentIndex.PROCESS_INSTANCE_UUID).getValue().toString() : null;

        ProcessInstanceUUID procInst = procInstString != null ? new ProcessInstanceUUID(procInstString) : null;
        ProcessDefinitionUUID procDef = procDefString != null ? new ProcessDefinitionUUID(procDefString) : null;
        if (procDef == null && procInst != null) {
            procDef = procInst.getProcessDefinitionUUID();
            procDefString = procDef.toString();
        }

        String attachmentName = crits.containsKey(DocumentIndex.NAME) ? crits.get(DocumentIndex.NAME).getValue().toString() : null;

        List<DLFileEntry> files = new ArrayList<DLFileEntry>();
        try {
            if (attachmentName == null) {
                files = documentLibrary.getProcessFiles(procDefString, procInstString);
            } else {
                files.add(documentLibrary.getProcessFile(procDefString, procInstString, attachmentName));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        List<Document> list = new ArrayList<Document>();
        if (!files.isEmpty()) {
            for (DLFileEntry file : files) {
//                System.out.println("document = " + file.getFileEntryId() + ", " + file.getTitle());
//                Document document = new DocumentImpl(file.getTitle(), file.getDescription(), "null", file.getSize());
                Document document = new DocumentImpl(file.getTitle(), "folderId",
                        "author", new Date(), new Date(), true, true, file.getVersion(), "", file.getDescription(),
                        "mimetype", file.getSize(), procDef, procInst);
                document.setId(String.valueOf(file.getFileEntryId()));
                list.add(document);
            }
            return new SearchResult(list, list.size());
        } else {
            return new SearchResult(list, 0);
        }
    }

    @Override
    public void clear() throws DocumentNotFoundException {
        System.out.println("clear");
    }

    @Override
    public void updateDocumentContent(String documentId, String fileName, String mimeType, int size, byte[] content) throws DocumentNotFoundException {
        System.out.println("updateDocumentContent");
    }

    @Override
    public void attachDocumentTo(ProcessDefinitionUUID processDefinitionUUID, String documentId) throws DocumentNotFoundException {
        System.out.println("attachDocumentTo");
    }

    @Override
    public void attachDocumentTo(ProcessDefinitionUUID processDefinitionUUID, ProcessInstanceUUID processInstanceUUID, String documentId) throws DocumentNotFoundException {
        System.out.println("attachDocumentTo2");
    }
}
