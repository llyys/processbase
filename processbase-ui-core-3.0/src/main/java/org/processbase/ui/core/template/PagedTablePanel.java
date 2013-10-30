package org.processbase.ui.core.template;

import org.processbase.ui.core.ProcessbaseApplication;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.ResizeEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

public abstract class PagedTablePanel extends WorkPanel implements
		Window.CloseListener, Window.ResizeListener {

	/** Serial version UID. */
	private static final long serialVersionUID = -5436171728536662914L;

	protected Table table = new Table();

	private int startPosition = 0;
	private int maxResults = 25;

	private Button previous;
	private Button next;

	@Override
	public void initUI() {
		table.setSizeFull();
		table.setPageLength(15);
		table.addStyleName("striped");

		VerticalLayout tableLayout = new VerticalLayout();
		tableLayout.addComponent(table);
		tableLayout.addComponent(createControls());

		horizontalLayout.addComponent(tableLayout, 0);
		horizontalLayout.setComponentAlignment(tableLayout, Alignment.TOP_LEFT);
		horizontalLayout.setExpandRatio(tableLayout, 1);
	}

	public void refreshTable() {
		if(!isInitialized()){
			initUI();
		}
		if(maxResults <= 25){
			table.setPageLength(maxResults);
		}else{
			table.setPageLength(25);
		}
		int results = 0;
		if(isInitialized()){
			results = load(startPosition, maxResults);
		}
		if (previous != null && next != null) {
			if (startPosition == 0) {
				previous.setEnabled(false);
			}else{
				previous.setEnabled(true);
			}
			if (results < maxResults) {
				next.setEnabled(false);
			}else{
				next.setEnabled(true);
			}
		}
	}

	public int load(int startPosition, int maxResults) {
		return 0;
	}

	public HorizontalLayout createControls() {

		Label itemsPerPageLabel = new Label(getText("pageSize"));
		final ComboBox itemsPerPageSelect = new ComboBox();

		itemsPerPageSelect.addItem("5");
		itemsPerPageSelect.addItem("10");
		itemsPerPageSelect.addItem("25");
		itemsPerPageSelect.addItem("50");
		itemsPerPageSelect.addItem("100");
		itemsPerPageSelect.setImmediate(true);
		itemsPerPageSelect.setNullSelectionAllowed(false);
		itemsPerPageSelect.setWidth("50px");
		itemsPerPageSelect.addListener(new ValueChangeListener() {
			private static final long serialVersionUID = -2255853716069800092L;

			public void valueChange(Property.ValueChangeEvent event) {
				maxResults = Integer.valueOf(String.valueOf(event.getProperty()
						.getValue()));
				if(isInitialized()){
					refreshTable();
				}
			}
		});
		itemsPerPageSelect.select("25");

		Label pageLabel = new Label("", Label.CONTENT_XHTML);

		final Label currentPageTextField = new Label();
		currentPageTextField.setValue(String
				.valueOf((startPosition / maxResults) + 1));
		currentPageTextField.setStyleName(Reindeer.TEXTFIELD_SMALL);
		currentPageTextField.setImmediate(true);

		pageLabel.setWidth(null);
		currentPageTextField.setWidth("20px");

		HorizontalLayout controlBar = new HorizontalLayout();
		HorizontalLayout pageSize = new HorizontalLayout();
		HorizontalLayout pageManagement = new HorizontalLayout();
		previous = new Button("<");
		previous.addListener(new ClickListener() {
			private static final long serialVersionUID = -355520120491283992L;

			public void buttonClick(ClickEvent event) {
				if (startPosition > 0) {
					startPosition -= maxResults;
				}
				if (startPosition <= 0) {
					startPosition = 0;
				}
				currentPageTextField.setValue(String
						.valueOf((startPosition / maxResults) + 1));
				if(isInitialized()){
					refreshTable();
				}
			}
		});
		next = new Button(">");
		next.addListener(new ClickListener() {
			private static final long serialVersionUID = -1927138212640638452L;

			public void buttonClick(ClickEvent event) {
				startPosition += maxResults;
				currentPageTextField.setValue(String
						.valueOf((startPosition / maxResults) + 1));
				if(isInitialized()){
					refreshTable();
				}
			}
		});

		pageSize.addComponent(itemsPerPageLabel);
		pageSize.addComponent(itemsPerPageSelect);
		pageSize.setComponentAlignment(itemsPerPageLabel, Alignment.MIDDLE_LEFT);
		pageSize.setComponentAlignment(itemsPerPageSelect,
				Alignment.MIDDLE_LEFT);
		pageSize.setSpacing(true);
		pageManagement.addComponent(previous);
		pageManagement.addComponent(pageLabel);
		pageManagement.addComponent(currentPageTextField);
		pageManagement.addComponent(next);
		pageManagement.setComponentAlignment(previous, Alignment.MIDDLE_LEFT);
		pageManagement.setComponentAlignment(pageLabel, Alignment.MIDDLE_LEFT);
		pageManagement.setComponentAlignment(currentPageTextField,
				Alignment.MIDDLE_LEFT);
		pageManagement.setComponentAlignment(next, Alignment.MIDDLE_LEFT);
		pageManagement.setWidth(null);
		pageManagement.setSpacing(true);
		controlBar.addComponent(pageSize);
		controlBar.addComponent(pageManagement);
		controlBar.setComponentAlignment(pageManagement,
				Alignment.MIDDLE_CENTER);
		controlBar.setWidth("100%");
		controlBar.setExpandRatio(pageSize, 1);

		return controlBar;
	}

	public String getText(String key) {
		return ProcessbaseApplication.getString(key, key);
	}

	@Override
	public void windowClose(CloseEvent e) {
		refreshTable();
	}

	@Override
	public void windowResized(ResizeEvent e) {
		refreshTable();
	}

}
