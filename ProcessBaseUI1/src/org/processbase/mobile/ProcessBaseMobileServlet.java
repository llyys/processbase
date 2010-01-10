package org.processbase.mobile;

import de.enough.polish.xml.XmlDomNode;
import de.enough.polish.xml.XmlDomParser;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ow2.bonita.facade.def.majorElement.ActivityDefinition;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.processbase.bpm.BPMModule;
import org.processbase.util.Constants;
import org.processbase.util.ProcessBaseClassLoader;
import org.processbase.util.db.HibernateUtil;
import org.processbase.util.db.PbActivityUi;
import org.processbase.util.ldap.LdapUtils;
import org.processbase.util.ldap.User;

/**
 *
 * @author mgubaidullin
 */
public class ProcessBaseMobileServlet extends HttpServlet {

    private User currentUser;
    private BPMModule bpmModule;
    private HibernateUtil hutil;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/xml");
        response.setLocale(new Locale("ru_RU"));
        response.setCharacterEncoding("UTF-8");
        super.service(request, response);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if (!Constants.LOADED) {
            Constants.loadConstants();
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            authenticate(request.getParameter("username"), request.getParameter("password"));

            if (request.getParameter("command").equals("getTasks")) {
                PrintWriter out = response.getWriter();
                out.println(getTasks());
                out.close();
            } else if (request.getParameter("command").equals("getTask")) {
                PrintWriter out = response.getWriter();
                out.println(getTask(request.getParameter("uuid")));
                out.close();
            } else if (request.getParameter("command").equals("getProcesses")) {
                PrintWriter out = response.getWriter();
                out.println(getProcesses());
                out.close();
            }

//            response.sendError(HttpServletResponse.SC_OK);
        } catch (NamingException ex) {
            ex.printStackTrace();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Ошибка авторизации! " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Ошибка! " + ex.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            XmlDomNode requestXML = XmlDomParser.parseTree(request.getInputStream(), "UTF-8");
            request.getInputStream().close();
            authenticate(requestXML.getAttribute("u"), requestXML.getAttribute("p"));

            if (requestXML.getAttribute("c").equals("getTasks")) {
                PrintWriter out = response.getWriter();
                out.println(getTasks());
                out.close();
            } else if (requestXML.getAttribute("c").equals("getTask")) {
                PrintWriter out = response.getWriter();
                out.println(getTask(requestXML.getChild(0).getText()));
                out.close();
            } else if (requestXML.getAttribute("c").equals("completeTask")) {
                PrintWriter out = response.getWriter();
                out.println(completeTask(requestXML.getChild(0).getText(), requestXML.getChild(1)));
                out.close();
            } else if (requestXML.getAttribute("c").equals("getProcesses")) {
                PrintWriter out = response.getWriter();
                out.println(getProcesses());
                out.close();
            }
        } catch (NamingException ex) {
            ex.printStackTrace();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Ошибка авторизации! " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Ошибка! " + ex.getMessage());
        }
    }

    public void authenticate(String login, String password) throws NamingException, Exception {
        LdapUtils ldapUtils = new LdapUtils(login, null, password);
        this.currentUser = ldapUtils.authenticate();
        this.bpmModule = new BPMModule(login);
        this.hutil = new HibernateUtil();
    }

    private String getTasks() throws Exception {
        StringBuffer result = new StringBuffer();
        Collection<ActivityInstance<TaskInstance>> tasks = bpmModule.getActivities(ActivityState.READY);
        tasks.addAll(bpmModule.getActivities(ActivityState.EXECUTING));
        tasks.addAll(bpmModule.getActivities(ActivityState.SUSPENDED));
        result.append("<tasks>");
        for (ActivityInstance<TaskInstance> task : tasks) {
            result.append("<task ");
            result.append("uuid=\"" + task.getUUID() + "\" ");
            Date currentDate = new Date(System.currentTimeMillis());
            String createdDate = null;
            GregorianCalendar c = new GregorianCalendar(currentDate.getYear()+1900, currentDate.getMonth(), currentDate.getDate());
            if (task.getBody().getCreatedDate().after(c.getTime())) {
                createdDate = String.format("%1$tH:%1$tM", task.getBody().getCreatedDate());
            } else {
                createdDate = String.format(new Locale("RU"), "%1$td %1$tb", task.getBody().getCreatedDate());
            }
            result.append("createdDate=\"" + createdDate + "\" ");
            ActivityDefinition activityDefinition = bpmModule.getProcessActivity(task.getProcessDefinitionUUID(), task.getActivityId());
            result.append("caption=\"" + activityDefinition.getName() + "\" ");
            result.append("desc=\"" + activityDefinition.getDescription() + "\" ");
            PbActivityUi pbActivityUi = hutil.findPbActivityUi(activityDefinition.getUUID().toString());
            result.append("mobile=\"" + pbActivityUi.getMobile() + "\" ");
            result.append("isassigned=\"" + (task.getBody().isTaskAssigned() ? "T" : "F") + "\">");
            result.append("</task>");
        }
        result.append("</tasks>");
        return result.toString();
    }

    private String getTask(String uuid) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ProcessNotFoundException, ActivityNotFoundException, Exception {
        ActivityInstance activityInstance = bpmModule.getActivityInstance(new ActivityInstanceUUID(uuid));
        ActivityDefinition activityDefinition = bpmModule.getProcessActivity(activityInstance.getProcessDefinitionUUID(), activityInstance.getActivityId());
        PbActivityUi pbActivityUi = hutil.findPbActivityUi(activityDefinition.getUUID().toString());
        Class b = ProcessBaseClassLoader.getCurrent().loadClass(pbActivityUi.getMobileUiClass());
        MobileTask mobileTask = (MobileTask) b.newInstance();
        mobileTask.setTaskInfo(bpmModule, null, activityInstance, currentUser);
        return mobileTask.getForm();
    }

    private String completeTask(String uuid, XmlDomNode form) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ProcessNotFoundException, ActivityNotFoundException, Exception {
        ActivityInstance activityInstance = bpmModule.getActivityInstance(new ActivityInstanceUUID(uuid));
        ActivityDefinition activityDefinition = bpmModule.getProcessActivity(activityInstance.getProcessDefinitionUUID(), activityInstance.getActivityId());
        PbActivityUi pbActivityUi = hutil.findPbActivityUi(activityDefinition.getUUID().toString());
        Class b = ProcessBaseClassLoader.getCurrent().loadClass(pbActivityUi.getMobileUiClass());
        MobileTask mobileTask = (MobileTask) b.newInstance();
        mobileTask.setTaskInfo(bpmModule, null, activityInstance, currentUser);
        return mobileTask.completeForm(form);
    }

    private String getProcesses() {
        return "<form><textField label=\"Ф.И.О\" maxSize=\"100\" constraints=\"0\">getProcesses</textField></form>";
    }
}
