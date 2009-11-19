/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.realm;

import java.util.*;

import com.sun.appserv.security.AppservRealm;
import com.sun.enterprise.security.auth.realm.*;

/**
 *
 * @author mgubaidullin
 */
public class ProcessBaseRealm extends AppservRealm {

    private String jaasCtxName;
    private String userCriteria;

    @Override
    protected void init(Properties props) throws BadRealmException, NoSuchRealmException {
        jaasCtxName = props.getProperty("jaas-context", "ProcessBaseRealm");
        userCriteria = props.getProperty("userCriteria", "j2ee");
    }
    
    public Enumeration getGroupNames(String string)
            throws InvalidOperationException, NoSuchUserException {
        List groupNames = new LinkedList();
        return (Enumeration) groupNames;
    }

    @Override
    public String getJAASContext() {
        return jaasCtxName;
    }

    /*
     *  A string description of the authentication type being used.
     */
    public String getAuthType() {
        return "ProcessBaseRealm";
    }

    public String getUserCriteria() {
        return userCriteria;
    }

    /**
     * Returns the information recorded about a particular named user.
     *
     * @param name Name of the user whose information is desired.
     * @return The user object.
     * @exception NoSuchUserException if the user doesn't exist.
     * @exception BadRealmException if realm data structures are bad.
     */
    @Override
    public User getUser(String name) throws NoSuchUserException {
        return null;
    }
}

