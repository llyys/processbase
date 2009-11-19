/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.template;

import com.vaadin.data.Container;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;
import java.util.ResourceBundle;

/**
 *
 * @author mgubaidullin
 */
public class PbTableFieldFactory implements TableFieldFactory {

    protected ResourceBundle messages = null;

    @Override
    public Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {
        TextField field = new TextField();
        if (propertyId.toString().equals("uiClass")){
            field.setInputPrompt("Type UI Form Class here");
            field.setWidth("300px");
            field.setNullRepresentation("");
        } else {
            return null;
        }
        return field;
    }

}

