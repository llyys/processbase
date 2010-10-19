package org.processbase.openesb.monitor.ui.template;

import com.vaadin.addon.treetable.TreeTable;

/**
 *
 * @author mgubaidullin
 */
public class TreeTablePanel extends TabPanel {

    protected TreeTable treeTable = new TreeTable();

    public TreeTablePanel(String caption) {
        super(caption);
        treeTable.setSizeFull();
        this.layout.addComponent(treeTable);
    }

    public void initTableUI() {

    }

     
}
