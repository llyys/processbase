package org.processbase.ui.core.bonita.forms.subprocess;

public class XMLSubProcessOutput {
	
	public XMLSubProcessOutput(String subprocessSource){
		this.setSubprocessSource(subprocessSource);
	}
		
	public void setSubprocessSource(String subprocessSource) {
		this.subprocessSource = subprocessSource;
	}
	public String getSubprocessSource() {
		return subprocessSource;
	}

	public void setProcessTarget(String processTarget) {
		this.processTarget = processTarget;
	}

	public String getProcessTarget() {
		return processTarget;
	}

	private String subprocessSource;
	private String processTarget;
	
}