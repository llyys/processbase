package ee.kovmen.ui.legislation;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.processbase.ui.core.template.PbWindow;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.OptionGroup;

import ee.kovmen.data.LegislationData;
import ee.kovmen.entities.KovLegislation;
import ee.kovmen.entities.KovProcess;

public class LegislationProcessView extends PbWindow implements Property.ValueChangeListener{

	private final KovLegislation currentLegislation;
	private boolean isNew;

	public LegislationProcessView(KovLegislation currentLegislation) {
		this.currentLegislation = currentLegislation;
		// TODO Auto-generated constructor stub
	}

	public void setIsNew(boolean isNew) {
		this.isNew = isNew;
	}

	public void initUI() {
		
		setModal(true);
		FormLayout form=new FormLayout();
		addComponent(form);

		final OptionGroup procSelect = new OptionGroup("Valige seotud protsessid");
		procSelect.setMultiSelect(true);
		procSelect.setImmediate(true);// react when the user selects something
		
		HibernateTemplate hibernate = LegislationData.getCurrent().getHibernate();
		List find = hibernate.find("select p from KovProcess p where p.state='ENABLED'");
		for (Object object : find) {
			Item item = procSelect.addItem(object);			
		}
		
		form.addComponent(procSelect);
		form.addComponent(new Button("Salvesta", new Button.ClickListener(){

			public void buttonClick(ClickEvent event) {
				Session session = LegislationData.getCurrent().getSession();
				Transaction t=session.beginTransaction();
				KovLegislation leg=(KovLegislation) session.get(KovLegislation.class, currentLegislation.getId());
				leg.getProcesses().clear();
				for (Object object : procSelect.getItemIds()) {
					if(procSelect.isSelected(object)){
						leg.getProcesses().add((KovProcess) object);
					}
				}
				t.commit();
				
				
			}
			
		}));

	}
	
	public void valueChange(ValueChangeEvent event) {
        getWindow().showNotification("Selected city: " + event.getProperty());

    }
	
}
