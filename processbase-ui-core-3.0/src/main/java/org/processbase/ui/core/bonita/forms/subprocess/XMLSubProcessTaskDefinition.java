package org.processbase.ui.core.bonita.forms.subprocess;

import java.util.ArrayList;
import java.util.List;

import org.processbase.ui.core.bonita.forms.XMLTaskDefinition;

/**
 * Object to describe sub-process task info
 * 
 * @author Lauri Lyys
 */
public class XMLSubProcessTaskDefinition extends XMLTaskDefinition {
	public XMLSubProcessTaskDefinition(String name, String label, String subprocessName) {
		super(name, label);
		this.subprocessName = subprocessName;
	}

	private String subprocessName;
	private List<XMLSubProcessInput> inputMappings;
	private List<XMLSubProcessOutput> outputMappings;
	
	public void setSubprocessName(String subprocessName) {
		this.subprocessName = subprocessName;
	}

	public String getSubprocessName() {
		return subprocessName;
	}

	public void addInputMapping(XMLSubProcessInput xmlSubProcessInput) {
		if (inputMappings == null)
			inputMappings = new ArrayList<XMLSubProcessInput>();
		inputMappings.add(xmlSubProcessInput);
	}

	public void addOutputMapping(XMLSubProcessOutput xmlSubProcessOutput) {
		if (outputMappings == null)
			outputMappings = new ArrayList<XMLSubProcessOutput>();
		outputMappings.add(xmlSubProcessOutput);
	}

	public List<XMLSubProcessInput> getInputMappings() {
		return inputMappings;
	}

	public List<XMLSubProcessOutput> getOutputMappings() {
		return outputMappings;
	}
}