package ee.kovmen.render;
import org.ow2.bonita.connector.core.ConnectorError;
import org.ow2.bonita.connector.core.ProcessConnector;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.runtime.AttachmentInstance;
import org.ow2.bonita.facade.runtime.ProcessInstance;

import org.ow2.bonita.util.AccessorUtil;

import org.ow2.bonita.facade.runtime.AttachmentInstance;
import org.ow2.bonita.util.AccessorUtil;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.AcroFields;


public class PdfConnector extends ProcessConnector {

	private org.ow2.bonita.facade.runtime.AttachmentInstance file;
	
	@Override
	protected void executeConnector() throws Exception {

		QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
		org.ow2.bonita.facade.runtime.Document doc = queryRuntimeAPI.getDocument(file.getUUID());
		
		ProcessInstance process= queryRuntimeAPI.getProcessInstance(getProcessInstanceUUID());
		
		byte[] source=queryRuntimeAPI.getDocumentContent(doc.getUUID());
		
		byte[] parsed=PdfParser.replaceTokensInPdfFile(process.getLastKnownVariableValues(), source);
		AccessorUtil.getRuntimeAPI().addDocumentVersion(doc.getUUID(), true, doc.getContentFileName(), doc.getContentMimeType(), parsed);
		
	}

	@Override
	protected List<ConnectorError> validateValues() {
		List<ConnectorError> errors = new ArrayList<ConnectorError>();
		if(this.file==null){
			errors.add(new ConnectorError("file", new Exception("attachment fail puudub")));
		}
		if(this.file.getFileName()==""){
			errors.add(new ConnectorError("file", new Exception("No files to parse")));
		}		
		return errors.size()==0?null:errors;
	}
	
	public void setFile(org.ow2.bonita.facade.runtime.AttachmentInstance file) {
		this.file = file;
	}

		
}
