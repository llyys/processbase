package org.processbase.ui.core;

import org.ow2.bonita.facade.IdentityAPI;
import org.ow2.bonita.facade.identity.Group;
import org.ow2.bonita.facade.identity.Role;

public class CustomRoleMapper {
	String groupPath;
	String roleName;
	private Group group;
	public Group getGroup() {
		return group;
	}
	public Role getRole() {
		return role;
	}

	private Role role;
	private Boolean newMembership;
	public String getGroupPath() {
		return groupPath;
	}
	public void setGroupPath(String groupPath) {
		this.groupPath = groupPath;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	public String[] getGroups(){
		if(groupPath==null)//failsafe
			return new String[]{};
		return groupPath.split(IdentityAPI.GROUP_PATH_SEPARATOR);
	}
	
	public void setGroup(Group group) {
		this.group = group;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	
	public Boolean isNewMembership(){
		return newMembership;
		//return this.group!=null && this.role!=null;
	}
	
	public void setNewMembership(Boolean isNew){
		newMembership=isNew;
	}
}
