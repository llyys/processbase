package ee.kovmen.ui.legislation;

import org.caliburn.viewmodels.ViewModelBinder;
import org.processbase.ui.core.template.PbWindow;

import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import ee.kovmen.data.LegislationData;
import ee.kovmen.entities.KovLegislation;
import ee.kovmen.entities.KovLegislationType;

public class KovLegislationTypeWindow extends PbWindow{
	private final KovLegislationType data;
	private ViewModelBinder<KovLegislationType> binder;
	
	private boolean hasChanged;
	public KovLegislationTypeWindow(KovLegislationType data){
		this.data = data;		
	}
	public void initUI(){
		FormLayout form=new FormLayout();
		addComponent(form);
		
		binder = new ViewModelBinder<KovLegislationType>(form, data==null?new KovLegislationType():data);		
		binder.addComponent(new TextField("Õigusakti tüüp"), "name");
		
		form.addComponent(new Button("Salvesta", new ClickListener() {			
			

			public void buttonClick(ClickEvent event) {
				LegislationData.getCurrent().getHibernate().save(binder.getBean());
				hasChanged=true;
				close();				
			}
		}));
	}
	
	public boolean isChanged() {
		return hasChanged;
	}
}
