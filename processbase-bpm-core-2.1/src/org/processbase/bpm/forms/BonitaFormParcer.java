package org.processbase.bpm.forms;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BonitaFormParcer {

    private XMLProcessDefinition process;

    public BonitaFormParcer(byte[] procBytes) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new ByteArrayInputStream(procBytes));
            doc.getDocumentElement().normalize();
            Node mainProcess = doc.getElementsByTagName("process:MainProcess").item(0);
            NodeList processNodes = mainProcess.getChildNodes();
            for (int i = 0; i < processNodes.getLength(); i++) {
                if (processNodes.item(i).getNodeName().equals("elements")
                        && processNodes.item(i).getAttributes().getNamedItem("xmi:type").getNodeValue().equals("process:Pool")) {
                    Node processNode = processNodes.item(i);
                    process = new XMLProcessDefinition(processNode.getAttributes().getNamedItem("name").getNodeValue(), processNode.getAttributes().getNamedItem("label").getNodeValue());
                    process.setByPassFormsGeneration(processNode.getAttributes().getNamedItem("byPassFormsGeneration") != null && processNode.getAttributes().getNamedItem("byPassFormsGeneration").getNodeValue().equals("true"));
                    NodeList processChilds = processNode.getChildNodes();
                    // get proccess level forms
                    process.addForms(getFormsFromNodeList(processChilds));


                    for (int y = 0; y < processChilds.getLength(); y++) {
                        // TASKS IN POOL (NO LANES)
                        if (processChilds.item(y).getNodeName().equals("elements")
                                && processChilds.item(y).getAttributes().getNamedItem("xmi:type").getNodeValue().equals("process:Task")) {
                            Node nodeTask = processChilds.item(y);
                            if (nodeTask.getChildNodes().getLength() > 0) {
                                XMLTaskDefinition task = new XMLTaskDefinition(nodeTask.getAttributes().getNamedItem("name").getNodeValue(), nodeTask.getAttributes().getNamedItem("label").getNodeValue());
                                task.addForms(getFormsFromNodeList(nodeTask.getChildNodes()));
                                task.setByPassFormsGeneration(nodeTask.getAttributes().getNamedItem("byPassFormsGeneration") != null && nodeTask.getAttributes().getNamedItem("byPassFormsGeneration").getNodeValue().equals("true"));
                                process.addTask(nodeTask.getAttributes().getNamedItem("name").getNodeValue(), task);
                            }
                            // TASKS IN LANES
                        } else if (processChilds.item(y).getNodeName().equals("elements")
                                && processChilds.item(y).getAttributes().getNamedItem("xmi:type").getNodeValue().equals("process:Lane")) {
                            NodeList laneChilds = processChilds.item(y).getChildNodes();
                            for (int z = 0; z < laneChilds.getLength(); z++) {
                                if (laneChilds.item(z).getNodeName().equals("elements")
                                        && laneChilds.item(z).getAttributes().getNamedItem("xmi:type").getNodeValue().equals("process:Task")) {
                                    Node nodeTask = laneChilds.item(z);
                                    if (nodeTask.getChildNodes().getLength() > 0) {
                                        XMLTaskDefinition task = new XMLTaskDefinition(nodeTask.getAttributes().getNamedItem("name").getNodeValue(), nodeTask.getAttributes().getNamedItem("label").getNodeValue());
                                        task.addForms(getFormsFromNodeList(nodeTask.getChildNodes()));
                                        task.setByPassFormsGeneration(nodeTask.getAttributes().getNamedItem("byPassFormsGeneration") != null && nodeTask.getAttributes().getNamedItem("byPassFormsGeneration").getNodeValue().equals("true"));
                                        process.addTask(nodeTask.getAttributes().getNamedItem("name").getNodeValue(), task);
                                    }
                                }
                            }
                          // DATA DEFINITON
                        }  else if (processChilds.item(y).getNodeName().equals("data")) {
                            XMLDataDefinition xml = new XMLDataDefinition(processChilds.item(y).getAttributes().getNamedItem("xmi:id").getNodeValue(),
                                    processChilds.item(y).getAttributes().getNamedItem("name").getNodeValue(),
                                    processChilds.item(y).getAttributes().getNamedItem("xmi:type").getNodeValue());
                            process.addData(xml);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public XMLProcessDefinition getProcess() {
        return process;
    }

    private ArrayList<XMLFormDefinition> getFormsFromNodeList(NodeList nodeList) {
        ArrayList<XMLFormDefinition> result = new ArrayList<XMLFormDefinition>();
        for (int y = 0; y < nodeList.getLength(); y++) {
            if (nodeList.item(y).getNodeName().equals("form")) {
                Node nodeForm = nodeList.item(y);
                XMLFormDefinition form = new XMLFormDefinition();
                if (nodeForm.getAttributes().getNamedItem("xmi:id") != null) {
                    form.setId(nodeForm.getAttributes().getNamedItem("xmi:id").getNodeValue());
                }
                if (nodeForm.getAttributes().getNamedItem("xmi:type") != null) {
                    form.setType(nodeForm.getAttributes().getNamedItem("xmi:type").getNodeValue());
                }
                if (nodeForm.getAttributes().getNamedItem("name") != null) {
                    form.setName(nodeForm.getAttributes().getNamedItem("name").getNodeValue());
                }
                if (nodeForm.getAttributes().getNamedItem("label") != null) {
                    form.setLabel(nodeForm.getAttributes().getNamedItem("label").getNodeValue());
                }
                if (nodeForm.getAttributes().getNamedItem("pageLabel") != null) {
                    form.setPageLabel(nodeForm.getAttributes().getNamedItem("pageLabel").getNodeValue());
                }
                if (nodeForm.getAttributes().getNamedItem("nColumn") != null) {
                    form.setnColumn(Integer.parseInt(nodeForm.getAttributes().getNamedItem("nColumn").getNodeValue()));
                }
                if (nodeForm.getAttributes().getNamedItem("nLine") != null) {
                    form.setnLine(Integer.parseInt(nodeForm.getAttributes().getNamedItem("nLine").getNodeValue()));
                }

                form.setWidgets(getWidgetsFromNodeList(nodeForm.getChildNodes()));
                getFormSize(form, nodeForm.getChildNodes());
                result.add(form);
            }
        }
        return result;
    }

    private void getFormSize(XMLFormDefinition form, NodeList nodeList) {
//        for (int y = 0; y < nodeList.getLength(); y++) {
//            if (nodeList.item(y).getNodeName().equals("columns")) {
//                Node nodeWidgets = nodeList.item(y);
//                if (nodeWidgets.getAttributes().getNamedItem("width") != null) {
//                    form.setWidth(nodeWidgets.getAttributes().getNamedItem("width").getNodeValue());
//                }
//            }
//            if (nodeList.item(y).getNodeName().equals("lines")) {
//                Node nodeWidgets = nodeList.item(y);
//                if (nodeWidgets.getAttributes().getNamedItem("height") != null) {
//                    form.setHeight(nodeWidgets.getAttributes().getNamedItem("height").getNodeValue());
//                }
//            }
//        }
    }

    private ArrayList<XMLWidgetsDefinition> getWidgetsFromNodeList(NodeList nodeList) {
        ArrayList<XMLWidgetsDefinition> result = new ArrayList<XMLWidgetsDefinition>();
        for (int y = 0; y < nodeList.getLength(); y++) {
            if (nodeList.item(y).getNodeName().equals("widgets")) {
                Node nodeWidgets = nodeList.item(y);
                XMLWidgetsDefinition widgets = new XMLWidgetsDefinition();
                if (nodeWidgets.getAttributes().getNamedItem("xmi:id") != null) {
                    widgets.setId(nodeWidgets.getAttributes().getNamedItem("xmi:id").getNodeValue());
                }
                if (nodeWidgets.getAttributes().getNamedItem("xmi:type") != null) {
                    widgets.setType(nodeWidgets.getAttributes().getNamedItem("xmi:type").getNodeValue());
                }
                if (nodeWidgets.getAttributes().getNamedItem("name") != null) {
                    widgets.setName(nodeWidgets.getAttributes().getNamedItem("name").getNodeValue());
                }
                if (nodeWidgets.getAttributes().getNamedItem("label") != null) {
                    widgets.setLabel(nodeWidgets.getAttributes().getNamedItem("label").getNodeValue());
                }
                if (nodeWidgets.getAttributes().getNamedItem("defaultValue") != null) {
                    widgets.setDefaultValue(nodeWidgets.getAttributes().getNamedItem("defaultValue").getNodeValue());
                }
                if (nodeWidgets.getAttributes().getNamedItem("enum") != null) {
                    widgets.setEnum1(nodeWidgets.getAttributes().getNamedItem("enum").getNodeValue());
                }
                if (nodeWidgets.getAttributes().getNamedItem("displayLabel") != null) {
                    widgets.setDisplayLabel(nodeWidgets.getAttributes().getNamedItem("displayLabel").getNodeValue());
                }
                if (nodeWidgets.getAttributes().getNamedItem("documentation") != null) {
                    widgets.setDocumentation(nodeWidgets.getAttributes().getNamedItem("documentation").getNodeValue());
                }
                if (nodeWidgets.getAttributes().getNamedItem("tooltip") != null) {
                    widgets.setTooltip(nodeWidgets.getAttributes().getNamedItem("tooltip").getNodeValue());
                }
                if (nodeWidgets.getAttributes().getNamedItem("realHtmlAttributes") != null) {
                    widgets.setRealHtmlAttributes(nodeWidgets.getAttributes().getNamedItem("realHtmlAttributes").getNodeValue());
                }
                if (nodeWidgets.getAttributes().getNamedItem("labelBehavior") != null) {
                    widgets.setLabelBehavior(Boolean.parseBoolean(nodeWidgets.getAttributes().getNamedItem("labelBehavior").getNodeValue()));
                }
                if (nodeWidgets.getAttributes().getNamedItem("showDisplayLabel") != null) {
                    widgets.setShowDisplayLabel(Boolean.parseBoolean(nodeWidgets.getAttributes().getNamedItem("showDisplayLabel").getNodeValue()));
                }
                if (nodeWidgets.getAttributes().getNamedItem("mandatory") != null) {
                    widgets.setMandatory(Boolean.parseBoolean(nodeWidgets.getAttributes().getNamedItem("mandatory").getNodeValue()));
                }
                if (nodeWidgets.getAttributes().getNamedItem("readOnly") != null) {
                    widgets.setReadOnly(Boolean.parseBoolean(nodeWidgets.getAttributes().getNamedItem("readOnly").getNodeValue()));
                }
                // parce table and grid specific attributes
                if (nodeWidgets.getAttributes().getNamedItem("firstRowIsHeader") != null) {
                    widgets.setFirstRowIsHeader(Boolean.parseBoolean(nodeWidgets.getAttributes().getNamedItem("firstRowIsHeader").getNodeValue()));
                }
                if (nodeWidgets.getAttributes().getNamedItem("LastRowIsHeader") != null) {
                    widgets.setLastRowIsHeader(Boolean.parseBoolean(nodeWidgets.getAttributes().getNamedItem("LastRowIsHeader").getNodeValue()));
                }
                if (nodeWidgets.getAttributes().getNamedItem("leftColumnIsHeader") != null) {
                    widgets.setLeftColumnIsHeader(Boolean.parseBoolean(nodeWidgets.getAttributes().getNamedItem("leftColumnIsHeader").getNodeValue()));
                }
                if (nodeWidgets.getAttributes().getNamedItem("rightColumnIsHeader") != null) {
                    widgets.setRightColumnIsHeader(Boolean.parseBoolean(nodeWidgets.getAttributes().getNamedItem("rightColumnIsHeader").getNodeValue()));
                }
                if (nodeWidgets.getAttributes().getNamedItem("allowSelection") != null) {
                    widgets.setAllowSelection(Boolean.parseBoolean(nodeWidgets.getAttributes().getNamedItem("allowSelection").getNodeValue()));
                }
                if (nodeWidgets.getAttributes().getNamedItem("selectionModeIsMultiple") != null) {
                    widgets.setSelectionModeIsMultiple(Boolean.parseBoolean(nodeWidgets.getAttributes().getNamedItem("selectionModeIsMultiple").getNodeValue()));
                }
                if (nodeWidgets.getAttributes().getNamedItem("limitMinNumberOfRow") != null) {
                    widgets.setLimitMinNumberOfRow(Boolean.parseBoolean(nodeWidgets.getAttributes().getNamedItem("limitMinNumberOfRow").getNodeValue()));
                }
                if (nodeWidgets.getAttributes().getNamedItem("limitMaxNumberOfRow") != null) {
                    widgets.setLimitMaxNumberOfRow(Boolean.parseBoolean(nodeWidgets.getAttributes().getNamedItem("limitMaxNumberOfRow").getNodeValue()));
                }
                if (nodeWidgets.getAttributes().getNamedItem("limitMinNumberOfColumn") != null) {
                    widgets.setLimitMinNumberOfColumn(Boolean.parseBoolean(nodeWidgets.getAttributes().getNamedItem("limitMinNumberOfColumn").getNodeValue()));
                }
                if (nodeWidgets.getAttributes().getNamedItem("limitMaxNumberOfColumn") != null) {
                    widgets.setLimitMaxNumberOfColumn(Boolean.parseBoolean(nodeWidgets.getAttributes().getNamedItem("limitMaxNumberOfColumn").getNodeValue()));
                }

                if (nodeWidgets.getAttributes().getNamedItem("allowAddRemoveColumn") != null) {
                    widgets.setAllowAddRemoveColumn(Boolean.parseBoolean(nodeWidgets.getAttributes().getNamedItem("allowAddRemoveColumn").getNodeValue()));
                }
                if (nodeWidgets.getAttributes().getNamedItem("allowAddRemoveRow") != null) {
                    widgets.setAllowAddRemoveRow(Boolean.parseBoolean(nodeWidgets.getAttributes().getNamedItem("allowAddRemoveRow").getNodeValue()));
                }

                if (nodeWidgets.getAttributes().getNamedItem("cells") != null) {
                    widgets.setCells(nodeWidgets.getAttributes().getNamedItem("cells").getNodeValue());
                }
                if (nodeWidgets.getAttributes().getNamedItem("horizontalHeader") != null) {
                    widgets.setHorizontalHeader(nodeWidgets.getAttributes().getNamedItem("horizontalHeader").getNodeValue());
                }
                if (nodeWidgets.getAttributes().getNamedItem("verticalHeader") != null) {
                    widgets.setVerticalHeader(nodeWidgets.getAttributes().getNamedItem("verticalHeader").getNodeValue());
                }
                if (nodeWidgets.getAttributes().getNamedItem("selectedValues") != null) {
                    widgets.setSelectedValues(nodeWidgets.getAttributes().getNamedItem("selectedValues").getNodeValue());
                }
                if (nodeWidgets.getAttributes().getNamedItem("columnForInitialSelectionIndex") != null) {
                    widgets.setColumnForInitialSelectionIndex(nodeWidgets.getAttributes().getNamedItem("columnForInitialSelectionIndex").getNodeValue());
                }
                if (nodeWidgets.getAttributes().getNamedItem("maxRowForPagination") != null) {
                    widgets.setMaxRowForPagination(nodeWidgets.getAttributes().getNamedItem("maxRowForPagination").getNodeValue());
                }
                if (nodeWidgets.getAttributes().getNamedItem("minNumberOfRow") != null) {
                    widgets.setMinNumberOfRow(nodeWidgets.getAttributes().getNamedItem("minNumberOfRow").getNodeValue());
                }
                if (nodeWidgets.getAttributes().getNamedItem("maxNumberOfRow") != null) {
                    widgets.setMaxNumberOfRow(nodeWidgets.getAttributes().getNamedItem("maxNumberOfRow").getNodeValue());
                }
                if (nodeWidgets.getAttributes().getNamedItem("minNumberOfColumn") != null) {
                    widgets.setMinNumberOfColumn(nodeWidgets.getAttributes().getNamedItem("minNumberOfColumn").getNodeValue());
                }
                if (nodeWidgets.getAttributes().getNamedItem("maxNumberOfColumn") != null) {
                    widgets.setMaxNumberOfColumn(nodeWidgets.getAttributes().getNamedItem("maxNumberOfColumn").getNodeValue());
                }

                // parce widgets script atributes
                NodeList widgetsProperties = nodeWidgets.getChildNodes();
                for (int x = 0; x < widgetsProperties.getLength(); x++) {
                    Node widgetsProperty = widgetsProperties.item(x);
                    if (widgetsProperty.getNodeName().equals("script")) {
                        if (widgetsProperty.getAttributes().getNamedItem("exprScript") != null) {
                            widgets.setExprScript(widgetsProperty.getAttributes().getNamedItem("exprScript").getNodeValue());
                        }
                        if (widgetsProperty.getAttributes().getNamedItem("inputScript") != null) {
                            widgets.setInputScript(widgetsProperty.getAttributes().getNamedItem("inputScript").getNodeValue());
                        }
                        if (widgetsProperty.getAttributes().getNamedItem("setVarScript") != null) {
                            widgets.setSetVarScript(widgetsProperty.getAttributes().getNamedItem("setVarScript").getNodeValue());
                        }
                    }
                    if (widgetsProperty.getNodeName().equals("widgetLayoutInfo")) {
                        if (widgetsProperty.getAttributes().getNamedItem("line") != null) {
                            widgets.setLine(Integer.parseInt(widgetsProperty.getAttributes().getNamedItem("line").getNodeValue()));
                        }
                        if (widgetsProperty.getAttributes().getNamedItem("column") != null) {
                            widgets.setColumn(Integer.parseInt(widgetsProperty.getAttributes().getNamedItem("column").getNodeValue()));
                        }
                        if (widgetsProperty.getAttributes().getNamedItem("horizontalSpan") != null) {
                            widgets.setHorizontalSpan(Integer.parseInt(widgetsProperty.getAttributes().getNamedItem("horizontalSpan").getNodeValue()));
                        }
                    }
                    if (widgetsProperty.getNodeName().equals("validators")) {
                        if (widgetsProperty.getAttributes().getNamedItem("validatorClass") != null) {
                            widgets.setValidatorClass(widgetsProperty.getAttributes().getNamedItem("validatorClass").getNodeValue());
                        }
                        if (widgetsProperty.getAttributes().getNamedItem("label") != null) {
                            widgets.setValidatorLabel(widgetsProperty.getAttributes().getNamedItem("label").getNodeValue());
                        }
                        if (widgetsProperty.getAttributes().getNamedItem("name") != null) {
                            widgets.setValidatorName(widgetsProperty.getAttributes().getNamedItem("name").getNodeValue());
                        }

                        if (widgetsProperty.getAttributes().getNamedItem("parameter") != null) {
                            widgets.setValidatorParameter(widgetsProperty.getAttributes().getNamedItem("parameter").getNodeValue());
                        }
                    }
                    if (widgetsProperty.getNodeName().equals("htmlAttributes")) {
                        if (widgetsProperty.getAttributes().getNamedItem("key") != null && widgetsProperty.getAttributes().getNamedItem("value") != null) {
                            if (widgetsProperty.getAttributes().getNamedItem("key").getNodeValue().equals("widget_css:width")) {
                                widgets.setWidgetWidth(widgetsProperty.getAttributes().getNamedItem("value").getNodeValue());
                            } else if (widgetsProperty.getAttributes().getNamedItem("key").getNodeValue().equals("widget_css:height")) {
                                widgets.setWidgetHeight(widgetsProperty.getAttributes().getNamedItem("value").getNodeValue());
                            } else if (widgetsProperty.getAttributes().getNamedItem("key").getNodeValue().equals("input_css:width")) {
                                widgets.setInputWidth(widgetsProperty.getAttributes().getNamedItem("value").getNodeValue());
                            } else if (widgetsProperty.getAttributes().getNamedItem("key").getNodeValue().equals("input_css:height")) {
                                widgets.setInputHeight(widgetsProperty.getAttributes().getNamedItem("value").getNodeValue());
                            }
                        }
                    }
                }
                result.add(widgets);
            }
        }
        return result;
    }
}
