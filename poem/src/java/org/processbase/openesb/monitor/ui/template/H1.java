package org.processbase.openesb.monitor.ui.template;

import com.vaadin.ui.Label;
import com.vaadin.ui.themes.Reindeer;

/**
 *
 * @author mgubaidullin
 */
public class H1 extends Label {

    public H1(String caption) {
        super(caption);
        setSizeUndefined();
        setStyleName(Reindeer.LABEL_H1);
    }
}
