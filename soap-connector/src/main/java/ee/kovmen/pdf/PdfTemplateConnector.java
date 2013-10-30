package ee.kovmen.pdf;

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

/**
 * PDF generator connector.
 * 
 * @author Margo
 */
public class PdfTemplateConnector extends ProcessConnector {

	/** Logger. */
	private static final Logger LOG = Logger
			.getLogger(PdfTemplateConnector.class);

	private String fileName;

	/** PDF template file. */
	private AttachmentInstance template;

	/** Generated PDF file. */
	private AttachmentInstance file;

	/** Tables */
	private List<Object> tables;

	@Override
	protected void executeConnector() throws Exception {

		RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
		QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();

		// Get process instance
		ProcessInstance process = queryRuntimeAPI
				.getProcessInstance(getProcessInstanceUUID());

		try {
			Document pdf = queryRuntimeAPI.getDocument(file.getUUID());

			ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();

			// Create pdf document
			com.itextpdf.text.Document pdfDoc = new com.itextpdf.text.Document();
			PdfCopy copy = new PdfCopy(pdfDoc, pdfOut);
			pdfDoc.open();

			if (template != null) {
				Document temp = queryRuntimeAPI.getDocument(template.getUUID());

				// Get variable values
				Map<String, Object> variables = process
						.getLastKnownVariableValues();

				// Read PDF template
				PdfReader reader = new PdfReader(
						queryRuntimeAPI.getDocumentContent(temp.getUUID()));

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				PdfStamper stamp = new PdfStamper(reader, out);

				// Replace acro fields
				AcroFields form = stamp.getAcroFields();
				for (String field : variables.keySet()) {
					Object value = variables.get(field);
					if (!(value instanceof List)) {
						form.setField(field,
								value == null ? "" : value.toString());
					}
				}
				stamp.setFormFlattening(true);
				stamp.close();

				int pages = 0;

				// Add template
				PdfReader outReader = new PdfReader(out.toByteArray());
				for (int i = 0; i < outReader.getNumberOfPages(); i++) {
					copy.addPage(copy.getImportedPage(outReader, ++pages));
				}

			}

			// Create tables
			if (tables != null) {

				for (Object object : tables) {
					if (object == null || !(object instanceof List)) {
						continue;
					}
					try {
						List<List<Object>> table = (List<List<Object>>) object;

						int cells = table.get(0).size();
						

						PdfPTable pdfTable = new PdfPTable(cells);
						pdfTable.setSplitLate(false);
						pdfTable.setKeepTogether(true);
						
						

						for (List<Object> row : table) {
							for (Object col : row) {
								pdfTable.addCell(col != null ? col.toString()
										: "");
							}
						}

						ByteArrayOutputStream out = new ByteArrayOutputStream();

						com.itextpdf.text.Document tableDoc = new com.itextpdf.text.Document();
						PdfWriter.getInstance(tableDoc, out);
						tableDoc.open();

						tableDoc.add(pdfTable);
						tableDoc.close();

						PdfReader outReader = new PdfReader(out.toByteArray());

						int pages = 0;
						for (int i = 0; i < outReader.getNumberOfPages(); i++) {
							copy.addPage(copy.getImportedPage(outReader,
									++pages));
						}

					} catch (Exception e) {
						LOG.warn("in executeConnector", e);
					}
				}
			}

			copy.close();
			
			if(!fileName.toLowerCase().endsWith(".pdf")){
				fileName = fileName + ".pdf";
			}

			// Save PDF document
			runtimeAPI.addDocumentVersion(pdf.getUUID(), true, fileName,
					"application/pdf", pdfOut.toByteArray());
		} catch (Exception e) {
			LOG.warn("in executeConnector", e);
		}
	}

	@Override
	protected List<ConnectorError> validateValues() {
		List<ConnectorError> errors = new ArrayList<ConnectorError>();
		if (this.file == null) {
			errors.add(new ConnectorError("file", new Exception(
					"No file specified")));
		}
		return errors.size() > 0 ? errors : null;
	}

	public void setTemplate(AttachmentInstance template) {
		this.template = template;
	}

	public void setFile(AttachmentInstance file) {
		this.file = file;
	}

	public List<Object> getTables() {
		return tables;
	}

	public void setTables(List<Object> tables) {
		this.tables = tables;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
