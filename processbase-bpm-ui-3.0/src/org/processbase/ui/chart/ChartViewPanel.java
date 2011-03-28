/**
 * Copyright (C) 2011 PROCESSBASE Ltd.
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
package org.processbase.ui.chart;

import com.github.wolfie.refresher.Refresher;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import org.processbase.bam.message.MessageController;
import org.processbase.ui.portlet.ChartPortlet;

import com.invient.vaadin.charts.Color;
import com.invient.vaadin.charts.Color.RGB;
import com.invient.vaadin.charts.Color.RGBA;
import com.invient.vaadin.charts.Gradient;
import com.invient.vaadin.charts.Gradient.LinearGradient.LinearColorStop;
import com.invient.vaadin.charts.InvientCharts;
import com.invient.vaadin.charts.InvientCharts.ChartClickEvent;
import com.invient.vaadin.charts.InvientCharts.ChartResetZoomEvent;
import com.invient.vaadin.charts.InvientCharts.ChartZoomEvent;
import com.invient.vaadin.charts.InvientCharts.ChartZoomListener;
import com.invient.vaadin.charts.InvientCharts.DateTimePoint;
import com.invient.vaadin.charts.InvientCharts.DateTimeSeries;
import com.invient.vaadin.charts.InvientCharts.DecimalPoint;
import com.invient.vaadin.charts.InvientCharts.PieChartLegendItemClickEvent;
import com.invient.vaadin.charts.InvientCharts.PointClickEvent;
import com.invient.vaadin.charts.InvientCharts.PointRemoveEvent;
import com.invient.vaadin.charts.InvientCharts.PointSelectEvent;
import com.invient.vaadin.charts.InvientCharts.PointUnselectEvent;
import com.invient.vaadin.charts.InvientCharts.Series;
import com.invient.vaadin.charts.InvientCharts.SeriesClickEvent;
import com.invient.vaadin.charts.InvientCharts.SeriesHideEvent;
import com.invient.vaadin.charts.InvientCharts.SeriesLegendItemClickEvent;
import com.invient.vaadin.charts.InvientCharts.SeriesShowEvent;
import com.invient.vaadin.charts.InvientCharts.SeriesType;
import com.invient.vaadin.charts.InvientCharts.XYSeries;
import com.invient.vaadin.charts.InvientChartsConfig;
import com.invient.vaadin.charts.InvientChartsConfig.AreaConfig;
import com.invient.vaadin.charts.InvientChartsConfig.AreaSplineConfig;
import com.invient.vaadin.charts.InvientChartsConfig.AxisBase.AxisTitle;
import com.invient.vaadin.charts.InvientChartsConfig.AxisBase.AxisTitleAlign;
import com.invient.vaadin.charts.InvientChartsConfig.AxisBase.DateTimePlotBand;
import com.invient.vaadin.charts.InvientChartsConfig.AxisBase.DateTimePlotBand.DateTimeRange;
import com.invient.vaadin.charts.InvientChartsConfig.AxisBase.Grid;
import com.invient.vaadin.charts.InvientChartsConfig.AxisBase.MinorGrid;
import com.invient.vaadin.charts.InvientChartsConfig.AxisBase.NumberPlotBand;
import com.invient.vaadin.charts.InvientChartsConfig.AxisBase.NumberPlotBand.NumberRange;
import com.invient.vaadin.charts.InvientChartsConfig.AxisBase.NumberPlotLine;
import com.invient.vaadin.charts.InvientChartsConfig.AxisBase.NumberPlotLine.NumberValue;
import com.invient.vaadin.charts.InvientChartsConfig.AxisBase.PlotLabel;
import com.invient.vaadin.charts.InvientChartsConfig.AxisBase.Tick;
import com.invient.vaadin.charts.InvientChartsConfig.AxisBase.TickmarkPlacement;
import com.invient.vaadin.charts.InvientChartsConfig.BarConfig;
import com.invient.vaadin.charts.InvientChartsConfig.CategoryAxis;
import com.invient.vaadin.charts.InvientChartsConfig.ChartLabel;
import com.invient.vaadin.charts.InvientChartsConfig.ChartLabel.ChartLabelItem;
import com.invient.vaadin.charts.InvientChartsConfig.ColumnConfig;
import com.invient.vaadin.charts.InvientChartsConfig.DashStyle;
import com.invient.vaadin.charts.InvientChartsConfig.DataLabel;
import com.invient.vaadin.charts.InvientChartsConfig.DateTimeAxis;
import com.invient.vaadin.charts.InvientChartsConfig.GeneralChartConfig.Margin;
import com.invient.vaadin.charts.InvientChartsConfig.GeneralChartConfig.Spacing;
import com.invient.vaadin.charts.InvientChartsConfig.GeneralChartConfig.ZoomType;
import com.invient.vaadin.charts.InvientChartsConfig.HorzAlign;
import com.invient.vaadin.charts.InvientChartsConfig.ImageMarker;
import com.invient.vaadin.charts.InvientChartsConfig.Legend;
import com.invient.vaadin.charts.InvientChartsConfig.Legend.Layout;
import com.invient.vaadin.charts.InvientChartsConfig.LineConfig;
import com.invient.vaadin.charts.InvientChartsConfig.MarkerState;
import com.invient.vaadin.charts.InvientChartsConfig.NumberXAxis;
import com.invient.vaadin.charts.InvientChartsConfig.NumberYAxis;
import com.invient.vaadin.charts.InvientChartsConfig.PieConfig;
import com.invient.vaadin.charts.InvientChartsConfig.PieDataLabel;
import com.invient.vaadin.charts.InvientChartsConfig.PointConfig;
import com.invient.vaadin.charts.InvientChartsConfig.Position;
import com.invient.vaadin.charts.InvientChartsConfig.ScatterConfig;
import com.invient.vaadin.charts.InvientChartsConfig.SeriesConfig;
import com.invient.vaadin.charts.InvientChartsConfig.SeriesState;
import com.invient.vaadin.charts.InvientChartsConfig.SplineConfig;
import com.invient.vaadin.charts.InvientChartsConfig.Stacking;
import com.invient.vaadin.charts.InvientChartsConfig.SymbolMarker;
import com.invient.vaadin.charts.InvientChartsConfig.SymbolMarker.Symbol;
import com.invient.vaadin.charts.InvientChartsConfig.Tooltip;
import com.invient.vaadin.charts.InvientChartsConfig.VertAlign;
import com.invient.vaadin.charts.InvientChartsConfig.XAxis;
import com.invient.vaadin.charts.InvientChartsConfig.XAxisDataLabel;
import com.invient.vaadin.charts.InvientChartsConfig.YAxis;
import com.invient.vaadin.charts.InvientChartsConfig.YAxisDataLabel;
import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 *
 * @author marat
 */
public class ChartViewPanel extends VerticalLayout implements Refresher.RefreshListener {

    private javax.portlet.PortletPreferences portletPreferences;
    private Refresher ref = new Refresher();
    private String refreshInterval = null;
    private String sqlText = null;
    private String title = null;
    private String legend = null;
    private String height = null;
    private String width = null;
    private String titleX = null;
    private String titleY = null;
    private String min = null;
    private String max = null;
    private String is3D = null;
    private String isStacked = null;
    private String chartType = null;
    private InvientCharts chart;
    private ArrayList<String> categories = new ArrayList<String>();
    private LinkedHashSet<String> series = new LinkedHashSet<String>();
    private HashMap<String, ArrayList<Double>> data = new HashMap<String, ArrayList<Double>>();
    private ArrayList<XYSeries> xySeries = new ArrayList<XYSeries>();
    private boolean created = false;

    public ChartViewPanel() {
        try {
            System.out.println("----------------start");
            removeAllComponents();
            portletPreferences = ChartPortlet.portletPreferences.get();
            for (String key : portletPreferences.getMap().keySet()) {
                String[] value = portletPreferences.getMap().get(key);
                if (key.equals("refreshInterval") && value.length > 0) {
                    refreshInterval = value[0];
                } else if (key.equals("sqlText") && value.length > 0) {
                    sqlText = value[0];
                } else if (key.equals("chartType") && value.length > 0) {
                    chartType = value[0];
                } else if (key.equals("min") && value.length > 0) {
                    min = value[0];
                } else if (key.equals("width") && value.length > 0) {
                    width = value[0];
                } else if (key.equals("titleX") && value.length > 0) {
                    titleX = value[0];
                } else if (key.equals("titleY") && value.length > 0) {
                    titleY = value[0];
                } else if (key.equals("title") && value.length > 0) {
                    title = value[0];
                } else if (key.equals("legend") && value.length > 0) {
                    legend = value[0];
                } else if (key.equals("height") && value.length > 0) {
                    height = value[0];
                } else if (key.equals("max") && value.length > 0) {
                    max = value[0];
                } else if (key.equals("is3D") && value.length > 0) {
                    is3D = value[0];
                } else if (key.equals("isStacked") && value.length > 0) {
                    isStacked = value[0];
                }
            }

            if (refreshInterval != null) {
                ref.setRefreshInterval(Integer.parseInt(refreshInterval) * 1000);
            }
            ref.addListener((Refresher.RefreshListener) this);
            addComponent(ref);
            if (chartType.equalsIgnoreCase("BarChart")) {
                refreshChart();
            } else if (chartType.equalsIgnoreCase("ColumnChart")) {
                prepareColumnChart();
            } else if (chartType.equalsIgnoreCase("PieChart")) {
                preparePieChart();
            } else if (chartType.equalsIgnoreCase("LineChart")) {
                prepareLineChart();
            } else if (chartType.equalsIgnoreCase("AreaChart")) {
                prepareAreaChart();
            } else if (chartType.equalsIgnoreCase("Gauge")) {
//                prepareGauge();
            }
            setMargin(false);
            setSpacing(true);
            setStyleName(Reindeer.LAYOUT_WHITE);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void refresh(Refresher source) {
        refreshChart();
    }

    private void refreshChart() {
        try {
            if (chartType.equalsIgnoreCase("BarChart")) {
                prepareBarChart();
            }

//            for (String label : labels) {
//                if (chartType.equalsIgnoreCase("BarChart")) {
//                    barChart.remove(label);
//                } else if (chartType.equalsIgnoreCase("ColumnChart")) {
//                    columnChart.remove(label);
//                } else if (chartType.equalsIgnoreCase("PieChart")) {
//                    pieChart.remove(label);
//                } else if (chartType.equalsIgnoreCase("LineChart")) {
//                    lineChart.remove(label);
//                } else if (chartType.equalsIgnoreCase("AreaChart")) {
//                    areaChart.remove(label);
//                } else if (chartType.equalsIgnoreCase("Gauge")) {
//                }
//            }
//            for (String label : values2.keySet()) {
//                if (chartType.equalsIgnoreCase("BarChart")) {
//                    barChart.add(label, values2.get(label));
//                } else if (chartType.equalsIgnoreCase("ColumnChart")) {
//                    columnChart.add(label, values2.get(label));
//                } else if (chartType.equalsIgnoreCase("LineChart")) {
//                    lineChart.add(label, values2.get(label));
//                } else if (chartType.equalsIgnoreCase("AreaChart")) {
//                    areaChart.add(label, values2.get(label));
//                }
//            }
//            for (String label : values1.keySet()) {
//                if (chartType.equalsIgnoreCase("PieChart")) {
//                    pieChart.add(label, values1.get(label));
//                } else if (chartType.equalsIgnoreCase("Gauge")) {
//                }
//            }
            if (!created) {
                addComponent(chart);
                created = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void prepareBarChart() {
        if (!created) {
            InvientChartsConfig chartConfig = new InvientChartsConfig();
            chartConfig.getTitle().setText(title != null ? title : " ");
            chartConfig.getSubtitle().setText("Source: PROCESSBASSE BPMS");

            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setCategories(new ArrayList(categories));
            xAxis.setTitle(new AxisTitle(titleX != null ? titleX : " "));
            LinkedHashSet<XAxis> xAxesSet = new LinkedHashSet<InvientChartsConfig.XAxis>();
            xAxesSet.add(xAxis);
            chartConfig.setXAxes(xAxesSet);

            NumberYAxis yAxis = new NumberYAxis();
            yAxis.setAllowDecimals(false);
            yAxis.setTitle(new AxisTitle(titleY != null ? titleY : " "));
            LinkedHashSet<YAxis> yAxesSet = new LinkedHashSet<InvientChartsConfig.YAxis>();
            yAxesSet.add(yAxis);
            chartConfig.setYAxes(yAxesSet);

            chart = new InvientCharts(chartConfig);
        } else {
//            for (XYSeries xy : xySeries) {
//                chart.removeSeries(xy);
//            }
        }

        for (XYSeries xy : xySeries) {
            chart.addSeries(xy);
        }
        if (height != null) {
            chart.setHeight(height + "px");
        }
        if (width != null) {
            chart.setWidth(width + "px");
        }
//        chart.setSizeFull();

    }

    private void preparePieChart() {
//        pieChart = new PieChart();
//        pieChart.setOption("is3D", is3D != null && is3D.equalsIgnoreCase("true") ? true : false);
//        pieChart.setOption("isStacked", isStacked != null && isStacked.equalsIgnoreCase("true") ? true : false);
//        pieChart.setOption("legend", legend != null ? legend : "bottom");
//        pieChart.setOption("title", title != null ? title : " ");
//        pieChart.setOption("titleX", titleX != null ? titleX : " ");
//        pieChart.setOption("titleY", titleY != null ? titleY : " ");
//        pieChart.setOption("height", height != null ? Integer.parseInt(height) : 600);
//        pieChart.setOption("width", width != null ? Integer.parseInt(width) : 600);
//        if (min != null && !min.isEmpty()) {
//            pieChart.setOption("min", Integer.parseInt(min));
//        }
//        if (max != null && !max.isEmpty()) {
//            pieChart.setOption("max", Integer.parseInt(max));
//        }
//        pieChart.setOption("is3D", true);
//        refreshChart();
//        pieChart.setSizeFull();
//        addComponent(pieChart);
    }

    private void prepareColumnChart() {
//        columnChart = new ColumnChart();
//        columnChart.setOption("is3D", is3D != null && is3D.equalsIgnoreCase("true") ? true : false);
//        columnChart.setOption("isStacked", isStacked != null && isStacked.equalsIgnoreCase("true") ? true : false);
//        columnChart.setOption("legend", legend != null ? legend : "bottom");
//        columnChart.setOption("title", title != null ? title : " ");
//        columnChart.setOption("titleX", titleX != null ? titleX : " ");
//        columnChart.setOption("titleY", titleY != null ? titleY : " ");
//        columnChart.setOption("height", height != null ? Integer.parseInt(height) : 600);
//        columnChart.setOption("width", width != null ? Integer.parseInt(width) : 600);
//        if (min != null && !min.isEmpty()) {
//            columnChart.setOption("min", Integer.parseInt(min));
//        }
//        if (max != null && !max.isEmpty()) {
//            columnChart.setOption("max", Integer.parseInt(max));
//        }
//        columnChart.addXAxisLabel(title != null ? title : UUID.randomUUID().toString());
//        columnChart.setOption("is3D", true);
//
//        refreshChart();
//        for (String bar : bars.keySet()) {
//            columnChart.addColumn(bar);
//        }
//        columnChart.setSizeFull();
//
//        addComponent(columnChart);
    }

    private void prepareLineChart() {
//        lineChart = new LineChart();
//        lineChart.setOption("is3D", is3D != null && is3D.equalsIgnoreCase("true") ? true : false);
//        lineChart.setOption("isStacked", isStacked != null && isStacked.equalsIgnoreCase("true") ? true : false);
//        lineChart.setOption("legend", legend != null ? legend : "bottom");
//        lineChart.setOption("title", title != null ? title : " ");
//        lineChart.setOption("titleX", titleX != null ? titleX : " ");
//        lineChart.setOption("titleY", titleY != null ? titleY : " ");
//        lineChart.setOption("height", height != null ? Integer.parseInt(height) : 600);
//        lineChart.setOption("width", width != null ? Integer.parseInt(width) : 600);
//        if (min != null && !min.isEmpty()) {
//            lineChart.setOption("min", Integer.parseInt(min));
//        }
//        if (max != null && !max.isEmpty()) {
//            lineChart.setOption("max", Integer.parseInt(max));
//        }
//        lineChart.addXAxisLabel(title != null ? title : UUID.randomUUID().toString());
//        lineChart.setOption("is3D", true);
//
//        refreshChart();
//        for (String bar : bars.keySet()) {
//            lineChart.addLine(bar);
//        }
//        lineChart.setSizeFull();
//
//        addComponent(lineChart);
    }

    private void prepareAreaChart() {
//        areaChart = new AreaChart();
//        areaChart.setOption("is3D", is3D != null && is3D.equalsIgnoreCase("true") ? true : false);
//        areaChart.setOption("isStacked", isStacked != null && isStacked.equalsIgnoreCase("true") ? true : false);
//        areaChart.setOption("legend", legend != null ? legend : "bottom");
//        areaChart.setOption("title", title != null ? title : " ");
//        areaChart.setOption("titleX", titleX != null ? titleX : " ");
//        areaChart.setOption("titleY", titleY != null ? titleY : " ");
//        areaChart.setOption("height", height != null ? Integer.parseInt(height) : 600);
//        areaChart.setOption("width", width != null ? Integer.parseInt(width) : 600);
//        if (min != null && !min.isEmpty()) {
//            areaChart.setOption("min", Integer.parseInt(min));
//        }
//        if (max != null && !max.isEmpty()) {
//            areaChart.setOption("max", Integer.parseInt(max));
//        }
//        areaChart.addXAxisLabel(title != null ? title : UUID.randomUUID().toString());
//        areaChart.setOption("is3D", true);
//
//        refreshChart();
//        for (String bar : bars.keySet()) {
//            areaChart.addArea(bar);
//        }
//        areaChart.setSizeFull();
//
//        addComponent(areaChart);
    }

    private void prepareValues(String sql) throws SQLException {
        Connection conn = null;
        try {
            conn = MessageController.newConnection();
            PreparedStatement ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            categories.clear();
            if (rs.getMetaData().getColumnCount() == 2) {
                series.add(" ");
                prepareDataMap();
                while (rs.next()) {
//                    addCategory(rs.getString(1));
                    addValueToDataMap(rs.getString(1), " ", rs.getDouble(2));
                }
            } else if (rs.getMetaData().getColumnCount() == 3) {
                while (rs.next()) {
                    addCategory(rs.getString(1));
                    series.add(rs.getString(2));
                }
                prepareDataMap();
                rs.first();
                addValueToDataMap(rs.getString(1), rs.getString(2), rs.getDouble(3));
                while (rs.next()) {
                    addValueToDataMap(rs.getString(1), rs.getString(2), rs.getDouble(3));
                }
            }
            prepareXYSeries();
            ps.close();
        } finally {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }

        }
    }

    private void addCategory(String category) {
        if (!categories.contains(category)) {
            categories.add(category);
        }
    }

    private void prepareDataMap() {
        data.clear();
        for (String serie : series) {
            data.put(serie, createZeroDoubleArray());
        }
    }

    private ArrayList<Double> createZeroDoubleArray() {
        ArrayList<Double> result = new ArrayList<Double>(categories.size());
        for (String x : categories) {
            result.add(0.0);
        }
        return result;
    }

    private void addValueToDataMap(String category, String serie, double value) {
        data.get(serie).set(categories.indexOf(category), value);
    }

    private void prepareXYSeries() {
        xySeries.clear();
        for (String key : data.keySet()) {
            XYSeries s = new XYSeries(key, SeriesType.BAR);
            s.setSeriesPoints(getPoints(s, data.get(key)));
            xySeries.add(s);
        }
    }

    private static LinkedHashSet<DecimalPoint> getPoints(Series series, ArrayList<Double> values) {
        LinkedHashSet<DecimalPoint> points = new LinkedHashSet<DecimalPoint>();
        for (double value : values) {
            points.add(new DecimalPoint(series, value));
        }
        return points;
    }
}
