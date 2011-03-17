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
package org.processbase.bpm.forms;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marat
 */
public class XMLWidgetsDefinition {

    String type;
    String id;
    String name;
    String label;
    String documentation;
    String tooltip;
    String displayLabel;
    String realHtmlAttributes;
    Boolean mandatory = Boolean.FALSE;
    Boolean readOnly = Boolean.FALSE;
    String exprScript;
    String inputScript;
    String setVarScript;
    Integer line = new Integer(0);
    Integer column = new Integer(0);
    Integer horizontalSpan = new Integer(0);
    String validatorClass;
    String validatorLabel;
    String validatorName;
    String validatorParameter;
    String widgetWidth;
    String widgetHeight;
    String inputWidth;
    String inputHeight;
    String defaultValue;
    String setVar;
    String enum1;
    Boolean showDisplayLabel = Boolean.TRUE;
    Boolean labelBehavior = Boolean.FALSE;
    // Table and Grid specific properties
    Boolean firstRowIsHeader = Boolean.TRUE;
    Boolean leftColumnIsHeader = Boolean.FALSE;
    Boolean lastRowIsHeader = Boolean.FALSE;
    Boolean rightColumnIsHeader = Boolean.FALSE;
    String cells;
    String horizontalHeader;
    String verticalHeader;
    Boolean allowSelection = Boolean.FALSE;
    Boolean selectionModeIsMultiple = Boolean.TRUE;
    String selectedValues;
    String columnForInitialSelectionIndex;
    String maxRowForPagination;
    Boolean limitMinNumberOfRow = Boolean.FALSE;
    String minNumberOfRow;
    Boolean limitMaxNumberOfRow = Boolean.FALSE;
    String maxNumberOfRow;
    Boolean limitMinNumberOfColumn = Boolean.FALSE;
    String minNumberOfColumn;
    Boolean limitMaxNumberOfColumn = Boolean.FALSE;
    String maxNumberOfColumn;
    Boolean allowAddRemoveColumn = Boolean.TRUE;
    Boolean allowAddRemoveRow = Boolean.TRUE;
    // Button action
    List<XMLActionDefinition> actions = null;

    public XMLWidgetsDefinition() {
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public void setDisplayLabel(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public String getExprScript() {
        return exprScript;
    }

    public void setExprScript(String exprScript) {
        this.exprScript = exprScript;
    }

    public Integer getHorizontalSpan() {
        return horizontalSpan;
    }

    public void setHorizontalSpan(Integer horizontalSpan) {
        this.horizontalSpan = horizontalSpan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInputScript() {
        return inputScript;
    }

    public void setInputScript(String inputScript) {
        this.inputScript = inputScript;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getRealHtmlAttributes() {
        return realHtmlAttributes;
    }

    public void setRealHtmlAttributes(String realHtmlAttributes) {
        this.realHtmlAttributes = realHtmlAttributes;
    }

    public String getSetVarScript() {
        return setVarScript;
    }

    public void setSetVarScript(String setVarScript) {
        this.setVarScript = setVarScript;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValidatorClass() {
        return validatorClass;
    }

    public void setValidatorClass(String validatorClass) {
        this.validatorClass = validatorClass;
    }

    public String getValidatorLabel() {
        return validatorLabel;
    }

    public void setValidatorLabel(String validatorLabel) {
        this.validatorLabel = validatorLabel;
    }

    public String getValidatorName() {
        return validatorName;
    }

    public void setValidatorName(String validatorName) {
        this.validatorName = validatorName;
    }

    public String getValidatorParameter() {
        return validatorParameter;
    }

    public void setValidatorParameter(String validatorParameter) {
        this.validatorParameter = validatorParameter;
    }

    public String getInputHeight() {
        return inputHeight;
    }

    public void setInputHeight(String inputHeight) {
        this.inputHeight = inputHeight;
    }

    public String getInputWidth() {
        return inputWidth;
    }

    public void setInputWidth(String inputWidth) {
        this.inputWidth = inputWidth;
    }

    public String getWidgetHeight() {
        return widgetHeight;
    }

    public void setWidgetHeight(String widgetHeight) {
        this.widgetHeight = widgetHeight;
    }

    public String getWidgetWidth() {
        return widgetWidth;
    }

    public void setWidgetWidth(String widgetWidth) {
        this.widgetWidth = widgetWidth;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getSetVar() {
        return setVar;
    }

    public void setSetVar(String setVar) {
        this.setVar = setVar;
    }

    public String getEnum1() {
        return enum1;
    }

    public void setEnum1(String enum1) {
        this.enum1 = enum1;
    }

    public Boolean isLabelBehavior() {
        return labelBehavior;
    }

    public void setLabelBehavior(Boolean labelBehavior) {
        this.labelBehavior = labelBehavior;
    }

    public Boolean getAllowSelection() {
        return allowSelection;
    }

    public void setAllowSelection(Boolean allowSelection) {
        this.allowSelection = allowSelection;
    }

    public String getCells() {
        return cells;
    }

    public void setCells(String cells) {
        this.cells = cells;
    }

    public String getColumnForInitialSelectionIndex() {
        return columnForInitialSelectionIndex;
    }

    public void setColumnForInitialSelectionIndex(String columnForInitialSelectionIndex) {
        this.columnForInitialSelectionIndex = columnForInitialSelectionIndex;
    }

    public Boolean getFirstRowIsHeader() {
        return firstRowIsHeader;
    }

    public void setFirstRowIsHeader(Boolean firstRowIsHeader) {
        this.firstRowIsHeader = firstRowIsHeader;
    }

    public String getHorizontalHeader() {
        return horizontalHeader;
    }

    public void setHorizontalHeader(String horizontalHeader) {
        this.horizontalHeader = horizontalHeader;
    }

    public Boolean getLastRowIsHeader() {
        return lastRowIsHeader;
    }

    public void setLastRowIsHeader(Boolean lastRowIsHeader) {
        this.lastRowIsHeader = lastRowIsHeader;
    }

    public Boolean getLeftColumnIsHeader() {
        return leftColumnIsHeader;
    }

    public void setLeftColumnIsHeader(Boolean leftColumnIsHeader) {
        this.leftColumnIsHeader = leftColumnIsHeader;
    }

    public Boolean getLimitMaxNumberOfColumn() {
        return limitMaxNumberOfColumn;
    }

    public void setLimitMaxNumberOfColumn(Boolean limitMaxNumberOfColumn) {
        this.limitMaxNumberOfColumn = limitMaxNumberOfColumn;
    }

    public Boolean getLimitMaxNumberOfRow() {
        return limitMaxNumberOfRow;
    }

    public void setLimitMaxNumberOfRow(Boolean limitMaxNumberOfRow) {
        this.limitMaxNumberOfRow = limitMaxNumberOfRow;
    }

    public Boolean getLimitMinNumberOfColumn() {
        return limitMinNumberOfColumn;
    }

    public void setLimitMinNumberOfColumn(Boolean limitMinNumberOfColumn) {
        this.limitMinNumberOfColumn = limitMinNumberOfColumn;
    }

    public Boolean getLimitMinNumberOfRow() {
        return limitMinNumberOfRow;
    }

    public void setLimitMinNumberOfRow(Boolean limitMinNumberOfRow) {
        this.limitMinNumberOfRow = limitMinNumberOfRow;
    }

    public String getMaxNumberOfColumn() {
        return maxNumberOfColumn;
    }

    public void setMaxNumberOfColumn(String maxNumberOfColumn) {
        this.maxNumberOfColumn = maxNumberOfColumn;
    }

    public String getMaxNumberOfRow() {
        return maxNumberOfRow;
    }

    public void setMaxNumberOfRow(String maxNumberOfRow) {
        this.maxNumberOfRow = maxNumberOfRow;
    }

    public String getMaxRowForPagination() {
        return maxRowForPagination;
    }

    public void setMaxRowForPagination(String maxRowForPagination) {
        this.maxRowForPagination = maxRowForPagination;
    }

    public String getMinNumberOfColumn() {
        return minNumberOfColumn;
    }

    public void setMinNumberOfColumn(String minNumberOfColumn) {
        this.minNumberOfColumn = minNumberOfColumn;
    }

    public String getMinNumberOfRow() {
        return minNumberOfRow;
    }

    public void setMinNumberOfRow(String minNumberOfRow) {
        this.minNumberOfRow = minNumberOfRow;
    }

    public Boolean getRightColumnIsHeader() {
        return rightColumnIsHeader;
    }

    public void setRightColumnIsHeader(Boolean rightColumnIsHeader) {
        this.rightColumnIsHeader = rightColumnIsHeader;
    }

    public String getSelectedValues() {
        return selectedValues;
    }

    public void setSelectedValues(String selectedValues) {
        this.selectedValues = selectedValues;
    }

    public Boolean getSelectionModeIsMultiple() {
        return selectionModeIsMultiple;
    }

    public void setSelectionModeIsMultiple(Boolean selectionModeIsMultiple) {
        this.selectionModeIsMultiple = selectionModeIsMultiple;
    }

    public String getVerticalHeader() {
        return verticalHeader;
    }

    public void setVerticalHeader(String verticalHeader) {
        this.verticalHeader = verticalHeader;
    }

    public Boolean getShowDisplayLabel() {
        return showDisplayLabel;
    }

    public void setShowDisplayLabel(Boolean showDisplayLabel) {
        this.showDisplayLabel = showDisplayLabel;
    }

    public Boolean getAllowAddRemoveColumn() {
        return allowAddRemoveColumn;
    }

    public void setAllowAddRemoveColumn(Boolean allowAddRemoveColumn) {
        this.allowAddRemoveColumn = allowAddRemoveColumn;
    }

    public Boolean getAllowAddRemoveRow() {
        return allowAddRemoveRow;
    }

    public void setAllowAddRemoveRow(Boolean allowAddRemoveRow) {
        this.allowAddRemoveRow = allowAddRemoveRow;
    }

    public List<XMLActionDefinition> getActions() {
        if (actions == null) {
            actions = new ArrayList<XMLActionDefinition>();
        }
        return actions;
    }

    public void setActions(List<XMLActionDefinition> actions) {
        this.actions = actions;
    }

    public void addAction(XMLActionDefinition action) {
        this.getActions().add(action);
    }
}
