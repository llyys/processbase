package org.processbase.ui.core.bonita.process;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.processbase.ui.core.bonita.forms.subprocess.XMLSubProcessInput;
import org.processbase.ui.core.bonita.forms.subprocess.XMLSubProcessOutput;
import org.processbase.ui.core.bonita.forms.subprocess.XMLSubProcessTaskDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import antlr.collections.List;

public class ProcessDefinition implements Serializable{
	
	private ArrayList<Process> processes;
	public ProcessDefinition(byte[] data) throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		 DocumentBuilder db = dbf.newDocumentBuilder();
         Document doc = db.parse(new ByteArrayInputStream(data));
         doc.getDocumentElement().normalize();
         Node process_definition = doc.getElementsByTagName("process-definition").item(0);
         NodeList processNodes = process_definition.getChildNodes();
          setProcesses(new ArrayList<Process>());
         for (int i = 0; i < processNodes.getLength(); i++) {
        	 Node processNode = processNodes.item(i);
        	 if(processNode.getNodeName().equals("process"))
        	 {
        		 Process p = new Process(processNode);
        		 getProcesses().add(p);
        	 }
         }
	}
	public void setProcesses(ArrayList<Process> processes) {
		this.processes = processes;
	}
	public ArrayList<Process> getProcesses() {
		return processes;
	}	
	
}
