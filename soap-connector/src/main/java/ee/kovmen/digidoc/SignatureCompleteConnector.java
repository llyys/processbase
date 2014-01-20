package ee.kovmen.digidoc;

import com.codeborne.security.signature.SignatureSession;
import com.codeborne.security.signature.SmartcardSigner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.ow2.bonita.connector.core.ConnectorError;
import org.ow2.bonita.connector.core.ProcessConnector;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.util.AccessorUtil;

import java.io.File;
import java.util.*;

public class SignatureCompleteConnector extends ProcessConnector {

	// DO NOT REMOVE NOR RENAME THIS FIELD
	private java.lang.String allkiri;
	private java.lang.String fileName;
	// DO NOT REMOVE NOR RENAME THIS FIELD
	private org.ow2.bonita.facade.runtime.AttachmentInstance file;
    private List<ConnectorError> errors;
    final static Logger logger = Logger.getLogger(SignatureCompleteConnector.class);

    @Override
	protected void executeConnector() throws Exception {
        String path = SignatureHelper.getTempDirectoryForProcess(getProcessInstanceUUID().toString());
        logger.info("executeConnector path:"+path);
        try{
            QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
            org.ow2.bonita.facade.runtime.Document doc = queryRuntimeAPI.getDocument(file.getUUID());

            Properties conf = SignatureHelper.getProperties(new File(SignatureHelper.getBonitaHomeDir(), "esteid.properties"));
            SmartcardSigner signer = new SmartcardSigner(conf.getProperty("DIGIDOC_SERVICE_URL"), conf.getProperty("DIGIDOC_SERVICE_TITLE"));


            SignatureSession session= (SignatureSession) SignatureHelper.deSerializeFromFile(new File(path, "Signature.obj"));
            String doc2 = signer.FinalizeSignature(session, allkiri);

            if(fileName==null
                && session.signedDocInfo!=null
                && session.signedDocInfo.getDataFileInfo(0)!=null)
            { //add default document filename as ddoc name
                    fileName= session.signedDocInfo.getDataFileInfo(0).getFilename();
                    fileName=fileName.substring(0, fileName.lastIndexOf('.'));
            }

            FileUtils.writeStringToFile(new File(path, fileName + ".ddoc"), doc2);

            Map<String, String> metadata=new Hashtable<String, String>();
            metadata.put("content-type", "application/octet-stream");

            RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
            runtimeAPI.createDocument(file.getName(), getProcessInstanceUUID(), fileName + ".ddoc", "application/octet-stream", doc2.getBytes());
            //AccessorUtil.getRuntimeAPI().addDocumentVersion(file.getUUID(), true, fileName + ".ddoc", "application/octet-stream", doc2.getBytes());
            FileUtils.deleteDirectory(new File(path));
        }catch (Exception e){
            logger.error("error on executing connector", e);
            errors.add(new ConnectorError("allkiri", e));
            FileUtils.deleteDirectory(new File(path));
           // throw e;
        }
	}

	@Override
	protected List<ConnectorError> validateValues() {
        getErrors();
        if(this.allkiri==null){
			errors.add(new ConnectorError("allkiri", new Exception("Allkiri puudub")));
		}
		try {
			
//			String tempDir = Sign.getProcessTempDirectory(getProcessInstanceUUID().toString());
//			if(new File(tempDir+File.separator+Sign.SIGNED_DOC+".obj").exists()==false)
//				errors.add(new ConnectorError("bdoc", new Exception("SignaturePrepare connector peab olema käivitatud enne SignatureComplete connectorit!")));
		} catch (Exception e) {
			errors.add(new ConnectorError("viga", e));
		}
        if(errors.size()>0)
            for (ConnectorError error:errors){
                logger.error(error.getField(), error.getError());
            }
		return errors.size()==0?null: errors;
	}

    private List<ConnectorError> getErrors() {
        if(errors==null)
        errors = new ArrayList<ConnectorError>();
        return errors;
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
