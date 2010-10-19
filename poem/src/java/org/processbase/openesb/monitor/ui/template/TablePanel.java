package org.processbase.openesb.monitor.ui.template;

import com.vaadin.ui.Table;

/**
 *
 * @author mgubaidullin
 */
public class TablePanel extends TabPanel {

    protected Table table = new Table();

    public TablePanel(String caption) {
        super(caption);
        table.setSizeFull();
        layout.addComponent(table);
    }

    public void initTableUI() {
    }
}
