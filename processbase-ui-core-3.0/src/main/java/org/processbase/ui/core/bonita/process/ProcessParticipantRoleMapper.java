package org.processbase.ui.core.bonita.process;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ProcessParticipantRoleMapper{
	String className;
	Map parameters;
	
	public ProcessParticipantRoleMapper(Node pnode) {
		NodeList nodes = pnode.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if(node.getNodeName().equalsIgnoreCase("classname")){
				className=node.getTextContent();
			}
			if(node.getNodeName().equalsIgnoreCase("parameters")){
				NodeList paramNodes = node.getChildNodes();
				if(paramNodes!=null){
					this.parameters= new HashMap<String, String>();
					for (int j = 0; j < paramNodes.getLength(); j++) {
						Node paramNode = paramNodes.item(j);
						if(paramNode.getAttributes()!=null){
							String paramName=paramNode.getAttributes().getNamedItem("name").getTextContent();
							String paramValue=paramNode.getTextContent();
							this.parameters.put(paramName, paramValue);
						}
					}
				}
			}
		}
	}
	public String getClassName() {
		return className;
	}
	public Map getParameters() {
		return parameters;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public void setParameters(Map parameters) {
		this.parameters = parameters;
	} 
}