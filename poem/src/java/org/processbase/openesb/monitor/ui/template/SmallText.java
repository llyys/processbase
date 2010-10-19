package org.processbase.openesb.monitor.ui.template;

import com.vaadin.ui.Label;
import com.vaadin.ui.themes.Reindeer;

/**
 *
 * @author mgubaidullin
 */
public class SmallText extends Label {

        public SmallText(String caption) {
            super(caption);
            setStyleName(Reindeer.LABEL_SMALL);
        }
    }
