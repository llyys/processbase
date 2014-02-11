package ee.kovmen.digidoc;

import com.codeborne.security.signature.SignatureSession;
import com.codeborne.security.signature.SmartcardSigner;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.ow2.bonita.connector.core.ConnectorError;
import org.ow2.bonita.connector.core.ProcessConnector;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.runtime.AttachmentInstance;
import org.ow2.bonita.facade.runtime.Document;
import org.ow2.bonita.util.AccessorUtil;

import java.io.File;
import java.util.*;

public class SignaturePrepareConnector extends ProcessConnector {
    final static Logger logger = Logger.getLogger(SignaturePrepareConnector.class);
	
	
	// DO NOT REMOVE NOR RENAME THIS FIELD
	private List files;
	// DO NOT REMOVE NOR RENAME THIS FIELD
	private java.lang.String cert;
	private java.lang.String certId;
	private String signatureHash;
    private List<ConnectorError> errors = new ArrayList<ConnectorError>();

	@Override
	protected void executeConnector() throws Exception {

        String path = SignatureHelper.getTempDirectoryForProcess(getProcessInstanceUUID().toString());
        logger.info("executeConnector path:"+path);
        try {
            SignatureHelper.initializeKeystore();
            Properties conf = SignatureHelper.getProperties(new File(SignatureHelper.getBonitaHomeDir(), "esteid.properties"));

            for (Object name : Collections.list(conf.propertyNames()))
            {
                logger.info(name.toString() + "=" + conf.getProperty(name.toString()));
            }

            SignatureHelper.getProperties(new File(SignatureHelper.getBonitaHomeDir(), "esteid.properties"));
            SmartcardSigner signer = new SmartcardSigner(conf.getProperty("DIGIDOC_SERVICE_URL"), conf.getProperty("DIGIDOC_SERVICE_TITLE"));
            SignatureSession session=null;


            List<Document> documents=new ArrayList<Document>();
            List<File> documentFiles=new ArrayList<File>();
            QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();

            for (int i = 0; i < files.size(); i++) {
                if(files.get(i) instanceof AttachmentInstance){
                    AttachmentInstance attachment = (AttachmentInstance) files.get(i);
                    org.ow2.bonita.facade.runtime.Document bonita_doc = queryRuntimeAPI.getDocument(attachment.getUUID());
                    if(bonita_doc != null){
                        //documents.add(bonita_doc);
                        documentFiles.add(SignatureHelper.getFileFromProcess(queryRuntimeAPI, bonita_doc, path));
                    }
                } else if(files.get(i) instanceof List) {
                    List list = (List) files.get(i);
                    for (int j = 0; j < list.size(); j++) {
                        if(list.get(j) instanceof AttachmentInstance){
                            AttachmentInstance attachment = (AttachmentInstance) list.get(j);
                            org.ow2.bonita.facade.runtime.Document bonita_doc = queryRuntimeAPI.getDocument(attachment.getUUID());
                            if(bonita_doc != null){
                                documentFiles.add(SignatureHelper.getFileFromProcess(queryRuntimeAPI, bonita_doc, path));
                            }
                        }
                    }
                }
            }
            session = signer.startSession(documentFiles, true);

//            if(!cert.startsWith("-----BEGIN CERTIFICATE-----"))
//            {
//                cert=new String(Base64.decodeBase64(cert));
//            }
            //kui on cert topelt kodeeritud (sest cerdi andmed on sisestatud <input type="text"> valjale ja see soob ara reavahetused ning cert selle tottu ei toota
            signatureHash=signer.PrepareSignature(session, cert, certId, "", "", "", "", "", "");
            SignatureHelper.serializeToDisc(path, "Signature.obj", session);

        }catch (Exception e){
            logger.error("executeConnector", e);
            errors.add(new ConnectorError("allkiri", e));
            FileUtils.deleteDirectory(new File(path));
            // throw e;
        }
	}

	@Override
	protected List<ConnectorError> validateValues() {

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
        if(errors.size()>0)
            for (ConnectorError error:errors){
                logger.error(error.getField(), error.getError());
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
     * Setter for input argument 'certId'
     * DO NOT REMOVE NOR RENAME THIS SETTER, unless you also change the related entry in the XML descriptor file
     */
	public void setCertId(java.lang.String certId) {
		this.certId = certId;
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
