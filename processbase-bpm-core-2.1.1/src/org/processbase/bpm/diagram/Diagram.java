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
package org.processbase.bpm.diagram;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author mgubaidullin
 */
public class Diagram {

    BufferedImage processImage = null;
    InputStream processXML;
    private static HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();
    private HashMap<String, Process> processes = new HashMap<String, Process>();
    private Set<LightActivityInstance> activityInstances;

    public Diagram(byte[] imageBytes, byte[] xmlBytes, Set<LightActivityInstance> activityInstances) {
        try {
            this.activityInstances = getLastActivities(activityInstances);
            this.processXML = new ByteArrayInputStream(xmlBytes);
            this.processImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            images.put("READY", ImageIO.read(getClass().getResource("/resources/ready.png")));
            images.put("EXECUTING", ImageIO.read(getClass().getResource("/resources/executing.png")));
            images.put("FINISHED", ImageIO.read(getClass().getResource("/resources/finished.png")));
            images.put("SUSPENDED", ImageIO.read(getClass().getResource("/resources/suspended.png")));
            images.put("INITIAL", ImageIO.read(getClass().getResource("/resources/initial.png")));
            images.put("ABORTED", ImageIO.read(getClass().getResource("/resources/aborted.png")));
            images.put("CANCELLED", ImageIO.read(getClass().getResource("/resources/cancelled.png")));
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
            int coefficientX = processImage.getWidth() * 100 / (process.getWidth() +20);
            int coefficientY = processImage.getHeight() * 100 / (process.getHeight() +20);


            for (String skey : process.getSteps().keySet()) {
                Step step = process.getSteps().get(skey);
                if (getStepState(step.getName()) != null) {
                    BufferedImage ind = Diagram.getImage(getStepState(step.getName()));
                    if (step.getType().equals("process:Task") || step.getType().equals("process:Activity")) {

                        processImage.createGraphics().drawImage(ind,
                                step.getX()*coefficientX/100 + (step.getWidth()*coefficientX/100)/2  + 20 ,
                                step.getY()*coefficientY/100 + (step.getHeight()*coefficientY/100)/2 + 4,
                                null);
                    } else {
                        processImage.createGraphics().drawImage(ind,
                                step.getX()*coefficientX/100 + (step.getWidth()*coefficientX/100)/2 ,
                                step.getY()*coefficientY/100 + (step.getHeight()*coefficientY/100)/2,
                                null);
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
                if (processNodes.item(i).getNodeName().equals("elements")) {
                    Node processNode = processNodes.item(i);
                    Process process = new Process(processNode.getAttributes().getNamedItem("name").getNodeValue());
                    parceSteps(process, processNode.getChildNodes());
                    processes.put(processNode.getAttributes().getNamedItem("xmi:id").getNodeValue(), process);
                }
            }
            Node mainDiagramm = doc.getElementsByTagName("notation:Diagram").item(0);
            NodeList diagramms = mainDiagramm.getChildNodes();
            for (int i = 0; i < diagramms.getLength(); i++) {
                if (diagramms.item(i).getNodeName().equals("children")) {
                    Node diagramm = diagramms.item(i);
                    NodeList diagramm2 = diagramm.getChildNodes();
                    for (int x = 0; x < diagramm2.getLength(); x++) {
                        if (diagramm2.item(x).getNodeName().equals("layoutConstraint")) {
                            Node processSize = diagramm2.item(x);
                            Process process = processes.get(diagramm.getAttributes().getNamedItem("element").getNodeValue());
                            process.setY(diagrammY);
                            process.setWidth(Integer.parseInt(processSize.getAttributes().getNamedItem("width").getNodeValue()));
                            process.setHeight(Integer.parseInt(processSize.getAttributes().getNamedItem("height").getNodeValue()));
                            processes.put(diagramm.getAttributes().getNamedItem("element").getNodeValue(), process);
                            diagrammY = diagrammY + 20 + Integer.parseInt(processSize.getAttributes().getNamedItem("height").getNodeValue());
                        } else if (diagramm2.item(x).getNodeName().equals("children") && diagramm2.item(x).getAttributes().getNamedItem("type").getNodeValue().equals("7001")) {
                            Process process = processes.get(diagramm.getAttributes().getNamedItem("element").getNodeValue());
                            parceSteps2(process, diagramm2.item(x).getChildNodes());
                            processes.put(diagramm.getAttributes().getNamedItem("element").getNodeValue(), process);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void parceSteps(Process process, NodeList steps) {
        try {
            for (int x = 0; x < steps.getLength(); x++) {
                if (steps.item(x).getNodeName().equals("elements")) {
                    Node stepNode = steps.item(x);
                    Step step = new Step(stepNode.getAttributes().getNamedItem("name").getNodeValue(), stepNode.getAttributes().getNamedItem("xmi:type").getNodeValue());
                    process.addStep(stepNode.getAttributes().getNamedItem("xmi:id").getNodeValue(), step);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void parceSteps2(Process process, NodeList steps) {
        try {
            for (int z = 0; z < steps.getLength(); z++) {
                if (steps.item(z).getNodeName().equals("children")) {
                    Node shape = steps.item(z);
                    for (int a = 0; a < shape.getChildNodes().getLength(); a++) {
                        if (shape.getChildNodes().item(a).getNodeName().equals("layoutConstraint")) {
                            Node coordinate = shape.getChildNodes().item(a);
                            Step step = process.getSteps().get(shape.getAttributes().getNamedItem("element").getNodeValue());
                            step.setX(Integer.parseInt(coordinate.getAttributes().getNamedItem("x").getNodeValue()));
                            step.setY(Integer.parseInt(coordinate.getAttributes().getNamedItem("y").getNodeValue()));
                            try {
                                step.setWidth(Integer.parseInt(coordinate.getAttributes().getNamedItem("width").getNodeValue()));
                                step.setHeight(Integer.parseInt(coordinate.getAttributes().getNamedItem("height").getNodeValue()));
                            } catch (Exception ex) {
                            }
                            process.addStep(shape.getAttributes().getNamedItem("element").getNodeValue(), step);
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

    private Set<LightActivityInstance> getLastActivities(Set<LightActivityInstance> activities){
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
        return  new HashSet<LightActivityInstance>(m0.values());
    }
}
