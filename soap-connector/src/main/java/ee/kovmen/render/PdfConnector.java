package ee.kovmen.render;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ow2.bonita.connector.core.ConnectorError;
import org.ow2.bonita.connector.core.ProcessConnector;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.facade.runtime.AttachmentInstance;
import org.ow2.bonita.facade.runtime.Document;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.util.AccessorUtil;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfConnector extends ProcessConnector {

	private static final Logger LOG = Logger.getLogger(PdfConnector.class);

	private AttachmentInstance file;

	@Override
	protected void executeConnector() throws Exception {

		RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
		QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
		Document doc = queryRuntimeAPI.getDocument(file.getUUID());

		ProcessInstance process = queryRuntimeAPI
				.getProcessInstance(getProcessInstanceUUID());

		Map<String, Object> variables = process.getLastKnownVariableValues();

		try {
			ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();

			com.itextpdf.text.Document pdfDoc = new com.itextpdf.text.Document();
			PdfCopy copy = new PdfCopy(pdfDoc, pdfOut);

			pdfDoc.open();

			PdfReader reader = new PdfReader(
					queryRuntimeAPI.getDocumentContent(doc.getUUID()));

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PdfStamper stamp = new PdfStamper(reader, out);

			List<Object> tables = new ArrayList<Object>();

			// Replace acro fields
			AcroFields form = stamp.getAcroFields();
			for (String field : variables.keySet()) {
				Object value = variables.get(field);
				if (value instanceof List) {
					tables.add(value);
				} else {
					form.setField(field, value == null ? "" : value.toString());
				}
			}
			//stamp.setFormFlattening(true);
			stamp.close();

			// Add template
			PdfReader outReader = new PdfReader(out.toByteArray());
			copy.addPage(copy.getImportedPage(outReader, 1));

			// Create tables
			for (Object object : tables) {
				try {
					List<List<String>> table = (List<List<String>>) object;
					if (table != null && table.size() > 0) {
						PdfPTable pdfTable = new PdfPTable(table.get(0).size());
						for (List<String> row : table) {
							for (String col : row) {
								pdfTable.addCell(col);
							}
						}

						
						out = new ByteArrayOutputStream();

						com.itextpdf.text.Document tableDoc = new com.itextpdf.text.Document();
						PdfWriter.getInstance(tableDoc, out);
						tableDoc.open();

						tableDoc.add(pdfTable);
						tableDoc.close();

						outReader = new PdfReader(out.toByteArray());
						copy.addPage(copy.getImportedPage(outReader, 1));
					}
				} catch (Exception e) {
					LOG.warn("in executeConnector", e);
				}
			}

			copy.close();

			runtimeAPI.addDocumentVersion(doc.getUUID(), true,
					doc.getContentFileName(), doc.getContentMimeType(),
					pdfOut.toByteArray());
		} catch (Exception e) {
			LOG.warn("in executeConnector", e);
		}
	}

	@Override
	protected List<ConnectorError> validateValues() {
		List<ConnectorError> errors = new ArrayList<ConnectorError>();
		if (this.file == null) {
			errors.add(new ConnectorError("file", new Exception(
					"no template specified")));
		}
		return errors.size() == 0 ? null : errors;
	}

	public void setFile(AttachmentInstance file) {
		this.file = file;
	}

}
