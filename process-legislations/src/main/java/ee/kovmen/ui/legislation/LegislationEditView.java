package ee.kovmen.ui.legislation;


import java.util.Arrays;
import java.util.List;

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
import com.vaadin.ui.OptionGroup;
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
	private boolean is_new=false;
	public LegislationEditView(KovLegislation data){
		this.data = data;
	}
	
	
	public void initUI(){
		
		List<String> liigid = Arrays.asList(new String[] {
	            "Riik", "Omavalitsus"});
		
		setModal(true);
		FormLayout form=new FormLayout();
		addComponent(form);
		
		String cap=isNew()?"Lisa uus ":"";
		setCaption(cap+"'"+data.getCategory().getName()+"' õigusakt");
		
		OptionGroup citySelect = new OptionGroup("Tüüp", liigid);		
		binder = new ViewModelBinder<KovLegislation>(form, data==null?new KovLegislation():data);
		binder.addComponent(citySelect, "type");
		TextField component = new TextField("Õigusakt");
		component.setWidth("100%");
		binder.addComponent(component, "name");
		TextField component2 = new TextField("Viide");
		component2.setWidth("100%");
		binder.addComponent(component2, "url");
		
		TextArea component3 = new TextArea("Kirjeldus");
		component3.setWidth("100%");
		binder.addComponent(component3, "description");
		 
		
		if(isNew()){
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


	public void setIsNew(boolean is_new) {
		this.is_new = is_new;
	}


	public boolean isNew() {
		return is_new;
	}


}