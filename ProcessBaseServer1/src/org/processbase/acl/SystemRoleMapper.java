/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.acl;

import java.util.HashSet;
import java.util.Set;
import org.ow2.bonita.definition.RoleMapper;
import org.ow2.bonita.facade.QueryAPIAccessor;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;

/**
 *
 * @author mgubaidullin
 */
public class SystemRoleMapper implements RoleMapper {

    public Set<String> searchMembers(QueryAPIAccessor queryAPIAccessor, ProcessInstanceUUID piUUID, String rolename) throws Exception {
        HashSet<String> result = new HashSet<String>();
        result.add(rolename);
        return result;
    }
}
