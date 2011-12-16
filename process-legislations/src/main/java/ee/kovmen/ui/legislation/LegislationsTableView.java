package ee.kovmen.ui.legislation;


import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.ow2.bonita.light.LightProcessDefinition;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.template.TableLinkButton;
import org.processbase.ui.core.template.TablePanel;
import org.processbase.ui.core.template.WorkPanel;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import ee.kovmen.data.LegislationData;
import ee.kovmen.entities.KovLegislation;
import ee.kovmen.entities.KovServiceCategory;


public class LegislationsTableView  extends WorkPanel implements Button.ClickListener{
	
    private TreeTable table;

	public void initUI() {
    	
        	setSizeFull();
        	
	        table = new TreeTable("Õigusaktid");
        	table.addContainerProperty("name", String.class, "");
        	table.addContainerProperty("description", String.class, "");
        	table.addContainerProperty("url", String.class, "");
        	
        	
        	horizontalLayout.addComponent(table);        	
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
				List<KovServiceCategory> data = session.createCriteria(KovServiceCategory.class).list();
				if(data!=null){
					for (KovServiceCategory category : data) {
						 Object tcategory = table.addItem(new Object[] { category.getName(), "", ""}, null);
						 if(category.getLegislations()!=null){
							 for (KovLegislation leg : category.getLegislations()) {
								 Object tleg = table.addItem(new Object[] { leg.getName(), leg.getUrl(), ""}, null);
								 table.setParent(tleg, tcategory);
								 /*if(leg.getProcesses()!=null){
									 for (KovLegislationProcess proc : leg.getProcesses()) {
										 Object tproc = table.addItem(new Object[] { proc.getName(), "", ""}, null);
										 table.setParent(tproc, tleg);
										 table.setChildrenAllowed(tproc, false);
									}
								 } else{
									 table.setChildrenAllowed(tleg, false);
								 }*/
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

}
