package org.processbase.ui.core.bonita.process;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Process implements Serializable {
	public Process(Node processNode){
		name=processNode.getAttributes().getNamedItem("name").getTextContent();
		this.participants=new ArrayList<ProcessParticipant>();
		NodeList nodes = processNode.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if(node.getNodeName().equalsIgnoreCase("participants")){
				NodeList participantNodes = node.getChildNodes();
				for (int j = 0; j < participantNodes.getLength(); j++) {
					Node participant=participantNodes.item(j);
					if("participant".equalsIgnoreCase(participant.getNodeName())){
						ProcessParticipant part=new ProcessParticipant(participant);
						//participants.add(participant.getAttributes().getNamedItem("name").getTextContent());
						participants.add(part);
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
	public List<ProcessParticipant> getParticipants() {
		return participants;
	}
	public void setParticipants(List<ProcessParticipant> participants) {
		this.participants = participants;
	}
	String description;
	String type;
	List<ProcessParticipant> participants;
}
