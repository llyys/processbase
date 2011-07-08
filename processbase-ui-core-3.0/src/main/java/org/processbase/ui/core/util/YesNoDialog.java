package org.processbase.ui.core.util;


import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;

public class YesNoDialog extends Window implements Button.ClickListener {

    Callback callback;
    Button yes = new Button("Yes", this);
    Button no = new Button("No", this);

    public YesNoDialog(String caption, String question, Callback callback) {
        super(caption);

        setModal(true);

        this.callback = callback;

        if (question != null) {
            setCaption(question);
        }

        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(yes);
        hl.addComponent(no);
        addComponent(hl);
       
    }

    public void buttonClick(ClickEvent event) {
        if (getParent() != null) {
            ((Window) getParent()).removeWindow(this);
        }
        callback.onDialogResult(event.getSource() == yes);
    }

    public interface Callback {

        public void onDialogResult(boolean resultIsYes);
    }

}
