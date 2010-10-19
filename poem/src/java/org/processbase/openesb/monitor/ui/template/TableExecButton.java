package org.processbase.openesb.monitor.ui.template;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import org.processbase.openesb.monitor.POEMConstants;

/**
 *
 * @author mgubaidullin
 */
public class TableExecButton extends Button {

    private Object tableValue = null;
    private int action = POEMConstants.ACTION_DEFAULT;

    public TableExecButton(String caption, String description, String iconName, Object tv, ClickListener cl) {
        super();
        this.setCaption(caption);
        this.addListener(cl);
        this.tableValue = tv;
        this.setDescription(description);
        this.setStyleName(Button.STYLE_LINK);
    }

    public TableExecButton(String description, String iconName, Object tv, ClickListener cl) {
        super();
        this.addListener(cl);
        this.tableValue = tv;
        this.setDescription(description);
        this.setStyleName(Button.STYLE_LINK);
        this.setIcon(new ThemeResource(iconName));
    }

    public TableExecButton(String description, String iconName, Object tv, ClickListener cl, int action) {
        super();
        this.action = action;
        this.addListener(cl);
        this.tableValue = tv;
        this.setDescription(description);
        this.setStyleName(Button.STYLE_LINK);
        this.setIcon(new ThemeResource(iconName));
    }

    public TableExecButton(String caption, String description, String iconName, Object tv, ClickListener cl, int action) {
        super();
        this.action = action;
        this.setCaption(caption);
        this.addListener(cl);
        this.tableValue = tv;
        this.setDescription(description);
        this.setStyleName(Button.STYLE_LINK);
    }

    public Object getTableValue() {
        return tableValue;
    }

    public void setTableValue(Object tableValue) {
        this.tableValue = tableValue;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }


}