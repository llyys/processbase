package ee.kovmen.xtee.adit;

import java.util.List;

import ee.finestmedia.xtee.client.adit.AditRequestInfo;
import ee.finestmedia.xtee.client.adit.AditResponse;
import ee.finestmedia.xtee.client.adit.AmetlikudDokumendidXTeeServiceException;
import ee.finestmedia.xtee.client.adit.domain.Document;
import ee.finestmedia.xtee.client.adit.domain.files.DocumentFile;

public class AditSaveDocument extends AditSendDocument {
	public Long saveDocument(String isikukood, String title, int previous_document_id) throws AmetlikudDokumendidXTeeServiceException, Exception{
		AditRequestInfo info = new AditRequestInfo(isikukood);
				
		if(!isikukood.startsWith("EE"))
			info.setIdCode("EE"+isikukood);
		else
			info.setIdCode(isikukood);
		
		Document document=new Document();
		document.setTitle(title);
		if(previous_document_id!=0)
			document.setPreviousId(new Integer(previous_document_id));
		
		List<DocumentFile> documentFiles = getDocumentFiles();
		document.setFiles(documentFiles);
		
		AditResponse<Long> saveDocument = getService().saveDocument(info, document);
		if(saveDocument.getSuccess())
		 return saveDocument.getExtra();		
		return null;
	}
}
