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
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.light.LightActivityInstance;
import org.processbase.ui.portlet.PbPortlet;
import org.processbase.ui.template.DashboardPanel;
import org.vaadin.ui.JFreeChartWrapper;

/**
 *
 * @author marat gubaidullin
 */
public class DashboardUserTaskPanel extends DashboardPanel {

    public DashboardUserTaskPanel() {
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
            GregorianCalendar midnight = new GregorianCalendar();
            midnight.set(Calendar.HOUR, 0);
            midnight.set(Calendar.MINUTE, 0);
            midnight.set(Calendar.SECOND, 0);
            midnight.set(Calendar.MILLISECOND, 0);
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            HashMap<String, Integer> startedTasks = new HashMap<String, Integer>();
            HashMap<String, Integer> todayStartTasks = new HashMap<String, Integer>();
            HashMap<String, Integer> todayDoneTasks = new HashMap<String, Integer>();
            HashMap<String, Integer> expiredTasks = new HashMap<String, Integer>();

            Collection<LightActivityInstance> ais = PbPortlet.getCurrent().bpmModule.getActivityInstances();
            for (LightActivityInstance ai : ais) {
                if (ai.isTask() && ai.getTask().isTaskAssigned() && ai.getState().equals(ActivityState.EXECUTING)) {
                    if (startedTasks.containsKey(ai.getTask().getTaskUser())) {
                        Integer c = startedTasks.get(ai.getTask().getTaskUser());
                        startedTasks.put(ai.getTask().getTaskUser(), c.intValue() + 1);
                    } else {
                        startedTasks.put(ai.getTask().getTaskUser().toString(), 1);
                    }
                    if (ai.getTask().getCreatedDate().after(midnight.getTime())) {
                        if (todayStartTasks.containsKey(ai.getTask().getTaskUser())) {
                            Integer c = todayStartTasks.get(ai.getTask().getTaskUser());
                            todayStartTasks.put(ai.getTask().getTaskUser(), c.intValue() + 1);
                        } else {
                            todayStartTasks.put(ai.getTask().getTaskUser().toString(), 1);
                        }
                    }
                } else if (ai.isTask() && ai.getTask().isTaskAssigned()
                        && ai.getState().equals(ActivityState.FINISHED) && ai.getTask().getEndedDate().after(midnight.getTime())) {
                    if (todayDoneTasks.containsKey(ai.getTask().getTaskUser())) {
                        Integer c = todayDoneTasks.get(ai.getTask().getTaskUser());
                        todayDoneTasks.put(ai.getTask().getTaskUser(), c.intValue() + 1);
                    } else {
                        todayDoneTasks.put(ai.getTask().getTaskUser().toString(), 1);
                    }
                }
                if (ai.isTask() && ai.getTask().isTaskAssigned()
                        && (ai.getState().equals(ActivityState.EXECUTING) || ai.getState().equals(ActivityState.READY) || ai.getState().equals(ActivityState.SUSPENDED))
                        && ai.getExpectedEndDate() != null && ai.getExpectedEndDate().before(now)) {
                    if (expiredTasks.containsKey(ai.getTask().getTaskUser())) {
                        Integer c = expiredTasks.get(ai.getTask().getTaskUser());
                        expiredTasks.put(ai.getTask().getTaskUser(), c.intValue() + 1);
                    } else {
                        expiredTasks.put(ai.getTask().getTaskUser(), 1);
                    }
                }
            }

            for (String perf : startedTasks.keySet()) {
                dataset.setValue(startedTasks.get(perf), PbPortlet.getCurrent().messages.getString("EXECUTING"), perf);
            }
            for (String perf : todayStartTasks.keySet()) {
                dataset.setValue(todayStartTasks.get(perf), PbPortlet.getCurrent().messages.getString("todayDone"), perf);
            }
            for (String perf : todayDoneTasks.keySet()) {
                dataset.setValue(todayDoneTasks.get(perf), PbPortlet.getCurrent().messages.getString("todayDone"), perf);
            }
            for (String perf : expiredTasks.keySet()) {
                dataset.setValue(expiredTasks.get(perf), PbPortlet.getCurrent().messages.getString("EXPIRED"), perf);
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
        JFreeChart c = ChartFactory.createBarChart3D("", // chart
                // title
                "", // domain axis label
                "Tasks by user", // range axis label
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
        GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.RED, 0.0f,
                0.0f, new Color(64, 0, 0));
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        renderer.setSeriesPaint(2, gp2);

        CategoryAxis domainAxis = plot.getDomainAxis();
//        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
        // OPTIONAL CUSTOMISATION COMPLETED.

        return c;

    }
}
