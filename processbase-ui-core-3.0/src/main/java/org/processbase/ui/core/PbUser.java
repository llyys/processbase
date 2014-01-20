package org.processbase.ui.core;

import java.io.Serializable;


public class PbUser implements Serializable {
    public enum AuthMethod{
        regular,
        mobile_id,
        smart_card
    }

    public String status;
    public String id;
    public String name;
    public Object data;
    public String username;
    public String firstName;
    public String lastName;
    public String password;
    public AuthMethod authMethod;

    public String domain;
    public boolean rememberMe;
}
