package org.processbase.bonita.services.impl;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.documentlibrary.NoSuchFolderException;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalService;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalService;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

/**
 * {@link DocumentationManager} that holds documents in Liferay document
 * library.
 * 
 * @author Margo
 */
public class LiferayDocumentManager implements DocumentationManager {

	/** Logger. */
	private static final Logger LOG = Logger
			.getLogger(LiferayDocumentManager.class);

	private DLFolderLocalService folderService = null;
	private DLFileEntryLocalService fileService = null;

	private Organization organization;
	private Company company;
	private User user;
	private Group group;

	private ResourceBundle mimeTypes = null;

	/**
	 * {@link LiferayDocumentManager} constructor.
	 * 
	 * @param companyMx
	 *            company MX.
	 * @param organizationId
	 *            organization ID.
	 * @param groupName
	 *            group name.
	 * @param userScreenName
	 *            user screen name.
	 * @throws PortalException
	 * @throws SystemException
	 */
	public LiferayDocumentManager(String companyMx, String organizationId,
			String groupName, String userScreenName) throws PortalException,
			SystemException {
		super();
		if (LOG.isDebugEnabled()) {
			LOG.debug("LiferayDocumentManager companyMx:" + companyMx
					+ " groupName:" + groupName + " userScreenName:"
					+ userScreenName);
		}


		folderService = DLFolderLocalServiceUtil.getService();
		fileService = DLFileEntryLocalServiceUtil.getService();

		company = CompanyLocalServiceUtil.getCompanyByMx(companyMx);

		organization = OrganizationLocalServiceUtil.getOrganization(Long
				.parseLong(organizationId));

		user = UserLocalServiceUtil.getUserByScreenName(company.getCompanyId(),
				userScreenName);
		group = GroupLocalServiceUtil.getGroup(company.getCompanyId(),
				groupName);

		mimeTypes = ResourceBundle.getBundle("mime");

	}

	public Folder createFolder(String folderName)
			throws FolderAlreadyExistsException {

		throw new UnsupportedOperationException("createFolder(folderName)");
	}

	public Folder createFolder(String folderName, String parentFolderId)
			throws FolderAlreadyExistsException {

		throw new UnsupportedOperationException(
				"createFolder(folderName, parentFolderId)");
	}

	public Document createDocument(String name,
			ProcessDefinitionUUID definitionUUID,
			ProcessInstanceUUID instanceUUID)
			throws DocumentationCreationException,
			DocumentAlreadyExistsException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("createDocument(name, definitionUUID, pinstanceUUID)");
		}
		return createDocument(name, definitionUUID, instanceUUID, null, null,
				new byte[0]);
	}

	public Document createDocument(String name,
			ProcessDefinitionUUID definitionUUID,
			ProcessInstanceUUID instanceUUID, String fileName,
			String contentMimeType, byte[] fileContent)
			throws DocumentationCreationException,
			DocumentAlreadyExistsException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("createDocument(" + name + ", " + definitionUUID + ", "
					+ instanceUUID + ", " + fileName + ", " + contentMimeType
					+ ", "
					+ (fileContent != null ? fileContent.length : "null") + ")");
		}

		Document doc = null;
		try {
			String definition = definitionUUID.toString();
			String instance = "DEFINITION_LEVEL_DOCUMENT";
			if (instanceUUID != null) {
				instance = instanceUUID.toString();
			}

			// Add file
			DLFileEntry file = addFile(definition, instance, name, fileName,
					contentMimeType, fileContent);

			// Create document info
			doc = createDocumentInfo(file);

		} catch (Exception e) {
			LOG.warn("in createDocument", e);
			e.printStackTrace();
			throw new DocumentationCreationException(e.getMessage());
		}

		return doc;
	}

	public Document createDocument(String name, String folderId,
			String fileName, String contentMimeType, byte[] fileContent)
			throws DocumentationCreationException,
			DocumentAlreadyExistsException {

		throw new UnsupportedOperationException(
				"createDocument(name, folderId, fileName, contentMimeType, fileContent)");
	}

	public Document createDocument(String name,
			ProcessDefinitionUUID definitionUUID,
			ProcessInstanceUUID instanceUUID, String author, Date versionDate)
			throws DocumentationCreationException,
			DocumentAlreadyExistsException {

		throw new UnsupportedOperationException(
				"createDocument(name, definitionUUID, instanceUUID, author, versionDate)");
	}

	public Document createDocument(String name,
			ProcessDefinitionUUID definitionUUID,
			ProcessInstanceUUID instanceUUID, String author, Date versionDate,
			String fileName, String mimeType, byte[] content)
			throws DocumentationCreationException,
			DocumentAlreadyExistsException {

		throw new UnsupportedOperationException(
				"createDocument(String name, definitionUUID, instanceUUID, "
						+ "author, versionDate, fileName, mimeType, content)");
	}

	public Document getDocument(String documentId)
			throws DocumentNotFoundException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("getDocument(" + documentId + ")");
		}

		Document document = null;
		try {
			DLFileEntry file = fileService.getDLFileEntry(Long
					.parseLong(documentId));

			// Create document info
			document = createDocumentInfo(file);

		} catch (Exception e) {
			LOG.warn("in createDocument", e);
			throw new DocumentNotFoundException(e.getMessage());
		}
		return document;
	}

	public void deleteDocument(String documentId, boolean allVersions)
			throws DocumentNotFoundException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("deleteDocument(" + documentId + ", " + allVersions + ")");
		}
		try {
			DLFileEntry file = fileService.getDLFileEntry(Long
					.parseLong(documentId));
			if (file != null) {
				fileService.deleteDLFileEntry(file);
			} else {
				throw new DocumentNotFoundException("");
			}
		} catch (Exception e) {
			LOG.warn("in createDocument", e);
			throw new DocumentNotFoundException(e.getMessage());
		}

	}

	public void deleteFolder(Folder folder) {
		throw new UnsupportedOperationException("deleteFolder(folder)");
	}

	public byte[] getContent(Document document)
			throws DocumentNotFoundException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("getContent(document)");
		}
		try {
			DLFileEntry file = fileService.getDLFileEntry(Long
					.parseLong(document.getId()));
			if (file != null) {
				if (file.getSize() > 0) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();

					IOUtils.copy(fileService.getFileAsStream(
							file.getCompanyId(), file.getUserId(),
							file.getGroupId(), file.getFolderId(),
							file.getName()), out);

					return out.toByteArray();
				} else {
					return null;
				}
			} else {
				throw new DocumentNotFoundException("");
			}
		} catch (Exception e) {
			LOG.warn("in getContent", e);
			throw new DocumentNotFoundException(e.getMessage());
		}
	}

	public List<Folder> getFolders(String folderName) {
		throw new UnsupportedOperationException("getFolders(folderName)");
	}

	public Folder getRootFolder() {
		throw new UnsupportedOperationException("getRootFolder()");
	}

	public List<Document> getChildrenDocuments(String folderId) {
		throw new UnsupportedOperationException(
				"getChildrenDocuments(folderId)");
	}

	public List<Folder> getChildrenFolder(String folderId) {
		throw new UnsupportedOperationException("getChildrenFolder(folderId)");
	}

	public List<Document> getVersionsOfDocument(String documentId)
			throws DocumentNotFoundException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("getVersionsOfDocument(" + documentId + ")");
		}

		try {
			List<Document> docs = new ArrayList<Document>();
			docs.add(getDocument(documentId));

			return docs;
		} catch (Exception e) {
			LOG.warn("in getVersionsOfDocument", e);
			e.printStackTrace();
			throw new DocumentNotFoundException(e.getMessage());
		}
	}

	public String getDocumentPath(String documentId)
			throws DocumentNotFoundException {
		throw new UnsupportedOperationException("getDocumentPath(documentId)");
	}

	public Document createVersion(String documentId, boolean isMajorVersion)
			throws DocumentationCreationException {
		throw new UnsupportedOperationException(
				"createVersion(documentId, isMajorVersion)");
	}

	public Document createVersion(String documentId, boolean isMajorVersion,
			String author, Date versionDate)
			throws DocumentationCreationException {
		throw new UnsupportedOperationException(
				"createVersion(documentId, isMajorVersion, author, versionDate)");
	}

	public Document createVersion(String documentId, boolean isMajorVersion,
			String fileName, String mimeType, byte[] content)
			throws DocumentationCreationException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("createVersion(" + documentId + ", " + isMajorVersion
					+ ", " + fileName + ", " + mimeType + ", " + content + ")");
		}

		Document document = null;
		try {
			// Find file
			DLFileEntry file = fileService.getDLFileEntry(Long
					.valueOf(documentId));

			// Update file
			DLFileEntry result = fileService.updateFileEntry(file.getUserId(),
					file.getGroupId(), file.getFolderId(), file.getName(),
					fileName, file.getTitle(), fileName, "", true, null,
					content, new ServiceContext());

			document = createDocumentInfo(result);
		} catch (Exception e) {
			LOG.warn("in createVersion", e);
			e.printStackTrace();
			throw new DocumentationCreationException(e.getMessage());
		}

		return document;
	}

	public Document createVersion(String documentId, boolean isMajorVersion,
			String author, Date versionDate, String fileName, String mimeType,
			byte[] content) throws DocumentationCreationException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("createVersion(documentId, isMajorVersion,"
					+ " author, versionDate, fileName, mimeType, content)");
		}
		return createVersion(documentId, isMajorVersion, fileName, mimeType,
				content);
	}

	public SearchResult search(DocumentSearchBuilder builder, int fromResult,
			int maxResults) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("search(builder, fromResult, maxResults)");
		}
		try {
			Map<DocumentIndex, DocumentCriterion> criterions = new HashMap<DocumentIndex, DocumentCriterion>();

			for (Object o : builder.getQuery()) {
				if (o instanceof String) {
				} else if (o instanceof DocumentCriterion) {
					DocumentCriterion dc = (DocumentCriterion) o;
					criterions.put(dc.getField(), dc);
				}
			}

			// Process definition UUID
			String pdUUID = null;
			if (criterions.containsKey(DocumentIndex.PROCESS_DEFINITION_UUID)) {
				pdUUID = criterions.get(DocumentIndex.PROCESS_DEFINITION_UUID)
						.getValue().toString();
			}

			// Process instance UUID
			String piUUID = null;
			if (criterions.containsKey(DocumentIndex.PROCESS_INSTANCE_UUID)) {
				piUUID = criterions.get(DocumentIndex.PROCESS_INSTANCE_UUID)
						.getValue().toString();
			}

			// Attachment name
			String attachmentName = null;
			if (criterions.containsKey(DocumentIndex.NAME)) {
				attachmentName = criterions.get(DocumentIndex.NAME).getValue()
						.toString();
			}

			if (LOG.isDebugEnabled()) {
				LOG.debug("pdUUID:" + pdUUID + " piUUID:" + piUUID
						+ " attachmentName:" + attachmentName);
			}

			List<DLFileEntry> files = new ArrayList<DLFileEntry>();
			try {
				if (attachmentName == null) {
					files.addAll(getProcessFiles(pdUUID, piUUID));
				} else {
					files.add(getProcessFile(pdUUID, piUUID, attachmentName));
				}
			} catch (Exception e) {
				LOG.debug("in search", e);
			}

			List<Document> docs = new ArrayList<Document>();
			for (DLFileEntry file : files) {
				try {
					docs.add(createDocumentInfo(file));
				} catch (Exception e) {
					LOG.warn("in search (create info)", e);
				}
			}

			LOG.debug("search results: " + docs);

			return new SearchResult(docs, docs.size());
		} catch (Exception e) {
			LOG.warn("search failed");
		}
		return null;
	}

	public void clear() throws DocumentNotFoundException {
		throw new UnsupportedOperationException("clear()");
	}

	public void updateDocumentContent(String documentId, String fileName,
			String mimeType, int size, byte[] content)
			throws DocumentNotFoundException {
		throw new UnsupportedOperationException(
				"updateDocumentContent(documentId, fileName, mimeType, size, content");
	}

	public void attachDocumentTo(ProcessDefinitionUUID processDefinitionUUID,
			String documentId) throws DocumentNotFoundException {
		throw new UnsupportedOperationException(
				"attachDocumentTo(processDefinitionUUID, documentId)");
	}

	public void attachDocumentTo(ProcessDefinitionUUID processDefinitionUUID,
			ProcessInstanceUUID processInstanceUUID, String documentId)
			throws DocumentNotFoundException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("attachDocumentTo(processDefinitionUUID, processInstanceUUID, documentId)");
		}

		try {
			DLFileEntry file = fileService.getDLFileEntry(Long
					.parseLong(documentId));

			ByteArrayOutputStream content = new ByteArrayOutputStream();

			IOUtils.copy(
					fileService.getFileAsStream(file.getCompanyId(),
							file.getUserId(), file.getGroupId(),
							file.getFolderId(), file.getName()), content);

			createDocument(file.getTitle(), processDefinitionUUID,
					processInstanceUUID, file.getDescription(),
					file.getExtraSettings(), content.toByteArray());

		} catch (Exception e) {
			LOG.warn("in createDocument", e);
			throw new DocumentNotFoundException(e.getMessage());
		}
	}

	/**
	 * Get document info.
	 * 
	 * @param file
	 *            document file.
	 * @return document info.
	 * @throws PortalException
	 * @throws SystemException
	 */
	private Document createDocumentInfo(DLFileEntry file)
			throws PortalException, SystemException {

		ProcessDefinitionUUID pdUUID = new ProcessDefinitionUUID(file
				.getFolder().getParentFolder().getName());
		ProcessInstanceUUID piUUID = new ProcessInstanceUUID(file.getFolder()
				.getName());

		String mimeType = "";
		if (StringUtils.isEmpty(mimeType)) {
			mimeType = getMimeType(file.getDescription());
		}

		Document doc = new DocumentImpl(file.getTitle(), file.getFolderId()
				+ "", "author", file.getCreateDate(), file.getModifiedDate(),
				true, true, file.getVersion(), "", file.getDescription(),
				mimeType, file.getSize(), pdUUID, piUUID);
		doc.setId(String.valueOf(file.getFileEntryId()));

		return doc;
	}

	/**
	 * Add file to Liferay document store.
	 * 
	 * @param definitionUUID
	 *            process definition UUID.
	 * @param instanceUUID
	 *            process instance UUID.
	 * @param name
	 *            document name.
	 * @param fileName
	 *            file name.
	 * @param mimeType
	 *            MIME type.
	 * @param body
	 *            document content.
	 * @return {@link DLFileEntry} created.
	 * @throws PortalException
	 * @throws SystemException
	 */
	public DLFileEntry addFile(String definitionUUID, String instanceUUID,
			String name, String fileName, String mimeType, byte[] body)
			throws PortalException, SystemException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("addFile(" + definitionUUID + ", " + instanceUUID + ", "
					+ name + ", " + fileName + ", " + mimeType + ", "
					+ (body != null ? body.length : "null"));
		}

		DLFileEntry fileEntry = null;

		DLFolder folder = getFolder(definitionUUID, instanceUUID);

		// try to find file entry
		try {
			fileEntry = fileService.getFileEntryByTitle(group.getGroupId(),
					folder.getFolderId(), name);
		} catch (Exception e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("could not find file entry" + e.getMessage());
			}
		}

		if (StringUtils.isEmpty(mimeType)) {
			mimeType = getMimeType(fileName);
		}

		if (body == null) {
			body = new byte[0];
		}

		if (fileEntry != null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("update file entry");
			}

			// try to update file entry
			fileEntry = fileService.updateFileEntry(fileEntry.getUserId(),
					fileEntry.getGroupId(), folder.getFolderId(),
					fileEntry.getName(), fileName, name, fileName, null, true,
					null, body, new ServiceContext());

		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("add file entry");
			}

			// try to add file entry
			fileEntry = fileService.addFileEntry(user.getUserId(),
					group.getGroupId(), folder.getFolderId(), fileName, name,
					fileName, null, null, body, new ServiceContext());

		}

		return fileEntry;
	}

	/**
	 * Get Liferay document store folder.
	 * 
	 * @param parentFolderId
	 *            parent folder.
	 * @param folderName
	 *            folder name.
	 * @return {@link DLFolder}
	 * @throws SystemException
	 * @throws PortalException
	 */
	private DLFolder getFolder(long parentFolderId, String folderName)
			throws PortalException, SystemException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("getFolder(" + parentFolderId + ", " + folderName + ")");
		}

		DLFolder result = null;
		try {
			result = folderService.getFolder(group.getGroupId(),
					parentFolderId, folderName);
		} catch (NoSuchFolderException e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("create folder " + folderName);
			}
			result = folderService.addFolder(user.getUserId(),
					group.getGroupId(), parentFolderId, folderName, folderName,
					new ServiceContext());
		}

		try {
			folderService.addFolderResources(result, false, false);
		} catch (Exception e) {
			LOG.warn("in getFolder", e);
		}

		return result;
	}

	/**
	 * Get process files folder.
	 * 
	 * @param pdUUID
	 *            process definition UUID.
	 * @param piUUID
	 *            process instance UUID.
	 * @return folder.
	 * @throws PortalException
	 * @throws SystemException
	 */
	public DLFolder getFolder(String pdUUID, String piUUID)
			throws PortalException, SystemException {
		if (piUUID == null) {
			piUUID = "DEFINITION_LEVEL_DOCUMENT";
		}

		if (pdUUID == null) {
			pdUUID = piUUID.substring(0, piUUID.lastIndexOf("--"));
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("getFolder pdUUID:" + pdUUID + " piUUID:" + piUUID);
		}

		DLFolder orgFolder = getFolder(0, "org");// organization.getName());

		DLFolder processRoot = getFolder(orgFolder.getFolderId(),
				"DO NOT MODIFY");

		DLFolder parent = getFolder(processRoot.getFolderId(), pdUUID);
		DLFolder folder = getFolder(parent.getFolderId(), piUUID);

		return folder;
	}

	/**
	 * Get process files.
	 * 
	 * @param pdUUID
	 *            process definition UUID.
	 * @param piUUID
	 *            process instance UUID.
	 * @return process files.
	 * @throws PortalException
	 * @throws SystemException
	 */
	public List<DLFileEntry> getProcessFiles(String pdUUID, String piUUID)
			throws PortalException, SystemException {

		DLFolder folder = getFolder(pdUUID, piUUID);
		return fileService.getFileEntries(group.getGroupId(),
				folder.getFolderId());
	}

	/**
	 * Get process file.
	 * 
	 * @param pdUUID
	 *            process definition UUID.
	 * @param piUUID
	 *            process instance UUID.
	 * @param name
	 *            file name.
	 * @return process file.
	 * @throws SystemException
	 * @throws PortalException
	 */
	public DLFileEntry getProcessFile(String pdUUID, String piUUID, String name)
			throws SystemException, PortalException {

		DLFolder folder = getFolder(pdUUID, piUUID);
		return fileService.getFileEntryByTitle(group.getGroupId(),
				folder.getFolderId(), name);
	}

	/**
	 * Try to resolve MIME type.
	 * 
	 * @param fileName
	 *            file name.
	 * @return MIME type found.
	 */
	private String getMimeType(String fileName) {

		String mimeType = "application/octet-stream";
		if (StringUtils.isNotEmpty(fileName)) {
			String extension = FilenameUtils.getExtension(fileName);
			try {
				mimeType = mimeTypes.getString(extension);
			} catch (Exception e) {
				LOG.debug("could not get extension for " + extension);
			}
		}
		return mimeType;
	}

}