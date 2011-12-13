package ee.kovmen.ui.legislation;


import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;
import org.processbase.ui.core.template.WorkPanel;

import ee.kovmen.entities.Oigusakt;

public class LegislationEditView  extends WorkPanel {
	
	private final Oigusakt data;
	private boolean isEditMode=true;
	private TextField txtNimi;
	private TextField txtUrl;
	private TextField txtComment;
	
	public LegislationEditView(Oigusakt data){
		if(data==null){
			data=new Oigusakt();
			isEditMode= false;//set mode to create
		}
		this.data = data;
		
	}
	
	@Override 
	public void initUI(){
		FormLayout form=new FormLayout();
		addComponent(form);
		
		txtNimi = new TextField("Nimi", data.getName());
		form.addComponent(txtNimi);
		
		txtUrl = new TextField("URL", data.getUrl());
		form.addComponent(txtUrl);
		
		txtComment = new TextField("Comment", data.getDescription());
		form.addComponent(txtComment);
		
		form.addComponent(new Button("Salvesta", new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				if(isEditMode){
					
				}
				else {
					Oigusakt akt=new Oigusakt();
					akt.setDescription((String)txtComment.getValue());
				}
			}
		}));
	
		super.initUI();
	}

}
