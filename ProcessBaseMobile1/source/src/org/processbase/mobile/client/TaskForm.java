package org.processbase.mobile.client;

import de.enough.polish.ui.Choice;
import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.Locale;
import de.enough.polish.xml.XmlDomNode;

/**
 *
 * @author mgubaidullin
 */
public class TaskForm extends Form {

    private Task task;
    private StringItem taskCaptionItem;
    private StringItem taskDescItem;

    public TaskForm() {
        //#style taskForm
        super(Locale.get("taskform.title"));
    }

    public void setTask(Task task) {
        this.task = task;
        //#style taskformcaption
        taskCaptionItem = new StringItem(task.getCaption(), null);
        this.append(taskCaptionItem);
        if (task.isMobile()) {
            for (int i = 0; i < task.getForm().getChildCount(); i++) {
                XmlDomNode item = task.getForm().getChild(i);
                if (item.getName().compareTo("textField") == 0) {
                    //#style forminput
                    TextField textField = new TextField(item.getAttribute("label"), item.getAttribute("text"), Integer.parseInt(item.getAttribute("maxSize")), Integer.parseInt(item.getAttribute("constraints")));
                    this.append(textField);
                } else if (item.getName().compareTo("choiceGroup") == 0) {
                    //#style taskformChoice
                    ChoiceGroup choiceGroup = new ChoiceGroup(item.getAttribute("label"), Choice.POPUP);
                    for (int y = 0; y < item.getChildCount(); y++) {
                        XmlDomNode choiceItem = item.getChild(y);
                        //#style choiceItem
                        choiceGroup.append(choiceItem.getText(), null);
                    }
                    this.append(choiceGroup);
                } else if (item.getName().compareTo("text") == 0) {
                    //#style text
                    StringItem text = new StringItem(null, item.getAttribute("text"));
                    this.append(text);
                }
            }
        } else {
            //#style taskformdesc
            taskDescItem = new StringItem(null, task.getDesc());
            this.append(taskDescItem);
        }
    }

    public Task getTask() {
        return task;
    }
}
