package org.processbase.ui.core.bonita.process;


import org.ow2.bonita.connector.core.RoleResolver;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ProcessParticipant {
	String description;
	String label;
	String name;
	private ProcessParticipantRoleMapper roleMapper;
	
	public ProcessParticipant(Node pnode) {
		name=pnode.getAttributes().getNamedItem("name").getTextContent();
		NodeList nodes = pnode.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if(node.getNodeName().equalsIgnoreCase("label")){
				name=node.getTextContent();
			}
			if(node.getNodeName().equalsIgnoreCase("description")){
				description=node.getTextContent();
			}
			if(node.getNodeName().equalsIgnoreCase("role-mapper")){
				roleMapper=new ProcessParticipantRoleMapper(node); 
				
			}
		}
	}
	public String getDescription() {
		return description;
	}
	public String getLabel() {
		return label;
	}
	public String getName() {
		return name;
	}
	public ProcessParticipantRoleMapper getRoleMapper() {
		return roleMapper;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public void setRoleMapper(ProcessParticipantRoleMapper roleMapper) {
		this.roleMapper = roleMapper;
	}
}