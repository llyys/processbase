package ee.kovmen.xtee.adit;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ow2.bonita.connector.core.ConnectorError;

import ee.finestmedia.xtee.client.adit.AditRequestInfo;
import ee.finestmedia.xtee.client.adit.AditResponse;
import ee.finestmedia.xtee.client.adit.types.ee.riik.xtee.ametlikud_dokumendid.producers.producer.ametlikud_dokumendid.RecipientStatus;

/**
 * ADIT send document.
 * 
 * @author Lauri
 * @author Margo
 */
public class AditSendDocument extends AbstractAditConnector {
	
	private static final Logger LOG = Logger.getLogger(AditSendDocument.class);
	
	/** Requester id code. */
	private String idCode;
	
	/** Document id. */
	private Long documentId;
	
	/** Receiver codes. */
	private List<String> receivers;

	@Override
	protected void executeConnector() throws Exception {
		
		AditRequestInfo info = new AditRequestInfo(idCode);
		
		if (!idCode.startsWith("EE")) {
			info.setIdCode("EE" + idCode);
		} else {
			info.setIdCode(idCode);
		}
		
		AditResponse<List<RecipientStatus>> sendDocument = getService()
				.sendDocument(info, documentId, receivers);
		
		if (sendDocument.getSuccess()) {
			LOG.warn("success");
			if (sendDocument.getExtra() != null) {
				for (RecipientStatus s : sendDocument.getExtra()) {
					LOG.warn(s.getCode() + " " + s.getSuccess());
				}
			}
		} else {
			LOG.warn("failure messages:" + sendDocument.getMessages());
			if (sendDocument.getExtra() != null) {
				for (RecipientStatus s : sendDocument.getExtra()) {
					LOG.warn(s.getCode() + " " + s.getSuccess());
				}
			}
		}
		
	}


	@Override
	protected List<ConnectorError> validateValues() {

		List<ConnectorError> errors = new ArrayList<ConnectorError>();
		if (this.idCode == null) {
			errors.add(new ConnectorError("idCode", new Exception(
					"ID code is required")));
		}
		if (documentId == null) {
			errors.add(new ConnectorError("documentId", new Exception(
					"Document id is reqired")));
		}
		if (receivers == null || receivers.isEmpty()) {
			errors.add(new ConnectorError("receivers", new Exception(
					"Receivers id is reqired")));
		}

		return errors;
	}
	
	

	public String getIdCode() {
		return idCode;
	}

	public void setIdCode(String idCode) {
		this.idCode = idCode;
	}

	public List<String> getReceivers() {
		return receivers;
	}

	public void setReceivers(List<String> receivers) {
		this.receivers = receivers;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

}

