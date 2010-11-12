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
