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
import java.awt.Color;
import java.awt.GradientPaint;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.processbase.bam.message.MessageController;
import org.processbase.ui.portlet.ChartPortlet;
import org.vaadin.ui.JFreeChartWrapper;

/**
 *
 * @author marat
 */
public class ChartViewPanel extends VerticalLayout implements Refresher.RefreshListener {

    private javax.portlet.PortletPreferences portletPreferences;
    private Refresher ref = new Refresher();
    private JFreeChart chart;
    private JFreeChartWrapper chartWrapper;
    String refreshInterval = null;
    String sqlText = null;
    String title = null;
    String categoryAxisLabel = null;
    String valueAxisLabel = null;
    String valueColumn = null;
    String rowKey = null;
    String columnKey = null;
    String orientation = null;
    String chartType = null;

    public ChartViewPanel() {
        try {
            portletPreferences = ChartPortlet.portletPreferences.get();
            for (String key : portletPreferences.getMap().keySet()) {
                String[] value = portletPreferences.getMap().get(key);
                if (key.equals("refreshInterval") && value.length > 0) {
                    refreshInterval = value[0];
                } else if (key.equals("sqlText") && value.length > 0) {
                    sqlText = value[0];
                } else if (key.equals("chartType") && value.length > 0) {
                    chartType = value[0];
                } else if (key.equals("orientation") && value.length > 0) {
                    orientation = value[0];
                } else if (key.equals("valueColumn") && value.length > 0) {
                    valueColumn = value[0];
                } else if (key.equals("rowKey") && value.length > 0) {
                    rowKey = value[0];
                } else if (key.equals("columnKey") && value.length > 0) {
                    columnKey = value[0];
                } else if (key.equals("title") && value.length > 0) {
                    title = value[0];
                } else if (key.equals("categoryAxisLabel") && value.length > 0) {
                    categoryAxisLabel = value[0];
                } else if (key.equals("valueAxisLabel") && value.length > 0) {
                    valueAxisLabel = value[0];
                }
            }
            chart = ChartViewPanel.createchart(
                    title,
                    categoryAxisLabel,
                    valueAxisLabel,
                    getCategoryDataset(valueColumn, rowKey, columnKey, sqlText),
                    orientation);
            chartWrapper = new JFreeChartWrapper(chart);
            addComponent(chartWrapper);
            setMargin(true);
            setSpacing(true);
            setWidth("100%");
            setStyleName(Reindeer.LAYOUT_WHITE);

            if (refreshInterval !=null)
            ref.setRefreshInterval(Integer.parseInt(refreshInterval)*1000);
            boolean addListener = ref.addListener((Refresher.RefreshListener)this);
            addComponent(ref);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void refresh(Refresher source) {
        try {
            System.out.println(System.currentTimeMillis());
            removeComponent(chartWrapper);
            chart = ChartViewPanel.createchart(title, categoryAxisLabel, valueAxisLabel, getCategoryDataset(valueColumn, rowKey, columnKey, sqlText), orientation);
            chartWrapper = new JFreeChartWrapper(chart);
            addComponent(chartWrapper);
        } catch (SQLException ex) {
            Logger.getLogger(ChartViewPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    public static JFreeChart createchart(
            String title,
            String categoryAxisLabel,
            String valueAxisLabel,
            CategoryDataset dataset,
            String orientation) {

        JFreeChart c = ChartFactory.createBarChart3D(title,
                categoryAxisLabel, // domain axis label
                valueAxisLabel, // range axis label
                dataset, // data
                orientation.equals("HORIZONTAL") ? PlotOrientation.HORIZONTAL : PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips?
                false // URLs?
                );

        // set the background color for the chart...
        c.setBackgroundPaint(Color.white);
        c.setAntiAlias(true);

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
        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f, 0.0f, new Color(0, 0, 64));
        GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green, 0.0f, 0.0f, new Color(0, 64, 0));
        GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.red, 0.0f, 0.0f, new Color(64, 0, 0));
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        renderer.setSeriesPaint(2, gp2);

        CategoryAxis domainAxis = plot.getDomainAxis();
//        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
        // OPTIONAL CUSTOMISATION COMPLETED.

        return c;
    }

    public static CategoryDataset getCategoryDataset(String valueColumnName, String rowColumnName, String columnKeyColumnNam, String sql) throws SQLException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Connection conn = null;
        try {
            conn = MessageController.newConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            while (rs.next()) {
                dataset.addValue(rs.getBigDecimal(valueColumnName), rs.getString(rowColumnName), rs.getString(columnKeyColumnNam));
            }
            ps.close();
        } finally {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }

        }
        return dataset;
    }
}
