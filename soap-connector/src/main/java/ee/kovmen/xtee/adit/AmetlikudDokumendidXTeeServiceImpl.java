package ee.kovmen.xtee.adit;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import ee.finestmedia.xtee.client.adit.AditRequestInfo;
import ee.finestmedia.xtee.client.adit.AditResponse;
import ee.finestmedia.xtee.client.adit.AmetlikudDokumendidCallback;
import ee.finestmedia.xtee.client.adit.AmetlikudDokumendidXTeeService;
import ee.finestmedia.xtee.client.adit.AmetlikudDokumendidXTeeServiceException;
import ee.finestmedia.xtee.client.adit.domain.Document;
import ee.finestmedia.xtee.client.adit.domain.DocumentListSearch;
import ee.finestmedia.xtee.client.adit.domain.DocumentStatus;
import ee.finestmedia.xtee.client.adit.domain.DocumentType;
import ee.finestmedia.xtee.client.adit.domain.SignerInfo;
import ee.finestmedia.xtee.client.adit.domain.UserType;
import ee.finestmedia.xtee.client.adit.domain.files.DocumentFile;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.ArrayOfCreatorApplication;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.ArrayOfDocumentDvkStatus;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.ArrayOfDocumentType;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.ArrayOfDocumentWorkflowStatus;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.ArrayOfRecipientStatus;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.ArrayOfUserCode;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.ConfirmSignatureRequest;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.ConfirmSignatureResponse;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.DeleteDocumentFileRequest;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.DeleteDocumentFileResponse;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.DeleteDocumentRequest;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.DeleteDocumentResponse;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.GetDocumentFileRequest;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.GetDocumentFileResponse;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.GetDocumentListRequest;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.GetDocumentListResponse;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.GetDocumentListResponse.DocumentList;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.GetDocumentRequest;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.GetDocumentResponse;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.GetUserInfoRequest;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.GetUserInfoResponse;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.GetUserInfoResponse.UserList;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.JoinRequest;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.JoinResponse;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.PrepareSignatureRequest;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.PrepareSignatureResponse;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.RecipientStatus;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.SaveDocumentFileRequest;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.SaveDocumentFileResponse;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.SaveDocumentRequest;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.SaveDocumentResponse;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.SendDocumentRequest;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.SendDocumentResponse;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.ShareDocumentRequest;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.ShareDocumentResponse;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.UnJoinRequest;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.UnJoinResponse;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.UnShareDocumentRequest;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.UnShareDocumentResponse;
import ee.finestmedia.xtee.client.adit.types.noNamespace.DocumentV1Type;
import ee.finestmedia.xtee.client.adit.types.noNamespace.FileType;
import ee.finestmedia.xtee.client.adit.types.noNamespace.GetDocumentFileResponseAttachmentV1Document;
import ee.finestmedia.xtee.client.adit.types.noNamespace.GetDocumentFileResponseAttachmentV1FilesType;
import ee.finestmedia.xtee.client.adit.types.noNamespace.GetDocumentListResponseAttachmentV1Document;
import ee.finestmedia.xtee.client.adit.types.noNamespace.GetDocumentListResponseAttachmentV1DocumentListType;
import ee.finestmedia.xtee.client.adit.types.noNamespace.GetDocumentResponseAttachmentV1Document;
import ee.finestmedia.xtee.client.adit.types.noNamespace.GetUserInfoRequestAttachmentV1Document;
import ee.finestmedia.xtee.client.adit.types.noNamespace.GetUserInfoRequestAttachmentV1UserListType;
import ee.finestmedia.xtee.client.adit.types.noNamespace.GetUserInfoResponseAttachmentV1Document;
import ee.finestmedia.xtee.client.adit.types.noNamespace.GetUserInfoResponseAttachmentV1UserType;
import ee.finestmedia.xtee.client.adit.types.noNamespace.SaveDocumentFileAttachmentV1Document;
import ee.finestmedia.xtee.client.adit.types.noNamespace.SaveDocumentRequestAttachmentV1Document;
import ee.finestmedia.xtee.client.adit.types.noNamespace.SaveDocumentRequestAttachmentV1DocumentType;
import ee.finestmedia.xtee.client.adit.types.noNamespace.SubDocumentFilesType;
import ee.webmedia.xtee.client.exception.XTeeServiceConsumptionException;
import ee.webmedia.xtee.client.service.callback.CustomCallback;
import ee.webmedia.xtee.client.service.configuration.SimpleXTeeServiceConfiguration;
import ee.webmedia.xtee.client.service.configuration.XTeeServiceConfiguration;
import ee.webmedia.xtee.client.service.consumer.XTeeConsumer;
import ee.webmedia.xtee.model.XTeeAttachment;
import ee.webmedia.xtee.model.XTeeMessage;
import ee.webmedia.xtee.model.XmlBeansXTeeMessage;

/**
 * ADIT X-Tee service implementation
 * 
 * @author Johan Miller
 * @version $Id: AmetlikudDokumendidXTeeServiceImpl.java 6 2011-04-18 09:21:51Z
 *          johanmiller $
 */
public class AmetlikudDokumendidXTeeServiceImpl implements
		AmetlikudDokumendidXTeeService {

	private String securityServer;

	private String institution;

	private String system;

	private XTeeConsumer consumer;

	private final String database = "ametlikud-dokumendid";

	private final String namespace = "http://producers.ametlikud-dokumendid.xtee.riik.ee/producer/ametlikud-dokumendid";

	private final String version = "v1";

	private final String objectMessage = "Could not parse xml object";

	public AditResponse<Object> join(AditRequestInfo info, UserType type,
			String name) throws AmetlikudDokumendidXTeeServiceException {

		JoinRequest request = JoinRequest.Factory.newInstance();
		request.setUserType(type.getName());
		request.setUserName(name);

		XTeeMessage<JoinResponse> response = send(request, "join", info);

		return new AditResponse<Object>(response.getContent().getSuccess(),
				response.getContent().getMessages());
	}

	public AditResponse<Object> unJoin(AditRequestInfo info)
			throws AmetlikudDokumendidXTeeServiceException {

		UnJoinRequest request = UnJoinRequest.Factory.newInstance();

		XTeeMessage<UnJoinResponse> response = send(request, "unJoin", info);

		return new AditResponse<Object>(response.getContent().getSuccess(),
				response.getContent().getMessages());
	}

	public AditResponse<List<GetUserInfoResponseAttachmentV1UserType>> getUserInfo(
			AditRequestInfo info, List<String> ids)
			throws AmetlikudDokumendidXTeeServiceException {

		try {
			GetUserInfoRequestAttachmentV1Document doc = GetUserInfoRequestAttachmentV1Document.Factory
					.newInstance();
			GetUserInfoRequestAttachmentV1UserListType userList = doc
					.addNewGetUserInfoRequestAttachmentV1().addNewUserList();

			for (String id : ids) {
				userList.addCode(id);
			}

			XTeeAttachment attachment = createAttachment(doc);

			GetUserInfoRequest request = GetUserInfoRequest.Factory
					.newInstance();
			request.addNewUserList().setHref(attachment.getCid());

			XTeeMessage<GetUserInfoResponse> response = send(request,
					"getUserInfo", info, attachment);

			AditResponse<List<GetUserInfoResponseAttachmentV1UserType>> aditResponse = new AditResponse<List<GetUserInfoResponseAttachmentV1UserType>>(
					response.getContent().getSuccess(), response.getContent()
							.getMessages());

			UserList respUserList = response.getContent().getUserList();

			if (respUserList != null
					&& StringUtils.isNotBlank(respUserList.getHref())) {
				String parsedAttachment = readAttachment(
						response.getAttachments(), response.getContent()
								.getUserList().getHref());

				if (StringUtils.isNotBlank(parsedAttachment)) {
					GetUserInfoResponseAttachmentV1Document responseDoc = GetUserInfoResponseAttachmentV1Document.Factory
							.parse(parsedAttachment);

					aditResponse.setExtra(responseDoc
							.getGetUserInfoResponseAttachmentV1().getUserList()
							.getUserList());
				}
			}

			return aditResponse;
		} catch (XmlException e) {
			throw new AmetlikudDokumendidXTeeServiceException(objectMessage, e);
		}
	}

	public AditResponse<GetDocumentListResponseAttachmentV1DocumentListType> getDocumentList(
			AditRequestInfo info, DocumentListSearch searchInfo)
			throws AmetlikudDokumendidXTeeServiceException {

		try {
			GetDocumentListRequest request = GetDocumentListRequest.Factory
					.newInstance();

			if (searchInfo.getFolder() != null) {
				request.setFolder(searchInfo.getFolder());
			}

			if (searchInfo.getDocumentTypes() != null && !searchInfo.getDocumentTypes().isEmpty()) {
				ArrayOfDocumentType types = request.addNewDocumentTypes();

				for (DocumentType type : searchInfo.getDocumentTypes()) {
					types.addDocumentType(type.getName());
				}
			}

			if (searchInfo.getDocumentStatuses() != null && !searchInfo.getDocumentStatuses().isEmpty()) {
				ArrayOfDocumentDvkStatus statuses = request
						.addNewDocumentDvkStatuses();

				for (DocumentStatus status : searchInfo.getDocumentStatuses()) {
					statuses.addStatusId(toBigInt(status.getCode()));
				}
			}

			if (searchInfo
					.getDocumentWorkflowStatuses() != null && !searchInfo
					.getDocumentWorkflowStatuses().isEmpty()) {
				ArrayOfDocumentWorkflowStatus statuses = request
						.addNewDocumentWorkflowStatuses();

				for (Integer status : searchInfo.getDocumentWorkflowStatuses()) {
					statuses.addStatusId(toBigInt(status));
				}
			}

			if (searchInfo.getViewed() != null) {
				request.setHasBeenViewed(searchInfo.isViewed());
			}

			request.setIsDeflated(searchInfo.isDeflated());

			if (searchInfo.getCreatorApplications() != null && !searchInfo.getCreatorApplications().isEmpty()) {
				ArrayOfCreatorApplication applications = request
						.addNewCreatorApplications();

				for (String application : searchInfo.getCreatorApplications()) {
					applications.addCreatorApplication(application);
				}
			}

			if (StringUtils.isNotBlank(searchInfo.getPhrase())) {
				request.setSearchPhrase(searchInfo.getPhrase());
			}

			if (searchInfo.getMaxResults() != null) {
				request.setMaxResults(toBigInt(searchInfo.getMaxResults()));
			}

			if (searchInfo.getStartIndex() != null) {
				request.setStartIndex(toBigInt(searchInfo.getStartIndex()));
			}

			if (searchInfo.getPeriodStart() != null) {
				request.setPeriodStart(toCal(searchInfo.getPeriodStart()));
			}

			if (searchInfo.getPeriodEnd() != null) {
				request.setPeriodEnd(toCal(searchInfo.getPeriodEnd()));
			}

			if (searchInfo.getSortBy() != null) {
				request.setSortBy(searchInfo.getSortBy());
			}

			if (searchInfo.getOrderBy() != null) {
				request.setSortOrder(searchInfo.getOrderBy());
			}

			XTeeMessage<GetDocumentListResponse> response = send(request,
					"getDocumentList", info);

			AditResponse<GetDocumentListResponseAttachmentV1DocumentListType> aditResponse = new AditResponse<GetDocumentListResponseAttachmentV1DocumentListType>(
					response.getContent().getSuccess(), response.getContent()
							.getMessages());

			DocumentList documentList = response.getContent().getDocumentList();

			if (documentList != null
					&& StringUtils.isNotBlank(documentList.getHref())) {
				String parsedAttachment = readAttachment(
						response.getAttachments(), response.getContent()
								.getDocumentList().getHref());

				if (StringUtils.isNotBlank(parsedAttachment)) {
					GetDocumentListResponseAttachmentV1Document responseDoc = GetDocumentListResponseAttachmentV1Document.Factory
							.parse(parsedAttachment);

					aditResponse.setExtra(responseDoc
							.getGetDocumentListResponseAttachmentV1()
							.getDocumentList());
				}
			}

			return aditResponse;
		} catch (XmlException e) {
			throw new AmetlikudDokumendidXTeeServiceException(objectMessage, e);
		}
	}

	public AditResponse<Long> saveDocument(AditRequestInfo info,
			Document document) throws AmetlikudDokumendidXTeeServiceException {

		SaveDocumentRequestAttachmentV1Document doc = SaveDocumentRequestAttachmentV1Document.Factory
				.newInstance();
		SaveDocumentRequestAttachmentV1DocumentType aditDocument = doc
				.addNewSaveDocumentRequestAttachmentV1().addNewDocument();

		if (document.getId() != null) {
			aditDocument.setId(toBigInt(document.getId()));
		}

		if (document.getPreviousId() != null) {
			aditDocument.setPreviousDocumentId(toBigInt(document
					.getPreviousId()));
		}

		if (document.getFiles() != null) {
			SubDocumentFilesType aditFiles = aditDocument.addNewFiles();

			for (DocumentFile file : document.getFiles()) {
				populateAditFile(aditFiles.addNewFile(), file);
			}
		}

		aditDocument.setTitle(document.getTitle());
		aditDocument.setDocumentType(DocumentType.APPLICATION);

		XTeeAttachment attachment = createAttachment(doc);

		SaveDocumentRequest request = SaveDocumentRequest.Factory.newInstance();
		request.addNewDocument().setHref(attachment.getCid());

		XTeeMessage<SaveDocumentResponse> response = send(request,
				"saveDocument", info, attachment);

		AditResponse<Long> aditResponse = new AditResponse<Long>(response
				.getContent().getSuccess(), response.getContent().getMessages());

		aditResponse
				.setExtra(response.getContent().getDocumentId().longValue());

		return aditResponse;
	}

	public AditResponse<DocumentV1Type> getDocument(
			AditRequestInfo info,
			Long id,
			Boolean includeFileContents,
			ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.ArrayOfFileType.FileType.Enum fileType)
			throws AmetlikudDokumendidXTeeServiceException {

		try {
			GetDocumentRequest request = GetDocumentRequest.Factory
					.newInstance();
			request.setDocumentId(toBigInt(id));
			request.setIncludeFileContents(BooleanUtils
					.toBoolean(includeFileContents));
			request.addNewFileTypes().addFileType(fileType);

			XTeeMessage<GetDocumentResponse> response = send(request,
					"getDocument", info);

			AditResponse<DocumentV1Type> aditResponse = new AditResponse<DocumentV1Type>(
					response.getContent().getSuccess(), response.getContent()
							.getMessages());

			if (response.getContent().getDocument() != null
					&& StringUtils.isNotBlank(response.getContent()
							.getDocument().getHref())) {

				String parsedAttachment = readAttachment(
						response.getAttachments(), response.getContent()
								.getDocument().getHref());

				if (StringUtils.isNotBlank(parsedAttachment)) {
					GetDocumentResponseAttachmentV1Document responseDoc = GetDocumentResponseAttachmentV1Document.Factory
							.parse(parsedAttachment);

					aditResponse
							.setExtra(responseDoc
									.getGetDocumentResponseAttachmentV1()
									.getDocument());
				}
			}

			return aditResponse;
		} catch (XmlException e) {
			throw new AmetlikudDokumendidXTeeServiceException(objectMessage, e);
		}
	}

	public AditResponse<Object> saveDocumentFile(AditRequestInfo info, Long id,
			DocumentFile file) throws AmetlikudDokumendidXTeeServiceException {

		SaveDocumentFileAttachmentV1Document doc = SaveDocumentFileAttachmentV1Document.Factory
				.newInstance();

		populateAditFile(doc.addNewSaveDocumentFileAttachmentV1().addNewFile(),
				file);

		XTeeAttachment attachment = createAttachment(doc);

		SaveDocumentFileRequest request = SaveDocumentFileRequest.Factory
				.newInstance();
		request.setDocumentId(toBigInt(id));
		request.addNewFile().setHref(attachment.getCid());

		XTeeMessage<SaveDocumentFileResponse> response = send(request,
				"saveDocumentFile", info, attachment);

		return new AditResponse<Object>(response.getContent().getSuccess(),
				response.getContent().getMessages());
	}

	public AditResponse<FileType> getDocumentFile(AditRequestInfo info,
			Long documentId, Long fileId)
			throws AmetlikudDokumendidXTeeServiceException {

		try {
			GetDocumentFileRequest request = GetDocumentFileRequest.Factory
					.newInstance();

			request.setDocumentId(toBigInt(documentId));
			request.addNewFileIdList().addFileId(toBigInt(fileId));

			XTeeMessage<GetDocumentFileResponse> response = send(request,
					"getDocumentFile", info);

			AditResponse<FileType> aditResponse = new AditResponse<FileType>(
					response.getContent().getSuccess(), response.getContent()
							.getMessages());

			if (response.getContent().getFiles() != null
					&& StringUtils.isNotBlank(response.getContent().getFiles()
							.getHref())) {

				String parsedAttachment = readAttachment(
						response.getAttachments(), response.getContent()
								.getFiles().getHref());

				if (StringUtils.isNotBlank(parsedAttachment)) {
					GetDocumentFileResponseAttachmentV1Document responseDoc = GetDocumentFileResponseAttachmentV1Document.Factory
							.parse(parsedAttachment);
					GetDocumentFileResponseAttachmentV1FilesType files = responseDoc
							.getGetDocumentFileResponseAttachmentV1()
							.getFiles();

					if (files != null && !files.getFileList().isEmpty()) {
						aditResponse.setExtra(files.getFileList().get(0));
					}
				}
			}

			return aditResponse;
		} catch (XmlException e) {
			throw new AmetlikudDokumendidXTeeServiceException(objectMessage, e);
		}
	}

	public AditResponse<Object> deleteDocumentFile(AditRequestInfo info,
			Long documentId, Long fileId)
			throws AmetlikudDokumendidXTeeServiceException {

		DeleteDocumentFileRequest request = DeleteDocumentFileRequest.Factory
				.newInstance();
		request.setDocumentId(toBigInt(documentId));
		request.setFileId(toBigInt(fileId));

		XTeeMessage<DeleteDocumentFileResponse> response = send(request,
				"deleteDocumentFile", info);

		return new AditResponse<Object>(response.getContent().getSuccess(),
				response.getContent().getMessages());
	}

	public AditResponse<Object> deleteDocument(AditRequestInfo info, Long id)
			throws AmetlikudDokumendidXTeeServiceException {

		DeleteDocumentRequest request = DeleteDocumentRequest.Factory
				.newInstance();
		request.setDocumentId(toBigInt(id));

		XTeeMessage<DeleteDocumentResponse> response = send(request,
				"deleteDocument", info);

		return new AditResponse<Object>(response.getContent().getSuccess(),
				response.getContent().getMessages());
	}

	public AditResponse<List<RecipientStatus>> sendDocument(
			AditRequestInfo info, Long id, List<String> receivers)
			throws AmetlikudDokumendidXTeeServiceException {

		SendDocumentRequest request = SendDocumentRequest.Factory.newInstance();
		request.setDocumentId(toBigInt(id));

		ArrayOfUserCode codes = request.addNewRecipientList();

		for (String receiver : receivers) {
			codes.addCode(receiver);
		}

		XTeeMessage<SendDocumentResponse> response = send(request,
				"sendDocument", info);

		AditResponse<List<RecipientStatus>> aditResponse = new AditResponse<List<RecipientStatus>>(
				response.getContent().getSuccess(), response.getContent()
						.getMessages());

		if (response.getContent().getRecipientList() != null) {
			aditResponse.setExtra(response.getContent().getRecipientList()
					.getRecipientList());
		}

		return aditResponse;
	}

	public AditResponse<RecipientStatus> shareDocument(AditRequestInfo info,
			Long id, String recipientCode, String reason, Boolean forSigning)
			throws AmetlikudDokumendidXTeeServiceException {

		ShareDocumentRequest request = ShareDocumentRequest.Factory
				.newInstance();
		request.setDocumentId(toBigInt(id));
		request.addNewRecipientList().addCode(recipientCode);
		request.setReasonForSharing(reason);
		request.setSharedForSigning(forSigning);

		XTeeMessage<ShareDocumentResponse> response = send(request,
				"shareDocument", info);

		AditResponse<RecipientStatus> aditResponse = new AditResponse<RecipientStatus>(
				response.getContent().getSuccess(), response.getContent()
						.getMessages());

		ArrayOfRecipientStatus statuses = response.getContent()
				.getRecipientList();

		if (statuses != null && !statuses.getRecipientList().isEmpty()) {
			aditResponse.setExtra(statuses.getRecipientList().get(0));
		}

		return aditResponse;
	}

	public AditResponse<RecipientStatus> unShareDocument(AditRequestInfo info,
			Long id, String sharingCode)
			throws AmetlikudDokumendidXTeeServiceException {

		UnShareDocumentRequest request = UnShareDocumentRequest.Factory
				.newInstance();
		request.setDocumentId(toBigInt(id));
		request.addNewRecipientList().addCode(sharingCode);

		XTeeMessage<UnShareDocumentResponse> response = send(request,
				"unShareDocument", info);

		AditResponse<RecipientStatus> aditResponse = new AditResponse<RecipientStatus>(
				response.getContent().getSuccess(), response.getContent()
						.getMessages());

		ArrayOfRecipientStatus statuses = response.getContent()
				.getRecipientList();

		if (statuses != null && !statuses.getRecipientList().isEmpty()) {
			aditResponse.setExtra(statuses.getRecipientList().get(0));
		}

		return aditResponse;
	}

	public AditResponse<String> prepareSignature(AditRequestInfo info,
			SignerInfo signerInfo)
			throws AmetlikudDokumendidXTeeServiceException {

		try {
			String certString = certToString(signerInfo.getCert());
			XTeeAttachment attachment = createAttachment(certString.getBytes(),
					"application/octet-stream");

			PrepareSignatureRequest request = PrepareSignatureRequest.Factory
					.newInstance();
			request.setDocumentId(toBigInt(signerInfo.getDocumentId()));
			request.setManifest(signerInfo.getManifest());
			request.setCountry(signerInfo.getCountry());
			request.setState(signerInfo.getState());
			request.setCity(signerInfo.getCity());
			request.setZip(signerInfo.getZip());
			request.addNewSignerCertificate().setHref(attachment.getCid());

			XTeeMessage<PrepareSignatureResponse> response = send(request,
					"prepareSignature", info, attachment);

			AditResponse<String> aditResponse = new AditResponse<String>(
					response.getContent().getSuccess(), response.getContent()
							.getMessages());

			aditResponse.setExtra(response.getContent().getSignatureHash());

			return aditResponse;
		} catch (CertificateEncodingException e) {
			throw new AmetlikudDokumendidXTeeServiceException(
					"Could not encode cert", e);
		}
	}

	public AditResponse<Object> confirmSignature(AditRequestInfo info,
			Long documentId, String signature)
			throws AmetlikudDokumendidXTeeServiceException {

		XTeeAttachment attachment = createAttachment(signature.getBytes(),
				"application/octet-stream");

		ConfirmSignatureRequest request = ConfirmSignatureRequest.Factory
				.newInstance();
		request.setDocumentId(toBigInt(documentId));
		request.addNewSignature().setHref(attachment.getCid());

		XTeeMessage<ConfirmSignatureResponse> response = send(request,
				"confirmSignature", info, attachment);

		return new AditResponse<Object>(response.getContent().getSuccess(),
				response.getContent().getMessages());
	}

	/**
	 * Sends an X-Tee request
	 * 
	 * @param <I>
	 *            the request object to send
	 * @param <O>
	 *            the response object
	 * @param request
	 *            the request to send
	 * @param method
	 *            the method to use
	 * @param info
	 *            the ADIT request info
	 * 
	 * @return the response object wrapped in an {@link XTeeMessage}
	 * 
	 * @throws AmetlikudDokumendidXTeeServiceException
	 */
	private <I extends XmlObject, O> XTeeMessage<O> send(I request,
			String method, AditRequestInfo info)
			throws AmetlikudDokumendidXTeeServiceException {

		return send(request, method, info, null);
	}

	/**
	 * Sends an X-Tee request
	 * 
	 * @param <I>
	 *            the request object to send
	 * @param <O>
	 *            the response object
	 * @param request
	 *            the request to send
	 * @param method
	 *            the method to use
	 * @param info
	 *            the ADIT request info
	 * @param attachment
	 *            the attachment to send
	 * 
	 * @return the response object wrapped in an {@link XTeeMessage}
	 * 
	 * @throws AmetlikudDokumendidXTeeServiceException
	 */
	private <I extends XmlObject, O> XTeeMessage<O> send(I request,
			String method, AditRequestInfo info, XTeeAttachment attachment)
			throws AmetlikudDokumendidXTeeServiceException {

		XmlBeansXTeeMessage<I> wrappedRequest = new XmlBeansXTeeMessage<I>(
				request);

		if (attachment != null) {
			wrappedRequest.getAttachments().add(attachment);
		}

		if (StringUtils.isBlank(info.getIdCode())) {
			throw new AmetlikudDokumendidXTeeServiceException(
					"ID code is required");
		}

		XTeeServiceConfiguration conf = createConf(method, info.getIdCode());

		CustomCallback callback = new AmetlikudDokumendidCallback(namespace,
				system, info.getInstitution());

		try {
			return consumer.sendRequest(wrappedRequest, conf, callback, null);
		} catch (XTeeServiceConsumptionException e) {
			throw new AmetlikudDokumendidXTeeServiceException(
					"XTee service consumption problem", e);
		}
	}

	/**
	 * Creates an {@link XTeeAttachment} from an {@link XmlObject}
	 * 
	 * @param doc
	 *            the object to create the attachment from
	 * 
	 * @return the created attachment
	 * 
	 * @throws AmetlikudDokumendidXTeeServiceException
	 */
	private XTeeAttachment createAttachment(XmlObject doc)
			throws AmetlikudDokumendidXTeeServiceException {
		try {
			return createAttachment(IOUtils.toByteArray(doc.newInputStream()),
					"text/xml");
		} catch (IOException e) {
			throw new AmetlikudDokumendidXTeeServiceException(
					"Could not create attachment", e);
		}
	}

	/**
	 * Creates an {@link XTeeAttachment} from the given bytes and with the given
	 * content type
	 * 
	 * @param bytes
	 *            the info bytes
	 * @param contentType
	 *            the content type
	 * 
	 * @return the created attachment
	 * 
	 * @throws AmetlikudDokumendidXTeeServiceException
	 */
	private XTeeAttachment createAttachment(byte[] bytes, String contentType)
			throws AmetlikudDokumendidXTeeServiceException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			Base64OutputStream b = new Base64OutputStream(out);
			GZIPOutputStream g = new GZIPOutputStream(b);
			IOUtils.write(bytes, g);

			g.close();
		} catch (IOException e) {
			throw new AmetlikudDokumendidXTeeServiceException(
					"Could not create attachment", e);
		}

		return new XTeeAttachment(RandomStringUtils.randomAlphanumeric(30),
				contentType, out.toByteArray());
	}

	/**
	 * Reads an {@link XTeeAttachment} with the given id
	 * 
	 * @param attachments
	 *            the list of returned attachments
	 * @param id
	 *            the if of the attachment to read
	 * 
	 * @return the parsed attachment if found, null otherwise
	 * 
	 * @throws AmetlikudDokumendidXTeeServiceException
	 */
	private String readAttachment(List<XTeeAttachment> attachments, String id)
			throws AmetlikudDokumendidXTeeServiceException {

		for (XTeeAttachment attachment : attachments) {
			if (attachment.toString().equals(id)) {
				try {
					GZIPInputStream stream = new GZIPInputStream(
							attachment.getInputStream());
					return IOUtils.toString(stream, CharEncoding.UTF_8);
				} catch (IOException e) {
					throw new AmetlikudDokumendidXTeeServiceException(
							"Could not read attachment", e);
				}
			}
		}

		return null;
	}

	/**
	 * Creates a new X-Tee request configuration
	 * 
	 * @param method
	 *            the method to execute
	 * @param idCode
	 *            the id of the user
	 * 
	 * @return the create configuration
	 */
	private XTeeServiceConfiguration createConf(String method, String idCode) {
		SimpleXTeeServiceConfiguration conf = new SimpleXTeeServiceConfiguration();
		conf.setDatabase(database);
		conf.setIdCode(idCode);
		conf.setInstitution(institution);
		conf.setMethod(method);
		conf.setSecurityServer(securityServer);
		conf.setVersion(version);
		conf.setWsdlDatabase(database);

		return conf;
	}

	/**
	 * Populates an ADIT {@link FileType} with the info from the
	 * {@link DocumentFile}
	 * 
	 * @param aditFile
	 *            the file to populate
	 * @param file
	 *            the file to populate from
	 * 
	 * @throws AmetlikudDokumendidXTeeServiceException
	 */
	private void populateAditFile(FileType aditFile, DocumentFile file)
			throws AmetlikudDokumendidXTeeServiceException {

		aditFile.setContentType(file.getContentType());
		aditFile.setName(file.getName());
		aditFile.setSizeBytes(toBigInt(file.getSize()));
		aditFile.setData(file.getData());
	}

	/**
	 * Creates a {@link BigInteger} from an {@link Integer}
	 * 
	 * @param value
	 *            the value to use
	 * 
	 * @return the created big integer
	 */
	private BigInteger toBigInt(Integer value) {
		return new BigInteger(value.toString());
	}

	/**
	 * Creates a {@link BigInteger} from a {@link Long}
	 * 
	 * @param value
	 *            the value to use
	 * 
	 * @return the created big integer
	 */
	private BigInteger toBigInt(Long value) {
		return new BigInteger(value.toString());
	}

	/**
	 * Creates a {@link Calendar} from a {@link Date}
	 * 
	 * @param date
	 *            the date to use
	 * 
	 * @return the created calendar
	 */
	private Calendar toCal(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		return cal;
	}

	/**
	 * Converts an {@link X509Certificate} to pem format
	 * 
	 * @param cert
	 *            the certificate to convert
	 * 
	 * @return the certificate in pem format
	 * 
	 * @throws CertificateEncodingException
	 */
	private String certToString(X509Certificate cert)
			throws CertificateEncodingException {
		StringBuilder sb = new StringBuilder();

		sb.append("-----BEGIN CERTIFICATE-----");
		sb.append('\n');
		sb.append(StringUtils.chomp(new Base64(64).encodeToString(cert
				.getEncoded())));
		sb.append('\n');
		sb.append("-----END CERTIFICATE-----");

		return sb.toString();
	}

	/**
	 * Sets the objects securityServer
	 * 
	 * @param securityServer
	 *            the securityServer to set
	 */
	public void setSecurityServer(String securityServer) {
		this.securityServer = securityServer;
	}

	/**
	 * Sets the objects institution
	 * 
	 * @param institution
	 *            the institution to set
	 */
	public void setInstitution(String institution) {
		this.institution = institution;
	}

	/**
	 * Sets the objects system
	 * 
	 * @param system
	 *            the system to set
	 */
	public void setSystem(String system) {
		this.system = system;
	}

	/**
	 * Sets the objects consumer
	 * 
	 * @param consumer
	 *            the consumer to set
	 */
	public void setConsumer(XTeeConsumer consumer) {
		this.consumer = consumer;
	}
}
