package ee.kovmen.digidoc;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.ow2.bonita.connector.core.ConnectorError;
import org.ow2.bonita.connector.core.ProcessConnector;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.facade.runtime.AttachmentInstance;
import org.ow2.bonita.facade.runtime.Document;
import org.ow2.bonita.util.AccessorUtil;

import ee.sk.digidoc.SignedDoc;

public class SignaturePrepareConnector extends ProcessConnector {

	
	
	// DO NOT REMOVE NOR RENAME THIS FIELD
	private List files;
	// DO NOT REMOVE NOR RENAME THIS FIELD
	private java.lang.String cert;
	private String signatureHash;
	

	@Override
	protected void executeConnector() throws Exception {
	
		Sign sign=new Sign(getProcessInstanceUUID().toString());
		sign.initSettings(Sign.getBonitaHomeDir());
		
		List<Document> documents=new ArrayList<Document>();
		
		QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
		sign.setQueryRuntimeAPI(queryRuntimeAPI);
		
		for (int i = 0; i < files.size(); i++) {
			if(files.get(i) instanceof AttachmentInstance){
				AttachmentInstance attachment = (AttachmentInstance) files.get(i);
				org.ow2.bonita.facade.runtime.Document bonita_doc = queryRuntimeAPI.getDocument(attachment.getUUID());
				if(bonita_doc != null){
					documents.add(bonita_doc);
				}
			} else if(files.get(i) instanceof List) {
				List list = (List) files.get(i);
				for (int j = 0; j < list.size(); j++) {
					if(list.get(j) instanceof AttachmentInstance){
						AttachmentInstance attachment = (AttachmentInstance) list.get(j);
						org.ow2.bonita.facade.runtime.Document bonita_doc = queryRuntimeAPI.getDocument(attachment.getUUID());
						if(bonita_doc != null){
							documents.add(bonita_doc);
						}
					}
				}
			}
		}
		
//		for (AttachmentInstance attachment : files) {
//				org.ow2.bonita.facade.runtime.Document bonita_doc = queryRuntimeAPI.getDocument(attachment.getUUID());
//				documents.add(bonita_doc);
//		}

		//kui on cert topelt kodeeritud (sest cerdi andmed on sisestatud <input type="text"> v�ljale ja see s��b �ra reavahetused ning cert selle t�ttu ei t��ta
		if(!cert.startsWith("-----BEGIN CERTIFICATE-----"))
		{
			byte[] decodeBase64 = Base64.decodeBase64(cert);
			cert=new String(decodeBase64);
		}
		
		signatureHash= sign.appendFilesAndGenerateHash(cert, documents);
		sign.SerializeDoc(Sign.SIGNED_DOC+".obj");
		
	}

	@Override
	protected List<ConnectorError> validateValues() {
		List<ConnectorError> errors = new ArrayList<ConnectorError>();
		if(this.cert==null){
			errors.add(new ConnectorError("Cert", new Exception("Cert is missing!")));
		}
		int filesToSign = 0;
		for (int i = 0; i < files.size(); i++) {
			if(files.get(i) instanceof AttachmentInstance){
				if(files.get(i) != null){
					filesToSign++;
				}
			} else if(files.get(i) instanceof List) {
				List list = (List) files.get(i);
				for (int j = 0; j < list.size(); j++) {
					if(list.get(j) instanceof AttachmentInstance){
						if(list.get(j) != null){
							filesToSign++;
						}
					}
				}
			}
		}
		if(filesToSign==0){
			errors.add(new ConnectorError("files", new Exception("No files to sign!")));
		}		
		return errors.size()==0?null:errors;
	}

	/**
	 * Setter for input argument 'files'
	 * DO NOT REMOVE NOR RENAME THIS SETTER, unless you also change the related entry in the XML descriptor file
	 */
	public void setFiles(List files) {
		this.files = files;
	}

	/**
	 * Setter for input argument 'cert'
	 * DO NOT REMOVE NOR RENAME THIS SETTER, unless you also change the related entry in the XML descriptor file
	 */
	public void setCert(java.lang.String cert) {
		this.cert = cert;
	}

	/**
	 * Getter for output argument 'signatureHash'
	 * DO NOT REMOVE NOR RENAME THIS GETTER, unless you also change the related entry in the XML descriptor file
	 */
	public String getSignatureHash() {
		// TODO Add return value for the output here
		return signatureHash;
	}


}
