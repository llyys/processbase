package org.processbase.openesb.monitor;

/**
 *
 * @author mgubaidullin
 */
import com.sun.caps.management.api.bpel.BPELManagementService;
import com.sun.caps.management.api.bpel.BPELManagementServiceFactory;
import com.sun.enterprise.tools.admingui.util.AMXUtil;
import com.sun.jbi.ui.client.JBIAdminCommandsClientFactory;
import com.sun.jbi.ui.common.JBIAdminCommands;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import com.vaadin.service.ApplicationContext.TransactionListener;

import com.vaadin.Application;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class POEM extends Application implements TransactionListener {

    private static ThreadLocal<POEM> currentApplication = new ThreadLocal<POEM>();
    private MainWindow mainWindow;
    public JMXConnector jmxConnector;
    public JBIAdminCommands jbiAdminCommands;
    public BPELManagementService bpelManagementService;
    public IndexedContainer targets = new IndexedContainer();
    public boolean isClusterSupported = false;


    @Override
    public void init() {
        setTheme("reindeermods");
        WebApplicationContext applicationContext = (WebApplicationContext) this.getContext();
        this.setLocale(applicationContext.getBrowser().getLocale());
        try {

            LoginWindow loginWindow = new LoginWindow();
            setMainWindow(loginWindow);
            loginWindow.address.setValue(this.getURL().getHost());

        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(POEM.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
//        if (!Constants.LOADED) {
//            Constants.loadConstants();
//        }
        if (getContext() != null) {
            getContext().addTransactionListener(this);
        }

    }

    private void prepareSharedUI() {
        if (POEM.getCurrent().isClusterSupported) {
            
        }
        
    }

    

  

    public void authenticate(String address, String port, String login, String password) throws NamingException, Exception {

        JMXServiceURL jmxUrl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + address + ":" + port + "/jmxrmi");
        Map environment = new HashMap();
        environment.put(JMXConnector.CREDENTIALS, new String[]{"admin", "adminadmin"});
        jmxConnector = JMXConnectorFactory.connect(jmxUrl, environment);
        MBeanServerConnection jmxMBeanServerCon = jmxConnector.getMBeanServerConnection();

        POEM.getCurrent().jbiAdminCommands = JBIAdminCommandsClientFactory.getInstance(jmxMBeanServerCon);

        POEM.getCurrent().bpelManagementService = BPELManagementServiceFactory.getBPELManagementServiceLocal(jmxMBeanServerCon);

        POEM.getCurrent().isClusterSupported = AMXUtil.supportCluster();

        prepareSharedUI();

        mainWindow = new MainWindow();
        setMainWindow(mainWindow);
    }

    /**
     * @return the current application instance
     */
    public static POEM getCurrent() {
        return currentApplication.get();
    }

    /**
     * Set the current application instance
     */
    public static void setCurrent(POEM application) {
        if (getCurrent() == null) {
            currentApplication.set(application);
        }
    }

    @Override
    public void transactionEnd(Application application, Object transactionData) {
        currentApplication.remove();
    }

    @Override
    public void transactionStart(Application application, Object transactionData) {
        POEM.setCurrent(this);

    }
}
