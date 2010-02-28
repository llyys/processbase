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
package org.processbase.ui.template;

import com.vaadin.data.Item;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

/**
 *
 * @author mgubaidullin
 */
public class ExampleFieldFactory extends DefaultFieldFactory {

    final ComboBox countries = new ComboBox("Country");

    public ExampleFieldFactory() {
        countries.setWidth("30em");
//        countries.setContainerDataSource(ExampleUtil.getISO3166Container());
//        countries.setItemCaptionPropertyId(ExampleUtil.iso3166_PROPERTY_NAME);
//        countries.setItemIconPropertyId(ExampleUtil.iso3166_PROPERTY_FLAG);
        countries.setFilteringMode(ComboBox.FILTERINGMODE_STARTSWITH);
    }

    @Override
    public Field createField(Item item, Object propertyId,
            Component uiContext) {
        if ("countryCode".equals(propertyId)) {
            // filtering ComboBox w/ country names
            return countries;
        }
        Field f = super.createField(item, propertyId, uiContext);
        if ("firstName".equals(propertyId)) {
            TextField tf = (TextField) f;
            tf.setRequired(true);
            tf.setRequiredError("Please enter a First Name");
            tf.setWidth("15em");
            tf.addValidator(new StringLengthValidator(
                    "First Name must be 3-25 characters", 3, 25, false));
        } else if ("lastName".equals(propertyId)) {
            TextField tf = (TextField) f;
            tf.setRequired(true);
            tf.setRequiredError("Please enter a Last Name");
            tf.setWidth("20em");
            tf.addValidator(new StringLengthValidator(
                    "Last Name must be 3-50 characters", 3, 50, false));
        } else if ("password".equals(propertyId)) {
            TextField tf = (TextField) f;
            tf.setSecret(true);
            tf.setRequired(true);
            tf.setRequiredError("Please enter a password");
            tf.setWidth("10em");
            tf.addValidator(new StringLengthValidator(
                    "Password must be 6-20 characters", 6, 20, false));
        } else if ("shoesize".equals(propertyId)) {
            TextField tf = (TextField) f;
            tf.addValidator(new IntegerValidator(
                    "Shoe size must be an Integer"));
            tf.setWidth("2em");
        } else if ("uuid".equals(propertyId)) {
            TextField tf = (TextField) f;
            tf.setWidth("20em");
        }

        return f;
    }
}
