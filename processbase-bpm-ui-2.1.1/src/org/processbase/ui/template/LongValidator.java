/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.processbase.ui.template;

import com.vaadin.data.validator.AbstractStringValidator;

/**
 *
 * @author marat
 */
public class LongValidator extends AbstractStringValidator {

    public LongValidator(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public boolean isValid(Object value) {
        if (value != null) {
            if (!(value instanceof Long)) {
                return isValidString(value.toString());
            }
        }
        return true;
    }

    @Override
    protected boolean isValidString(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
