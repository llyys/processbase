package org.processbase.ui.core.bonita.diagram;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.light.LightActivityInstance;
import org.ow2.bonita.light.LightProcessInstance;
import org.processbase.ui.core.BPMModule;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ProcessParser {

	public static Pattern pattern = Pattern.compile("process:(.+)Type");
	
	protected HashMap<String, Process> processes = new HashMap<String, Process>();

	private ProcessParser() {
		super();
	}

	public void parceProcesses(InputStream processXML, ProcessDefinitionUUID processDefinition) {
	    int diagrammY = 40;
	    try {
	    	
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        Document doc = db.parse(processXML);
	        doc.getDocumentElement().normalize();
	        
	        Node mainProcess = doc.getElementsByTagName("process:MainProcess").item(0);
	        NodeList processNodes = mainProcess.getChildNodes();
	        
	        for (int i = 0; i < processNodes.getLength(); i++) {
	        	Node processNode = processNodes.item(i);
	            String node = processNode.getNodeName();
	            String nodeType=getNodeAttribute(processNode, "xmi:type");
				if ("process:Pool".equals(nodeType)) {                   
	                
	                String nodeName = getNodeAttribute(processNode, "name");
	                String nodeId = getNodeAttribute(processNode, "xmi:id");
					
	                Process process=new Process(nodeName);
	                process.setId(node);
	                
					parseProcessDataTypes(mainProcess, process);
		            
		            parceSteps(process, processNode.getChildNodes());     
		            ProcessParser.getProcessDefinitions().put(processDefinition, process);
					//processes.put(nodeId, process);
	            }
	        }
	        
	        Node mainDiagramm = doc.getElementsByTagName("notation:Diagram").item(0);
	        parseProcessDiagramms(doc.getElementsByTagName("notation:Diagram"));
	        
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}

	/**
	 * Will load process data types from process xml declaration
	 * @param processNode
	 * @param process
	 */
	private void parseProcessDataTypes(Node processNode, Process process) {
		for (int j = 0; j < processNode.getChildNodes().getLength(); j++) {
			Node n=processNode.getChildNodes().item(j);
			if("datatypes".equalsIgnoreCase(n.getNodeName())){
				ProcessDataType data=new ProcessDataType();
				data.setLabel(getNodeAttribute(n, "label"));
				data.setName(getNodeAttribute(n, "name"));
				data.setType(getNodeAttribute(n, "xmi:type"));
				process.registerProcessDataType(getNodeAttribute(n, "xmi:id"), data);
			}
		}
	}

	private void parseProcessDiagramms(NodeList diagramms) throws Exception {
		for (int i = 0; i < diagramms.getLength(); i++) {
			Node diagramm=diagramms.item(i);
			if("Process".equalsIgnoreCase(getNodeAttribute(diagramm, "type"))){
				Process process=findDiagrammProcess(diagramm);
				
				if(process!=null){
					Node decorationNode=getChildNodeByAttr(diagramm,"type", "7002");
					if(decorationNode!=null)
						parseProcessShapes(process, decorationNode);
					else{
						decorationNode=getChildNodeByAttr(diagramm,"type", "7001");
						parseProcessShapes(process, decorationNode);
					}
					//find process bounds
					for (int j = 0; j < decorationNode.getParentNode().getChildNodes().getLength(); j++) {
						Node c=decorationNode.getParentNode().getChildNodes().item(j);
						if("notation:Bounds".equals(getNodeAttribute(c, "xmi:type"))){
							if(getNodeAttribute(c,"x")!=null)
								process.setWidth(Integer.parseInt(getNodeAttribute(c,"x")));
							else if(getNodeAttribute(c,"width")!=null)
								process.setWidth(Integer.parseInt(getNodeAttribute(c,"width")));
							if(getNodeAttribute(c,"y")!=null)
	                    		process.setHeight(Integer.parseInt(getNodeAttribute(c,"y")));
							else if(getNodeAttribute(c,"height")!=null)
								process.setHeight(Integer.parseInt(getNodeAttribute(c,"height")));
	                    	
						}
					}
				}
			}
		}
	}

	private Process findDiagrammProcess(Node diagramm) throws Exception {
		for (int i = 0; i < diagramm.getChildNodes().getLength(); i++) {
			Node notation=diagramm.getChildNodes().item(i);
			if("notation:Node".equals(getNodeAttribute(notation, "xmi:type"))){
				String nodeAttribute = getNodeAttribute(notation, "element");
				return ProcessParser.findProcessById(nodeAttribute);
				//return processes.get(nodeAttribute);
			}
		}
		return null;
	}

	

	private void parseProcessShapes(Process process, Node decorationNode) {
		HashMap<String, Step> steps = process.getSteps();
		for (int i = 0; i < decorationNode.getChildNodes().getLength(); i++) {
			Node shape=decorationNode.getChildNodes().item(i);
			if("notation:Shape".equals(getNodeAttribute(shape, "xmi:type"))){
				Step step=steps.get(getNodeAttribute(shape, "element"));
				
				for (int j = 0; j < shape.getChildNodes().getLength(); j++) {
					Node child=shape.getChildNodes().item(j);
					if("notation:Bounds".equals(getNodeAttribute(child, "xmi:type"))){
						
	                    step.setX(Integer.parseInt(getNodeAttribute(child,"x")));
	                    step.setY(Integer.parseInt(getNodeAttribute(child,"y")));
	                    try {                        	
	                    	if(getNodeAttribute(child,"width")!=null)
	                    		step.setWidth(Integer.parseInt(getNodeAttribute(child,"width")));
	                    	if(getNodeAttribute(child,"height")!=null)
	                    		step.setHeight(Integer.parseInt(getNodeAttribute(child,"height")));
	                    } catch (Exception ex) {
	                    }
						break;
					}
				}
			}
		}
		
	}

	private Node getChildNodeByAttr(Node diagramm, String attribute, String value) {
		for (int i = 0; i < diagramm.getChildNodes().getLength(); i++) {
			Node child=diagramm.getChildNodes().item(i);
			if(value.equalsIgnoreCase(getNodeAttribute(child, attribute)))
				return child;
			if(child.hasChildNodes())
			{
				Node result=getChildNodeByAttr(child, attribute, value);
				if(result!=null)
					return result;
			}
			
		}
		return null;		
	}

	private String getNodeAttribute(Node node, String attr) {
		NamedNodeMap attributes = node.getAttributes();
		if(attributes!=null) {
			Node namedItem = attributes.getNamedItem(attr);
			if(namedItem!=null)
				return namedItem.getNodeValue();
		}
		return null;
	}

	private void parceSteps(Process process, NodeList steps) {
	    try {
	        for (int x = 0; x < steps.getLength(); x++) {
	            Node item = steps.item(x);
				String node = item.getNodeName();
	            String nodeType = getNodeAttribute(item, "xmi:type");
	            String nodeId = getNodeAttribute(item, "xmi:id");
	            if("process:Lane".equals(nodeType)){
	            	process.setLane(getNodeAttribute(item, "xmi:id"), getNodeAttribute(item, "name"));
	            	parseProcessStep(process, item);
	            	return;
	            }
	            else if (node.equals("elements")) {
	            	parseProcessStep(process, item);
	            }
	            
	            if("data".equalsIgnoreCase(item.getNodeName())){
	            	parseProcessDataFields(process, item);
	            }
	            
	        }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}

	private void parseProcessDataFields(Process process, Node item) {
		ProcessData data=new ProcessData();
		data.setName(getNodeAttribute(item, "name"));
		String dataTypeId = getNodeAttribute(item, "dataType");
		data.setDataType(process.getProcessDataType(dataTypeId));
		
		data.setType(getNodeAttribute(item, "xmi:type"));
		
		process.registerProcessData(getNodeAttribute(item, "xmi:id"), data);
	}

	private void parseProcessStep(Process process, Node item) {
		String nodeType;
		String nodeId;
		for (int i = 0; i < item.getChildNodes().getLength(); i++) {
			Node stepNode = item.getChildNodes().item(i);
			nodeType = getNodeAttribute(stepNode, "xmi:type");
			if(nodeType!=null){
				String nodeName = getNodeAttribute(stepNode, "name");
				nodeId = getNodeAttribute(stepNode, "xmi:id");
				Step step = new Step(nodeName, nodeType);
				process.addStep(nodeId, step);
			}
		}
	}

	private Node findChildNodeNamed(String nodeName, Node parent) {
		
		for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
			Node current=parent.getChildNodes().item(i);
			if(current.getNodeName().equals(nodeName))
				return current;
			if(current.hasChildNodes())
			{
				Node found=findChildNodeNamed(nodeName, current);
				if(found!=null)
					return found;
			}
		}
		
		return null;
		
	}

	private void parceSteps2(Process process, NodeList steps) {
	        try {
	            for (int z = 0; z < steps.getLength(); z++) {
	                String nodeName = steps.item(z).getNodeName();
					if (nodeName.equals("children")) {
						String nodeType = getNodeAttribute(steps.item(z), "xmi:type");
	                    Node shape = steps.item(z);
	                    for (int a = 0; a < shape.getChildNodes().getLength(); a++) {
	//                        Node item = shape.getChildNodes().item(a);
							Node item=findChildNodeNamed("layoutConstraint", shape.getChildNodes().item(a));
							String nodeName2 = item.getNodeName();
							if (nodeName2.equals("layoutConstraint")) {
	                            Node coordinate = item;
	                            String nodeValue = getNodeAttribute(shape, "element");
								Step step = process.getSteps().get(nodeValue);
	                            step.setX(Integer.parseInt(getNodeAttribute(coordinate,"x")));
	                            step.setY(Integer.parseInt(getNodeAttribute(coordinate,"y")));
	                            try {
	                                step.setWidth(Integer.parseInt(getNodeAttribute(coordinate,"width")));
	                                step.setHeight(Integer.parseInt(getNodeAttribute(coordinate,"height")));
	                            } catch (Exception ex) {
	                            }
	                            process.addStep(nodeValue, step);
	                        }
	                    }
	                }
	            }
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	    }

	
	private static HashMap<ProcessDefinitionUUID,Process> processDefinitions=null;
	

	public static Process getProcessDefinition(BPMModule bpmModule, ProcessDefinitionUUID pi) throws Exception {
		
		
		if(getProcessDefinitions().containsKey(pi)){
			return getProcessDefinitions().get(pi);
		}
		
		byte[] xmlBytes = null;
		Map<String, byte[]> resource = bpmModule.getBusinessArchive(pi);
        for (String key : resource.keySet()) {
            if (key.substring(key.length() - 4, key.length()).equals("proc")) {
            	xmlBytes = resource.get(key);
            	break;
            } 
        }
        
        ProcessParser parser=new ProcessParser();
        parser.parceProcesses(new ByteArrayInputStream(xmlBytes), pi);
		return getProcessDefinitions().get(pi);
	}

	

	public static HashMap<ProcessDefinitionUUID,Process> getProcessDefinitions() {
		if(processDefinitions==null)
			processDefinitions=new HashMap<ProcessDefinitionUUID, Process>();
		return processDefinitions;
	}
	
	private static Process findProcessById(String processId) throws Exception {
		for (Entry<ProcessDefinitionUUID, Process> p : getProcessDefinitions().entrySet()) {
			if(processId.equals(p.getValue().getId()))
				return p.getValue();
		}
		throw new Exception("Process by Id "+processId+" not found");
	}

}