package org.processbase.mobile.client;

import de.enough.polish.event.ThreadedCommandListener;
import de.enough.polish.io.RmsStorage;
import de.enough.polish.ui.Alert;
import de.enough.polish.ui.AlertType;
import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.CommandListener;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TextField;
import de.enough.polish.ui.splash.ApplicationInitializer;
import de.enough.polish.ui.splash.InitializerSplashScreen;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Locale;
import de.enough.polish.xml.XmlDomNode;
import de.enough.polish.xml.XmlDomParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * @author mgubaidullin
 */
public class ProcessBaseMobile extends MIDlet
        implements CommandListener, ApplicationInitializer {

    private Command exitCmd;
    private Command loginCmd;
    private Command backCmd;
    private Command openCmd;
    private Command createCmd;
    private Command refreshCmd;
    private Command completeCmd;
    private LoginForm loginForm;
    private TaskList taskList;
    private TaskForm taskForm;
    private Display display;
    private StringItem message;
    private PbConnector pbConnector;
    private RmsStorage storage;
    public static ProcessBaseBO processBaseBO;
    private InitializerSplashScreen splashScreen;
//    #define isdemo=true;

    public void commandAction(Command cmd, Displayable disp) {
        try {
            if (cmd == this.exitCmd) {
                destroyApp(false);
                notifyDestroyed();
            } else if (cmd == this.loginCmd) {
                ProcessBaseMobile.processBaseBO.setUsername(this.loginForm.username.getText());
                ProcessBaseMobile.processBaseBO.setUrl(this.loginForm.url.getText());
                ProcessBaseMobile.processBaseBO.setPassword(this.loginForm.password.getString());
                openTaskList();
            } else if (cmd == this.openCmd) {
                openTaskFrom(((TaskChoiceItem) this.taskList.getCurrentItem()).getTask());
            } else if (cmd == this.refreshCmd) {
                openTaskList();
            } else if (cmd == this.backCmd && this.display.getCurrent().equals(taskForm)) {
                this.display.setCurrent(taskList);
            } else if (cmd == this.completeCmd) {
                completeTask();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    protected void startApp() throws MIDletStateChangeException {
        display = Display.getDisplay(this);
        Image splashImage = null;
        try {
            splashImage = Image.createImage("/logo.png");
        } catch (Exception e) {
        }
        this.splashScreen = new InitializerSplashScreen(
                this.display,
                splashImage,
                0xFFFFFF,
                null, // no message, so we proceed to the initial screen as soon as possible
                0, // since we have no message, there's no need to define a message color
                this);
        this.display.setCurrent(this.splashScreen);
    }

    protected void openTaskList() throws UnsupportedEncodingException, IOException, Exception {
        this.taskList.deleteAll();
        int messageID = taskList.append(message);
        if (!this.display.getCurrent().equals(taskList)) {
            this.display.setCurrent(taskList);
        }
        ArrayList tasks = getTasks();
        this.taskList.delete(messageID);
        this.taskList.setTasks(tasks);
    }

    protected void openTaskFrom(Task task) throws UnsupportedEncodingException, IOException {
        this.taskForm.deleteAll();
        this.taskForm.removeAllCommands();
        int messageID = this.taskForm.append(message);
        this.display.setCurrent(this.taskForm);
        this.taskForm.addCommand(this.backCmd);
        if (task.isMobile()) {
            task.setForm(getTask(task));
            this.taskForm.addCommand(this.completeCmd);
        }
        this.taskForm.setTask(task);
        this.taskForm.delete(messageID);
    }

    private ArrayList getTasks() throws UnsupportedEncodingException, IOException, Exception {
        ArrayList result = new ArrayList();
        //#if isdemo
//#         for (int i = 0; i < 10; i++) {
//#             Task task = new Task("1" + i, Locale.get("demo.text5"), Locale.get("demo.text1"), true, Locale.get("demo.text2"));
//#             result.add(task);
//#             task = new Task("0" + i, "12:34", Locale.get("demo.text3"), false, Locale.get("demo.text4"));
//#             result.add(task);
//#         }
        //#else
        StringBuffer sb = new StringBuffer();
        sb.append("<r c=\"getTasks\" u=\"admin\" p=\"admin\">");
        sb.append("</r>");
        XmlDomNode root = pbConnector.post("http://" + processBaseBO.getUrl() + "/ProcessBaseMobileServer1", sb.toString());
        for (int i = 0; i < root.getChildCount(); i++) {
            XmlDomNode child = root.getChild(i);
            Task task = new Task(child.getAttribute("uuid"), child.getAttribute("createdDate"), child.getAttribute("caption"), child.getAttribute("mobile").compareTo("T") == 0, child.getAttribute("desc"));
            result.add(task);
        }
        //#endif
        return result;
    }

    private XmlDomNode getTask(Task task) throws UnsupportedEncodingException, IOException {
        XmlDomNode result = null;
        //#if isdemo
//#         result = XmlDomParser.parseTree(Locale.get("demo.form1"), "UTF-8");
        //#else
        StringBuffer sb = new StringBuffer();
        sb.append("<r c=\"getTask\" u=\"admin\" p=\"admin\">");
        sb.append("<uuid>" + task.getUuid() + "</uuid>");
        sb.append("</r>");
        result = pbConnector.post("http://" + processBaseBO.getUrl() + "/ProcessBaseMobileServer1", sb.toString());
        //#endif
        return result;
    }

    private void completeTask() throws UnsupportedEncodingException, IOException, Exception {
        //#if isdemo
//#         result = XmlDomParser.parseTree(Locale.get("demo.form1"), "UTF-8");
        //#else
        StringBuffer sb = new StringBuffer();
        sb.append("<r c=\"completeTask\" u=\"admin\" p=\"admin\">");
        sb.append("<uuid>" + taskForm.getTask().getUuid() + "</uuid>");
        sb.append("<f>");
        for (int i = 0; i < taskForm.size(); i++) {
            if (taskForm.get(i) instanceof ChoiceGroup) {
                sb.append("<choiceGroup>");
                sb.append(((ChoiceGroup) taskForm.get(i)).getString(((ChoiceGroup) taskForm.get(i)).getSelectedIndex()));
                sb.append("</choiceGroup>");
            } else if (taskForm.get(i) instanceof TextField) {
                sb.append("<textField>");
                sb.append(((TextField) taskForm.get(i)).getText());
                sb.append("</textField>");
            }
        }
        sb.append("</f>");
        sb.append("</r>");
        XmlDomNode result = pbConnector.post("http://" + processBaseBO.getUrl() + "/ProcessBaseMobileServer1", sb.toString());
        if (result.getText().compareTo("OK") != 0) {
            throw new Exception(result.getText());
        }
        //#endif
    }

    private void showError(String message) {
        //#style messageAlert
        Alert alert = new Alert(Locale.get("alert.error"), message, null, AlertType.ERROR);
        alert.setTimeout(Alert.FOREVER);
        this.display.setCurrent(alert, this.display.getCurrent());
    }

    private void loadStorage() {
        try {
            this.storage = new RmsStorage(null);
            ProcessBaseMobile.processBaseBO = (ProcessBaseBO) this.storage.read("ProcessBaseBO");
        } catch (IOException ex) {
            ProcessBaseMobile.processBaseBO = new ProcessBaseBO();
        }

    }

    private void saveStorage() throws IOException {
        ProcessBaseMobile.processBaseBO.setUsername(this.loginForm.username.getText());
        ProcessBaseMobile.processBaseBO.setUrl(this.loginForm.url.getText());
        ProcessBaseMobile.processBaseBO.setPassword(null);
        this.storage.save(ProcessBaseMobile.processBaseBO, "ProcessBaseBO");
    }

    protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
        try {
            saveStorage();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected void pauseApp() {
        // TODO Auto-generated method stub
    }

    public Displayable initApp() {
        // create commands
        exitCmd = new Command(Locale.get("cmd.exit"), Command.EXIT, 10);
        loginCmd = new Command(Locale.get("cmd.login"), Command.OK, 20);
        backCmd = new Command(Locale.get("cmd.back"), Command.BACK, 30);
        openCmd = new Command(Locale.get("cmd.open"), Command.OK, 90);
        createCmd = new Command(Locale.get("cmd.create"), Command.ITEM, 90);
        refreshCmd = new Command(Locale.get("cmd.refresh"), Command.ITEM, 90);
        completeCmd = new Command(Locale.get("cmd.send"), Command.ITEM, 90);
        // create objects
        taskList = new TaskList();
        taskForm = new TaskForm();
        //#style loadingmessage
        message = new StringItem(null, Locale.get("tasklist.loading"));
        pbConnector = new PbConnector();
        loadStorage();
        // prepare loginForm
        loginForm = new LoginForm();
        loginForm.addCommand(this.loginCmd);
        loginForm.addCommand(this.exitCmd);
        loginForm.setCommandListener(new ThreadedCommandListener(this));

        // prepare taskList
        this.taskList.addCommand(this.openCmd);
        this.taskList.addCommand(this.createCmd);
        this.taskList.addCommand(this.refreshCmd);
        this.taskList.addCommand(this.exitCmd);
        this.taskList.setCommandListener(new ThreadedCommandListener(this));

        // prepare taskForm
        this.taskForm.setCommandListener(this);
        return loginForm;
    }
}
