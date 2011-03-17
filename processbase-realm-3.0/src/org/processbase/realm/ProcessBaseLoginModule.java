/**
 * Copyright (C) 2011 PROCESSBASE Ltd.
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
package org.processbase.realm;

import com.sun.appserv.security.AppservPasswordLoginModule;
//import com.sun.enterprise.deployment.PrincipalImpl;
import org.glassfish.security.common.PrincipalImpl;
import java.util.Set;
import javax.security.auth.login.LoginException;

/**
 *
 * @author mgubaidullin
 */
public class ProcessBaseLoginModule extends AppservPasswordLoginModule {

    @Override
    protected void authenticateUser() throws LoginException {
        Set principals = _subject.getPrincipals();
        principals.add(new PrincipalImpl(_username));
        String grpList[] = new String[1];
        grpList[0] = "User";
        this.commitUserAuthentication(grpList);
    }
}

