package org.processbase.ui.core.bonita.forms;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.processbase.ui.core.bonita.forms.subprocess.XMLSubProcessInput;
import org.processbase.ui.core.bonita.forms.subprocess.XMLSubProcessOutput;
import org.processbase.ui.core.bonita.forms.subprocess.XMLSubProcessTaskDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BonitaFormParcer {

    private Map<String, XMLProcessDefinition> processPoolMap;
    
    

    public BonitaFormParcer(byte[] procBytes) {
        try {
        	processPoolMap=new Hashtable<String,XMLProcessDefinition>();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new ByteArrayInputStream(procBytes));
            doc.getDocumentElement().normalize();
            Node mainProcess = doc.getElementsByTagName("process:MainProcess").item(0);
            NodeList processNodes = mainProcess.getChildNodes();
            for (int i = 0; i < processNodes.getLength(); i++) {
                if (processNodes.item(i)!=null 
                		&& processNodes.item(i).getNodeName().equals("elements")
                        && processNodes.item(i).getAttributes().getNamedItem("xmi:type").getNodeValue().equals("process:Pool")) {
                    Node processNode = processNodes.item(i);
                    String nodeName=processNode.getAttributes().getNamedItem("name").getNodeValue();
                   
                    XMLProcessDefinition process = new XMLProcessDefinition(nodeName, processNode.getAttributes().getNamedItem("label").getNodeValue());
                    process.setByPassFormsGeneration(processNode.getAttributes().getNamedItem("byPassFormsGeneration") != null && processNode.getAttributes().getNamedItem("byPassFormsGeneration").getNodeValue().equals("true"));
                    NodeList processChilds = processNode.getChildNodes();
                    for (int y = 0; y < processChilds.getLength(); y++) {
                        // TASKS IN POOL (NO LANES)
                        String nodeName2 = processChilds.item(y).getNodeName();
						if (nodeName2.equals("elements") && processChilds.item(y).getAttributes().getNamedItem("xmi:type").getNodeValue().equals("process:Task")) {
                            Node nodeTask = processChilds.item(y);
                            if (nodeTask.getChildNodes().getLength() > 0) {
                                XMLTaskDefinition task = new XMLTaskDefinition(nodeTask.getAttributes().getNamedItem("name").getNodeValue(), nodeTask.getAttributes().getNamedItem("label").getNodeValue());
                                task.setByPassFormsGeneration(nodeTask.getAttributes().getNamedItem("byPassFormsGeneration") != null && nodeTask.getAttributes().getNamedItem("byPassFormsGeneration").getNodeValue().equals("true"));
                                process.addTask(nodeTask.getAttributes().getNamedItem("name").getNodeValue(), task);
                            }
						}    
						//Sub process
						else if (nodeName2.equals("elements") && processChilds.item(y).getAttributes().getNamedItem("xmi:type").getNodeValue().equals("process:SubProcess")) {
                            Node nodeTask = processChilds.item(y);
                            if (nodeTask.getChildNodes().getLength() > 0) {
                                XMLSubProcessTaskDefinition task = ExtractSubProcessTaskDefinition(nodeTask);
                                process.addTask(nodeTask.getAttributes().getNamedItem("name").getNodeValue(), task);
                            }                         
                        } 
                         // TASKS IN LANES
                         else if (nodeName2.equals("elements") && processChilds.item(y).getAttributes().getNamedItem("xmi:type").getNodeValue().equals("process:Lane")) {
                            NodeList laneChilds = processChilds.item(y).getChildNodes();
                            for (int z = 0; z < laneChilds.getLength(); z++) {
                                if (laneChilds.item(z).getNodeName().equals("elements") && laneChilds.item(z).getAttributes().getNamedItem("xmi:type").getNodeValue().equals("process:Task")) {
                                    Node nodeTask = laneChilds.item(z);
                                    if (nodeTask.getChildNodes().getLength() > 0) {
                                        XMLTaskDefinition task = new XMLTaskDefinition(nodeTask.getAttributes().getNamedItem("name").getNodeValue(), nodeTask.getAttributes().getNamedItem("label").getNodeValue());
                                        task.setByPassFormsGeneration(nodeTask.getAttributes().getNamedItem("byPassFormsGeneration") != null && nodeTask.getAttributes().getNamedItem("byPassFormsGeneration").getNodeValue().equals("true"));
                                        process.addTask(nodeTask.getAttributes().getNamedItem("name").getNodeValue(), task);
                                    }
                                }
                                else if (laneChilds.item(z).getNodeName().equals("elements") && laneChilds.item(z).getAttributes().getNamedItem("xmi:type").getNodeValue().equals("process:SubProcess")) {
                                    Node nodeTask = laneChilds.item(z);
                                    if (nodeTask.getChildNodes().getLength() > 0) {
                                    	XMLSubProcessTaskDefinition task = ExtractSubProcessTaskDefinition(nodeTask);
                                    	process.addTask(nodeTask.getAttributes().getNamedItem("name").getNodeValue(), task);
                                    }
                                }
                            
                            }
                        }
                        processPoolMap.put(nodeName, process);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


	public XMLSubProcessTaskDefinition ExtractSubProcessTaskDefinition( Node nodeTask) {
		
		NamedNodeMap attributes = nodeTask.getAttributes();
		XMLSubProcessTaskDefinition task = new XMLSubProcessTaskDefinition(attributes.getNamedItem("name").getNodeValue(), attributes.getNamedItem("label").getNodeValue(), attributes.getNamedItem("subprocessName").getNodeValue());
		
		task.setByPassFormsGeneration(attributes.getNamedItem("byPassFormsGeneration") != null 
				&& attributes.getNamedItem("byPassFormsGeneration").getNodeValue().equals("true"));
		
		for (int j = 0; j < nodeTask.getChildNodes().getLength(); j++) {
			
			Node inputMap=nodeTask.getChildNodes().item(j);
			NamedNodeMap attributes2 = inputMap.getAttributes();
			if("inputMappings".equals(inputMap.getNodeName()))
				task.addInputMapping(new XMLSubProcessInput(attributes2.getNamedItem("subprocessTarget").getNodeValue()));	
			else if("outputMappings".equals(inputMap.getNodeName())){
				task.addOutputMapping(new XMLSubProcessOutput(attributes2.getNamedItem("subprocessSource").getNodeValue()));
			}									
		}
		return task;
	}

    
    public Map<String, XMLProcessDefinition> getProcess() {
        return processPoolMap;
    }

    

    public static FormsDefinition createFormsDefinition(String xmlString) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(FormsDefinition.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        ByteArrayInputStream is = new ByteArrayInputStream(xmlString.getBytes());
		return (FormsDefinition) unmarshaller.unmarshal(is);
    }
}
