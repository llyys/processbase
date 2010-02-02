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
package org.processbase.acl;

import java.lang.reflect.Method;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ow2.bonita.facade.BonitaSecurityContext;
import org.ow2.bonita.facade.EJB3SecurityContext;
import org.ow2.bonita.util.BonitaRuntimeException;
import org.ow2.bonita.util.Misc;

/**
 *
 * @author mgubaidullin
 */
public class ProcessBaseEJB3SecurityContext implements BonitaSecurityContext {

    private static final Logger LOG = Logger.getLogger(EJB3SecurityContext.class.getName());

    public String getUser() {
        Logger.getLogger(ProcessBaseEJB3SecurityContext.class.getName()).log(Level.SEVERE, "------------------");
        try {
            final Object ejbContext = Misc.lookup("java:comp/EJBContext", null);
//            Logger.getLogger(ProcessBaseEJB3SecurityContext.class.getName()).log(Level.SEVERE, "Looking for user in " + EJB3SecurityContext.class + ", ejbContext=" + ejbContext);
            final Class<?> ejbContextClass = Class.forName("javax.ejb.EJBContext");
//            Logger.getLogger(ProcessBaseEJB3SecurityContext.class.getName()).log(Level.SEVERE, "ejbContextClass = " + (ejbContextClass).toString());
            final Method getCallerPrincipalMethod = ejbContextClass.getMethod("getCallerPrincipal");
            final Principal principal = (Principal) getCallerPrincipalMethod.invoke(ejbContext);
            String user = principal.getName();
//            Logger.getLogger(ProcessBaseEJB3SecurityContext.class.getName()).log(Level.SEVERE, "User found in " + ProcessBaseEJB3SecurityContext.class + ": " + user);
            return user;
        } catch (Exception e) {
            Logger.getLogger(ProcessBaseEJB3SecurityContext.class.getName()).log(Level.SEVERE, e.getMessage());
            throw new BonitaRuntimeException(e);
        }

    }
}


