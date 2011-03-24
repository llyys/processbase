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
package org.processbase.ui.generator;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.light.LightProcessDefinition;
import org.processbase.bpm.forms.XMLWidgetsDefinition;
import org.processbase.ui.Processbase;
import org.processbase.ui.template.TableLinkButton;

/**
 *
 * @author marat
 */
public class GeneratedTable extends Table
        implements Table.FooterClickListener, Button.ClickListener {

    private XMLWidgetsDefinition widgets;
    private TaskInstance task;
    private LightProcessDefinition processDef;
    protected List<String> columnHeaders = new ArrayList<String>();
    protected List<String> rowHeaders = new ArrayList<String>();
    protected List<List> values = new ArrayList<List>();
    protected List selectedValues = new ArrayList();
    protected int indexColumn = 0;

    public GeneratedTable(XMLWidgetsDefinition widgets, TaskInstance task, LightProcessDefinition processDef) {
        super();
        this.widgets = widgets;
        this.task = task;
        this.processDef = processDef;
        try {
            addStyleName("striped");
            if (widgets.getShowDisplayLabel()) {
                setCaption(widgets.getDisplayLabel());
            }
            setSelectable(widgets.getAllowSelection() && !widgets.getReadOnly());
            setMultiSelect(widgets.getSelectionModeIsMultiple() && !widgets.getReadOnly());
            if (widgets.getShowDisplayLabel()) {
                setCaption(widgets.getDisplayLabel());
            }
            setDescription(widgets.getTooltip() != null ? widgets.getTooltip() : "");

            if (task != null) {
                preparedForTask();
            } else if (task == null) {
                preparedForNewProcess();
            }

            for (String col : columnHeaders) {
                if (widgets.getType().equals("form:Table")) {
                    addContainerProperty(col, String.class, null, col, null, null);
                } else if (widgets.getType().equals("form:DynamicTable")) {
                    addContainerProperty(col, String.class, null, col, null, null);
                }
            }
            if (widgets.getType().equals("form:DynamicTable") && !widgets.getReadOnly()) {
                setEditable(true);
                if (widgets.getAllowAddRemoveRow()) {
                    setColumnFooter(columnHeaders.get(0), Processbase.getCurrent().messages.getString("addRow"));
                    setFooterVisible(true);
                    addListener((Table.FooterClickListener) this);
                    columnHeaders.add(Processbase.getCurrent().messages.getString("tableCaptionActions"));
                    addContainerProperty(Processbase.getCurrent().messages.getString("tableCaptionActions"), TableLinkButton.class, null, Processbase.getCurrent().messages.getString("tableCaptionActions"), null, null);
                }
            }
            setVisibleColumns(columnHeaders.toArray());

            if (!rowHeaders.isEmpty()) {
                setRowHeaderMode(Table.ROW_HEADER_MODE_EXPLICIT);
            }

            for (int z = 0; z < values.size(); z++) {
                List row = values.get(z);
                Object id = row.get(indexColumn);
                Item woItem = addItem(id);
                for (int i = 0; i < columnHeaders.size(); i++) {
                    if (widgets.getType().equals("form:Table")) {
                        woItem.getItemProperty(columnHeaders.get(i)).setValue(row.get(i));
                    } else if (widgets.getType().equals("form:DynamicTable")) {
                        if (columnHeaders.get(i).equals(Processbase.getCurrent().messages.getString("tableCaptionActions"))) {
                            TableLinkButton execBtn = new TableLinkButton(Processbase.getCurrent().messages.getString("btnDelete"), "icons/cancel.png", id, this, "DELETE");
                            woItem.getItemProperty(Processbase.getCurrent().messages.getString("tableCaptionActions")).setValue(execBtn);
                        } else {
                            woItem.getItemProperty(columnHeaders.get(i)).setValue(row.get(i));
                        }
                    }
                    if (!rowHeaders.isEmpty()) {
                        setItemCaption(row.get(indexColumn), rowHeaders.get(z));
                    }
                }
            }
            if (!selectedValues.isEmpty()) {
                setValue(selectedValues);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(GeneratedTable.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    public Object getTableValue() {
        if (widgets.getType().equals("form:Table")) {
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

    private void preparedForTask() throws Exception {
        if (widgets.getMaxRowForPagination() != null) {
            setPageLength(Integer.valueOf((String) Processbase.getCurrent().bpmModule.evaluateExpression(widgets.getMaxRowForPagination(), task, true)));
        } else {
            setPageLength(5);
        }
        if (widgets.getColumnForInitialSelectionIndex() != null) {
            indexColumn = Integer.valueOf((String) Processbase.getCurrent().bpmModule.evaluateExpression(widgets.getColumnForInitialSelectionIndex(), task, true));
        }
        if (widgets.getInputWidth() != null) {
            setWidth(widgets.getInputWidth());
        }
        if (widgets.getHorizontalHeader() != null) {
            columnHeaders.addAll((Collection<? extends String>) Processbase.getCurrent().bpmModule.evaluateExpression(widgets.getHorizontalHeader(), task, true));
        }
        if (widgets.getVerticalHeader() != null) {
            rowHeaders.addAll((Collection<? extends String>) Processbase.getCurrent().bpmModule.evaluateExpression(widgets.getVerticalHeader(), task, true));
        }
        if (widgets.getInputScript() != null) {
            Collection v = (Collection) Processbase.getCurrent().bpmModule.evaluateExpression(widgets.getInputScript(), task, true);
            values.addAll(v != null ? v : new ArrayList());
        }
        if (widgets.getSelectedValues() != null) {
            Collection sv = (Collection) Processbase.getCurrent().bpmModule.evaluateExpression(widgets.getSelectedValues(), task, true);
            selectedValues.addAll(sv != null ? sv : new ArrayList());
        }
    }

    private void preparedForNewProcess() throws Exception {
        if (widgets.getMaxRowForPagination() != null) {
            setPageLength(Integer.valueOf((String) Processbase.getCurrent().bpmModule.evaluateExpression(widgets.getMaxRowForPagination(), task, true)));
        } else {
            setPageLength(5);
        }
        if (widgets.getColumnForInitialSelectionIndex() != null) {
            indexColumn = Integer.valueOf((String) Processbase.getCurrent().bpmModule.evaluateExpression(widgets.getColumnForInitialSelectionIndex(), task, true));
        }
        if (widgets.getInputWidth() != null) {
            setWidth(widgets.getInputWidth());
        }
        if (widgets.getHorizontalHeader() != null) {
            columnHeaders.addAll((Collection<? extends String>) Processbase.getCurrent().bpmModule.evaluateExpression(widgets.getHorizontalHeader(), processDef.getUUID()));
        }
        if (widgets.getInputScript() != null) {
            Object v = Processbase.getCurrent().bpmModule.evaluateExpression(widgets.getInputScript(), processDef.getUUID());
            values = v != null ? (List<List>) v : new ArrayList<List>();
        }
        if (widgets.getSelectedValues() != null) {
            Object sv = Processbase.getCurrent().bpmModule.evaluateExpression(widgets.getSelectedValues(), processDef.getUUID());
            selectedValues = sv != null ? (List) sv : new ArrayList();
        }
    }

    public void footerClick(FooterClickEvent event) {
        Object id = Math.random();
        Item woItem = addItem(id);
        for (int i = 0; i < columnHeaders.size(); i++) {
            if (columnHeaders.get(i).equals(Processbase.getCurrent().messages.getString("tableCaptionActions"))) {
                TableLinkButton execBtn = new TableLinkButton(Processbase.getCurrent().messages.getString("btnDelete"), "icons/cancel.png", id, this, "DELETE");
                woItem.getItemProperty(Processbase.getCurrent().messages.getString("tableCaptionActions")).setValue(execBtn);
            } else {
                woItem.getItemProperty(columnHeaders.get(i)).setValue("");
            }
        }
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton() instanceof TableLinkButton) {
            TableLinkButton tlb = (TableLinkButton) event.getButton();
            removeItem(tlb.getTableValue());
        }
    }
}
