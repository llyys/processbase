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
import java.util.List;
import java.util.Set;
import org.processbase.acl.persistence.HibernateUtil;
import org.ow2.bonita.definition.RoleMapper;
import org.ow2.bonita.facade.QueryAPIAccessor;
import org.ow2.bonita.facade.def.majorElement.ParticipantDefinition;
import org.ow2.bonita.facade.def.majorElement.ParticipantDefinition.ParticipantType;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;

/**
 *
 * @author mgubaidullin
 */
public class DefaultRoleMapper implements RoleMapper {

    public Set<String> searchMembers(QueryAPIAccessor queryAPIAccessor, ProcessInstanceUUID piUUID, String rolename) throws Exception {
        ProcessInstance processInstance = queryAPIAccessor.getQueryRuntimeAPI().getProcessInstance(piUUID);
        ProcessDefinition processDefinition = queryAPIAccessor.getQueryDefinitionAPI().getProcess(processInstance.getProcessDefinitionUUID());
        ParticipantDefinition participantDefinition = queryAPIAccessor.getQueryDefinitionAPI().getProcessParticipant(processDefinition.getProcessDefinitionUUID(), rolename);
        HibernateUtil hutil = new HibernateUtil();
        HashSet<String> result = new HashSet<String>();
        if (participantDefinition.getParticipantType().equals(ParticipantType.ROLE)) {
            List<String> users = hutil.findUsernamesByRoleName(participantDefinition.getName());
            result.addAll((List<String>) users);
        } else if (participantDefinition.getParticipantType().equals(ParticipantType.HUMAN)) {
            result.add(participantDefinition.getName());
        } else if (participantDefinition.getParticipantType().equals(ParticipantType.SYSTEM)) {
            result.add(participantDefinition.getName());
        } else if (participantDefinition.getParticipantType().equals(ParticipantType.ORGANIZATIONAL_UNIT)) {
            List<String> users = hutil.findUsernamesByOrgUnit(participantDefinition.getName());
            result.addAll((List<String>) users);
        }
//        Logger.getLogger(DefaultRoleMapper.class.getName()).log(Level.SEVERE, "result = " + result.toString());
        return result;
    }
}
