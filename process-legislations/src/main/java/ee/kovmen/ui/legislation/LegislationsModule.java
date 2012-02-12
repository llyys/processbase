package ee.kovmen.ui.legislation;

import java.util.Locale;

import org.processbase.ui.osgi.PbPanelModule;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window.CloseListener;

import ee.kovmen.entities.KovLegislation;
import ee.kovmen.entities.KovServiceCategory;
import ee.kovmen.entities.Oigusakt;

public class LegislationsModule extends PbPanelModule{

	@Override
	public String getTitle(Locale locale) {
		// TODO Auto-generated method stub
		return "Õigusaktid";
	}
	
	

	@Override
	public void initUI() {
		final LegislationsTableView table=new LegislationsTableView();
		table.setModule(this);
		
		addComponent(table);
		table.setSizeFull();
		setExpandRatio(table, 1);
		table.initUI();
		table.refreshTable();
	}

	

}
