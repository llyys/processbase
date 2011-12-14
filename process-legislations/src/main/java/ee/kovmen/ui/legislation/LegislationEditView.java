package ee.kovmen.ui.legislation;


import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;

import org.caliburn.viewmodels.ViewModelBinder;
import org.processbase.ui.core.template.WorkPanel;

import ee.kovmen.data.LegislationData;
import ee.kovmen.entities.Oigusakt;

public class LegislationEditView  extends WorkPanel implements ClickListener {
	
	private final Oigusakt data;
	private ViewModelBinder<Oigusakt> binder;
	public LegislationEditView(Oigusakt data){
		this.data = data;
	}
	
	@Override 
	public void initUI(){
		FormLayout form=new FormLayout();
		addComponent(form);
		form.setMargin(true);
		
		binder = new ViewModelBinder<Oigusakt>(form, data);		
		binder.addComponent(new TextField("Nimi"), "name");
		binder.addComponent(new TextField("URL"), "url");
		binder.addComponent(new TextArea("Kirjeldus"), "description");
		
		form.addComponent(new Button("Salvesta", this));
		super.initUI();
	}

	public void buttonClick(ClickEvent event) {
		LegislationData.getCurrent().SaveLegislation(binder.getBean());
		
	}

}
