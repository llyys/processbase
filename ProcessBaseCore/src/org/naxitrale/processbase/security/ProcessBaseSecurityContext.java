/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.security;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.naxitrale.processbase.ProcessBase;
import org.ow2.bonita.facade.BonitaSecurityContext;

/**
 *
 * @author mgubaidullin
 */
public class ProcessBaseSecurityContext implements BonitaSecurityContext {

    public String getUser() {
        return (ProcessBase.getCurrent().getUser().getPbuser()).getUsername();
    }
}
