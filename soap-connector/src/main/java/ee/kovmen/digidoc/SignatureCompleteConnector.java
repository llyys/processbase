package ee.kovmen.digidoc;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ow2.bonita.connector.core.ConnectorError;
import org.ow2.bonita.connector.core.ProcessConnector;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.util.AccessorUtil;

import ee.sk.digidoc.SignedDoc;

public class SignatureCompleteConnector extends ProcessConnector {

	// DO NOT REMOVE NOR RENAME THIS FIELD
	private java.lang.String allkiri;
	private java.lang.String fileName;
	// DO NOT REMOVE NOR RENAME THIS FIELD
	private org.ow2.bonita.facade.runtime.AttachmentInstance file;

	@Override
	protected void executeConnector() throws Exception {
		QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
		org.ow2.bonita.facade.runtime.Document doc = queryRuntimeAPI.getDocument(file.getUUID());
	
		Sign sign = new Sign(getProcessInstanceUUID().toString());
		sign.initSettings(Sign.getBonitaHomeDir());
		sign.setQueryRuntimeAPI(queryRuntimeAPI);
		
		sign.DeSerializeDoc(Sign.SIGNED_DOC+".obj");
		sign.appendSignatureToDocument(allkiri);
		SignedDoc signedDoc=sign.getSdoc();
		sign.writeToFile(Sign.SIGNED_DOC+".ddoc");
		byte[] byteArray=Sign.readFileBytes(getProcessInstanceUUID().toString(), Sign.SIGNED_DOC+".ddoc");
		if(StringUtils.isEmpty(fileName))
			fileName=Sign.SIGNED_DOC;
		Sign.toByteArray(signedDoc);
		Map<String, String> metadata=new Hashtable<String, String>();
        metadata.put("content-type", Sign.MIME_TYPE);
        AccessorUtil.getRuntimeAPI().addDocumentVersion(file.getUUID(), true, fileName+".ddoc", Sign.MIME_TYPE, byteArray);
	}

	@Override
	protected List<ConnectorError> validateValues() {
		List<ConnectorError> errors = new ArrayList<ConnectorError>();
		if(this.allkiri==null){
			errors.add(new ConnectorError("allkiri", new Exception("Allkiri puudub")));
		}
		try {
			
			String tempDir = Sign.getProcessTempDirectory(getProcessInstanceUUID().toString());
			if(new File(tempDir+File.separator+Sign.SIGNED_DOC+".obj").exists()==false)
				errors.add(new ConnectorError("bdoc", new Exception("SignaturePrepare connector peab olema k�ivitatud enne SignatureComplete connectorit!")));
		} catch (Exception e) {
			errors.add(new ConnectorError("viga", e)); 
		}		
		return errors.size()==0?null:errors;
	}

	

	/**
	 * Setter for input argument 'allkiri'
	 * DO NOT REMOVE NOR RENAME THIS SETTER, unless you also change the related entry in the XML descriptor file
	 */
	public void setAllkiri(java.lang.String allkiri) {
		this.allkiri = allkiri;
	}

	/**
	 * Setter for input argument 'file'
	 * DO NOT REMOVE NOR RENAME THIS SETTER, unless you also change the related entry in the XML descriptor file
	 */
	public void setFile(org.ow2.bonita.facade.runtime.AttachmentInstance file) {
		this.file = file;
	}
	
	/**
	 * Setter for input argument 'file'
	 * DO NOT REMOVE NOR RENAME THIS SETTER, unless you also change the related entry in the XML descriptor file
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
