package org.processbase.ui.core;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Map;

import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.services.Document;
import org.processbase.ui.core.template.ByteArraySource;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.terminal.URIHandler;

public class ParameterizedResource implements URIHandler, ParameterHandler {
	String processInstanceUUID = null;
	String file = null;

	/**
	 * Provides the dynamic resource if the URI matches the resource URI. The
	 * matching URI is "/myresource" under the application URI context.
	 * 
	 * Returns null if the URI does not match. Otherwise returns a download
	 * stream that contains the response from the server.
	 */
	@Override
	public DownloadStream handleURI(URL context, String relativeUri) {
		// Catch the given URI that identifies the resource,
		// otherwise let other URI handlers or the Application
		// to handle the response.
		if (!relativeUri.startsWith("process_file"))
			return null;

		// Create an image and draw some background on it.

		ProcessbaseApplication current = ProcessbaseApplication.getCurrent();
		processInstanceUUID=current.getHttpServletRequest().getParameter("download");
		file=current.getHttpServletRequest().getParameter("file");
		BPMModule bpmModule = current.getBpmModule();
		// AttachmentInstance attachment =
		// bpmModule.getAttachment(processUUID,widget.getVariableBound());
		Document document;
		try {
			document = bpmModule.getDocument(new ProcessInstanceUUID(
					processInstanceUUID), file);

			// Document document = bpmModule.getDocument(attachment.getUUID());
			byte[] bytes = bpmModule.getDocumentBytes(document);
			if(bytes==null)//there is no content :S
				return null;
			// Return a stream from the buffer.
			ByteArrayInputStream istream = new ByteArrayInputStream(bytes);
			return new DownloadStream(istream, document.getContentMimeType(), document.getContentFileName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Handle the URL parameters and store them for the URI handler to use.
	 */
	@Override
	public void handleParameters(Map<String, String[]> parameters) {
		// Get and store the passed HTTP parameter.
		if (parameters.containsKey("download"))
			processInstanceUUID = ((String[]) parameters.get("download"))[0];
		if (parameters.containsKey("file"))
			file = ((String[]) parameters.get("file"))[0];
	}

}
