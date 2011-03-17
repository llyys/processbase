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
import java.util.GregorianCalendar;
import java.util.Set;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.InstanceState;
import org.ow2.bonita.light.LightProcessInstance;
import org.processbase.ui.portlet.PbPortlet;
import org.processbase.ui.template.DashboardPanel;
import org.vaadin.ui.JFreeChartWrapper;

/**
 *
 * @author marat gubaidullin
 */
public class DashboardProcessesPanel extends DashboardPanel {

    public DashboardProcessesPanel() {
        super();
    }

    @Override
    public void initUI() {
    }

    @Override
    public void refresh() {
        try {
            super.refresh();
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            GregorianCalendar midnight = new GregorianCalendar();
            midnight.set(Calendar.HOUR, 0);
            midnight.set(Calendar.MINUTE, 0);
            midnight.set(Calendar.SECOND, 0);
            midnight.set(Calendar.MILLISECOND, 0);

            Set<ProcessDefinition> pds = PbPortlet.getCurrent().bpmModule.getProcessDefinitions();
            for (ProcessDefinition pd : pds) {
                Set<LightProcessInstance> pis = PbPortlet.getCurrent().bpmModule.getLightProcessInstances(pd.getUUID());
                int countAll = 0;
                int countToday = 0;
                for (LightProcessInstance pi : pis) {
                    if (pi.getInstanceState().equals(InstanceState.STARTED)){
                        countAll++;
                    }
                    if (pi.getStartedDate().after(midnight.getTime())){
                        countToday++;
                    }
                }
                dataset.setValue(countAll, "All", pd.getLabel());
                dataset.setValue(countToday, "Today", pd.getLabel());
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
                "Process Definition", // domain axis label
                "Started processes", // range axis label
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
        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f,
                0.0f, new Color(0, 0, 64));
        GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green, 0.0f,
                0.0f, new Color(0, 64, 0));
        GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.red, 0.0f,
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
