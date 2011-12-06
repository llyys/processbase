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
package org.processbase.ui.core.bonita.process;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.light.LightProcessDefinition;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.bonita.forms.Widget;
import org.processbase.ui.core.bonita.forms.WidgetType;
import org.processbase.ui.core.template.TableLinkButton;

/**
 *
 * @author marat
 */
public class GeneratedTable extends Table implements Table.FooterClickListener, Button.ClickListener {

    private Widget widget;
    protected List<String> columnHeaders = new ArrayList<String>();
    protected List<String> rowHeaders = new ArrayList<String>();
    protected List<List> values = new ArrayList<List>();
    protected List selectedValues = new ArrayList();
    protected int indexColumn = 0;
//

    public GeneratedTable(Widget widget, Object value, Map<String, Object> groovyScripts) {
        super();
        this.widget = widget;
        try {
            addStyleName("striped");
            setCaption(widget.getLabel());
//            setSelectable(widgets.getAllowSelection() && !widgets.getReadOnly());
//            setMultiSelect(widgets.getSelectionModeIsMultiple() && !widgets.getReadOnly());
            setDescription(widget.getTitle() != null ? widget.getTitle() : "");


//            if (task != null) {
//                preparedForTask();
//            } else if (task == null) {
//                preparedForNewProcess();
//            }

            if (groovyScripts.containsKey(widget.getHorizontalHeader()) && groovyScripts.get(widget.getHorizontalHeader()) instanceof List) {
                columnHeaders = (List<String>) groovyScripts.get(widget.getHorizontalHeader());
                for (String col : columnHeaders) {
                    addContainerProperty(col, String.class, null, col, null, null);
                }
            }


            if (widget.getType().equals(WidgetType.EDITABLE_GRID) && !widget.isReadonly()) {
                setEditable(true);
                if (widget.isVariableRows()) {
                    setColumnFooter(columnHeaders.get(0), ProcessbaseApplication.getCurrent().getPbMessages().getString("addRow"));
                    setFooterVisible(true);
                    addListener((Table.FooterClickListener) this);
                    columnHeaders.add(ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionActions"));
                    addContainerProperty(ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionActions"), TableLinkButton.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionActions"), null, null);
                }
            }
            setVisibleColumns(columnHeaders.toArray());

            if (!rowHeaders.isEmpty()) {
                setRowHeaderMode(Table.ROW_HEADER_MODE_EXPLICIT);
            }

            if (value != null && value instanceof List) {
                values = (List<List>) value;
                for (int z = 0; z < values.size(); z++) {
                    List row = values.get(z);
                    Object id = row.get(indexColumn);
                    Item woItem = addItem(id);
                    for (int i = 0; i < columnHeaders.size(); i++) {
                        if (widget.getType().equals(WidgetType.EDITABLE_GRID) && (i == columnHeaders.size() - 1)) {
                            TableLinkButton execBtn = new TableLinkButton(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnDelete"), "icons/cancel.png", id, this, "DELETE");
                            woItem.getItemProperty(ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionActions")).setValue(execBtn);
                        } else {
                            woItem.getItemProperty(columnHeaders.get(i)).setValue(row.get(i));
                        }
//                        if (widget.getType().equals(WidgetType.TABLE)) {
//                            woItem.getItemProperty(columnHeaders.get(i)).setValue(row.get(i));
//                        } else if (widget.getType().equals(WidgetType.EDITABLE_GRID)) {
//                            if (columnHeaders.get(i).equals(ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionActions"))) {
//                                TableLinkButton execBtn = new TableLinkButton(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnDelete"), "icons/cancel.png", id, this, "DELETE");
//                                woItem.getItemProperty(ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionActions")).setValue(execBtn);
//                            } else {
//                                woItem.getItemProperty(columnHeaders.get(i)).setValue(row.get(i));
//                            }
//                        }
                        if (!rowHeaders.isEmpty()) {
                            setItemCaption(row.get(indexColumn), rowHeaders.get(z));
                        }
                    }
                }
            }
            if (!selectedValues.isEmpty()) {
                setValue(selectedValues);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(GeneratedTable.class.getName()).log(Level.SEVERE, ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public Object getTableValue() {
        if (widget.getType().equals(WidgetType.TABLE)) {
            return getValue();
        } else {
            List result = new ArrayList(getContainerDataSource().getItemIds().size());
            for (Object id : getContainerDataSource().getItemIds()) {
                List row = new ArrayList(getContainerPropertyIds().size());
                for (Object prop : getContainerPropertyIds()) {
                    row.add(this.getItem(id).getItemProperty(prop).getValue().toString());
                }
                result.add(row);
            }
            return result;
        }
    }

//    private void preparedForTask() throws Exception {
//        if (widgets.getMaxRowForPagination() != null) {
//            setPageLength(Integer.valueOf((String) ProcessbaseApplication.getCurrent().getBpmModule().evaluateExpression(widgets.getMaxRowForPagination(), task, true)));
//        } else {
//            setPageLength(5);
//        }
//        if (widgets.getColumnForInitialSelectionIndex() != null) {
//            indexColumn = Integer.valueOf((String) ProcessbaseApplication.getCurrent().getBpmModule().evaluateExpression(widgets.getColumnForInitialSelectionIndex(), task, true));
//        }
//        if (widgets.getInputWidth() != null) {
//            setWidth(widgets.getInputWidth());
//        }
//        if (widgets.getHorizontalHeader() != null) {
//            columnHeaders.addAll((Collection<? extends String>) ProcessbaseApplication.getCurrent().getBpmModule().evaluateExpression(widgets.getHorizontalHeader(), task, true));
//        }
//        if (widgets.getVerticalHeader() != null) {
//            rowHeaders.addAll((Collection<? extends String>) ProcessbaseApplication.getCurrent().getBpmModule().evaluateExpression(widgets.getVerticalHeader(), task, true));
//        }
//        if (widgets.getInputScript() != null) {
//            Collection v = (Collection) ProcessbaseApplication.getCurrent().getBpmModule().evaluateExpression(widgets.getInputScript(), task, true);
//            values.addAll(v != null ? v : new ArrayList());
//        }
//        if (widgets.getSelectedValues() != null) {
//            Collection sv = (Collection) ProcessbaseApplication.getCurrent().getBpmModule().evaluateExpression(widgets.getSelectedValues(), task, true);
//            selectedValues.addAll(sv != null ? sv : new ArrayList());
//        }
//    }
//
//    private void preparedForNewProcess() throws Exception {
//        if (widgets.getMaxRowForPagination() != null) {
//            setPageLength(Integer.valueOf((String) ProcessbaseApplication.getCurrent().getBpmModule().evaluateExpression(widgets.getMaxRowForPagination(), task, true)));
//        } else {
//            setPageLength(5);
//        }
//        if (widgets.getColumnForInitialSelectionIndex() != null) {
//            indexColumn = Integer.valueOf((String) ProcessbaseApplication.getCurrent().getBpmModule().evaluateExpression(widgets.getColumnForInitialSelectionIndex(), task, true));
//        }
//        if (widgets.getInputWidth() != null) {
//            setWidth(widgets.getInputWidth());
//        }
//        if (widgets.getHorizontalHeader() != null) {
//            columnHeaders.addAll((Collection<? extends String>) ProcessbaseApplication.getCurrent().getBpmModule().evaluateExpression(widgets.getHorizontalHeader(), processDef.getUUID()));
//        }
//        if (widgets.getInputScript() != null) {
//            Object v = ProcessbaseApplication.getCurrent().getBpmModule().evaluateExpression(widgets.getInputScript(), processDef.getUUID());
//            values = v != null ? (List<List>) v : new ArrayList<List>();
//        }
//        if (widgets.getSelectedValues() != null) {
//            Object sv = ProcessbaseApplication.getCurrent().getBpmModule().evaluateExpression(widgets.getSelectedValues(), processDef.getUUID());
//            selectedValues = sv != null ? (List) sv : new ArrayList();
//        }
//    }
//
    public void footerClick(FooterClickEvent event) {
        Object id = Math.random();
        Item woItem = addItem(id);
        for (int i = 0; i < columnHeaders.size(); i++) {
            if (i == columnHeaders.size() - 1) {
                TableLinkButton execBtn = new TableLinkButton(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnDelete"), "icons/cancel.png", id, this, "DELETE");
                woItem.getItemProperty(ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionActions")).setValue(execBtn);
            } else {
                woItem.getItemProperty(columnHeaders.get(i)).setValue("");
            }
//            if (columnHeaders.get(i).equals(ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionActions"))) {
//                TableLinkButton execBtn = new TableLinkButton(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnDelete"), "icons/cancel.png", id, this, "DELETE");
//                woItem.getItemProperty(ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionActions")).setValue(execBtn);
//            } else {
//                woItem.getItemProperty(columnHeaders.get(i)).setValue("");
//            }
        }
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton() instanceof TableLinkButton) {
            TableLinkButton tlb = (TableLinkButton) event.getButton();
            removeItem(tlb.getTableValue());
        }
    }
}
