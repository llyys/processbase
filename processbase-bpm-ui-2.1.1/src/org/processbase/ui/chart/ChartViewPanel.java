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
import org.vaadin.vaadinvisualizations.AreaChart;
import org.vaadin.vaadinvisualizations.BarChart;
import org.vaadin.vaadinvisualizations.ColumnChart;
import org.vaadin.vaadinvisualizations.Gauge;
import org.vaadin.vaadinvisualizations.LineChart;
import org.vaadin.vaadinvisualizations.PieChart;

/**
 *
 * @author marat
 */
public class ChartViewPanel extends VerticalLayout implements Refresher.RefreshListener {

    private javax.portlet.PortletPreferences portletPreferences;
    private Refresher ref = new Refresher();
    private BarChart barChart;
    private ColumnChart columnChart;
    private PieChart pieChart;
    private LineChart lineChart;
    private AreaChart areaChart;
    private Gauge gauge;
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
    private HashMap<String, Integer> bars = new HashMap<String, Integer>();
    private HashMap<String, double[]> values2 = new HashMap<String, double[]>();
    private HashMap<String, Double> values1 = new HashMap<String, Double>();
    private HashSet<String> labels = new HashSet<String>();

    public ChartViewPanel() {
        try {
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
                prepareBarChart();
            } else if (chartType.equalsIgnoreCase("ColumnChart")) {
                prepareColumnChart();
            } else if (chartType.equalsIgnoreCase("PieChart")) {
            } else if (chartType.equalsIgnoreCase("LineChart")) {
                prepareLineChart();
            } else if (chartType.equalsIgnoreCase("AreaChart")) {
                 prepareAreaChart();
            } else if (chartType.equalsIgnoreCase("Gauge")) {
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
            prepareValues(sqlText);
            for (String label : labels) {
                if (chartType.equalsIgnoreCase("BarChart")) {
                    barChart.remove(label);
                } else if (chartType.equalsIgnoreCase("ColumnChart")) {
                    columnChart.remove(label);
                } else if (chartType.equalsIgnoreCase("PieChart")) {
                } else if (chartType.equalsIgnoreCase("LineChart")) {
                    lineChart.remove(label);
                } else if (chartType.equalsIgnoreCase("AreaChart")) {
                    areaChart.remove(label);
                } else if (chartType.equalsIgnoreCase("Gauge")) {
                }
            }
            for (String label : values2.keySet()) {
                if (chartType.equalsIgnoreCase("BarChart")) {
                    barChart.add(label, values2.get(label));
                } else if (chartType.equalsIgnoreCase("ColumnChart")) {
                    columnChart.add(label, values2.get(label));
                } else if (chartType.equalsIgnoreCase("PieChart")) {
                } else if (chartType.equalsIgnoreCase("LineChart")) {
                    lineChart.add(label, values2.get(label));
                } else if (chartType.equalsIgnoreCase("AreaChart")) {
                    areaChart.add(label, values2.get(label));
                } else if (chartType.equalsIgnoreCase("Gauge")) {
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void prepareBarChart() {
        barChart = new BarChart();
        barChart.setOption("is3D", is3D != null && is3D.equalsIgnoreCase("true") ? true : false);
        barChart.setOption("isStacked", isStacked != null && isStacked.equalsIgnoreCase("true") ? true : false);
        barChart.setOption("legend", legend != null ? legend : "bottom");
        barChart.setOption("title", title != null ? title : " ");
        barChart.setOption("titleX", titleX != null ? titleX : " ");
        barChart.setOption("titleY", titleY != null ? titleY : " ");
        barChart.setOption("height", height != null ? Integer.parseInt(height) : 600);
        barChart.setOption("width", width != null ? Integer.parseInt(width) : 600);
        if (min != null && !min.isEmpty()) {
            barChart.setOption("min", Integer.parseInt(min));
        }
        if (max != null && !max.isEmpty()) {
            barChart.setOption("max", Integer.parseInt(max));
        }
        barChart.addXAxisLabel(title != null ? title : UUID.randomUUID().toString());
        barChart.setOption("is3D", true);

        refreshChart();
        for (String bar : bars.keySet()) {
            barChart.addBar(bar);
        }
        barChart.setSizeFull();

        addComponent(barChart);
    }

    private void prepareColumnChart() {
        columnChart = new ColumnChart();
        columnChart.setOption("is3D", is3D != null && is3D.equalsIgnoreCase("true") ? true : false);
        columnChart.setOption("isStacked", isStacked != null && isStacked.equalsIgnoreCase("true") ? true : false);
        columnChart.setOption("legend", legend != null ? legend : "bottom");
        columnChart.setOption("title", title != null ? title : " ");
        columnChart.setOption("titleX", titleX != null ? titleX : " ");
        columnChart.setOption("titleY", titleY != null ? titleY : " ");
        columnChart.setOption("height", height != null ? Integer.parseInt(height) : 600);
        columnChart.setOption("width", width != null ? Integer.parseInt(width) : 600);
        if (min != null && !min.isEmpty()) {
            columnChart.setOption("min", Integer.parseInt(min));
        }
        if (max != null && !max.isEmpty()) {
            columnChart.setOption("max", Integer.parseInt(max));
        }
        columnChart.addXAxisLabel(title != null ? title : UUID.randomUUID().toString());
        columnChart.setOption("is3D", true);

        refreshChart();
        for (String bar : bars.keySet()) {
            columnChart.addColumn(bar);
        }
        columnChart.setSizeFull();

        addComponent(columnChart);
    }

    private void prepareLineChart() {
        lineChart = new LineChart();
        lineChart.setOption("is3D", is3D != null && is3D.equalsIgnoreCase("true") ? true : false);
        lineChart.setOption("isStacked", isStacked != null && isStacked.equalsIgnoreCase("true") ? true : false);
        lineChart.setOption("legend", legend != null ? legend : "bottom");
        lineChart.setOption("title", title != null ? title : " ");
        lineChart.setOption("titleX", titleX != null ? titleX : " ");
        lineChart.setOption("titleY", titleY != null ? titleY : " ");
        lineChart.setOption("height", height != null ? Integer.parseInt(height) : 600);
        lineChart.setOption("width", width != null ? Integer.parseInt(width) : 600);
        if (min != null && !min.isEmpty()) {
            lineChart.setOption("min", Integer.parseInt(min));
        }
        if (max != null && !max.isEmpty()) {
            lineChart.setOption("max", Integer.parseInt(max));
        }
        lineChart.addXAxisLabel(title != null ? title : UUID.randomUUID().toString());
        lineChart.setOption("is3D", true);

        refreshChart();
        for (String bar : bars.keySet()) {
            lineChart.addLine(bar);
        }
        lineChart.setSizeFull();

        addComponent(lineChart);
    }

    private void prepareAreaChart() {
        areaChart = new AreaChart();
        areaChart.setOption("is3D", is3D != null && is3D.equalsIgnoreCase("true") ? true : false);
        areaChart.setOption("isStacked", isStacked != null && isStacked.equalsIgnoreCase("true") ? true : false);
        areaChart.setOption("legend", legend != null ? legend : "bottom");
        areaChart.setOption("title", title != null ? title : " ");
        areaChart.setOption("titleX", titleX != null ? titleX : " ");
        areaChart.setOption("titleY", titleY != null ? titleY : " ");
        areaChart.setOption("height", height != null ? Integer.parseInt(height) : 600);
        areaChart.setOption("width", width != null ? Integer.parseInt(width) : 600);
        if (min != null && !min.isEmpty()) {
            areaChart.setOption("min", Integer.parseInt(min));
        }
        if (max != null && !max.isEmpty()) {
            areaChart.setOption("max", Integer.parseInt(max));
        }
        areaChart.addXAxisLabel(title != null ? title : UUID.randomUUID().toString());
        areaChart.setOption("is3D", true);

        refreshChart();
        for (String bar : bars.keySet()) {
            areaChart.addArea(bar);
        }
        areaChart.setSizeFull();

        addComponent(areaChart);
    }

    private void prepareValues(String sql) throws SQLException {
        Connection conn = null;
        try {
            conn = MessageController.newConnection();
            PreparedStatement ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            values1.clear();
            values2.clear();
            bars.clear();
            labels.clear();
            if (rs.getMetaData().getColumnCount() == 2) {
                while (rs.next()) {
                    values1.put(rs.getString(1), rs.getDouble(2));
                    labels.add(rs.getString(1));
                }
            } else if (rs.getMetaData().getColumnCount() == 3) {
                while (rs.next()) {
                    addBar(rs.getString(2));
                    labels.add(rs.getString(1));
                }
                rs.first();
                addValue2(rs.getString(1), rs.getString(2), rs.getDouble(3));
                while (rs.next()) {
                    addValue2(rs.getString(1), rs.getString(2), rs.getDouble(3));
                }
            }
            ps.close();
        } finally {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }

        }
    }

    private void addBar(String barName) {
        if (!bars.containsKey(barName)) {
            bars.put(barName, bars.size());
        }
    }

    private void addValue2(String label, String barName, double value) {
//        System.out.println(label +  " " + barName +" " + value);
        double[] valuesArray = null;
        if (!values2.containsKey(label)) {
            valuesArray = new double[bars.size()];
        } else {
            valuesArray = values2.get(label);
        }
        valuesArray[bars.get(barName).intValue()] = value;
        values2.put(label, valuesArray);
//        System.out.println("values2 = " + values2.size());
    }
}
