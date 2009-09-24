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

import org.naxitrale.processbase.persistence.entity.Pbuser;

/**
 *
 * @author mgubaidullin
 */
public class User {
    private Pbuser pbuser;
    private boolean bpmAdmin;
    private boolean aclAdmin;
    private boolean dashboardAdmin;

    public User(Pbuser pbuser) {
        this.pbuser = pbuser;
    }

    public User(Pbuser pbuser, boolean bpmAdmin, boolean aclAdmin, boolean dashboardAdmin) {
        this.pbuser = pbuser;
        this.bpmAdmin = bpmAdmin;
        this.aclAdmin = aclAdmin;
        this.dashboardAdmin = dashboardAdmin;
    }

    public Pbuser getPbuser() {
        return pbuser;
    }

    public boolean isAclAdmin() {
        return aclAdmin;
    }

    public void setAclAdmin(boolean aclAdmin) {
        this.aclAdmin = aclAdmin;
    }

    public boolean isBpmAdmin() {
        return bpmAdmin;
    }

    public void setBpmAdmin(boolean bpmAdmin) {
        this.bpmAdmin = bpmAdmin;
    }

    public boolean isDashboardAdmin() {
        return dashboardAdmin;
    }

    public void setDashboardAdmin(boolean dashboardAdmin) {
        this.dashboardAdmin = dashboardAdmin;
    }
   

}
