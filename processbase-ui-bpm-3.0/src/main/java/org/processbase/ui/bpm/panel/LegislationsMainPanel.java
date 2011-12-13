package org.processbase.ui.bpm.panel;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.hibernate.annotations.Entity;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition.ProcessState;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.PbColumnGenerator;
import org.processbase.ui.core.template.TableLinkButton;
import org.processbase.ui.core.template.TablePanel;
import org.processbase.ui.core.template.WorkPanel;
import org.processbase.ui.osgi.PbPanelModule;


import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class LegislationsMainPanel extends PbPanelModule{

	@Override
	public String getTitle(Locale locale) {
		return "Õigusaktid";
	}

	@Override
	public void initUI() {
		
	}
	
}




