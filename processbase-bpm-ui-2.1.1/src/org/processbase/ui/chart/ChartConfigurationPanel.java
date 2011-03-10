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

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.processbase.bam.message.MessageController;
import org.processbase.ui.portlet.ChartPortlet;
import org.processbase.ui.template.ButtonBar;
import org.vaadin.ui.JFreeChartWrapper;

/**
 *
 * @author marat
 */
public class ChartConfigurationPanel extends GridLayout implements Button.ClickListener {

    private javax.portlet.PortletPreferences portletPreferences;
    private TextField refreshInterval;
    private TextArea sqlText;
    private NativeSelect chartType;
    private Table testTable;
    private TextField title;
    private TextField categoryAxisLabel;
    private TextField valueAxisLabel;
    private NativeSelect orientation;
    private NativeSelect valueColumn;
    private NativeSelect rowKey;
    private NativeSelect columnKey;
    private Button btnSave;
    private Button btnTestSQL;
    private Button btnPreview;
    private ButtonBar buttons = new ButtonBar();
    private IndexedContainer columns;

    public ChartConfigurationPanel() {
        super(3, 6);
        setWidth("100%");
        chartType = new NativeSelect("Chart Type");
        chartType.addItem("BarChart");
        chartType.addItem("BarChart3D");
        chartType.addItem("LineChart");
        chartType.addItem("LineChart3D");
        chartType.addItem("PieChart");
        chartType.addItem("PieChart3D");
        chartType.setWidth("100%");

        orientation = new NativeSelect("orientation");
        orientation.setWidth("100%");
        orientation.addItem("HORIZONTAL");
        orientation.addItem("VERTICAL");

        refreshInterval = new TextField("Refresh Interval");
        refreshInterval.setMaxLength(3);
        refreshInterval.setRequired(true);
        refreshInterval.addValidator(new IntegerValidator(""));
        refreshInterval.setValue(new Integer("10"));

        title = new TextField("Chart title");
        title.setWidth("100%");
        categoryAxisLabel = new TextField("Category Axis Label");
        categoryAxisLabel.setWidth("100%");
        valueAxisLabel = new TextField("Value Axis Label");
        valueAxisLabel.setWidth("100%");

        valueColumn = new NativeSelect("value");
        valueColumn.setWidth("100%");
        rowKey = new NativeSelect("rowKey");
        rowKey.setWidth("100%");
        columnKey = new NativeSelect("columnKey");
        columnKey.setWidth("100%");

        sqlText = new TextArea("SQL text");
        sqlText.setWidth("100%");
        sqlText.setRequired(true);
        sqlText.setRows(7);

        btnSave = new Button(ChartPortlet.getCurrent().messages.getString("btnSave"), this);
        btnTestSQL = new Button(ChartPortlet.getCurrent().messages.getString("btnTestSQL"), this);
        btnPreview = new Button(ChartPortlet.getCurrent().messages.getString("btnPreview"), this);

        buttons.addComponent(btnSave);
        buttons.addComponent(btnTestSQL);
        buttons.addComponent(btnPreview);

        addComponent(chartType, 0, 0);
        addComponent(orientation, 1, 0);
        addComponent(refreshInterval, 2, 0);

        addComponent(title, 0, 1);
        addComponent(categoryAxisLabel, 1, 1);
        addComponent(valueAxisLabel, 2, 1);

        addComponent(valueColumn, 0, 2);
        addComponent(rowKey, 1, 2);
        addComponent(columnKey, 2, 2);

        addComponent(sqlText, 0, 3, 2, 3);
        addComponent(buttons, 0, 5, 2, 5);

        setComponentAlignment(btnSave, Alignment.TOP_RIGHT);
        setMargin(true);
        setSpacing(true);

        try {
            portletPreferences = ChartPortlet.portletPreferences.get();
            for (String key : portletPreferences.getMap().keySet()) {
                String[] value = portletPreferences.getMap().get(key);
                if (key.equals("refreshInterval") && value.length > 0) {
                    refreshInterval.setValue(value[0]);
                } else if (key.equals("sqlText") && value.length > 0) {
                    sqlText.setValue(value[0]);
                } else if (key.equals("chartType") && value.length > 0) {
                    chartType.setValue(value[0]);
                } else if (key.equals("orientation") && value.length > 0) {
                    orientation.setValue(value[0]);
                } else if (key.equals("valueColumn") && value.length > 0) {
                    valueColumn.addItem(value[0]);
                    valueColumn.setValue(value[0]);
                } else if (key.equals("rowKey") && value.length > 0) {
                    rowKey.addItem(value[0]);
                    rowKey.setValue(value[0]);
                } else if (key.equals("columnKey") && value.length > 0) {
                    columnKey.addItem(value[0]);
                    columnKey.setValue(value[0]);
                } else if (key.equals("title") && value.length > 0) {
                    title.setValue(value[0]);
                } else if (key.equals("categoryAxisLabel") && value.length > 0) {
                    categoryAxisLabel.setValue(value[0]);
                } else if (key.equals("valueAxisLabel") && value.length > 0) {
                    valueAxisLabel.setValue(value[0]);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void buttonClick(ClickEvent event) {
        try {
            chartType.commit();
            sqlText.commit();
            refreshInterval.commit();
            valueColumn.commit();
            rowKey.commit();
            columnKey.commit();
            title.commit();
            categoryAxisLabel.commit();
            valueAxisLabel.commit();
            if (event.getButton().equals(btnSave)) {
                portletPreferences = ChartPortlet.portletPreferences.get();
                portletPreferences.setValue("chartType", chartType.getValue().toString());
                portletPreferences.setValue("orientation", orientation.getValue().toString());
                portletPreferences.setValue("refreshInterval", refreshInterval.getValue().toString());
                portletPreferences.setValue("valueColumn", valueColumn.getValue().toString());
                portletPreferences.setValue("rowKey", rowKey.getValue().toString());
                portletPreferences.setValue("columnKey", columnKey.getValue().toString());
                portletPreferences.setValue("title", title.getValue().toString());
                portletPreferences.setValue("categoryAxisLabel", categoryAxisLabel.getValue().toString());
                portletPreferences.setValue("valueAxisLabel", valueAxisLabel.getValue().toString());
                portletPreferences.setValue("sqlText", sqlText.getValue().toString());
                portletPreferences.store();
            } else if (event.getButton().equals(btnTestSQL)) {
                testSQL();
            } else if (event.getButton().equals(btnPreview)) {
                previewChart();
            }
        } catch (Exception ex) {
            getWindow().showNotification(ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void testSQL() {
        Connection conn = null;
        try {
            conn = MessageController.newConnection();
            PreparedStatement ps = conn.prepareStatement(sqlText.getValue().toString());
            ps.execute();
            ResultSetMetaData rsm = ps.getMetaData();
            testTable = new Table();
            testTable.setWidth("100%");
            testTable.setPageLength(5);
            removeComponent(0, 4);
            addComponent(testTable, 0, 4, 2, 4);
            columns = new IndexedContainer();
            for (int i = 1; i <= rsm.getColumnCount(); i++) {
                testTable.addContainerProperty(rsm.getColumnName(i), String.class, null);
                Item item = columns.addItem(rsm.getColumnName(i));
            }
            configChartDataset();
            ResultSet rs = ps.getResultSet();
            while (rs.next()) {
                Item item = testTable.addItem(rs.getRow());
                for (Object column : testTable.getContainerPropertyIds()) {
                    item.getItemProperty(column).setValue(rs.getString(column.toString()));
                }
            }
            ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            getWindow().showNotification(ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                getWindow().showNotification(ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
            }
        }
    }

    private void configChartDataset() {
        String v1 = valueColumn.getValue() != null ? valueColumn.getValue().toString() : null;
        valueColumn.setContainerDataSource(columns);
        if (columns.containsId(v1)) {
            valueColumn.setValue(v1);
        }

        String v2 = rowKey.getValue() != null ? rowKey.getValue().toString() : null;
        rowKey.setContainerDataSource(columns);
        if (columns.containsId(v2)) {
            rowKey.setValue(v2);
        }

        String v3 = columnKey.getValue() != null ? columnKey.getValue().toString() : null;
        columnKey.setContainerDataSource(columns);
        if (columns.containsId(v3)) {
            columnKey.setValue(v3);
        }
    }

    private void previewChart() throws SQLException {
        CategoryDataset dataset = ChartViewPanel.getCategoryDataset(
                valueColumn.getValue().toString(),
                rowKey.getValue().toString(),
                columnKey.getValue().toString(),
                sqlText.getValue().toString());

        JFreeChart chart = ChartViewPanel.createchart(
                title.getValue().toString(),
                categoryAxisLabel.getValue().toString(),
                valueAxisLabel.getValue().toString(),
                dataset,
                orientation.getValue().toString());
        JFreeChartWrapper chartWrapper = new JFreeChartWrapper(chart);
        Window previewWindow = new Window();
        previewWindow.addComponent(chartWrapper);
        previewWindow.setModal(true);
        previewWindow.setWidth("90%");
        previewWindow.setHeight("90%");
        getWindow().addWindow(previewWindow);
    }

    
}
