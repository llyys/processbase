package ee.kovmen.ui.legislation;


import java.util.List;

import org.processbase.ui.core.Constants;
import org.processbase.ui.core.template.TableLinkButton;
import org.processbase.ui.core.template.TablePanel;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import ee.kovmen.data.LegislationData;
import ee.kovmen.entities.Oigusakt;

public class LegislationsTableView  extends TablePanel implements Button.ClickListener{
	@Override
    public void initUI() {
		if(isInitialized())
			return;
        super.initUI();
        
			table.addContainerProperty("name", TableLinkButton.class, null, "Õigusakti nimetus", null, null);
	        table.setColumnExpandRatio("name", 1);
	        
	        table.addContainerProperty("descritpion", String.class, "");
	        
	        table.addContainerProperty("url", String.class, null, "Viide", null, null);
	        table.setImmediate(true);
	        setMargin(true);
        
    }
	
	@Override
    public void refreshTable() {
        try {
        	table.removeAllItems();
            List<Oigusakt> pds = LegislationData.getCurrent().findAllLegislations();
            for (Oigusakt pd : pds) {
                Item woItem = table.addItem(pd);
                TableLinkButton teb = new TableLinkButton(pd.getName(), "", null, pd, this, Constants.ACTION_OPEN);
                woItem.getItemProperty("name").setValue(teb);
                woItem.getItemProperty("descritpion").setValue(pd.getDescription());
                woItem.getItemProperty("url").setValue(pd.getUrl());
            }
            table.setSortContainerPropertyId("name");
            
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	public void onAddNew(){
		
	}

}
