package org.processbase.openesb.monitor.ui.template;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;

/**
 *
 * @author marat
 */
public class TableExecButtonBar extends HorizontalLayout{

    public TableExecButtonBar() {
        super();
    }

    public void addButton(TableExecButton teb) {
        this.addComponent(teb);
    }
    public void addButton(TableExecButton teb, Alignment alignment) {
        this.addComponent(teb);
        this.setComponentAlignment(teb, alignment);
    }
}