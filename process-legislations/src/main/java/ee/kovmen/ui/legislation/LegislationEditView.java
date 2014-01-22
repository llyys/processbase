package ee.kovmen.ui.legislation;


import java.util.Arrays;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.UserError;
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

import org.apache.commons.lang.StringUtils;
import org.caliburn.viewmodels.ViewModelBinder;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.bonita.forms.Validators;
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
		
		String cap=isNew() ? getMessage("legislationAddNew"): getMessage("legislationEdit");
		setCaption(cap);
		
		binder = new ViewModelBinder<KovLegislation>(form, data == null ? new KovLegislation() : data);
		
		final OptionGroup typeSelect = new OptionGroup(getMessage("legislationType"), liigid);		
		typeSelect.setRequired(true);
		typeSelect.setRequiredError(getMessage("requiredField"));
		typeSelect.setValidationVisible(true);
		binder.addComponent(typeSelect, "type");
		
		final TextField component = new TextField(getMessage("legislation"));
		component.setWidth("100%");
		component.setRequired(true);
		component.setRequiredError(getMessage("requiredField"));
		component.setValidationVisible(true);
		binder.addComponent(component, "name");
		
		final TextField component2 = new TextField(getMessage("legislationUrl"));
		component2.setWidth("100%");
		component2.setRequired(true);
		component2.setRequiredError(getMessage("requiredField"));
		component2.setValidationVisible(true);
		binder.addComponent(component2, "url");
		
		TextArea component3 = new TextArea(getMessage("legislationDescription"));
		component3.setWidth("100%");
		binder.addComponent(component3, "description");
		 
		
		if(isNew()){
			form.addComponent(new Button(getMessage("btnAdd"), new ClickListener() {			
				public void buttonClick(ClickEvent event) {
					try{
						if(typeSelect.isValid() && component.isValid() && component2.isValid()){
							KovLegislation legislation = binder.getBean();
							LegislationData.getCurrent().getHibernate().save(legislation);
							close();
						}
					}catch (Exception e) {
						LOGGER.error("", e);
					}
				}
			}));
		}
		else{
			HorizontalLayout hl=new HorizontalLayout();
			form.addComponent(hl);
			hl.setSpacing(true);
			hl.addComponent(new Button(getMessage("btnDelete"), new ClickListener() {			
				public void buttonClick(ClickEvent event) {
					DeleteTask();
					close();
				}
			}));
			hl.addComponent(new Button(getMessage("btnEdit"), new ClickListener() {			
				public void buttonClick(ClickEvent event) {
					try{
						if(typeSelect.isValid() && component.isValid() && component2.isValid()){
							KovLegislation legislation = binder.getBean();
							LegislationData.getCurrent().getHibernate().saveOrUpdate(legislation);
							close();
						}
					}catch (Exception e) {
						LOGGER.error("", e);
					}
				}
			}));
			
			
		}
		
	}
	private void DeleteTask(){
		ConfirmDialog.show(this.getParent(),
				getMessage("windowCaptionConfirm"),getMessage("legislationDeleteConfirm"),
				getMessage("btnYes"),getMessage("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                            	LegislationData.getCurrent().DeleteLegislation(data);
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

	private String getMessage(String key){
		try {
			return ProcessbaseApplication.getString(key);
		} catch (Exception e) {
			//ignore
		}
		return key;
	}

}