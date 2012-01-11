/**
 * Copyright (C) 2010 PROCESSBASE
 * PROCESSBASE Ltd, Almaty, Kazakhstan
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.processbase.ui.core.bonita.diagram;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.ow2.bonita.light.LightActivityInstance;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author mgubaidullin
 */
public class Diagram {

    BufferedImage processImage = null;
    InputStream processXML;
    private static HashMap<String, BufferedImage> images =null;
    private HashMap<String, Process> processes = new HashMap<String, Process>();
    private Set<LightActivityInstance> activityInstances;

    public Diagram(byte[] imageBytes, byte[] xmlBytes, Set<LightActivityInstance> activityInstances) {
        try {
            this.activityInstances = getLastActivities(activityInstances);
            this.processXML = new ByteArrayInputStream(xmlBytes);
            this.processImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if(images!=null)
            	return;
            
            images= new HashMap<String, BufferedImage>();
            images.put("READY", ImageIO.read(getClass().getResource("/icons/start.png")));
            images.put("EXECUTING", ImageIO.read(getClass().getResource("/icons/document.png")));
            images.put("FINISHED", ImageIO.read(getClass().getResource("/icons/accept.png")));
            images.put("SUSPENDED", ImageIO.read(getClass().getResource("/icons/pause.png")));
            images.put("INITIAL", ImageIO.read(getClass().getResource("/icons/start.png")));
            images.put("ABORTED", ImageIO.read(getClass().getResource("/icons/document-delete.png")));
            images.put("CANCELLED", ImageIO.read(getClass().getResource("/icons/cancel.png")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static BufferedImage getImage(String type) {
        return images.get(type);
    }

    public byte[] getImage() throws IOException {
        parceProcesses();
        for (String pkey : processes.keySet()) {
            Process process = processes.get(pkey);
            
            float processWidth = (float)process.getWidth();
			float coefficientX = (1-processWidth/processImage.getWidth());
			
            int processHeigh = process.getHeight();
            float coefficientY = (1-processHeigh/processImage.getHeight());
			
            // well there is a bug in bonita software. Rendered image and task positions are totally off. But when process designer minimizes the diagram into right propotions then positions are ok.
            if(process.getWidth()==0 || coefficientX==0)
            	coefficientX=1.0F;
            if(process.getHeight()==0 || coefficientY==0)
            	coefficientY=1.0F;

            for (String skey : process.getSteps().keySet()) {
                Step step = process.getSteps().get(skey);
                String stepState = getStepState(step.getName());
				if (stepState != null) {
                    BufferedImage ind = Diagram.getImage(stepState);
                    String type = step.getType();
					if (type.equals("process:Task") || type.equals("process:Activity") || type.equals("process:SubProcess")) {
                        int x = (int) ((step.getX() * coefficientX)  + ((step.getWidth() * coefficientX) / 2));
						int y = (int) ((step.getY() * coefficientY ) + ((step.getHeight() * coefficientY ) / 2));
						processImage.createGraphics().drawImage(ind,x,y,null);
                    } else {
                        //System.out.println(" DEBUG: " + step.getName() + (step.getX() * coefficientX / 100 + (step.getWidth() * coefficientX / 100) / 2) + " " + (step.getY() * coefficientY / 100 + (step.getHeight() * coefficientY / 100) / 2));
                        int x = (int) (step.getX() * coefficientX);
						int y = (int) (step.getY() * coefficientY);
						processImage.createGraphics().drawImage(ind,x,y,null);
                    }
                }
            }
        }
        processImage.createGraphics().dispose();
        ByteArrayOutputStream ios = new ByteArrayOutputStream();
        ImageIO.write(processImage, "PNG", ios);
        ios.close();
        return ios.toByteArray();
    }

    private void parceProcesses() {
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
                    parceSteps(process, processNode.getChildNodes());                    
					processes.put(nodeId, process);
                }
            }
            
            Node mainDiagramm = doc.getElementsByTagName("notation:Diagram").item(0);
            parseProcessDiagramms(doc.getElementsByTagName("notation:Diagram"));
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void parseProcessDiagramms(NodeList diagramms) {
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

	

	private Process findDiagrammProcess(Node diagramm) {
		for (int i = 0; i < diagramm.getChildNodes().getLength(); i++) {
			Node notation=diagramm.getChildNodes().item(i);
			if("notation:Node".equals(getNodeAttribute(notation, "xmi:type"))){
				String nodeAttribute = getNodeAttribute(notation, "element");
				return processes.get(nodeAttribute);
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

	

	private String getNodeAttribute(Node node, String attr){
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
                String node = steps.item(x).getNodeName();
                String nodeType = getNodeAttribute(steps.item(x), "xmi:type");
                String nodeId = getNodeAttribute(steps.item(x), "xmi:id");
                if("process:Lane".equals(nodeType)){
                	process.setLane(getNodeAttribute(steps.item(x), "xmi:id"), getNodeAttribute(steps.item(x), "name"));
                	for (int i = 0; i < steps.item(x).getChildNodes().getLength(); i++) {
						Node stepNode = steps.item(x).getChildNodes().item(i);
						nodeType = getNodeAttribute(stepNode, "xmi:type");
						if(nodeType!=null){
							String nodeName = getNodeAttribute(stepNode, "name");
							nodeId = getNodeAttribute(stepNode, "xmi:id");
							Step step = new Step(nodeName, nodeType);
							process.addStep(nodeId, step);
						}
					}
                	return;
                }
                else if (node.equals("elements")) {
                	for (int i = 0; i < steps.item(x).getChildNodes().getLength(); i++) {
						Node stepNode = steps.item(x).getChildNodes().item(i);
						nodeType = getNodeAttribute(stepNode, "xmi:type");
						String nodeName = getNodeAttribute(stepNode, "name");
						nodeId = getNodeAttribute(stepNode, "xmi:id");
						Step step = new Step(nodeName, nodeType);
						process.addStep(nodeId, step);
					}
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Node findChildNodeNamed(String nodeName, Node parent){
    	
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

    private String getStepState(String name) {
        for (Iterator iter = activityInstances.iterator(); iter.hasNext();) {
            LightActivityInstance ai = (LightActivityInstance) iter.next();
            if (ai.getActivityName().equals(name)) {
                return ai.getState().toString();
            }
        }
        return null;
    }

    private Set<LightActivityInstance> getLastActivities(Set<LightActivityInstance> activities) {
        HashMap<String, LightActivityInstance> m0 = new HashMap<String, LightActivityInstance>();
        for (LightActivityInstance lai : activities) {
            LightActivityInstance newLai = m0.get(lai.getActivityName());
            if (newLai != null) {
                if (newLai.getReadyDate().compareTo(lai.getReadyDate()) < 0) {
                    m0.put(lai.getActivityName(), lai);
                }
            } else {
                m0.put(lai.getActivityName(), lai);
            }
        }
        return new HashSet<LightActivityInstance>(m0.values());
    }
}
