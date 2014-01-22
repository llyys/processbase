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

import com.invient.vaadin.charts.Color.RGB;
import com.invient.vaadin.charts.InvientCharts;
import com.invient.vaadin.charts.InvientCharts.DecimalPoint;
import com.invient.vaadin.charts.InvientCharts.Series;
import com.invient.vaadin.charts.InvientCharts.SeriesType;
import com.invient.vaadin.charts.InvientCharts.XYSeries;
import com.invient.vaadin.charts.InvientChartsConfig;
import com.invient.vaadin.charts.InvientChartsConfig.AxisBase.AxisTitle;
import com.invient.vaadin.charts.InvientChartsConfig.AxisBase.AxisTitleAlign;
import com.invient.vaadin.charts.InvientChartsConfig.CategoryAxis;
import com.invient.vaadin.charts.InvientChartsConfig.HorzAlign;
import com.invient.vaadin.charts.InvientChartsConfig.Legend;
import com.invient.vaadin.charts.InvientChartsConfig.Legend.Layout;
import com.invient.vaadin.charts.InvientChartsConfig.NumberYAxis;
import com.invient.vaadin.charts.InvientChartsConfig.Position;
import com.invient.vaadin.charts.InvientChartsConfig.VertAlign;
import com.invient.vaadin.charts.InvientChartsConfig.XAxis;
import com.invient.vaadin.charts.InvientChartsConfig.YAxis;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import org.ow2.bonita.facade.def.majorElement.ActivityDefinition;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.light.LightActivityInstance;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.DashboardPanel;

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

    public void refresh() {
        try {
            Date now = new Date();
            HashMap<String, Double> readyTasks = new HashMap<String, Double>();
            HashMap<String, Double> execTasks = new HashMap<String, Double>();
            HashMap<String, Double> suspendTasks = new HashMap<String, Double>();
            HashMap<String, Double> expireTasks = new HashMap<String, Double>();

            HashSet<String> performersNames = new HashSet<String>();

            Collection<LightActivityInstance> ais = ProcessbaseApplication.getCurrent().getBpmModule().getActivityInstances();
            for (LightActivityInstance ai : ais) {
                ActivityDefinition ad = ProcessbaseApplication.getCurrent().getBpmModule().getProcessActivity(ai.getProcessDefinitionUUID(), ai.getActivityName());
                if (!ad.getPerformers().isEmpty()) {
                    performersNames.add(ad.getPerformers().toString());
                }
                if (ai.isTask() && ai.getState().equals(ActivityState.READY)) {
                    if (readyTasks.containsKey(ad.getPerformers().toString())) {
                        Double c = readyTasks.get(ad.getPerformers().toString());
                        readyTasks.put(ad.getPerformers().toString(), c.doubleValue() + 1);
                    } else {
                        readyTasks.put(ad.getPerformers().toString(), 1.0);
                    }
                } else if (ai.isTask() && ai.getState().equals(ActivityState.EXECUTING)) {
                    if (execTasks.containsKey(ad.getPerformers().toString())) {
                        Double c = execTasks.get(ad.getPerformers().toString());
                        execTasks.put(ad.getPerformers().toString(), c.doubleValue() + 1);
                    } else {
                        execTasks.put(ad.getPerformers().toString(), 1.0);
                    }
                } else if (ai.isTask() && ai.getState().equals(ActivityState.SUSPENDED)) {
                    if (suspendTasks.containsKey(ad.getPerformers().toString())) {
                        Double c = suspendTasks.get(ad.getPerformers().toString());
                        suspendTasks.put(ad.getPerformers().toString(), c.doubleValue() + 1);
                    } else {
                        suspendTasks.put(ad.getPerformers().toString(), 1.0);
                    }
                }
                if (ai.isTask()
                        && (ai.getState().equals(ActivityState.EXECUTING) || ai.getState().equals(ActivityState.READY) || ai.getState().equals(ActivityState.SUSPENDED))
                        && ai.getExpectedEndDate() != null && ai.getExpectedEndDate().before(now)) {
                    if (expireTasks.containsKey(ad.getPerformers().toString())) {
                        Double c = expireTasks.get(ad.getPerformers().toString());
                        expireTasks.put(ad.getPerformers().toString(), c.doubleValue() + 1);
                    } else {
                        expireTasks.put(ad.getPerformers().toString(), 1.0);
                    }
                }
            }
            ArrayList<XYSeries> xySeries = new ArrayList<XYSeries>();
            XYSeries seriesDataREADY = new XYSeries("READY");
            ArrayList<Double> r = new ArrayList<Double>();
            XYSeries seriesDataEXECUTING = new XYSeries("EXECUTING");
            ArrayList<Double> rEXECUTING = new ArrayList<Double>();
            XYSeries seriesDataSUSPEND = new XYSeries("SUSPEND");
            ArrayList<Double> rSUSPEND = new ArrayList<Double>();
            XYSeries seriesDataEXPIRED = new XYSeries("EXPIRED");
            ArrayList<Double> rEXPIRED = new ArrayList<Double>();

            for (String perf : performersNames) {
                r.add(readyTasks.containsKey(perf) ? readyTasks.get(perf) : 0);
                rEXECUTING.add(execTasks.containsKey(perf) ? execTasks.get(perf) : 0);
                rSUSPEND.add(suspendTasks.containsKey(perf) ? suspendTasks.get(perf) : 0);
                rEXPIRED.add(expireTasks.containsKey(perf) ? expireTasks.get(perf) : 0);
            }
            seriesDataREADY.setSeriesPoints(getPoints(seriesDataREADY, r));
            xySeries.add(seriesDataREADY);
            seriesDataEXECUTING.setSeriesPoints(getPoints(seriesDataEXECUTING, rEXECUTING));
            xySeries.add(seriesDataEXECUTING);
            seriesDataSUSPEND.setSeriesPoints(getPoints(seriesDataSUSPEND, rSUSPEND));
            xySeries.add(seriesDataSUSPEND);
            seriesDataEXPIRED.setSeriesPoints(getPoints(seriesDataEXPIRED, rEXPIRED));
            xySeries.add(seriesDataEXPIRED);

            removeAllComponents();
            InvientCharts ich = createchart(new ArrayList(performersNames), xySeries);
            ich.setWidth("100%");
            addComponent(ich); 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private InvientCharts createchart(ArrayList<String> performersNames, ArrayList<XYSeries> xySeries) {
        InvientChartsConfig chartConfig = new InvientChartsConfig();
        chartConfig.getGeneralChartConfig().setType(SeriesType.BAR);

        chartConfig.getTitle().setText(ProcessbaseApplication.getString("taskByPerformers"));
        chartConfig.getSubtitle().setText("Source: PROCESSBASE BPMS");

        CategoryAxis xAxisMain = new CategoryAxis();
        xAxisMain.setCategories(performersNames);
        LinkedHashSet<XAxis> xAxesSet = new LinkedHashSet<InvientChartsConfig.XAxis>();
        xAxesSet.add(xAxisMain);
        chartConfig.setXAxes(xAxesSet);

        NumberYAxis yAxis = new NumberYAxis();
        yAxis.setAllowDecimals(false);
        yAxis.setTitle(new AxisTitle(ProcessbaseApplication.getString("processCount")));
        yAxis.getTitle().setAlign(AxisTitleAlign.HIGH);
        LinkedHashSet<YAxis> yAxesSet = new LinkedHashSet<InvientChartsConfig.YAxis>();
        yAxesSet.add(yAxis);
        chartConfig.setYAxes(yAxesSet);

        Legend legend = new Legend();
        legend.setLayout(Layout.VERTICAL);
        legend.setPosition(new Position());
        legend.getPosition().setAlign(HorzAlign.RIGHT);
        legend.getPosition().setVertAlign(VertAlign.TOP);
        legend.getPosition().setX(-50);
        legend.getPosition().setY(50);
        legend.setFloating(true);
        legend.setBorderWidth(1);
        legend.setBackgroundColor(new RGB(255, 255, 255));
        legend.setShadow(true);
        chartConfig.setLegend(legend);

        InvientCharts barChart = new InvientCharts(chartConfig);
        for (XYSeries xy : xySeries) {
            barChart.addSeries(xy);
        }
        return barChart;

    }

    private static LinkedHashSet<DecimalPoint> getPoints(Series series,
            double... values) {
        LinkedHashSet<DecimalPoint> points = new LinkedHashSet<DecimalPoint>();
        for (double value : values) {
            points.add(new DecimalPoint(series, value));
        }
        return points;
    }

    private static LinkedHashSet<DecimalPoint> getPoints(Series series, ArrayList<Double> values) {
        LinkedHashSet<DecimalPoint> points = new LinkedHashSet<DecimalPoint>();
        for (double value : values) {
            points.add(new DecimalPoint(series, value));
        }
        return points;
    }
}
