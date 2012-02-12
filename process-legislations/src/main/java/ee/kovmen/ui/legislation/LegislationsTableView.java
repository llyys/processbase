package ee.kovmen.ui.legislation;


import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.ow2.bonita.light.LightProcessDefinition;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.template.IPbTable;
import org.processbase.ui.core.template.TableLinkButton;
import org.processbase.ui.core.template.TablePanel;
import org.processbase.ui.core.template.WorkPanel;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree.ExpandListener;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import ee.kovmen.data.LegislationData;
import ee.kovmen.entities.KovLegislation;
import ee.kovmen.entities.KovProcess;
import ee.kovmen.entities.KovServiceCategory;


public class LegislationsTableView  extends WorkPanel implements IPbTable, Button.ClickListener{
	
    private TreeTable table;
	private Button btnAddNew;
	private LegislationsModule module;
	
	Button btnAddNewLegislation;
	Button btnAddNewLegislationProcess;
	protected KovServiceCategory currentCategory;
	protected KovLegislation currentLegislation;
	
	public void initUI() {
    	VerticalLayout layout=new VerticalLayout();
    	horizontalLayout.addComponent(layout);
		btnAddNewLegislation = new Button("Lisa", new Button.ClickListener() {			
			public void buttonClick(ClickEvent event) {				
				currentLegislation = new KovLegislation();
				currentLegislation.setCategory(currentCategory);
				
				LegislationEditView view = new LegislationEditView(currentLegislation);
				view.setIsNew(true);
				view.addListener(new CloseListener() {					
					public void windowClose(CloseEvent e) { 
						refreshTable();
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
						refreshTable();
					}
				});				
				view.setWidth("300px");
				view.initUI();				
				getApplication().getMainWindow().addWindow(view);
			}
		});
		 HorizontalLayout hl=new HorizontalLayout();
		 btnAddNewLegislation.setVisible(false);
		 btnAddNewLegislationProcess.setVisible(false);
		 
		 	
			hl.setSpacing(true);
			hl.setMargin(true, true, false, false);
			Label label = new Label("");
			hl.addComponent(label);
			hl.setExpandRatio(label, 1);
			hl.addComponent(btnAddNewLegislation);
			hl.addComponent(btnAddNewLegislationProcess);
			
			
			
			hl.setWidth("100%");
			
			layout.addComponent(hl);
			hl.setComponentAlignment(btnAddNewLegislation, Alignment.MIDDLE_RIGHT);
			
			if(getModule()==null){
				 Button btnRefresh = new Button("Uuenda", new Button.ClickListener() {			
						public void buttonClick(ClickEvent event) {				
							refreshTable();
						}
					});
				 	hl.addComponent(btnRefresh);
				 	hl.setComponentAlignment(btnRefresh, Alignment.MIDDLE_RIGHT);
			 }
			
			
		 
        	setSizeFull();
        	
        	
        	
	        table = new TreeTable("Õigusaktid");
        	table.addContainerProperty("name", String.class, "");
        	table.addContainerProperty("description", String.class, "");
        	table.addContainerProperty("url", String.class, "");
        	
        	table.setSelectable(true);
        	table.setImmediate(true);
            table.setMultiSelect(false);
         // listen for valueChange, a.k.a 'select' and update the label
            table.addListener(new Table.ValueChangeListener(){
				

				public void valueChange(ValueChangeEvent event) {
					Object rowObj=event.getProperty().getValue();
					if(rowObj instanceof KovServiceCategory){						
						SetSelectedCategory((KovServiceCategory)rowObj);
						SetSelectedLegislation(null);
					}
					else if(rowObj instanceof KovLegislation){
						SetSelectedLegislation((KovLegislation)rowObj);
					}
					else{
						SetSelectedCategory(null);
						SetSelectedLegislation(null);
					}
				}
            });
            
            
            
        	layout.addComponent(table);        	
        	table.setSizeFull();
        
    }
	
	
    public void refreshTable() {
        try {
        	Session session=null;
        	Transaction tran=null;
        	table.removeAllItems();
        	try {
				session = LegislationData.getCurrent().getSession();
				tran = session.beginTransaction();
				List<KovServiceCategory> data = session.createCriteria(KovServiceCategory.class).add(Restrictions.isNull("displayed")).list();
				if(data!=null){
					for (KovServiceCategory category : data) {
						 Object tcategory = table.addItem(new Object[] { category.getName(), "", ""}, category);
						 if(category.getLegislations()!=null){
							 for (KovLegislation leg : category.getLegislations()) {
								 Object tleg = table.addItem(new Object[] { leg.getName(), leg.getUrl(), ""}, leg);
								 table.setParent(tleg, tcategory);
								 if(leg.getProcesses()!=null){
									 for (KovProcess proc : leg.getProcesses()) {
										 Object tproc = table.addItem(new Object[] { proc.getName(), "", ""}, null);
										 table.setParent(tproc, tleg);
										 table.setChildrenAllowed(tproc, false);
									}
								 } else{
									 table.setChildrenAllowed(tleg, false);
								 }
							}
						 }
					}
				}
				tran.commit();
			} catch (Exception e) {
				if(tran!=null)
					tran.rollback();
				e.printStackTrace();
			}
			finally{				
				if(session!=null && session.isOpen())
					session.close();
			}
        	
        	/*table.removeAllItems();
            List<KovLegislation> pds = LegislationData.getCurrent().findAllLegislations();
        	
            for (KovLegislation pd : pds) {
                Item woItem = table.addItem(pd);
                TableLinkButton teb = new TableLinkButton(pd.getName(), "", null, pd, this, Constants.ACTION_OPEN);
                woItem.getItemProperty("name").setValue(teb);                
                woItem.getItemProperty("url").setValue(pd.getUrl());
            }
            table.setSortContainerPropertyId("name");*/
        	
            
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }
	public void buttonClick(ClickEvent event) {
		
		 if (event.getButton() instanceof TableLinkButton) {
	            try {
	            	KovLegislation akd = (KovLegislation) ((TableLinkButton) event.getButton()).getTableValue();
	            	LegislationEditView view = new LegislationEditView(akd);
					view.addListener(new CloseListener() {					
						public void windowClose(CloseEvent e) {
							refreshTable();
						}
					});				
					view.setWidth("300px");
					view.initUI();				
					getApplication().getMainWindow().addWindow(view);
	            }
	            catch(Exception e){
	            	
	            }
		
		 }
	}
	
	public void onAddNew(){
		
	}


	public void registerButtonNew(Button btnAddNew) {
		this.btnAddNew = btnAddNew;
		// TODO Auto-generated method stub
		
	}


	public void setModule(LegislationsModule module) {
		this.module = module;
	}


	public LegislationsModule getModule() {
		if(this.module==null){
			//this is a portlet then initialize it from here
			this.module= new LegislationsModule();
			
		}
		return module;
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
