package org.processbase.ui.servlet;

public class PbModule{
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPanel() {
		return panel;
	}
	public void setPanel(String panel) {
		this.panel = panel;
	}
	public String[] getRoles() {
		return roles;
	}
	public void setRoles(String[] roles) {
		this.roles = roles;
	}
	private String panel;
	private String[] roles;
}