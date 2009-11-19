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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
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
public class DefaultRoleMapper extends ProcessBaseRoleMapper {

    @Override
    public Set<String> searchMembers(QueryAPIAccessor queryAPIAccessor, ProcessInstanceUUID piUUID, String rolename) throws Exception {
//        Logger.getLogger(DefaultRoleMapper.class.getName()).log(Level.SEVERE, "rolename = " + rolename);
        loadProperties();
        ProcessInstance processInstance = queryAPIAccessor.getQueryRuntimeAPI().getProcessInstance(piUUID);
        ProcessDefinition processDefinition = queryAPIAccessor.getQueryDefinitionAPI().getProcess(processInstance.getProcessDefinitionUUID());
        ParticipantDefinition participantDefinition = queryAPIAccessor.getQueryDefinitionAPI().getProcessParticipant(processDefinition.getProcessDefinitionUUID(), rolename);
//        Logger.getLogger(DefaultRoleMapper.class.getName()).log(Level.SEVERE, "participantDefinition.getParticipantId = " + participantDefinition.getParticipantId());
//        Logger.getLogger(DefaultRoleMapper.class.getName()).log(Level.SEVERE, "participantDefinition.getName = " + participantDefinition.getName());
//        Logger.getLogger(DefaultRoleMapper.class.getName()).log(Level.SEVERE, "participantDefinition.getParticipantType = " + participantDefinition.getParticipantType().toString());
        HashSet<String> result = new HashSet<String>();
        if (participantDefinition.getParticipantType().equals(ParticipantType.ROLE) || participantDefinition.getParticipantType().equals(ParticipantType.ORGANIZATIONAL_UNIT)) {
            connect();
            NamingEnumeration<SearchResult> groupSearch = ctx.search(properties.getProperty("BASE_GROUP_DN"), "cn=" + participantDefinition.getName(), new SearchControls());
            SearchResult groupSR = groupSearch.next();
            Attribute atr = groupSR.getAttributes().get("uniquemember");
            if (atr != null) {
                for (int i = 0; i < atr.size(); i++) {
                    String userDN = atr.get(i).toString();
                    result.add(userDN.substring(4, userDN.indexOf(properties.getProperty("BASE_PEOPLE_DN")) - 1));
                }
            }
            ctx.close();
        } else if (participantDefinition.getParticipantType().equals(ParticipantType.HUMAN)) {
            result.add(participantDefinition.getName());
        } else if (participantDefinition.getParticipantType().equals(ParticipantType.SYSTEM)) {
            result.add(participantDefinition.getName());
        }
        return result;
    }
}
