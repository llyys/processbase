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
import com.invient.vaadin.charts.InvientCharts.SeriesType;
import com.invient.vaadin.charts.InvientCharts.XYSeries;
import com.invient.vaadin.charts.InvientChartsConfig;
import com.invient.vaadin.charts.InvientChartsConfig.GeneralChartConfig.Margin;
import com.invient.vaadin.charts.InvientChartsConfig.PieConfig;
import com.invient.vaadin.charts.InvientChartsConfig.PieDataLabel;
import com.invient.vaadin.charts.InvientChartsConfig.PointConfig;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Random;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.light.LightActivityInstance;
import org.processbase.ui.core.Processbase;
import org.processbase.ui.core.template.DashboardPanel;

/**
 *
 * @author marat gubaidullin
 */
public class DashboardUserTaskPanel extends DashboardPanel {

    private HashMap<String, RGB> colors = new HashMap<String, RGB>();

    public DashboardUserTaskPanel() {
        super();
    }

    @Override
    public void initUI() {
    }

    public void refresh() {
        try {
            Date now = new Date();
            GregorianCalendar midnight = new GregorianCalendar();
            midnight.set(Calendar.HOUR, 0);
            midnight.set(Calendar.AM_PM, Calendar.AM);
            midnight.set(Calendar.MINUTE, 0);
            midnight.set(Calendar.SECOND, 0);
            midnight.set(Calendar.MILLISECOND, 0);
            HashMap<String, Double> startedTasks = new HashMap<String, Double>();
            HashMap<String, Double> todayStartTasks = new HashMap<String, Double>();
            HashMap<String, Double> todayDoneTasks = new HashMap<String, Double>();
            HashMap<String, Double> expiredTasks = new HashMap<String, Double>();

            Collection<LightActivityInstance> ais = ((Processbase)getApplication()).getBpmModule().getActivityInstances();
            for (LightActivityInstance ai : ais) {
                if (ai.isTask() && ai.getTask().isTaskAssigned() && ai.getState().equals(ActivityState.EXECUTING)) {
                    if (startedTasks.containsKey(ai.getTask().getTaskUser())) {
                        Double c = startedTasks.get(ai.getTask().getTaskUser());
                        startedTasks.put(ai.getTask().getTaskUser(), c.doubleValue() + 1);
                    } else {
                        startedTasks.put(ai.getTask().getTaskUser().toString(), 1.0);
                    }
                    if (ai.getTask().getCreatedDate().after(midnight.getTime())) {
                        if (todayStartTasks.containsKey(ai.getTask().getTaskUser())) {
                            Double c = todayStartTasks.get(ai.getTask().getTaskUser());
                            todayStartTasks.put(ai.getTask().getTaskUser(), c.doubleValue() + 1);
                        } else {
                            todayStartTasks.put(ai.getTask().getTaskUser().toString(), 1.0);
                        }
                    }
                } else if (ai.isTask() && ai.getTask().isTaskAssigned()
                        && ai.getState().equals(ActivityState.FINISHED) && ai.getTask().getEndedDate().after(midnight.getTime())) {
                    if (todayDoneTasks.containsKey(ai.getTask().getTaskUser())) {
                        Double c = todayDoneTasks.get(ai.getTask().getTaskUser());
                        todayDoneTasks.put(ai.getTask().getTaskUser(), c.doubleValue() + 1);
                    } else {
                        todayDoneTasks.put(ai.getTask().getTaskUser().toString(), 1.0);
                    }
                }
                if (ai.isTask() && ai.getTask().isTaskAssigned()
                        && (ai.getState().equals(ActivityState.EXECUTING) || ai.getState().equals(ActivityState.READY) || ai.getState().equals(ActivityState.SUSPENDED))
                        && ai.getExpectedEndDate() != null && ai.getExpectedEndDate().before(now)) {
                    if (expiredTasks.containsKey(ai.getTask().getTaskUser())) {
                        Double c = expiredTasks.get(ai.getTask().getTaskUser());
                        expiredTasks.put(ai.getTask().getTaskUser(), c.doubleValue() + 1);
                    } else {
                        expiredTasks.put(ai.getTask().getTaskUser(), 1.0);
                    }
                }
            }
            removeAllComponents();

            PieConfig pieCfg1 = new PieConfig();
            pieCfg1.setInnerSize(65);
            pieCfg1.setDataLabel(new PieDataLabel(false));

            XYSeries series1 = new XYSeries("Started", SeriesType.PIE, pieCfg1);
            LinkedHashSet<DecimalPoint> points1 = new LinkedHashSet<DecimalPoint>();
            for (String perf : todayStartTasks.keySet()) {
                DecimalPoint point1 = new DecimalPoint(series1, perf, todayStartTasks.get(perf));
                point1.setConfig(new PointConfig(getColor(perf)));
                points1.add(point1);
            }
//            DecimalPoint point1 = new DecimalPoint(series1, "ivan", 4.0);
//            point1.setConfig(new PointConfig(getColor("ivan")));
//            points1.add(point1);
//            point1 = new DecimalPoint(series1, "marat", 3.0);
//            point1.setConfig(new PointConfig(getColor("marat")));
//            points1.add(point1);
            series1.setSeriesPoints(points1);

            PieConfig pieCfg2 = new PieConfig();
            pieCfg2.setInnerSize(150);
            pieCfg2.setDataLabel(new PieDataLabel());
            pieCfg2.setColor(new RGB(0, 0, 0));
            pieCfg2.getDataLabel().setConnectorColor(new RGB(0, 0, 0));
            XYSeries series2 = new XYSeries("Finished", SeriesType.PIE, pieCfg2);
            LinkedHashSet<DecimalPoint> points2 = new LinkedHashSet<DecimalPoint>();
            for (String perf : todayDoneTasks.keySet()) {
                DecimalPoint point2 = new DecimalPoint(series2, perf, todayDoneTasks.get(perf));
                point2.setConfig(new PointConfig(getColor(perf)));
                points2.add(point2);
            }
//            DecimalPoint point2 = new DecimalPoint(series2, "ivan", 10.0);
//            point2.setConfig(new PointConfig(getColor("ivan")));
//            points2.add(point2);
//            point2 = new DecimalPoint(series2, "marat", 8.0);
//            point2.setConfig(new PointConfig(getColor("marat")));
//            points2.add(point2);
            series2.setSeriesPoints(points2);

            InvientCharts ich2 = createchart(series1, series2);
            ich2.setWidth("100%");
            addComponent(ich2);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private InvientCharts createchart(XYSeries series1, XYSeries series2) {
        InvientChartsConfig chartConfig = new InvientChartsConfig();
        chartConfig.getGeneralChartConfig().setType(SeriesType.PIE);

        chartConfig.getGeneralChartConfig().setMargin(new Margin());
        chartConfig.getGeneralChartConfig().getMargin().setTop(50);
        chartConfig.getGeneralChartConfig().getMargin().setRight(0);
        chartConfig.getGeneralChartConfig().getMargin().setBottom(0);
        chartConfig.getGeneralChartConfig().getMargin().setLeft(0);
        chartConfig.getTitle().setText(((Processbase)getApplication()).getMessages().getString("taskByUser"));

        chartConfig.getTooltip().setFormatterJsFunc(
                "function() {"
                + " return '<b>'+ this.series.name +'</b><br/>'+ "
                + "     this.point.name +': '+ this.y; " + "}");

        InvientCharts chart = new InvientCharts(chartConfig);
        chart.addSeries(series1);
        chart.addSeries(series2);
        return chart;

    }

    private RGB getColor(String name) {
        if (colors.containsKey(name)) {
            return colors.get(name);
        } else {
            Random x = new Random();
            RGB rgb = new RGB(x.nextInt(255), x.nextInt(255), x.nextInt(255));
            colors.put(name, rgb);
            return rgb;
        }
    }
}
