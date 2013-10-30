package org.processbase.ui.bpm.generator.view;

//this panel handles only one task view
public class UserTaskPanel /*extends PbPanel*/{
	/*
	
	private TaskManager taskManager = null;
	
	//create new process
	public UserTaskPanel(TaskManager taskManager){		
		this.taskManager=taskManager;
	}
	
	//open existing task
	public void initUI() {		
		setSizeFull();
		
		List<Component> pages=taskManager.GeneratePages();
		if(pages.size()>1)
		{
			Accordion accordionLayout=new Accordion();
			accordionLayout.setSizeFull();
			
			for (Component page : pages) {
				accordionLayout.addTab(page, page.getCaption(), null);
			}
			this.addComponent(accordionLayout);
		}
		
	}
	
	

	private static final String FIELD = "field_";
	
	private Component RenderPageComponents(Page page) {
		// TODO Auto-generated method stub
		int gridColumns=1;
		int gridRows=page.getWidgets().getWidgetsAndGroups().size();
		taskManager.registerActions(page.getActions().getActions()); 
		TableStyle tableStyle = barResource.getTableStyle(page);
		if(tableStyle!=null)
		{
			gridColumns=tableStyle.getColumns();
			gridRows=tableStyle.getRows();
		}
		
		GridLayout gridLayout = new GridLayout(gridColumns, gridRows);
		gridLayout.setSizeFull();
		int row1 = 0;
		int row2 = 0;
		
		for (Object wg : page.getWidgets().getWidgetsAndGroups()) {
			Component c = null;
			if (wg instanceof Widget) {
				Widget widget = (Widget) wg;
				c = createComponent(widget);

				if (c != null) {
										
					int col1 = 0;
					int col2 = 0;
					if (tableStyle!=null) {
						ComponentStyle componentStyle = tableStyle.getElements().get(widget.getId());
						
						col1 = componentStyle.getPosition().getFColumn();
						row1 = componentStyle.getPosition().getFRow();
						col2 = componentStyle.getPosition().getTColumn();
						row2 = componentStyle.getPosition().getTRow();
						
						CSSProperty cssProperty = componentStyle.getCss();
						gridLayout.addComponent(c, col1, row1, col2, row2);
					} else {
						gridLayout.addComponent(c, col1, row1, col2, row2);
						row2++;
						row1++;
					}
				}
			} else if (wg instanceof WidgetGroup) {
				// TODO WidgetGroup
			}
		}
	
		return gridLayout;
		
	}
	



	private List<Page> LoadPages(String activityName){
		//loaded an xml from /forms/forms.xml file
		org.processbase.ui.core.bonita.forms.FormsDefinition.Process process=barResource.getFormsDefinition().getProcesses().get(0);
		PageFlow pageFlow=process.getPageflow();
		//find first page
		Activity activity=findActivityByName(activityName, process);
		if(activity==null)
			return GenerateDynamicPage(activityName);
		
		if (activity.getPageflow() != null) {
			return activity.getPageflow().getPages().getPages();			
		}
		return GenerateDynamicPage(activityName);
	}
	
	

	private Activity findActivityByName(String name, org.processbase.ui.core.bonita.forms.FormsDefinition.Process process){
		for (Activity activity : process.getActivities().getActivities()) {
			if(name==null)
				return activity;
			if(name.equals(activity.getName()))
				return activity;
		}
		return null;
	}
	
	public void ValidateFields() throws InvalidValueException, EmptyValueException{
		for (TaskField field : taskManager.getIterator()) {
			field.validate();
		}
	}
	
	public void FinishTask(){
		ValidateFields();
	}

	public void setProcessDataFields(Map<String, DataFieldDefinition> processDataFields) {
		this.processDataFields = processDataFields;
	}

	public Map<String, DataFieldDefinition> getProcessDataFields() {
		return processDataFields;
	}
*/	
}
