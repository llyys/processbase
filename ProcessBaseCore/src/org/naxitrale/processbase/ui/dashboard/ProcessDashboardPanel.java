/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.ui.dashboard;

import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Window.Notification;
import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import org.jfree.chart.plot.MeterInterval;
import org.jfree.data.Range;
import org.naxitrale.processbase.ProcessBase;
import org.naxitrale.processbase.ui.template.AnalyticPanel;
import org.ow2.bonita.facade.runtime.InstanceState;

/**
 *
 * @author mgubaidullin
 */
public class ProcessDashboardPanel extends AnalyticPanel {

    public ProcessDashboardPanel() {
        super();
    }

    @Override
    public void refreshDashboard() {
        try {
            grid.removeAllComponents();
            grid.addComponent(getMeterImage("Ошибки процессов", adminModule.getProcessInstancesByStatus(InstanceState.ABORTED).size()), 0, 1, 0, 1);

            ArrayList<BarValue> values = new ArrayList<BarValue>();
            values.add(new BarValue(adminModule.getProcessInstancesByStatus(InstanceState.INITIAL).size(), "Инициализация", ""));
            values.add(new BarValue(adminModule.getProcessInstancesByStatus(InstanceState.CANCELLED).size(), "Отменен", ""));
            values.add(new BarValue(adminModule.getProcessInstancesByStatus(InstanceState.FINISHED).size(), "Завершен", ""));
            values.add(new BarValue(adminModule.getProcessInstancesByStatus(InstanceState.ABORTED).size(), "Прекращен", ""));
            values.add(new BarValue(adminModule.getProcessInstancesByStatus(InstanceState.STARTED).size(), "Выполняется", ""));
            grid.addComponent(getBarImage("Процессы по статусам", values), 1, 1, 1, 1);

            grid.addComponent(getBar3DImage("Процессы по статусам", values), 2, 1, 2, 1);

            ArrayList<PieValue> pvs = new ArrayList<PieValue>();
            Map<String, Integer> res = analyticModule.countPIbyType(InstanceState.STARTED);
            for (Iterator i = res.keySet().iterator(); i.hasNext();) {
                String pName = i.next().toString();
                pvs.add(new PieValue(pName, res.get(pName)));
            }
            grid.addComponent(getPieImage("Процессы по типам (запущено)", pvs), 1, 2, 1, 2);

            ArrayList<PieValue> pvs2 = new ArrayList<PieValue>();
            Map<String, Integer> res2 = analyticModule.countPIbyType(InstanceState.FINISHED);
            for (Iterator i = res2.keySet().iterator(); i.hasNext();) {
                String pName = i.next().toString();
                pvs2.add(new PieValue(pName, res2.get(pName)));
            }
            grid.addComponent(getPie3DImage("Процессы по типам (завершено)", pvs2), 2, 2, 2, 2);

            grid.addComponent(getDialImage("Текущее время"), 0, 2, 0, 2);
        } catch (Exception ex) {
            getWindow().showNotification("Ошибка", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
        }

    }

    private Component getMeterImage(String caption, Number value) {
        ArrayList<MeterInterval> mis = new ArrayList<MeterInterval>();
        mis.add(new MeterInterval("Норма", new Range(-1, 0), Color.GREEN, new BasicStroke(4), null));
        mis.add(new MeterInterval("Внимание", new Range(0, 5), Color.YELLOW, new BasicStroke(4), null));
        mis.add(new MeterInterval("Тревога", new Range(5, 10), Color.RED, new BasicStroke(4), null));
        StreamResource.StreamSource imagesource = new MeterImageSource(new Range(-1, 10), mis, "", null);
        ((MeterImageSource) imagesource).setValue(value);
        @SuppressWarnings("static-access")
        StreamResource imageresource = new StreamResource(imagesource, "image1.png", ((ProcessBase)getApplication()).getCurrent());
        imageresource.setCacheTime(0);
        Embedded image = new Embedded(caption, imageresource);
        return image;
    }

    private Component getThermometerImage(String caption, Number value) {
        StreamResource.StreamSource imagesource = new ThermometerImageSource(-10, 10, null);
        ((ThermometerImageSource) imagesource).setValue(value);
        StreamResource imageresource = new StreamResource(imagesource, "image2.png", ((ProcessBase)getApplication()).getCurrent());
        imageresource.setCacheTime(0);
        Embedded image = new Embedded(caption, imageresource);
        return image;
    }

    private Component getDialImage(String caption) {
        Date d = new Date();
        StreamResource.StreamSource imagesource = new DialImageSource(new Integer(d.getHours()), new Integer(d.getMinutes()));
        StreamResource imageresource = new StreamResource(imagesource, "image3.png", ((ProcessBase)getApplication()).getCurrent());
        imageresource.setCacheTime(0);
        Embedded image = new Embedded(caption, imageresource);
        return image;
    }

    private Component getBarImage(String caption, ArrayList<BarValue> values) {
        StreamResource.StreamSource imagesource = new BarImageSource();
        ((BarImageSource) imagesource).setValue(values);
        StreamResource imageresource = new StreamResource(imagesource, "image4.png", ((ProcessBase)getApplication()).getCurrent());
        imageresource.setCacheTime(0);
        Embedded image = new Embedded(caption, imageresource);
        return image;
    }

    private Component getBar3DImage(String caption, ArrayList<BarValue> values) {
        StreamResource.StreamSource imagesource = new Bar3DImageSource();
        ((Bar3DImageSource) imagesource).setValue(values);
        StreamResource imageresource = new StreamResource(imagesource, "image5.png", ((ProcessBase)getApplication()).getCurrent());
        imageresource.setCacheTime(0);
        Embedded image = new Embedded(caption, imageresource);
        return image;
    }

    private Component getPieImage(String caption, ArrayList<PieValue> values) {
        StreamResource.StreamSource imagesource = new PieImageSource();
        ((PieImageSource) imagesource).setValue(values);
        StreamResource imageresource = new StreamResource(imagesource, "image6.png", ((ProcessBase)getApplication()).getCurrent());
        imageresource.setCacheTime(0);
        Embedded image = new Embedded(caption, imageresource);
        return image;
    }

    private Component getPie3DImage(String caption, ArrayList<PieValue> values) {
        StreamResource.StreamSource imagesource = new Pie3DImageSource();
        ((Pie3DImageSource) imagesource).setValue(values);
        StreamResource imageresource = new StreamResource(imagesource, "image7.png", ((ProcessBase)getApplication()).getCurrent());
        imageresource.setCacheTime(0);
        Embedded image = new Embedded(caption, imageresource);
        return image;
    }
}
