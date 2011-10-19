package org.processbase.servlets;

import java.util.Map;
import java.util.Set;

import org.ow2.bonita.facade.identity.ContactInfo;
import org.ow2.bonita.facade.identity.Membership;
import org.ow2.bonita.facade.identity.ProfileMetadata;
import org.ow2.bonita.facade.identity.User;

public class BonitaUser implements User {
	public BonitaUser(String username, String firstName, String lastName){
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		
	}
	String username;
	String firstName;
	String lastName;
	
	@Override
	public String getUUID() {
		return null;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public String getJobTitle() {
		return null;
	}

	@Override
	public String getManagerUUID() {
		return null;
	}

	@Override
	public String getDelegeeUUID() {
		return null;
	}

	@Override
	public String getEmail() {
		return null;
	}

	@Override
	public ContactInfo getPersonalContactInfo() {
		return null;
	}

	@Override
	public ContactInfo getProfessionalContactInfo() {
		return null;
	}

	@Override
	public Map<ProfileMetadata, String> getMetadata() {
		return null;
	}

	@Override
	public Set<Membership> getMemberships() {
		return null;
	}

}
