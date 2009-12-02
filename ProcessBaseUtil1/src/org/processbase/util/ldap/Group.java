/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.util.ldap;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

/**
 *
 * @author mgubaidullin
 */
public class Group extends Entity {

    ArrayList<String> uniquemember = new ArrayList<String>();
    String description = null;

    public Group(String cn, String dn, String description, Attribute uniquemember) {
        super("groupOfUniqueNames", cn, dn);
        this.description = description;
        if (uniquemember != null) {
            for (int i = 0; i < uniquemember.size(); i++) {
                try {
                    this.uniquemember.add(uniquemember.get(i).toString());
                } catch (NamingException ex) {
                    Logger.getLogger(Group.class.getName()).log(Level.SEVERE, ex.getMessage());
                }
            }
        }
    }

    public Group() {
        super();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getUniquemember() {
        return uniquemember;
    }

    @Override
    public String toString() {
        return this.dn;
    }
}
