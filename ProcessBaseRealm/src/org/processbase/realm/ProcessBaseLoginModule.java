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

import com.sun.appserv.security.AppservPasswordLoginModule;
import com.sun.enterprise.deployment.PrincipalImpl;
import java.util.Set;
import javax.security.auth.login.LoginException;

/**
 *
 * @author mgubaidullin
 */
public class ProcessBaseLoginModule extends AppservPasswordLoginModule {

    protected void authenticateUser() throws LoginException {
        Set principals = _subject.getPrincipals();
        principals.add(new PrincipalImpl(_username));
        String grpList[] = new String[1];
        grpList[0] = "User";
        this.commitUserAuthentication(grpList);
    }
}

