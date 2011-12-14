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
		Button btnAddNew = new Button("Lisa", new Button.ClickListener() {			
			public void buttonClick(ClickEvent event) {				
				LegislationEditView view = new LegislationEditView(new Oigusakt());
				Window wnd=new Window("Lisa uus õigusakt");
				wnd.addComponent(view);
				wnd.addListener(new CloseListener() {					
					public void windowClose(CloseEvent e) {
						table.refreshTable();
					}
				});
				
				wnd.setWidth("300px");
				view.setSizeFull();
				view.setMargin(true);
				view.initUI();
				wnd.setModal(true);
				getApplication().getMainWindow().addWindow(wnd);
			}
		});
		
		Button btnRefresh = new Button("Uuenda", new Button.ClickListener() {			
			public void buttonClick(ClickEvent event) {				
				table.refreshTable();
			}
		});
		
		
		HorizontalLayout hl=new HorizontalLayout();
		hl.setSpacing(true);
		hl.setMargin(true, true, false, false);
		Label label = new Label("");
		hl.addComponent(label);
		hl.setExpandRatio(label, 1);
		hl.addComponent(btnAddNew);
		
		
		hl.addComponent(btnRefresh);
		hl.setWidth("100%");
		
		addComponent(hl);
		hl.setComponentAlignment(btnAddNew, Alignment.MIDDLE_RIGHT);
		hl.setComponentAlignment(btnRefresh, Alignment.MIDDLE_RIGHT);
		
		addComponent(table);
		table.setSizeFull();
		setExpandRatio(table, 1);
		table.initUI();
		table.refreshTable();
	}

}
