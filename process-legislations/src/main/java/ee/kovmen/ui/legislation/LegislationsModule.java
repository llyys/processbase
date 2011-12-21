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
	
	Button btnAddNewLegislation;
	Button btnAddNewLegislationProcess;
	protected KovServiceCategory currentCategory;
	protected KovLegislation currentLegislation;

	@Override
	public void initUI() {
		final LegislationsTableView table=new LegislationsTableView();
		 btnAddNewLegislation = new Button("Lisa", new Button.ClickListener() {			
			public void buttonClick(ClickEvent event) {				
				currentLegislation = new KovLegislation();
				currentLegislation.setCategory(currentCategory);
				
				LegislationEditView view = new LegislationEditView(currentLegislation);
				view.setIsNew(true);
				view.addListener(new CloseListener() {					
					public void windowClose(CloseEvent e) { 
						table.refreshTable();
					}
				});				
				view.setWidth("300px");
				view.initUI();				
				getApplication().getMainWindow().addWindow(view);
			}
		});
		 btnAddNewLegislationProcess=new Button("Vali õigusakti protsessid", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				
				LegislationProcessView view = new LegislationProcessView(currentLegislation);
				view.setIsNew(true);
				view.addListener(new CloseListener() {					
					public void windowClose(CloseEvent e) {
						table.refreshTable();
					}
				});				
				view.setWidth("300px");
				view.initUI();				
				getApplication().getMainWindow().addWindow(view);
			}
		});
		 
		 table.setModule(this);
		 btnAddNewLegislation.setVisible(false);
		 btnAddNewLegislationProcess.setVisible(false);
		 
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
		hl.addComponent(btnAddNewLegislation);
		hl.addComponent(btnAddNewLegislationProcess);
		
		
		hl.addComponent(btnRefresh);
		hl.setWidth("100%");
		
		addComponent(hl);
		hl.setComponentAlignment(btnAddNewLegislation, Alignment.MIDDLE_RIGHT);
		hl.setComponentAlignment(btnRefresh, Alignment.MIDDLE_RIGHT);
		
		addComponent(table);
		table.setSizeFull();
		setExpandRatio(table, 1);
		table.initUI();
		table.refreshTable();
	}

	public void SetSelectedCategory(KovServiceCategory kat) {		
		currentCategory=kat;
		if(kat!=null){
			btnAddNewLegislation.setCaption("Lisa uus '"+kat.getName()+ "' õigusakt");
			btnAddNewLegislation.setVisible(true);
		}
		else{
			btnAddNewLegislation.setVisible(false);
		}
	}

	public void SetSelectedLegislation(KovLegislation rowObj) {
		currentLegislation=rowObj;
		if(currentLegislation!=null){
			btnAddNewLegislationProcess.setCaption("Määra '"+rowObj.getName()+ "' protsessid");
			btnAddNewLegislationProcess.setVisible(true);
		}
		else{
			btnAddNewLegislationProcess.setVisible(false);
		}
	}

}
