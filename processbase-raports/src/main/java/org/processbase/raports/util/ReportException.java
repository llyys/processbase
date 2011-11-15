package org.processbase.raports.util;

import java.io.FileNotFoundException;

public class ReportException extends Exception {

	public ReportException(String string, Exception e) {
		super(string, e);
	}

	public ReportException(String string) {
		super(string);
	}

}
