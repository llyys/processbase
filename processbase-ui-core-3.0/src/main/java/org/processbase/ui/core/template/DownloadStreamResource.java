package org.processbase.ui.core.template;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.Application;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.StreamResource;

public class DownloadStreamResource extends StreamResource {

	private static final long serialVersionUID = 2372236237865086394L;
	
	private Map<String, String> params;

	public DownloadStreamResource(StreamSource streamSource, String filename,
			Application application) {
		super(streamSource, filename, application);
	}

	@Override
	public DownloadStream getStream() {
		DownloadStream stream = super.getStream();
		if (params != null) {
			for (String p : params.keySet()) {
				stream.setParameter(p, params.get(p));
			}
		}
		return stream;
	}

	public void setParameter(String name, String value) {
		if (params == null) {
			params = new HashMap<String, String>();
		}
		params.put(name, value);
	}

}
