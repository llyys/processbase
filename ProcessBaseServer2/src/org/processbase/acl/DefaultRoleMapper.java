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
        try {
            loadProperties();
            ProcessInstance processInstance = queryAPIAccessor.getQueryRuntimeAPI().getProcessInstance(piUUID);
            ProcessDefinition processDefinition = queryAPIAccessor.getQueryDefinitionAPI().getProcess(processInstance.getProcessDefinitionUUID());
            ParticipantDefinition participantDefinition = queryAPIAccessor.getQueryDefinitionAPI().getProcessParticipant(processDefinition.getProcessDefinitionUUID(), rolename);
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
        } catch (Exception e) {
            e.printStackTrace();
            return new HashSet<String>();
        }
    }
}
