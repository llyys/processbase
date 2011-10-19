package org.processbase.ui.core.bonita.process;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Process implements Serializable {
	public Process(Node processNode){
		name=processNode.getAttributes().getNamedItem("name").getTextContent();
		this.participants=new ArrayList<String>();
		NodeList nodes = processNode.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if(node.getNodeName().equalsIgnoreCase("participants")){
				NodeList participantNodes = node.getChildNodes();
				for (int j = 0; j < participantNodes.getLength(); j++) {
					Node participant=participantNodes.item(j);
					if("participant".equalsIgnoreCase(participant.getNodeName())){
						participants.add(participant.getAttributes().getNamedItem("name").getTextContent());
					}
				}
			}
		}
		
	}
	String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<String> getParticipants() {
		return participants;
	}
	public void setParticipants(List<String> participants) {
		this.participants = participants;
	}
	String description;
	String type;
	List<String> participants;
}
