package ee.kovmen.xtee.adit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.io.IOUtils;
import org.ow2.bonita.connector.core.ConnectorError;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.exception.DocumentNotFoundException;
import org.ow2.bonita.util.AccessorUtil;

import ee.finestmedia.xtee.client.adit.AditRequestInfo;
import ee.finestmedia.xtee.client.adit.AditResponse;
import ee.finestmedia.xtee.client.adit.AmetlikudDokumendidXTeeServiceException;
import ee.finestmedia.xtee.client.adit.domain.Document;
import ee.finestmedia.xtee.client.adit.domain.files.DocumentFile;

public class AditSendDocument extends AditConnector {

	// DO NOT REMOVE NOR RENAME THIS FIELD
	private org.ow2.bonita.facade.runtime.AttachmentInstance files;
	// DO NOT REMOVE NOR RENAME THIS FIELD
	private java.lang.String title;
	// DO NOT REMOVE NOR RENAME THIS FIELD
	private java.lang.String previous_doc;
	// DO NOT REMOVE NOR RENAME THIS FIELD
	private java.lang.String isikukood;
	// DO NOT REMOVE NOR RENAME THIS FIELD
	private java.lang.String document_type;
	private Long document_id;
	
	@Override
	protected void executeConnector() throws Exception {
		document_id=sendDocument(isikukood, title, previous_doc, getDocumentFiles());
		
	}

	@Override
	protected List<ConnectorError> validateValues() {
		List<ConnectorError> errors = new ArrayList<ConnectorError>();
		if(this.isikukood==null){
			errors.add(new ConnectorError("Isikukood", new Exception("Isikukood puudub")));
		}
		if(!this.isikukood.startsWith("EE")){
			isikukood="EE"+isikukood;
		}
		if(files==null){
			errors.add(new ConnectorError("files", new Exception("No files added")));
		}
		
		return errors.size()==0?null:errors;
	}
	public static String createData(byte[] bytes) throws AmetlikudDokumendidXTeeServiceException
	{
		//return new String(bytes);
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		 
		try {
			Base64OutputStream b = new Base64OutputStream(out);
			//GZIPOutputStream g = new GZIPOutputStream(b);
			//IOUtils.write(bytes, g);
			IOUtils.write(bytes, b);
			b.close();
			//g.close();
			byte[] outarr = out.toByteArray();
			out.close();
			return new String( outarr);
		} catch (IOException e) {
			throw new AmetlikudDokumendidXTeeServiceException("Could not create document", e);
		}
	
	}
	public List<DocumentFile> getDocumentFiles() throws DocumentNotFoundException,	AmetlikudDokumendidXTeeServiceException {
		QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
		List<DocumentFile> documentFiles=new ArrayList<DocumentFile>();
			DocumentFile df=new DocumentFile();
						
			org.ow2.bonita.facade.runtime.Document bdoc = queryRuntimeAPI.getDocument(this.files.getUUID());
			df.setName(bdoc.getName());
			df.setSize((int) bdoc.getContentSize());
			df.setContentType(bdoc.getContentMimeType());
			byte[] content=queryRuntimeAPI.getDocumentContent(this.files.getUUID());
			df.setData(createData(content));
			documentFiles.add(df);							
		
		return documentFiles;
	}
	
	public Long sendDocument(String isikukood, String title, String previous_doc, List<DocumentFile> documentFiles) throws Exception {
		
		AditRequestInfo info = new AditRequestInfo(isikukood);
				
		if(!isikukood.startsWith("EE"))
			info.setIdCode("EE"+isikukood);
		else
			info.setIdCode(isikukood);
		
		Document document=new Document();
		document.setTitle(title);
		
		if(previous_doc!=null)
		{
			document.setPreviousId(new Integer(previous_doc));
		}
		
		document.setFiles(documentFiles);
		
		AditResponse<Long> saveDocument = getService().saveDocument(info, document);
		boolean success= saveDocument.getSuccess();
		return saveDocument.getExtra();	

	}

}
