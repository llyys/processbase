
/**
 * Copyright (C) 2011 PROCESSBASE Ltd.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, see <http://www.gnu.org/licenses/>.
 */
package org.processbase.ui.bpm.generator;

import com.vaadin.data.Validator;
import java.io.Serializable;
import java.util.Locale;
import org.bonitasoft.forms.client.model.FormFieldValue;
import org.bonitasoft.forms.server.validator.*;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.util.GroovyExpression;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.bonita.forms.Widget;

/**
 *
 * @author marat
 */
public class GeneratedValidator implements Validator {

    private Widget widget;
    private TaskInstance task;
    private LightProcessDefinition processDef;
    private Locale locale;
    private BPMModule bpmModule;
    private String validatorLabel = null;

    public GeneratedValidator(Widget widget, TaskInstance task, LightProcessDefinition processDef, Locale locale, BPMModule bpmModule) {
        this.widget = widget;
        this.task = task;
        this.processDef = processDef;
        this.locale = locale;
        this.bpmModule = bpmModule;
    }

    public void validate(Object value) throws InvalidValueException {
        try {
            if (!isValid(value)) {
                if (GroovyExpression.isGroovyExpression(validatorLabel)) {
                    if (task != null) {
                        throw new InvalidValueException((String) bpmModule.evaluateExpression(validatorLabel, task, true));
                    } else {
                        throw new InvalidValueException((String) bpmModule.evaluateExpression(validatorLabel, processDef.getUUID()));
                    }
                } else {
                    throw new InvalidValueException(validatorLabel);
                }
            }
        } catch (Exception ex) {
//            ex.printStackTrace();
            throw new InvalidValueException(ex.getMessage());
        }
    }

    public boolean isValid(Object value) {
        for (org.processbase.ui.core.bonita.forms.Validators.Validator val : widget.getValidators().getValidators()) {
            IFormFieldValidator validator = null;
            if (val.getClassname().equals("org.bonitasoft.forms.server.validator.LengthValidator")) {
                validator = new LengthValidator();
            } else if (val.getClassname().equals("org.bonitasoft.forms.server.validator.CharFieldValidator")) {
                validator = new CharFieldValidator();
            } else if (val.getClassname().equals("org.bonitasoft.forms.server.validator.DateFieldValidator")) {
                validator = new DateFieldValidator();
            } else if (val.getClassname().equals("org.bonitasoft.forms.server.validator.MailValidator")) {
                validator = new MailValidator();
            } else if (val.getClassname().equals("org.bonitasoft.forms.server.validator.NumericDoubleFieldValidator")) {
                validator = new NumericDoubleFieldValidator();
            } else if (val.getClassname().equals("org.bonitasoft.forms.server.validator.NumericFloatFieldValidator")) {
                validator = new NumericFloatFieldValidator();
            } else if (val.getClassname().equals("org.bonitasoft.forms.server.validator.NumericIntegerFieldValidator")) {
                validator = new NumericIntegerFieldValidator();
            } else if (val.getClassname().equals("org.bonitasoft.forms.server.validator.NumericLongFieldValidator")) {
                validator = new NumericLongFieldValidator();
            } else if (val.getClassname().equals("org.bonitasoft.forms.server.validator.NumericShortFieldValidator")) {
                validator = new NumericShortFieldValidator();
            } else if (val.getClassname().equals("org.bonitasoft.forms.server.validator.PhoneNumberValidator")) {
                validator = new PhoneNumberValidator();
            } else if (val.getClassname().equals("org.bonitasoft.forms.server.validator.RegexFieldValidator")) {
                validator = new RegexFieldValidator();
            }
            validatorLabel = val.getLabel();
            FormFieldValue ffv = null;
            if (validator instanceof RegexFieldValidator) {
                ffv = new FormFieldValue((Serializable) value, null);
            } else {
                ffv = new FormFieldValue((Serializable) value, null, val.getRegex());
            }

            if (!validator.validate(ffv, locale)) {
                return false;
            }
        }
        return true;
    }
}
