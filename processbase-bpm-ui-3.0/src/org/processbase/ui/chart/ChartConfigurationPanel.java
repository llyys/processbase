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
import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window.Notification;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import javax.portlet.PortletMode;
import org.processbase.bam.message.MessageController;
import org.processbase.ui.portlet.ChartPortlet;
import org.processbase.ui.template.ButtonBar;

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
    private NativeSelect legend = null;
    private TextField height = null;
    private TextField width = null;
    private TextField titleX = null;
    private TextField titleY = null;
    private TextField min = null;
    private TextField max = null;
    private CheckBox is3D = null;
    private CheckBox isStacked = null;
    private Button btnSave;
    private Button btnTestSQL;
    private Button btnView;
    private ButtonBar buttons = new ButtonBar();

    public ChartConfigurationPanel() {
        super(4, 7);
        setWidth("100%");
        chartType = new NativeSelect("Chart Type");
        chartType.addItem("BarChart");
        chartType.addItem("ColumnChart");
        chartType.addItem("PieChart");
        chartType.addItem("LineChart");
        chartType.addItem("AreaChart");
        chartType.addItem("Gauge");

        chartType.setWidth("100px");

        refreshInterval = new TextField(ChartPortlet.getCurrent().messages.getString("refreshInterval"));
        refreshInterval.setMaxLength(3);
        refreshInterval.setRequired(true);
        refreshInterval.addValidator(new IntegerValidator(ChartPortlet.getCurrent().messages.getString("refreshInterval") + " " + ChartPortlet.getCurrent().messages.getString("IntegerValidatorError")));
        refreshInterval.setValue(new Integer("10"));

        title = new TextField(ChartPortlet.getCurrent().messages.getString("title"));
        title.setWidth("100%");
        title.setRequired(true);

        legend = new NativeSelect(ChartPortlet.getCurrent().messages.getString("legend"));
        legend.setWidth("100%");
        legend.addItem("bottom");
        legend.addItem("top");
        legend.addItem("left");
        legend.addItem("right");

        titleX = new TextField(ChartPortlet.getCurrent().messages.getString("titleX"));
        titleX.setWidth("100%");
        titleX.setRequired(true);

        titleY = new TextField(ChartPortlet.getCurrent().messages.getString("titleY"));
        titleY.setWidth("100%");
        titleY.setRequired(true);

        height = new TextField(ChartPortlet.getCurrent().messages.getString("height"));
        height.setWidth("70px");
        height.addValidator(new IntegerValidator(ChartPortlet.getCurrent().messages.getString("height") + " " + ChartPortlet.getCurrent().messages.getString("IntegerValidatorError")));
        height.setRequired(true);

        width = new TextField(ChartPortlet.getCurrent().messages.getString("width"));
        width.setWidth("70px");
        width.addValidator(new IntegerValidator(ChartPortlet.getCurrent().messages.getString("width") + " " + ChartPortlet.getCurrent().messages.getString("IntegerValidatorError")));
        width.setRequired(true);

        min = new TextField(ChartPortlet.getCurrent().messages.getString("min"));
        min.setWidth("70px");
        min.addValidator(new IntegerValidator(ChartPortlet.getCurrent().messages.getString("min") + " " + ChartPortlet.getCurrent().messages.getString("IntegerValidatorError")));

        max = new TextField(ChartPortlet.getCurrent().messages.getString("max"));
        max.setWidth("70px");
        max.addValidator(new IntegerValidator(ChartPortlet.getCurrent().messages.getString("max") + " " + ChartPortlet.getCurrent().messages.getString("IntegerValidatorError")));

        sqlText = new TextArea(ChartPortlet.getCurrent().messages.getString("sqlText"));
        sqlText.setWidth("100%");
        sqlText.setRequired(true);
        sqlText.setRows(7);

        is3D = new CheckBox("3D");
        isStacked = new CheckBox("Stacked");

        btnSave = new Button(ChartPortlet.getCurrent().messages.getString("btnSave"), this);
        btnTestSQL = new Button(ChartPortlet.getCurrent().messages.getString("btnTestSQL"), this);
        btnView = new Button(ChartPortlet.getCurrent().messages.getString("btnView"), this);

        buttons.addComponent(btnSave);
        buttons.addComponent(btnTestSQL);
        buttons.addComponent(btnView);

        addComponent(chartType, 0, 0, 1, 0);
        addComponent(is3D, 2, 0);
        addComponent(isStacked, 3, 0);
        
        addComponent(title, 0, 1, 1, 1);
        addComponent(legend, 2, 1);
        addComponent(refreshInterval, 3, 1);

        addComponent(titleX, 0, 2, 1, 2);
        addComponent(titleY, 2, 2, 3, 2);

        addComponent(height, 0, 3);
        addComponent(width, 1, 3);
        addComponent(max, 2, 3);
        addComponent(min, 3, 3);

        addComponent(sqlText, 0, 4, 3, 4);
        addComponent(buttons, 0, 6, 3, 6);

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
                } else if (key.equals("title") && value.length > 0) {
                    title.setValue(value[0]);
                } else if (key.equals("legend") && value.length > 0) {
                    legend.setValue(value[0]);

                } else if (key.equals("height") && value.length > 0) {
                    height.setValue(value[0]);
                } else if (key.equals("width") && value.length > 0) {
                    width.setValue(value[0]);
                } else if (key.equals("min") && value.length > 0) {
                    min.setValue(value[0]);
                } else if (key.equals("max") && value.length > 0) {
                    max.setValue(value[0]);
                } else if (key.equals("titleX") && value.length > 0) {
                    titleX.setValue(value[0]);
                } else if (key.equals("titleY") && value.length > 0) {
                    titleY.setValue(value[0]);
                } else if (key.equals("isStacked") && value.length > 0) {
                    isStacked.setValue(Boolean.parseBoolean(value[0]));
                } else if (key.equals("is3D") && value.length > 0) {
                    is3D.setValue(Boolean.parseBoolean(value[0]));
                } else if (key.equals("legend") && value.length > 0) {
                    legend.setValue(value[0]);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void buttonClick(ClickEvent event) {
        try {
            commitFields();
            if (event.getButton().equals(btnSave)) {
                portletPreferences = ChartPortlet.portletPreferences.get();
                portletPreferences.setValue("chartType", chartType.getValue() != null ? chartType.getValue().toString() : "");
                portletPreferences.setValue("refreshInterval", refreshInterval.getValue() != null ? refreshInterval.getValue().toString() : "");
                portletPreferences.setValue("sqlText", sqlText.getValue() != null ? sqlText.getValue().toString() : "");
                portletPreferences.setValue("title", title.getValue() != null ? title.getValue().toString() : "");
                portletPreferences.setValue("legend", legend.getValue() != null ? legend.getValue().toString() : "");
                portletPreferences.setValue("height", height.getValue() != null ? height.getValue().toString() : "");
                portletPreferences.setValue("width", width.getValue() != null ? width.getValue().toString() : "");
                portletPreferences.setValue("titleX", titleX.getValue() != null ? titleX.getValue().toString() : "");
                portletPreferences.setValue("titleY", titleY.getValue() != null ? titleY.getValue().toString() : "");
                portletPreferences.setValue("min", min.getValue() != null ? min.getValue().toString() : "");
                portletPreferences.setValue("max", max.getValue() != null ? max.getValue().toString() : "");
                portletPreferences.setValue("is3D", is3D.getValue() != null ? is3D.getValue().toString() : "false");
                portletPreferences.setValue("isStacked", isStacked.getValue() != null ? isStacked.getValue().toString() : "false");
                portletPreferences.store();
                ChartPortlet.getCurrent().recreateChartView();
            } else if (event.getButton().equals(btnTestSQL)) {
                testSQL();
            } else if (event.getButton().equals(btnView)) {
                viewChart();
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
            removeComponent(0, 5);
            addComponent(testTable, 0, 5, 3, 5);
            for (int i = 1; i <= rsm.getColumnCount(); i++) {
                testTable.addContainerProperty(rsm.getColumnName(i), String.class, null);
            }
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

    private void viewChart() throws SQLException {
        try {
            ChartPortlet.getCurrent().portletApplicationContext2.setPortletMode(getWindow(), PortletMode.VIEW);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void commitFields() {
        for (Iterator<Component> iterator = this.getComponentIterator(); iterator.hasNext();) {
            Component comp = iterator.next();
            if (comp instanceof AbstractField) {
                try {
                    ((AbstractField) comp).setComponentError(null);
                    ((AbstractField) comp).validate();
                } catch (InvalidValueException ex) {
                    if (ex instanceof EmptyValueException) {
                        ((AbstractField) comp).setComponentError(new UserError(((AbstractField) comp).getRequiredError()));
                    }
                    throw ex;
                }

            }
        }
    }
}
