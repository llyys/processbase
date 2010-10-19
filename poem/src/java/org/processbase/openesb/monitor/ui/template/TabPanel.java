package org.processbase.openesb.monitor.ui.template;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 *
 * @author mgubaidullin
 */
public class TabPanel extends VerticalLayout implements Button.ClickListener{

    public ButtonBar buttonBar = new ButtonBar();
    public HorizontalLayout layout = new HorizontalLayout();
    public Button refreshBtn = new Button();
    private Label emptyLabel = new Label("");


    public TabPanel(String caption) {
        setCaption(caption);
        setMargin(true, false, true, false);
        setSpacing(false);
//        setStyleName(Reindeer.LAYOUT_WHITE);
        setSizeFull();

        buttonBar.addComponent(emptyLabel, 0);
        buttonBar.setExpandRatio(emptyLabel, 1);

        refreshBtn.setCaption("Refresh");
        refreshBtn.addListener((Button.ClickListener)this);
        buttonBar.addComponent(refreshBtn, 1);
        buttonBar.setComponentAlignment(refreshBtn, Alignment.MIDDLE_RIGHT);
        
        addComponent(buttonBar);

        layout.setSpacing(true);
        layout.setMargin(true, false, false, false);
//        layout.setStyleName(Reindeer.LAYOUT_BLUE);
        layout.setSizeFull();
        addComponent(layout);
        this.setExpandRatio(layout, 1);
    }

    public void buttonClick(ClickEvent event) {
    }
}
