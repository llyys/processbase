/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.dashboard;

import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import org.jfree.chart.plot.MeterInterval;
import org.jfree.data.Range;
import org.ow2.bonita.facade.runtime.InstanceState;
import org.processbase.ui.template.WorkPanel;

/**
 *
 * @author mgubaidullin
 */
public class ProcessDashboardPanel extends WorkPanel {

    public ProcessDashboardPanel(PortletApplicationContext2 portletApplicationContext2) {
        super(portletApplicationContext2);
        setStyleName("white");
    }

    public void addProcessesByStatus() {
//        try {
        this.removeAllComponents();
        ArrayList<BarValue> values = new ArrayList<BarValue>();
        values.add(new BarValue(bpmModule.getProcessInstancesByStatus(InstanceState.CANCELLED).size(), messages.getString(InstanceState.CANCELLED.name()), ""));
        values.add(new BarValue(bpmModule.getProcessInstancesByStatus(InstanceState.FINISHED).size(), messages.getString(InstanceState.FINISHED.name()), ""));
        values.add(new BarValue(bpmModule.getProcessInstancesByStatus(InstanceState.ABORTED).size(), messages.getString(InstanceState.ABORTED.name()), ""));
        values.add(new BarValue(bpmModule.getProcessInstancesByStatus(InstanceState.STARTED).size(), messages.getString(InstanceState.STARTED.name()), ""));
        getBar3DImage("", values);
//            grid.addComponent(getMeterImage("Processes Errors", bpmModule.getProcessInstancesByStatus(InstanceState.ABORTED).size()), 0, 1, 0, 1);
//            grid.addComponent(getBarImage("Processes by status", values), 1, 1, 1, 1);

//            grid.addComponent(getBar3DImage("Processes by status", values), 2, 1, 2, 1);

//            ArrayList<PieValue> pvs = new ArrayList<PieValue>();
//            Map<String, Integer> res = analyticModule.countPIbyType(InstanceState.STARTED);
//            for (Iterator i = res.keySet().iterator(); i.hasNext();) {
//                String pName = i.next().toString();
//                pvs.add(new PieValue(pName, res.get(pName)));
//            }
//            grid.addComponent(getPieImage("Processes by types  (executed)", pvs), 1, 2, 1, 2);
//
//            ArrayList<PieValue> pvs2 = new ArrayList<PieValue>();
//            Map<String, Integer> res2 = analyticModule.countPIbyType(InstanceState.FINISHED);
//            for (Iterator i = res2.keySet().iterator(); i.hasNext();) {
//                String pName = i.next().toString();
//                pvs2.add(new PieValue(pName, res2.get(pName)));
//            }
//            grid.addComponent(getPie3DImage("Processes by types (finished)", pvs2), 2, 2, 2, 2);
//
//            grid.addComponent(getDialImage("Current time"), 0, 2, 0, 2);
//        } catch (Exception ex) {
//            showError(ex.toString());
//        }

    }

    private Component getThermometerImage(String caption, Number value) {
        StreamResource.StreamSource imagesource = new ThermometerImageSource(-10, 10, null);
        ((ThermometerImageSource) imagesource).setValue(value);
        StreamResource imageresource = new StreamResource(imagesource, "image2.png", getApplication());
        imageresource.setCacheTime(0);
        Embedded image = new Embedded(caption, imageresource);
        return image;
    }

    private void addBarImage(String caption, ArrayList<BarValue> values) {
        StreamResource.StreamSource imagesource = new BarImageSource();
        ((BarImageSource) imagesource).setValue(values);
        StreamResource imageresource = new StreamResource(imagesource, "image4.png", getApplication());
        imageresource.setCacheTime(0);
        Embedded image = new Embedded(caption, imageresource);
        
        this.addComponent(image);
    }

    private void getBar3DImage(String caption, ArrayList<BarValue> values) {
        StreamResource.StreamSource imagesource = new Bar3DImageSource();
        ((Bar3DImageSource) imagesource).setValue(values);
        StreamResource imageresource = new StreamResource(imagesource, "image5.png", getApplication());
        imageresource.setCacheTime(0);
        Embedded image = new Embedded(caption, imageresource);
        image.setStyleName("white");
        this.addComponent(image);
    }

    private Component getPieImage(String caption, ArrayList<PieValue> values) {
        StreamResource.StreamSource imagesource = new PieImageSource();
        ((PieImageSource) imagesource).setValue(values);
        StreamResource imageresource = new StreamResource(imagesource, "image6.png", getApplication());
        imageresource.setCacheTime(0);
        Embedded image = new Embedded(caption, imageresource);
        return image;
    }

    private Component getPie3DImage(String caption, ArrayList<PieValue> values) {
        StreamResource.StreamSource imagesource = new Pie3DImageSource();
        ((Pie3DImageSource) imagesource).setValue(values);
        StreamResource imageresource = new StreamResource(imagesource, "image7.png", getApplication());
        imageresource.setCacheTime(0);
        Embedded image = new Embedded(caption, imageresource);
        return image;
    }
}
