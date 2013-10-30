package org.processbase.ui.portlet;
import java.util.Map;
import java.util.Set;

import org.ow2.bonita.facade.identity.ContactInfo;
import org.ow2.bonita.facade.identity.Membership;
import org.ow2.bonita.facade.identity.ProfileMetadata;
import org.ow2.bonita.facade.identity.User;

public class PortalUser implements User{

	private com.liferay.portal.model.User portalUser;
	private final String userName;
	private final String firstName;

	public PortalUser(com.liferay.portal.model.User portalUser){
		this.portalUser = portalUser;
		this.userName = portalUser.getScreenName();
		this.firstName = portalUser.getFirstName();
		
	}
	public PortalUser(String screenName, String firstName) {
		this.userName = screenName;
		this.firstName = firstName;
		
	}
	public String getUUID() {
		return null;
	}

	public String getUsername() {
		// TODO Auto-generated method stub
		
			return userName;
		
	}

	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getFirstName() {
		// TODO Auto-generated method stub
		return firstName;
	}

	public String getLastName() {
		// TODO Auto-generated method stub
		return portalUser.getLastName();
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getJobTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getManagerUUID() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDelegeeUUID() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEmail() {
		// TODO Auto-generated method stub
		return null;
	}

	public ContactInfo getPersonalContactInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public ContactInfo getProfessionalContactInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<ProfileMetadata, String> getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Membership> getMemberships() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public com.liferay.portal.model.User getPortalUser() {
		return portalUser;
	}

}
