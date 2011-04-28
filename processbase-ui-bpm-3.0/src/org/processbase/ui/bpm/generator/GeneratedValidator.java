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
package org.processbase.ui.bpm.generator;

import com.vaadin.data.Validator;
import java.io.Serializable;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bonitasoft.forms.client.model.FormFieldValue;
import org.bonitasoft.forms.server.validator.*;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.util.GroovyException;
import org.processbase.ui.core.BPMModule;
//import org.processbase.ui.core.bonita.forms.XMLWidgetsDefinition;

/**
 *
 * @author marat
 */
public class GeneratedValidator {
//implements Validator {

//    private XMLWidgetsDefinition widgets;
//    private TaskInstance task;
//    private LightProcessDefinition processDef;
//    private Locale locale;
//    private BPMModule bpmModule;
//
//    public GeneratedValidator(XMLWidgetsDefinition widgets, TaskInstance task, LightProcessDefinition processDef, Locale locale, BPMModule bpmModule) {
//        this.widgets = widgets;
//        this.task = task;
//        this.processDef = processDef;
//        this.locale = locale;
//        this.bpmModule = bpmModule;
//    }
//
//    public void validate(Object value) throws InvalidValueException {
//        try {
//            if (!isValid(value)) {
//                if (task != null) {
//                    throw new InvalidValueException((String) bpmModule.evaluateExpression(widgets.getValidatorLabel(), task, true));
//                } else {
//                    throw new InvalidValueException((String) bpmModule.evaluateExpression(widgets.getValidatorLabel(), processDef.getUUID()));
//                }
//            }
//        } catch (InstanceNotFoundException ex) {
//            Logger.getLogger(GeneratedValidator.class.getName()).log(Level.SEVERE, ex.getMessage());
//            throw new InvalidValueException(ex.getMessage());
//        } catch (GroovyException ex) {
//            Logger.getLogger(GeneratedValidator.class.getName()).log(Level.SEVERE, ex.getMessage());
//            throw new InvalidValueException(ex.getMessage());
//        } catch (Exception ex) {
//            Logger.getLogger(GeneratedValidator.class.getName()).log(Level.SEVERE, ex.getMessage());
//            throw new InvalidValueException(ex.getMessage());
//        }
//    }
//
//    public boolean isValid(Object value) {
//        IFormFieldValidator validator = null;
//        if (widgets.getValidatorClass().equals("org.bonitasoft.forms.server.validator.Length4Validator")) {
//            validator = new Length4Validator();
//        } else if (widgets.getValidatorClass().equals("org.bonitasoft.forms.server.validator.CharFieldValidator")) {
//            validator = new CharFieldValidator();
//        } else if (widgets.getValidatorClass().equals("org.bonitasoft.forms.server.validator.DateFieldValidator")) {
//            validator = new DateFieldValidator();
//        } else if (widgets.getValidatorClass().equals("org.bonitasoft.forms.server.validator.MailValidator")) {
//            validator = new MailValidator();
//        } else if (widgets.getValidatorClass().equals("org.bonitasoft.forms.server.validator.NumericDoubleFieldValidator")) {
//            validator = new NumericDoubleFieldValidator();
//        } else if (widgets.getValidatorClass().equals("org.bonitasoft.forms.server.validator.NumericFloatFieldValidator")) {
//            validator = new NumericFloatFieldValidator();
//        } else if (widgets.getValidatorClass().equals("org.bonitasoft.forms.server.validator.NumericIntegerFieldValidator")) {
//            validator = new NumericIntegerFieldValidator();
//        } else if (widgets.getValidatorClass().equals("org.bonitasoft.forms.server.validator.NumericLongFieldValidator")) {
//            validator = new NumericLongFieldValidator();
//        } else if (widgets.getValidatorClass().equals("org.bonitasoft.forms.server.validator.NumericShortFieldValidator")) {
//            validator = new NumericShortFieldValidator();
//        } else if (widgets.getValidatorClass().equals("org.bonitasoft.forms.server.validator.PhoneNumberValidator")) {
//            validator = new PhoneNumberValidator();
//        } else if (widgets.getValidatorClass().equals("org.bonitasoft.forms.server.validator.RegexFieldValidator")) {
//            validator = new RegexFieldValidator(widgets.getValidatorParameter());
//        }
//        FormFieldValue ffv = new FormFieldValue((Serializable) value, null);
//        return validator.validate(ffv, locale);
//    }
}
