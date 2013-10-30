package ee.kovmen.xtee.adit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.ow2.bonita.connector.core.ConnectorError;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.exception.DocumentNotFoundException;
import org.ow2.bonita.facade.runtime.AttachmentInstance;
import org.ow2.bonita.util.AccessorUtil;

import ee.finestmedia.xtee.client.adit.AditRequestInfo;
import ee.finestmedia.xtee.client.adit.AditResponse;
import ee.finestmedia.xtee.client.adit.AmetlikudDokumendidXTeeServiceException;
import ee.finestmedia.xtee.client.adit.domain.Document;
import ee.finestmedia.xtee.client.adit.domain.files.DocumentFile;

/**
 * ADIT save document.
 * 
 * @author Lauri
 * @author Margo
 */
public class AditSaveDocument extends AbstractAditConnector {
	
	private static final Logger LOG = Logger.getLogger(AditSaveDocument.class);

	private String isikukood;

	private List<Object> files;

	private String title;

	private Long previousDocId;

	private Long documentId;

	@Override
	protected void executeConnector() throws Exception {

		AditRequestInfo info = new AditRequestInfo(isikukood);
		if (!isikukood.startsWith("EE"))
			info.setIdCode("EE" + isikukood);
		else
			info.setIdCode(isikukood);

		Document document = new Document();
		document.setTitle(title);
		if (previousDocId != null && previousDocId != 0) {
			document.setPreviousId(previousDocId.intValue());
		}

		List<DocumentFile> documentFiles = getDocumentFiles();
		if (documentFiles.size() > 0) {
			document.setFiles(documentFiles);

			AditResponse<Long> saveDocument = getService().saveDocument(info,
					document);
			if (saveDocument.getSuccess()) {
				documentId = saveDocument.getExtra();
				LOG.warn("success documentId = " + documentId);
			} else {
				LOG.warn("failure messages:" + saveDocument.getMessages());
			}
		} else {
			LOG.debug("no documents");
		}
	}

	@Override
	protected List<ConnectorError> validateValues() {
		List<ConnectorError> errors = new ArrayList<ConnectorError>();
		if (this.isikukood == null) {
			errors.add(new ConnectorError("Isikukood", new Exception(
					"Isikukood puudub")));
		}
		if (!this.isikukood.startsWith("EE")) {
			isikukood = "EE" + isikukood;
		}
		if (files == null) {
			errors.add(new ConnectorError("files", new Exception(
					"No files added")));
		}
		return errors.size() == 0 ? null : errors;
	}

	private String createData(byte[] bytes)
			throws AmetlikudDokumendidXTeeServiceException {

		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		try {
			Base64OutputStream b = new Base64OutputStream(out);
			// GZIPOutputStream g = new GZIPOutputStream(b);
			// IOUtils.write(bytes, g);
			IOUtils.write(bytes, b);
			b.close();
			// g.close();
			byte[] outarr = out.toByteArray();
			out.close();
			return new String(outarr);
		} catch (IOException e) {
			throw new AmetlikudDokumendidXTeeServiceException(
					"Could not create document", e);
		}

	}

	private List<DocumentFile> getDocumentFiles()
			throws DocumentNotFoundException,
			AmetlikudDokumendidXTeeServiceException {

		List<DocumentFile> documentFiles = new ArrayList<DocumentFile>();

		for (int i = 0; i < files.size(); i++) {

			if (files.get(i) instanceof AttachmentInstance) {
				AttachmentInstance attachment = (AttachmentInstance) files
						.get(i);
				if(attachment != null){
					DocumentFile docFile = getDocument(attachment);
					if(docFile.getSize() > 0){
						documentFiles.add(docFile);
					}
				}
			} else if (files.get(i) instanceof List) {
				
				@SuppressWarnings("unchecked")
				List<Object> list = (List<Object>) files.get(i);
				for (int j = 0; j < list.size(); j++) {
					if (list.get(j) instanceof AttachmentInstance) {
						AttachmentInstance attachment = (AttachmentInstance) list
								.get(j);
						if(attachment != null){
							DocumentFile docFile = getDocument(attachment);
							if(docFile.getSize() > 0){
								documentFiles.add(docFile);
							}
						}
					}
				}
			}
		}

		return documentFiles;
	}

	private DocumentFile getDocument(AttachmentInstance attachment)
			throws DocumentNotFoundException,
			AmetlikudDokumendidXTeeServiceException {

		QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();

		DocumentFile df = new DocumentFile();

		org.ow2.bonita.facade.runtime.Document bdoc = queryRuntimeAPI
				.getDocument(attachment.getUUID());
		df.setName(bdoc.getContentFileName() != null ? bdoc.getContentFileName() : bdoc.getName());
		df.setSize((int) bdoc.getContentSize());
		df.setContentType(bdoc.getContentMimeType());

		byte[] content = queryRuntimeAPI.getDocumentContent(attachment
				.getUUID());
		df.setData(createData(content));
		
		return df;
	}

	public String getIsikukood() {
		return isikukood;
	}

	public void setIsikukood(String isikukood) {
		this.isikukood = isikukood;
	}

	public List<Object> getFiles() {
		return files;
	}

	public void setFiles(List<Object> files) {
		this.files = files;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getPreviousDocId() {
		return previousDocId;
	}

	public void setPreviousDocId(Long previousDocId) {
		this.previousDocId = previousDocId;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

}
