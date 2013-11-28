package org.processbase.bonita.services.impl;

import org.apache.commons.lang.NotImplementedException;
import org.ow2.bonita.facade.def.InternalProcessDefinition;
import org.ow2.bonita.facade.def.element.AttachmentDefinition;
import org.ow2.bonita.facade.exception.*;
import org.ow2.bonita.facade.impl.FacadeUtil;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.services.Document;
import org.ow2.bonita.services.DocumentationManager;
import org.ow2.bonita.services.Folder;
import org.ow2.bonita.services.LargeDataRepository;
import org.ow2.bonita.services.impl.DocumentImpl;
import org.ow2.bonita.util.EnvTool;
import org.ow2.bonita.util.Misc;
import org.processbase.bonita.services.impl.filedocument.AttachmentInstance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public abstract class AbstractDocumentationManager implements DocumentationManager {

    public static final String DEFINITION_LEVEL_DOCUMENT = "DEFINITION_LEVEL_DOCUMENT";
    protected static ResourceBundle mimeProperties=null;

    public Folder createFolder(String folderName) throws FolderAlreadyExistsException {
        throw new NotImplementedException("createFolder1");
    }

    public Folder createFolder(String folderName, String parentFolderId) throws FolderAlreadyExistsException {
        throw new NotImplementedException("createDocument2");
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

    public void deleteFolder(Folder folder) {
        // TODO Auto-generated method stub
        throw new NotImplementedException("deleteFolder");
    }

    protected AttachmentInstance getLargeDataRepositoryAttachment(ProcessDefinitionUUID processDefinitionUUID, String attachmentName) {
        if(processDefinitionUUID==null)
            return null;
        List<String> attachmentCategories = Misc.getBusinessArchiveCategories(processDefinitionUUID);
        final LargeDataRepository ldr = EnvTool.getLargeDataRepository();

        try {
            InternalProcessDefinition process = FacadeUtil.getProcessDefinition(processDefinitionUUID);

            AttachmentDefinition attachmentdef = process.getAttachment(attachmentName);
            AttachmentInstance attachment=new AttachmentInstance();
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

    protected List<Document> getProcessDefinitionDocuments(ProcessInstanceUUID processInstanceUUID, ProcessDefinitionUUID processDefinitionUUID, String attachmentName) throws DocumentationCreationException {
        List<Document> files;//try load attachments from processdefinition and initialize.
        //this happens when document repository does not have documents in it but

        files=new ArrayList<Document>();

        AttachmentInstance attachemnt=getLargeDataRepositoryAttachment(processDefinitionUUID, attachmentName);

        if(attachemnt!=null){
            String fileType=null;
            String mimeType=null;
            AttachmentDefinition attachmentDefinition = attachemnt.getAttachmentDefinition();
            if(attachmentDefinition.getFileName()!=null){
                fileType=attachmentDefinition.getFileName().substring(attachmentDefinition.getFileName().lastIndexOf('.')+1);
                mimeType=mimeProperties.getString(fileType);
            }

            Document document = new DocumentImpl(attachmentDefinition.getName(), DEFINITION_LEVEL_DOCUMENT, EnvTool.getUserId(), new Date(), new Date(), true, true,null, null, attachmentDefinition.getFileName(), mimeType, 0, processDefinitionUUID, null);
            files.add(document);
        }
        return files;
    }
}
