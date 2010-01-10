package org.processbase.mobile.client;

import de.enough.polish.ui.Form;
import de.enough.polish.ui.Item;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Locale;

/**
 *
 * @author mgubaidullin
 */
public class TaskList extends Form {

    public TaskList() {
        //#style taskListform
        super(Locale.get("tasklist.title"));
    }

    public void setTasks(ArrayList tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            //#style taskchoice
            TaskChoiceItem item = new TaskChoiceItem((Task) tasks.get(i));
            this.append(item);
        }
    }

    public void itemStateChanged(Item arg0) {
    }

    public void deleteTask(Task task) {
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i) instanceof TaskChoiceItem && ((TaskChoiceItem) this.get(i)).getTask().getUuid().compareTo(task.getUuid()) == 0) {
                this.delete(i);
            }
        }
    }
}
