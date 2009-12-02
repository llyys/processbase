/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.util.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 */
@Entity
@Table(name = "PB_PROCESS_ACL")
public class PbProcessAcl implements java.io.Serializable {

    private String id;
    private String proccessUuid;
    private String groupDn;

    public PbProcessAcl() {
    }

    public PbProcessAcl(String id, String proccessUuid, String groupDn) {
        this.id = id;
        this.proccessUuid = proccessUuid;
        this.groupDn = groupDn;
    }

    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 100)
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "PROCCESS_UUID", nullable = false, length = 2000)
    public String getProccessUuid() {
        return this.proccessUuid;
    }

    public void setProccessUuid(String proccessUuid) {
        this.proccessUuid = proccessUuid;
    }

    @Column(name = "GROUP_DN", nullable = false, length = 2000)
    public String getGroupDn() {
        return this.groupDn;
    }

    public void setGroupDn(String groupDn) {
        this.groupDn = groupDn;
    }
}


