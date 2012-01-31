package org.processbase.ui.core.bonita.diagram;

public class ProcessData {
	String type;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ProcessDataType getDataType() {
		return dataType;
	}
	public void setDataType(ProcessDataType dataType) {
		this.dataType = dataType;
		this.type=dataType.getType();
	}
	String name;
	ProcessDataType dataType;
	
}
