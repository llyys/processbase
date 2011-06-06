package org.processbase.ui.bpm.admin.process;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.ow2.bonita.facade.IdentityAPI;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.identity.Group;
import org.ow2.bonita.facade.identity.Membership;
import org.ow2.bonita.facade.identity.Role;
import org.ow2.bonita.facade.privilege.Rule;
import org.ow2.bonita.facade.privilege.Rule.RuleType;
import org.processbase.ui.bpm.admin.ProcessDefinitionWindow;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ITabsheetPanel;
import org.processbase.ui.core.template.PbPanel;
import org.processbase.ui.core.template.PbWindow;
import org.processbase.ui.core.template.TableLinkButton;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.Runo;
/**
*
* @author llyys
*/
public class ProcessAccessPanel extends PbPanel implements ITabsheetPanel  {
	private Set<String> deletedMembership = new HashSet<String>();
	private Table tableMembership = new Table();
	private Button addBtn;
	private Button saveAccessBtn;
	
	@Override
	public void initUI() {
		setSpacing(true);

		addBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnAdd")
				, new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						try {
							addTableMembershipRow(null);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							showError(e.getMessage());
							e.printStackTrace();
						}
					}
				});
		
		addBtn.setStyleName(Runo.BUTTON_SMALL);
		addComponent(addBtn);
		setComponentAlignment(addBtn, Alignment.MIDDLE_RIGHT);

		saveAccessBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnSaveProcessAccess"), new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				saveProcessAccess();
				getParentWindow().close();
			}
		});
         
		getParentWindow().getButtons().addButton(saveAccessBtn);
        getParentWindow().getButtons().setComponentAlignment(saveAccessBtn, Alignment.MIDDLE_RIGHT);

		prepareTableMembership();
		addComponent(tableMembership);
		setSizeFull();
		refreshTableMembership();
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	private ProcessDefinition processDefinition = null;

	public void showError(String errorMessage) {
		((PbWindow) getWindow()).showError(errorMessage);
	}

	private void removeProcessAccess() throws Exception {
    	ProcessbaseApplication.getCurrent().getBpmModule().removeRuleFromEntities(rule.getUUID(), null, null, null, deletedMembership, null);
    }
	
	private void addTableMembershipRow(Membership membership) throws Exception {
		String uuid = membership != null ? membership.getUUID() : "NEW_MEMBERSHIP_UUID_" + UUID.randomUUID().toString();
		Item woItem = tableMembership.addItem(uuid);

		if (membership != null) {
			Label groups = new Label(getGroups().getItem(membership != null ? membership.getGroup().getUUID() : null).getItemProperty("path"));
			woItem.getItemProperty("group").setValue(groups);

			Label roles = new Label(getRoles().getItem(membership != null ? membership.getRole().getUUID() : null).getItemProperty("name"));
			woItem.getItemProperty("role").setValue(roles);

		} else {
			ComboBox groups = new ComboBox();
			groups.setWidth("100%");
			groups.setContainerDataSource(getGroups());
			groups.setItemCaptionPropertyId("path");
			groups.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
			groups.setValue(membership != null ? membership.getGroup()
					.getUUID() : null);
			woItem.getItemProperty("group").setValue(groups);

			ComboBox roles = new ComboBox();
			roles.setWidth("100%");
			roles.setContainerDataSource(getRoles());
			roles.setItemCaptionPropertyId("name");
			roles.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
			roles.setValue(membership != null ? membership.getRole().getUUID()
					: null);
			woItem.getItemProperty("role").setValue(roles);

		}

		TableLinkButton tlb = new TableLinkButton(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnDelete"), "icons/cancel.png", uuid, 
				new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						TableLinkButton tlb = (TableLinkButton) event.getButton();
						String uuid = (String) tlb.getTableValue();
						tableMembership.removeItem(uuid);
						if (!uuid.startsWith("NEW_MEMBERSHIP_UUID")) {
							deletedMembership.add(uuid);
						}
					}
				}, Constants.ACTION_DELETE);
		woItem.getItemProperty("actions").setValue(tlb);
	}

	public IndexedContainer getGroups() throws Exception {
		
		IndexedContainer container = new IndexedContainer();
		
		container.addContainerProperty("name", String.class, null);
		container.addContainerProperty("label", String.class, null);
		container.addContainerProperty("uuid", String.class, null);
		container.addContainerProperty("path", String.class, null);
		
		List<Group> groups = ProcessbaseApplication.getCurrent().getBpmModule().getAllGroups();
		
		for (Group groupX : groups) {
			String path = getGroupPath(groupX);
			// if (!path.startsWith("/" + IdentityAPI.DEFAULT_GROUP_NAME)) {
			Item item = container.addItem(groupX.getUUID());
			item.getItemProperty("name").setValue(groupX.getName());
			item.getItemProperty("label").setValue(groupX.getLabel());
			item.getItemProperty("uuid").setValue(groupX.getUUID());
			item.getItemProperty("path").setValue(path);
			// }
		}
		container.sort(new Object[] { "name" }, new boolean[] { true });
		return container;
	}

	private String getGroupPath(Group group) {
		StringBuilder result = new StringBuilder(IdentityAPI.GROUP_PATH_SEPARATOR + group.getName() + IdentityAPI.GROUP_PATH_SEPARATOR);
		Group parent = group.getParentGroup();
		while (parent != null) {
			result.insert(0,IdentityAPI.GROUP_PATH_SEPARATOR + parent.getName());
			parent = parent.getParentGroup();
		}
		return result.toString();
	}

	public IndexedContainer getRoles() throws Exception {
		IndexedContainer container = new IndexedContainer();
		container.addContainerProperty("name", String.class, null);
		container.addContainerProperty("label", String.class, null);
		container.addContainerProperty("uuid", String.class, null);
		
		List<Role> roles = ProcessbaseApplication.getCurrent().getBpmModule().getAllRoles();
		
		for (Role roleX : roles) {
			// if (!roleX.getName().equals(IdentityAPI.ADMIN_ROLE_NAME)) {
			Item item = container.addItem(roleX.getUUID());
			item.getItemProperty("name").setValue(roleX.getName());
			item.getItemProperty("label").setValue(roleX.getLabel());
			item.getItemProperty("uuid").setValue(roleX.getUUID());
			// }
		}
		
		container.sort(new Object[] { "name" }, new boolean[] { true });
		return container;
	}
	private Rule rule;

	public void saveProcessAccess() {
		try {

			BPMModule bpmModule = ProcessbaseApplication.getCurrent().getBpmModule();

			Set<String> membershipUUIDs = new HashSet<String>();
			for (Object itemId : tableMembership.getItemIds()) {
				Item woItem = tableMembership.getItem(itemId);
				
				if (woItem.getItemProperty("group").getValue() instanceof ComboBox && woItem.getItemProperty("role").getValue() instanceof ComboBox) {
					ComboBox groups = (ComboBox) woItem.getItemProperty("group").getValue();
					ComboBox roles = (ComboBox) woItem.getItemProperty("role").getValue();
					Membership membership = bpmModule.getMembershipForRoleAndGroup(roles.getValue().toString(), groups.getValue().toString());
					membershipUUIDs.add(membership.getUUID());
				}
				
			}
			if (membershipUUIDs.size() > 0) // If threre is no items selected in combo, then there is no point of saveing this
			{
				if (rule == null) // crete rule for process starting
					rule = bpmModule.createRule(processDefinition.getUUID()
							.toString(), "ENTITY_PROCESS_START",
							"Rule to start a process", RuleType.PROCESS_START);

				Set<String> entityUUIDs = new HashSet<String>();
				entityUUIDs.add(processDefinition.getUUID().toString());
				bpmModule.applyRuleToEntities(rule.getUUID(), null, null, null,membershipUUIDs, entityUUIDs);
			}

		} catch (Exception ex) {
			showError(ex.getMessage());
		}
	}

	private void prepareTableMembership() {
		
		tableMembership.addContainerProperty("group",Component.class,null,ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionGroup"), null, null);
		tableMembership.addContainerProperty("role",Component.class,null,ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionRole"), null, null);		
		tableMembership.addContainerProperty("actions",Component.class,null,ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionActions"), null, null);
	
		tableMembership.setColumnWidth("actions", 50);
		tableMembership.setImmediate(true);
		tableMembership.setWidth("100%");
		tableMembership.setPageLength(10);
	}

	private void refreshTableMembership() {
		try {
			rule = ProcessbaseApplication.getCurrent().getBpmModule().findRule(processDefinition.getUUID().toString());
			tableMembership.removeAllItems();
			for (String membershipUUID : rule.getMemberships()) {
				Membership membership = ProcessbaseApplication.getCurrent().getBpmModule().getMembershipByUUID(membershipUUID);
				addTableMembershipRow(membership);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onActivate(boolean isActive) {
		saveAccessBtn.setVisible(isActive);		
	}

	public void setParentWindow(ProcessDefinitionWindow parentWindow) {
		this.parentWindow = parentWindow;
	}

	public ProcessDefinitionWindow getParentWindow() {
		return parentWindow;
	}

	private ProcessDefinitionWindow parentWindow;


}
