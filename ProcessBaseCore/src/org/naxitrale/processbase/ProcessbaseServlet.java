/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase;

import com.vaadin.terminal.gwt.server.ApplicationServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import org.naxitrale.processbase.persistence.controller.HibernateUtil;

/**
 *
 * @author mgubaidullin
 */
public class ProcessbaseServlet extends ApplicationServlet {

    HibernateUtil hutil = new HibernateUtil();

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        if (!hutil.isInstalled()){
            hutil.createNewProcessbaseSchema();
        }
    }
}
