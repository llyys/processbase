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
    Integer line =  new Integer(0);
    Integer column =  new Integer(0);
    Integer horizontalSpan = new Integer(0);
    String validatorClass;
    String validatorLabel;
    String validatorName;
    String validatorParameter;
    String widgetWidth;
    String widgetHeight;
    String inputWidth;
    String inputHeight;

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

    

}
