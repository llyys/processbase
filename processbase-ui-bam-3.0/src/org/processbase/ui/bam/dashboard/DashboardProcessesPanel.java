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
package org.processbase.ui.bam.dashboard;

import com.invient.vaadin.charts.InvientCharts;
import com.invient.vaadin.charts.InvientCharts.DecimalPoint;
import com.invient.vaadin.charts.InvientCharts.Series;
import com.invient.vaadin.charts.InvientCharts.SeriesType;
import com.invient.vaadin.charts.InvientCharts.XYSeries;
import com.invient.vaadin.charts.InvientChartsConfig;
import com.invient.vaadin.charts.InvientChartsConfig.AxisBase.AxisTitle;
import com.invient.vaadin.charts.InvientChartsConfig.CategoryAxis;
import com.invient.vaadin.charts.InvientChartsConfig.NumberYAxis;
import com.invient.vaadin.charts.InvientChartsConfig.XAxis;
import com.invient.vaadin.charts.InvientChartsConfig.YAxis;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.Set;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.InstanceState;
import org.ow2.bonita.light.LightProcessInstance;
import org.processbase.ui.core.Processbase;
import org.processbase.ui.core.template.DashboardPanel;

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

    public void refresh() {
        try {
            GregorianCalendar midnight = new GregorianCalendar();
            midnight.set(Calendar.HOUR, 0);
            midnight.set(Calendar.AM_PM, Calendar.AM);
            midnight.set(Calendar.MINUTE, 0);
            midnight.set(Calendar.SECOND, 0);
            midnight.set(Calendar.MILLISECOND, 0);

            Set<ProcessDefinition> pds = ((Processbase)getApplication()).getBpmModule().getProcessDefinitions();
            ArrayList<Double> processAll = new ArrayList<Double>(pds.size());
            ArrayList<Double> processToday = new ArrayList<Double>(pds.size());
            ArrayList<String> processNames = new ArrayList<String>(pds.size());
            for (ProcessDefinition pd : pds) {
                Set<LightProcessInstance> pis = ((Processbase)getApplication()).getBpmModule().getLightProcessInstances(pd.getUUID());
                double countAll = 0;
                double countToday = 0;
                for (LightProcessInstance pi : pis) {
                    if (pi.getInstanceState().equals(InstanceState.STARTED)) {
                        countAll++;
                    }
                    if (pi.getStartedDate().after(midnight.getTime())) {
                        countToday++;
                    }
                }
                processNames.add(pd.getLabel());
                processAll.add(new Double(countAll));
                processToday.add(new Double(countToday));
            }
            ArrayList<XYSeries> xySeries = new ArrayList<XYSeries>();

            XYSeries seriesDataAll = new XYSeries("All", SeriesType.COLUMN);
            seriesDataAll.setSeriesPoints(getPoints(seriesDataAll, processAll));
            xySeries.add(seriesDataAll);

            XYSeries seriesDataToday = new XYSeries("Today", SeriesType.COLUMN);
            seriesDataToday.setSeriesPoints(getPoints(seriesDataToday, processToday));
            xySeries.add(seriesDataToday);

            removeAllComponents();
            InvientCharts ich = createchart(processNames, xySeries);
            ich.setWidth("100%");
            addComponent(ich);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private InvientCharts createchart(ArrayList<String> processNames, ArrayList<XYSeries> xySeries) {

        InvientChartsConfig chartConfig = new InvientChartsConfig();
        chartConfig.getTitle().setText(((Processbase)getApplication()).getMessages().getString("startedProcesses"));
        chartConfig.getSubtitle().setText("Source: PROCESSBASSE BPMS");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(processNames);
//        xAxis.setLabel(new XAxisDataLabel());
//        xAxis.getLabel().setRotation(-45);
//        xAxis.getLabel().setAlign(HorzAlign.RIGHT);
        LinkedHashSet<XAxis> xAxesSet = new LinkedHashSet<InvientChartsConfig.XAxis>();
        xAxesSet.add(xAxis);
        chartConfig.setXAxes(xAxesSet);

        NumberYAxis yAxis = new NumberYAxis();
        yAxis.setAllowDecimals(false);
        yAxis.setTitle(new AxisTitle(((Processbase)getApplication()).getMessages().getString("processCount")));
        LinkedHashSet<YAxis> yAxesSet = new LinkedHashSet<InvientChartsConfig.YAxis>();
        yAxesSet.add(yAxis);
        chartConfig.setYAxes(yAxesSet);

        InvientCharts chart = new InvientCharts(chartConfig);

        for (XYSeries xy : xySeries) {
            chart.addSeries(xy);
        }
        return chart;
    }

    private static LinkedHashSet<DecimalPoint> getPoints(Series series, ArrayList<Double> values) {
        LinkedHashSet<DecimalPoint> points = new LinkedHashSet<DecimalPoint>();
        for (double value : values) {
            points.add(new DecimalPoint(series, value));
        }
        return points;
    }
}
