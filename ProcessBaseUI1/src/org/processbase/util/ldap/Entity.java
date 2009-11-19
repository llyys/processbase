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

/**
 *
 * @author mgubaidullin
 */
public class Entity {

    String objectclass = null;
    String cn = null;
    String dn = null;

    public Entity() {
    }

    public Entity(String objectclass, String cn, String dn) {
        this.objectclass = objectclass;
        this.cn = cn;
        this.dn = dn;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public String getObjectclass() {
        return objectclass;
    }

    public void setObjectclass(String objectclass) {
        this.objectclass = objectclass;
    }
}
