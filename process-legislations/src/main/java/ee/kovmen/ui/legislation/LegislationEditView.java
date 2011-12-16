package ee.kovmen.ui.legislation;


import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;

import org.caliburn.viewmodels.ViewModelBinder;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ConfirmDialog;
import org.processbase.ui.core.template.PbWindow;
import org.processbase.ui.core.template.WorkPanel;

import ee.kovmen.data.LegislationData;
import ee.kovmen.entities.KovLegislation;
import ee.kovmen.entities.KovLegislationType;
import ee.kovmen.entities.KovServiceCategory;
import ee.kovmen.entities.Oigusakt;

public class LegislationEditView extends PbWindow{
	
	private final KovLegislation data;
	private ViewModelBinder<KovLegislation> binder;
	private ComboBox combo;
	public LegislationEditView(KovLegislation data){
		this.data = data;
	}
	
	
	public void initUI(){
		setModal(true);
		FormLayout form=new FormLayout();
		addComponent(form);
		BeanItemContainer<KovServiceCategory> teenused=new BeanItemContainer(KovServiceCategory.class, LegislationData.getCurrent().getHibernate().loadAll(KovServiceCategory.class));
		
		
		combo = new ComboBox("Teenuse katekooria", teenused);
		combo.setItemCaptionMode(Select.ITEM_CAPTION_MODE_PROPERTY);
		combo.setItemCaptionPropertyId("name");
		combo.setNewItemsAllowed(false);
		combo.setImmediate(true);		
		combo.setTextInputAllowed(false);
		
		form.addComponent(combo);
		
		binder = new ViewModelBinder<KovLegislation>(form, data==null?new KovLegislation():data);		
		binder.addComponent(new TextField("Õigusakt"), "name");
		binder.addComponent(new TextField("Viide"), "url");
		binder.addComponent(new TextArea("Tüüp"), "description");
		binder.registerComponent(combo, "category"); 
		
		if(data==null){
			form.addComponent(new Button("Lisa uus", new ClickListener() {			
				public void buttonClick(ClickEvent event) {
					LegislationData.getCurrent().getHibernate().save(binder.getBean());
				}
			}));
		}
		else{
			HorizontalLayout hl=new HorizontalLayout();
			form.addComponent(hl);
			hl.setSpacing(true);
			hl.addComponent(new Button("Kustuta", new ClickListener() {			
				public void buttonClick(ClickEvent event) {
					DeleteTask();
				}
			}));
			hl.addComponent(new Button("Muuda", new ClickListener() {			
				public void buttonClick(ClickEvent event) {
					LegislationData.getCurrent().getHibernate().saveOrUpdate(binder.getBean());
					close();
				}
			}));
			
			
		}
		
	}
	private void DeleteTask(){
		ConfirmDialog.show(this.getParent(),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("windowCaptionConfirm"),"Kas kustutada antud teenus?",
                ProcessbaseApplication.getCurrent().getPbMessages().getString("btnYes"),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                            	LegislationData.getCurrent().DeleteLegislation(binder.getBean());
                            	close();
                                
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                });
	}


}