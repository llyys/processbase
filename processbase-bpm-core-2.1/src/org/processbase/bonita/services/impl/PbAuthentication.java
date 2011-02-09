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
package org.processbase.bonita.services.impl;

import org.ow2.bonita.facade.exception.UserNotFoundException;
import org.ow2.bonita.services.AuthenticationService;

/**
 *
 * @author marat
 */
public class PbAuthentication implements AuthenticationService {

    public boolean isUserAdmin(String string) throws UserNotFoundException {
        System.out.println("PbAuthentication.isUserAdmin + " + string);
        return true;
    }

    public boolean checkUserCredentials(String string, String string1) {
        System.out.println("PbAuthentication.checkUserCredentials + " + string + " + " + string1);
        return true;
    }

    public boolean checkUserCredentialsWithPasswordHash(String string, String string1) {
        System.out.println("PbAuthentication.checkUserCredentials + " + string + " + " + string1);
        return true;
    }

}
