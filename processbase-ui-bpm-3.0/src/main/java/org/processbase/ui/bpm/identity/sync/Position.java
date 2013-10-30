package org.processbase.ui.bpm.identity.sync;

import java.util.HashSet;
import java.util.Set;

/**
 * Position
 * 
 * @author Margo
 */
public class Position {

	/** AS - asendaja */
	public static final String TYPE_AS = "AS";
	/** PK - põhikohaga */
	public static final String TYPE_PK = "PK";
	/** LP - lepinguline */
	public static final String TYPE_LP = "LP";
	/** PK - põhikohaga */
	public static final String TYPE_KT = "KT";

	private Integer id;

	/** Position name. */
	private String name;

	/** Organization registry code. */
	private String organization;

	/** Roles. */
	private Set<String> roles;

	/** Position type. */
	private String type;

	public Position() {
		roles = new HashSet<String>();
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the roles
	 */
	public Set<String> getRoles() {
		return roles;
	}

	/**
	 * @param roles
	 *            the roles to set
	 */
	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the organization
	 */
	public String getOrganization() {
		return organization;
	}

	/**
	 * @param organization
	 *            the organization to set
	 */
	public void setOrganization(String organization) {
		this.organization = organization;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Position [id=").append(id).append(", name=")
				.append(name).append(", organization=").append(organization)
				.append(", roles=").append(roles).append(", type=")
				.append(type).append("]");
		return builder.toString();
	}

}
