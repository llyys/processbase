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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.processbase.ui.core.bonita.forms.SelectMode;
import org.processbase.ui.core.bonita.forms.Widget;
import org.processbase.ui.core.bonita.forms.WidgetType;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.AbstractSelect.MultiSelectMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * {@link WidgetType#EDITABLE_GRID}
 * 
 * @author Margo
 */
public class GeneratedTable extends GridLayout implements Button.ClickListener {

	/** Serial version UID */
	private static final long serialVersionUID = -4002285075490988259L;

	private static final Logger LOG = Logger.getLogger(GeneratedTable.class);

	private Widget widget;

	private List<String> horizontalHeaders = new ArrayList<String>();
	private List<String> verticalHeaders = new ArrayList<String>();

	private Table table;

	private Button addRow;
	private Button removeRow;

	private Button addColumn;
	private Button removeColumn;

	private int minRows = 0;
	private int maxRows = -1;

	private int minColumns = 0;
	private int maxColumns = -1;

	private int valueColumn = 0;

	@SuppressWarnings("unchecked")
	public GeneratedTable(Widget widget, Object value,
			Map<String, Object> values) {
		super();
		this.widget = widget;
		setColumns(2);
		setRows(2);

		table = new Table();
		addComponent(table, 0, 0);

		setColumnExpandRatio(0, 1);
		setRowExpandRatio(0, 1);
		setSpacing(true);

		if (widget.getType().equals(WidgetType.EDITABLE_GRID)) {

			// Minimums and maximums
			if (widget.getMinRows() != null) {
				Object t = values.get(widget.getMinRows());
				try {
					minRows = Integer.parseInt(t.toString());
				} catch (Exception e) {
					LOG.warn("could not parse " + t + "(" + e.getMessage()
							+ ")");
				}
			}
			if (widget.getMaxRows() != null) {
				Object t = values.get(widget.getMaxRows());
				try {
					maxRows = Integer.parseInt(t.toString());
				} catch (Exception e) {
					LOG.warn("could not parse " + t + "(" + e.getMessage()
							+ ")");
				}
			}
			if (widget.getMinColumns() != null) {
				Object t = values.get(widget.getMinColumns());
				try {
					minColumns = Integer.parseInt(t.toString());
				} catch (Exception e) {
					LOG.warn("could not parse " + t + "(" + e.getMessage()
							+ ")");
				}
			}
			if (widget.getMaxColumns() != null) {
				Object t = values.get(widget.getMaxColumns());
				try {
					maxColumns = Integer.parseInt(t.toString());
				} catch (Exception e) {
					LOG.warn("could not parse " + t + "(" + e.getMessage()
							+ ")");
				}
			}
			if (minRows < 0) {
				minRows = 0;
			}
			if (minColumns < 0) {
				minColumns = 0;
			}

			// ---

			// Row buttons
			HorizontalLayout rowButtons = new HorizontalLayout();
			rowButtons.setSpacing(true);
			addComponent(rowButtons, 0, 1);

			addRow = new Button("+");
			addRow.setVisible(false);
			addRow.addListener(this);
			rowButtons.addComponent(addRow);

			removeRow = new Button("-");
			removeRow.setVisible(false);
			removeRow.addListener(this);
			rowButtons.addComponent(removeRow);

			// ---

			// Column buttons
			VerticalLayout colButtons = new VerticalLayout();
			colButtons.setSpacing(true);
			addComponent(colButtons, 1, 0);

			addColumn = new Button("+");
			addColumn.setVisible(false);
			addColumn.addListener(this);
			colButtons.addComponent(addColumn);

			removeColumn = new Button("-");
			removeColumn.setVisible(false);
			removeColumn.addListener(this);
			colButtons.addComponent(removeColumn);

			// ---

		} else if (WidgetType.TABLE.equals(widget.getType())) {
			if (widget.getValueColumnIndex() != null) {
				Object t = values.get(widget.getValueColumnIndex());
				try {
					valueColumn = Integer.parseInt(t.toString());
				} catch (Exception e) {
					LOG.warn("could not parse " + t + "(" + e.getMessage()
							+ ")");
				}
			}
		}

		// table.setSizeFull();
		table.setPageLength(15);
		table.addStyleName("striped");
		table.setCaption(widget.getLabel());
		table.setDescription(widget.getTitle() != null ? widget.getTitle() : "");

		// Disable sort
		table.setSortDisabled(true);

		// Horizontal headers
		Object tmp = values.get(widget.getHorizontalHeader());
		if (tmp != null && tmp instanceof List) {
			horizontalHeaders = (List<String>) tmp;
		}

		// Vertical headers
		tmp = values.get(widget.getVerticalHeader());
		if (tmp != null && tmp instanceof List) {
			verticalHeaders = (List<String>) tmp;
		}

		if (!verticalHeaders.isEmpty()) {
			if (verticalHeaders.size() > 0) {
				horizontalHeaders.remove(0);
			}
			table.setRowHeaderMode(Table.ROW_HEADER_MODE_EXPLICIT);
		}

		if (widget.getType().equals(WidgetType.EDITABLE_GRID)) {
			table.setEditable(!widget.isReadonly());
		} else {
			table.setEditable(false);

			if (widget.getSelectMode() != null) {
				if (SelectMode.SINGLE.equals(widget.getSelectMode())) {
					table.setSelectable(true);
				} else if (SelectMode.MULTIPLE.equals(widget.getSelectMode())) {
					table.setSelectable(true);
					table.setMultiSelect(true);
				}
				table.setImmediate(true);
			}
		}

		// Find columns count
		int maxCols = 0;
		if (value != null && value instanceof List) {
			List<Object> rows = (List<Object>) value;
			for (int r = 0; r < rows.size(); r++) {
				if (rows.get(r) instanceof List) {
					List<Object> columns = (List<Object>) rows.get(r);
					if (columns.size() > maxCols) {
						maxCols = columns.size();
					}
				}
			}
		}

		// Create columns
		for (int i = 0; i < maxCols; i++) {
			if (horizontalHeaders.size() > i) {
				table.addContainerProperty(i, String.class, "",
						horizontalHeaders.get(i), null, null);
			} else {
				table.addContainerProperty(i, String.class, "", "", null, null);
			}
		}

		// Set values
		if (value != null && value instanceof List) {

			List<Object> rows = (List<Object>) value;
			for (int r = 0; r < rows.size(); r++) {

				if (rows.get(r) instanceof List) {
					List<Object> columns = (List<Object>) rows.get(r);

					Item item = table.addItem(r);

					for (int c = 0; c < columns.size(); c++) {
						Property p = item.getItemProperty(c);
						if (p != null) {
							p.setValue(columns.get(c));
						}
					}

					if (verticalHeaders.size() > r) {
						table.setItemCaption(r, verticalHeaders.get(r));
					}
				}
			}
		}

		if (widget.getType().equals(WidgetType.EDITABLE_GRID)) {

			if (!widget.isReadonly()) {
				if (widget.isVariableRows()) {
					int rowCount = table.getContainerDataSource().size();
					if (rowCount > minRows) {
						removeRow.setVisible(true);
					} else {
						removeRow.setVisible(false);
					}
					if (rowCount < maxRows || maxRows == -1) {
						addRow.setVisible(true);
					} else {
						addRow.setVisible(false);
					}
				}
				if (widget.isVariableColumns()) {
					int colCount = table.getContainerPropertyIds().size();
					if (colCount > minColumns) {
						removeColumn.setVisible(true);
					} else {
						removeColumn.setVisible(false);
					}
					if (colCount < maxColumns || maxColumns == -1) {
						addColumn.setVisible(true);
					} else {
						addColumn.setVisible(false);
					}

				}
			}
		} else if (WidgetType.TABLE.equals(widget.getType())) {

			Object selected = values.get(widget.getVariableBound());
			if (selected == null && widget.getInitialValue() != null) {
				selected = values.get(widget.getInitialValue().getExpression());
			}

			if(selected == null){
				//Select first
				for (Object id : table.getContainerDataSource()
						.getItemIds()) {
					Item item = table.getItem(id);
					Property p = item.getItemProperty(valueColumn);
					selected = p.getValue();
					break;
				}
			}
			
			if (selected != null) {
				Set<Object> set = new HashSet<Object>();

				if (SelectMode.SINGLE.equals(widget.getSelectMode())) {
					set.add(selected);

					for (Object s : set) {
						for (Object id : table.getContainerDataSource()
								.getItemIds()) {
							Item item = table.getItem(id);
							Property p = item.getItemProperty(valueColumn);
							if (s != null
									&& p != null
									&& s.toString().equals(
											p.getValue().toString())) {
								table.setValue(id);
								break;
							}
						}
					}
				} else if (SelectMode.MULTIPLE.equals(widget.getSelectMode())) {
					if (selected instanceof Collection) {
						set.addAll((Collection<Object>) selected);
					} else {
						set.add(selected);
					}

					Set<Object> tableValue = new HashSet<Object>();

					for (Object s : set) {
						for (Object id : table.getContainerDataSource()
								.getItemIds()) {
							Item item = table.getItem(id);
							Property p = item.getItemProperty(valueColumn);
							if (s != null
									&& p != null
									&& s.toString().equals(
											p.getValue().toString())) {
								tableValue.add(id);
								break;
							}
						}
					}

					table.setValue(tableValue);
					table.setMultiSelectMode(MultiSelectMode.SIMPLE);
				}

			}
		}

	}

	/**
	 * Get table value.
	 * 
	 * @return value.
	 */
	public Object getTableValue() {
		if (WidgetType.EDITABLE_GRID.equals(widget.getType())) {

			List<Object> result = new ArrayList<Object>();
			for (Object id : table.getContainerDataSource().getItemIds()) {
				List<Object> row = new ArrayList<Object>();
				for (Object p : table.getContainerPropertyIds()) {
					row.add(table.getItem(id).getItemProperty(p).getValue()
							.toString());
				}
				result.add(row);
			}
			return result;

		} else if (WidgetType.TABLE.equals(widget.getType())) {

			if (SelectMode.SINGLE.equals(widget.getSelectMode())) {
				Object id = table.getValue();
				if (id != null) {
					Item item = table.getItem(id);
					if (item != null) {
						Property p = item.getItemProperty(valueColumn);
						if (p != null) {
							return p.getValue();
						}
					}
				}
			} else if (SelectMode.MULTIPLE.equals(widget.getSelectMode())) {
				Collection<Object> ids = (Collection<Object>) table.getValue();
				if (ids != null) {
					List<Object> values = new ArrayList<Object>();
					for (Object id : ids) {
						Item item = table.getItem(id);
						if (item != null) {
							Property p = item.getItemProperty(valueColumn);
							if (p != null) {
								values.add(p.getValue());
							}
						}
					}
					return values;
				}
			}
		}
		return null;
	}

	@Override
	public void buttonClick(ClickEvent event) {

		if (widget.getType().equals(WidgetType.EDITABLE_GRID)) {

			int rowCount = table.getContainerDataSource().size();
			int colCount = table.getContainerPropertyIds().size();

			if (addRow.equals(event.getButton())) {
				table.addItem(rowCount);
			} else if (removeRow.equals(event.getButton())) {
				table.removeItem(rowCount - 1);
			} else if (addColumn.equals(event.getButton())) {
				table.addContainerProperty(colCount, String.class, "", "",
						null, null);
				if (verticalHeaders.size() > colCount) {
					table.setItemCaption(colCount,
							verticalHeaders.get(colCount));
				}
			} else if (removeColumn.equals(event.getButton())) {
				table.removeContainerProperty(colCount - 1);
			}

			if (rowCount < maxRows || maxRows == -1) {
				addRow.setEnabled(true);
			} else {
				addRow.setEnabled(false);
			}
			if (rowCount > minRows) {
				removeRow.setEnabled(true);
			} else {
				removeRow.setEnabled(false);
			}

			if (colCount < maxColumns || maxColumns == -1) {
				addColumn.setVisible(true);
			} else {
				addColumn.setVisible(false);
			}
			if (colCount > minColumns) {
				removeColumn.setVisible(true);
			} else {
				removeColumn.setVisible(false);
			}
		}
	}

}
