package test;

import com.sun.appserv.security.ProgrammaticLogin;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.facade.runtime.impl.ObjectVariable;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightProcessInstance;
import org.ow2.bonita.util.AccessorUtil;
import org.processbase.bpm.BPMModule;
import org.processbase.demo.mortgage.bo.SCPPReport;

/**
 *
 * @author mgubaidullin
 */
public class Test {
//    final RuntimeAPI runtimeAPI = AccessorUtil.getAPIAccessor(Constants.EJB_ENV).getRuntimeAPI();
//    final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getAPIAccessor(Constants.EJB_ENV).getQueryRuntimeAPI();
//    final ManagementAPI managementAPI = AccessorUtil.getAPIAccessor(Constants.EJB_ENV).getManagementAPI();
//    final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getAPIAccessor(Constants.EJB_ENV).getQueryDefinitionAPI();
//    final ProgrammaticLogin programmaticLogin = new ProgrammaticLogin();

    public static void main(String[] args) throws URISyntaxException, IOException, Exception {

        System.setProperty("org.ow2.bonita.api-type", "EJB3");
        System.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
        System.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
        System.setProperty("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
        System.setProperty("java.naming.provider.url", "iiop://localhost:3700");
        System.setProperty("java.security.auth.login.config", "appclientlogin.conf");

        ProgrammaticLogin programmaticLogin = new ProgrammaticLogin();
        programmaticLogin.login("marat", "marat");
//        Map<String, Object> z = new HashMap<String, Object>();
//        ClientExt client = new ClientExt();
//        client.setFirstName("Марат");
//        client.setLastName("Губайдуллин");
//        client.setTin("600720032779");
//        client.setId(new Long(1));
//        z.put("client", new ObjectVariable(client));
//        z.put("customID", client.getTin());
//        RuntimeAPI r = AccessorUtil.getAPIAccessor().getRuntimeAPI();
//        ProcessInstanceUUID pi = r.instantiateProcess(new ProcessDefinitionUUID("TestConnector1--1.0"), z);
//        System.out.println(pi);

//        AccessorUtil.getAPIAccessor().getWebAPI().addLabel(
//                "600720032779", "marat", "", "",
//      "", true, true, "",
//      null, 1, true);
//        AccessorUtil.getAPIAccessor().getWebAPI().removeLabel("marat", "600720032779");
//        for (Label label : AccessorUtil.getAPIAccessor().getWebAPI().getSystemLabels("marat")) {
//            System.out.println("system label = " + label.getLabelName() + " " + label.toString());
//        }
//        for (Label label : AccessorUtil.getAPIAccessor().getWebAPI().getLabels("marat")) {
//            System.out.println("label = " + label.getLabelName() + " " + label.getOwnerName());
////
//        }

//        BPMModule bpmModule = new BPMModule("marat");
//        for (LightProcessInstance process : bpmModule.getLightUserInstances()) {
//            ProcessDefinition pd = bpmModule.getProcessDefinition(process.getProcessDefinitionUUID());
//            System.out.println(pd);
//            System.out.println(bpmModule.getProcessInstanceVariable(process.getUUID(), "customID"));
//        }

        ProcessInstanceUUID piUUID = new ProcessInstanceUUID("Ипотека--1.0--25");
        SCPPReport xxx = new SCPPReport(new Long(1));
        xxx.setReport("HELLO WORLD");
        AccessorUtil.getAPIAccessor().getRuntimeAPI().setProcessInstanceVariable(piUUID, "scppReport", xxx);

//        for (String key : AccessorUtil.getAPIAccessor().getQueryRuntimeAPI().getProcessInstanceVariables(piUUID).keySet()) {
//
//            System.out.println(AccessorUtil.getAPIAccessor().getQueryRuntimeAPI().getProcessInstanceVariable(piUUID, key).getClass().getName());
//        }
    }
}
