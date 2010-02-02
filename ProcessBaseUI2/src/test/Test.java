package test;

import java.io.IOException;
import java.net.URISyntaxException;
import org.ow2.bonita.util.AccessorUtil;

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
//        System.setProperty("java.security.auth.login.config", "jaas-standard.cfg");

//        System.setProperty("org.ow2.bonita.api-type", "EJB3");
//        System.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
//        System.setProperty("java.naming.factory.url.pkgs", "org.jboss.naming");
//        System.setProperty("java.naming.provider.url", "localhost:1099");

        int x = AccessorUtil.getAPIAccessor().getQueryDefinitionAPI().getNumberOfProcesses();
        System.out.println("" + x);
        System.out.println("" + AccessorUtil.getAPIAccessor().getQueryDefinitionAPI().getProcesses());
    }
}
