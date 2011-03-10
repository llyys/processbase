/**
 * Copyright (C) 2010 PROCESSBASE Ltd.
 *
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
package org.processbase.ui.dashboard;

import java.awt.Color;
import java.awt.GradientPaint;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.ow2.bonita.facade.def.majorElement.ActivityDefinition;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.light.LightActivityInstance;
import org.processbase.ui.portlet.PbPortlet;
import org.processbase.ui.template.DashboardPanel;
import org.vaadin.ui.JFreeChartWrapper;

/**
 *
 * @author marat gubaidullin
 */
public class DashboardPerformerTaskPanel extends DashboardPanel {

    public DashboardPerformerTaskPanel() {
        super();
    }

    @Override
    public void initUI() {
    }

    @Override
    public void refresh() {
        try {
            super.refresh();
            Date now = new Date();
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            HashMap<String, Integer> readyTasks = new HashMap<String, Integer>();
            HashMap<String, Integer> execTasks = new HashMap<String, Integer>();
            HashMap<String, Integer> suspendTasks = new HashMap<String, Integer>();
            HashMap<String, Integer> expireTasks = new HashMap<String, Integer>();

            Collection<LightActivityInstance> ais = PbPortlet.getCurrent().bpmModule.getActivityInstances();
            for (LightActivityInstance ai : ais) {
                if (ai.isTask() && ai.getState().equals(ActivityState.READY)) {
                    ActivityDefinition ad = PbPortlet.getCurrent().bpmModule.getProcessActivity(ai.getProcessDefinitionUUID(), ai.getActivityName());
                    if (readyTasks.containsKey(ad.getPerformers().toString())) {
                        Integer c = readyTasks.get(ad.getPerformers().toString());
                        readyTasks.put(ad.getPerformers().toString(), c.intValue() + 1);
                    } else {
                        readyTasks.put(ad.getPerformers().toString(), 1);
                    }
                } else if (ai.isTask() && ai.getState().equals(ActivityState.EXECUTING)) {
                    ActivityDefinition ad = PbPortlet.getCurrent().bpmModule.getProcessActivity(ai.getProcessDefinitionUUID(), ai.getActivityName());
                    if (execTasks.containsKey(ad.getPerformers().toString())) {
                        Integer c = execTasks.get(ad.getPerformers().toString());
                        execTasks.put(ad.getPerformers().toString(), c.intValue() + 1);
                    } else {
                        execTasks.put(ad.getPerformers().toString(), 1);
                    }
                } else if (ai.isTask() && ai.getState().equals(ActivityState.SUSPENDED)) {
                    ActivityDefinition ad = PbPortlet.getCurrent().bpmModule.getProcessActivity(ai.getProcessDefinitionUUID(), ai.getActivityName());
                    if (suspendTasks.containsKey(ad.getPerformers().toString())) {
                        Integer c = suspendTasks.get(ad.getPerformers().toString());
                        suspendTasks.put(ad.getPerformers().toString(), c.intValue() + 1);
                    } else {
                        suspendTasks.put(ad.getPerformers().toString(), 1);
                    }
                }
                if (ai.isTask()
                        && (ai.getState().equals(ActivityState.EXECUTING) || ai.getState().equals(ActivityState.READY) || ai.getState().equals(ActivityState.SUSPENDED))
                        && ai.getExpectedEndDate() != null && ai.getExpectedEndDate().before(now)) {
                    ActivityDefinition ad = PbPortlet.getCurrent().bpmModule.getProcessActivity(ai.getProcessDefinitionUUID(), ai.getActivityName());
                    if (expireTasks.containsKey(ad.getPerformers().toString())) {
                        Integer c = expireTasks.get(ad.getPerformers().toString());
                        expireTasks.put(ad.getPerformers().toString(), c.intValue() + 1);
                    } else {
                        expireTasks.put(ad.getPerformers().toString(), 1);
                    }
                }
            }

            for (String perf : readyTasks.keySet()) {
                dataset.setValue(readyTasks.get(perf), PbPortlet.getCurrent().messages.getString("READY"), perf);
            }
            for (String perf : execTasks.keySet()) {
                dataset.setValue(execTasks.get(perf), PbPortlet.getCurrent().messages.getString("EXECUTING"), perf);
            }
            for (String perf : suspendTasks.keySet()) {
                dataset.setValue(suspendTasks.get(perf), PbPortlet.getCurrent().messages.getString("SUSPENDED"), perf);
            }
            for (String perf : expireTasks.keySet()) {
                dataset.setValue(expireTasks.get(perf), PbPortlet.getCurrent().messages.getString("EXPIRED"), perf);
            }
            JFreeChart chart = createchart(dataset);
            JFreeChartWrapper xyChartWrapper = new JFreeChartWrapper(chart);
            removeAllComponents();
            addComponent(xyChartWrapper);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private JFreeChart createchart(CategoryDataset dataset) {

        // create the chart...
        JFreeChart c = ChartFactory.createBarChart3D("", 
                "", // domain axis label
                "Tasks by performers", // range axis label
                dataset, // data
                PlotOrientation.HORIZONTAL, // orientation
                true, // include legend
                true, // tooltips?
                false // URLs?
                );

        // set the background color for the chart...
        c.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = (CategoryPlot) c.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.white);


        // set the range axis to display integers only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        BarRenderer3D renderer = (BarRenderer3D) plot.getRenderer();
        // renderer.setDrawBarOutline(false);

        // set up gradient paints for series...
        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.YELLOW, 0.0f,
                0.0f, new Color(0, 0, 64));
        GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.GREEN, 0.0f,
                0.0f, new Color(0, 64, 0));
        GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.BLUE, 0.0f,
                0.0f, new Color(64, 0, 0));
        GradientPaint gp3 = new GradientPaint(0.0f, 0.0f, Color.RED, 0.0f,
                0.0f, new Color(64, 0, 0));
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        renderer.setSeriesPaint(2, gp2);
        renderer.setSeriesPaint(3, gp3);

        CategoryAxis domainAxis = plot.getDomainAxis();
//        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
        // OPTIONAL CUSTOMISATION COMPLETED.

        return c;

    }
}
