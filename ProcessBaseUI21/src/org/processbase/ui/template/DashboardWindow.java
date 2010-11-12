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

import org.processbase.ui.dashboard.*;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author mgubaidullin
 */
public abstract class DashboardWindow extends PbWindow implements Button.ClickListener {

    protected Context context = null;
    protected Connection connection = null;
    protected PreparedStatement preparedStatement = null;
    protected DataSource dataSource = null;

    public DashboardWindow(PortletApplicationContext2 portletApplicationContext2) {
        super(portletApplicationContext2);
        try {
            context = new InitialContext();
            dataSource = (DataSource) context.lookup("jdbc/pbbam2");
            connection = dataSource.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public abstract void refresh();

    protected void closeAll() {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
            if (context != null) {
                context.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected Component getThermometerImage(String caption, Number value, int width, int height) {
        StreamResource.StreamSource imagesource = new ThermometerImageSource(-10, 10, null, width, height);
        ((ThermometerImageSource) imagesource).setValue(value);
        StreamResource imageresource = new StreamResource(imagesource, "image2.png", getApplication());
        imageresource.setCacheTime(0);
        Embedded image = new Embedded(caption, imageresource);
        return image;
    }

    protected Component addBarImage(String caption, ArrayList<BarValue> values,int width, int height) {
        StreamResource.StreamSource imagesource = new BarImageSource(width, height);
        ((BarImageSource) imagesource).setValue(values);
        StreamResource imageresource = new StreamResource(imagesource, "image4.png", getApplication());
        imageresource.setCacheTime(0);
        Embedded image = new Embedded(caption, imageresource);
        return image;
    }

    protected Component getBar3DImage(String caption, ArrayList<BarValue> values,int width, int height) {
        StreamResource.StreamSource imagesource = new Bar3DImageSource(width, height);
        ((Bar3DImageSource) imagesource).setValue(values);
        StreamResource imageresource = new StreamResource(imagesource, "image5.png", getApplication());
        imageresource.setCacheTime(0);
        Embedded image = new Embedded(caption, imageresource);
//        image.setStyleName("white");
        return image;
    }

    protected Component getPieImage(String caption, ArrayList<PieValue> values,int width, int height) {
        StreamResource.StreamSource imagesource = new PieImageSource(width, height);
        ((PieImageSource) imagesource).setValue(values);
        StreamResource imageresource = new StreamResource(imagesource, "image6.png", getApplication());
        imageresource.setCacheTime(0);
        Embedded image = new Embedded(caption, imageresource);
        return image;
    }

    protected Component getPie3DImage(String caption, ArrayList<PieValue> values,int width, int height) {
        StreamResource.StreamSource imagesource = new Pie3DImageSource(width, height);
        ((Pie3DImageSource) imagesource).setValue(values);
        StreamResource imageresource = new StreamResource(imagesource, "image7.png", getApplication());
        imageresource.setCacheTime(0);
        Embedded image = new Embedded(caption, imageresource);
        return image;
    }

    public void buttonClick(ClickEvent event) {
        try {
            refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
