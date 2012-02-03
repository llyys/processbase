package org.processbase.ui.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.Runo;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.servlet.http.Cookie;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.ow2.bonita.facade.IdentityAPI;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.identity.Group;
import org.ow2.bonita.facade.identity.Membership;
import org.ow2.bonita.facade.identity.User;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;

import org.processbase.raports.ui.RaportModule;
import org.processbase.ui.bam.panel.BAMConfigurationPanel;
import org.processbase.ui.bam.panel.BPMMonitoringPanel;
import org.processbase.ui.bpm.generator.view.ProcessController;
import org.processbase.ui.bpm.generator.view.ProcessManager;
import org.processbase.ui.bpm.panel.*;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.PbPanel;
import org.processbase.ui.core.template.PbWindow;
import org.processbase.ui.osgi.PbPanelModule;
import org.processbase.ui.osgi.PbPanelModuleService;

/**
 *
 * @author marat
 */
public class MainWindow extends PbWindow implements SelectedTabChangeListener {

	private VerticalLayout mainLayout;
    private TabSheet tabs;
    
    private TaskListPanel consolePanel;
    private BPMConfigurationPanel bpmConfigurationPanel;
    private IdentityPanel identityPanel;
    private BAMConfigurationPanel bamConfigurationPanel;
    private BPMMonitoringPanel bpmMonitoringPanel;
    private DevelopmentPanel developmentPanel;
    private RaportModule raportListPanel;
    
    private User user;
    private List<Group> userGroups;
    private Group activeGroup;
    private List<String> accessSet;
	private ProcessController processController;

    public MainWindow() {
        super(ProcessbaseApplication.getString("appName","BPMS"));
    }

   

	public void initLogin() {
        mainLayout = (VerticalLayout) getContent();
        LoginPanel loginPanel = new LoginPanel();
        mainLayout.addComponent(loginPanel);
        loginPanel.initUI();

    }

    public void initUI() {
        try {
            defineAccess();
            mainLayout = (VerticalLayout) getContent();
            mainLayout.removeAllComponents();
            mainLayout.setMargin(true);
            mainLayout.setSizeFull();
            
            tabs = new TabSheet();
            

            // prepare tabs
            String userName = ProcessbaseApplication.getCurrent().getUserName();
            prepareTabs();
            mainLayout.addComponent(getHeader());
            /*if (accessSet.contains("tasklist")) {
            	consolePanel = new TaskListPanel();
            	
            	if(accessSet.size()==1){
            		 
            		 consolePanel.initUI();
            		 consolePanel.setInitialized(true);
            		 consolePanel.setSizeFull();
            		 mainLayout.addComponent(consolePanel);
                     mainLayout.setExpandRatio(consolePanel, 1);
            		 return;
            	}
                
                tabs.addTab(consolePanel, getPbMessages("bpmTasklist"), null);
            }
            if (accessSet.contains("bpm")) {
                bpmConfigurationPanel = new BPMConfigurationPanel();
                tabs.addTab(bpmConfigurationPanel, getPbMessages("bpmAdmin"), null);
            }
            if (accessSet.contains("identity")) {
                identityPanel = new IdentityPanel(); 
                tabs.addTab(identityPanel, getPbMessages("bpmIdentity"), null);
            }
            if (accessSet.contains("bam")) {
                bamConfigurationPanel = new BAMConfigurationPanel();
                tabs.addTab(bamConfigurationPanel, getPbMessages("bamAdmin"), null);
            }
            if (accessSet.contains("monitoring")) {
                bpmMonitoringPanel = new BPMMonitoringPanel();
                tabs.addTab(bpmMonitoringPanel, getPbMessages("bpmMonitoring"), null);
            }
            if (accessSet.contains("development")) {
            	developmentPanel = new DevelopmentPanel();
            	tabs.addTab(developmentPanel, getPbMessages("bpmDevelopment"), null);
            }
            
            if (accessSet.contains("raport")) {
            	raportListPanel = new RaportModule();
                tabs.addTab(raportListPanel, "Raports", null);
            }
            
*/
            if (tabs.getSelectedTab() != null && tabs.getSelectedTab() instanceof PbPanel) {
                PbPanel first = (PbPanel) tabs.getSelectedTab();
                first.initUI();
                first.setInitialized(true);
                first.setSizeFull();
            }
           
            tabs.setSizeFull();
            mainLayout.addComponent(tabs);
            //mainLayout.addComponent(tabs);
            mainLayout.setExpandRatio(tabs, 1);
            
            tabs.addListener((SelectedTabChangeListener) this);
            tabs.setImmediate(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

	private String getPbMessages(String msg) {
		return ProcessbaseApplication.getString(msg, msg);
	}
	
	 
    Layout getHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidth("100%");
        header.setMargin(false);
        header.setSpacing(true);

        ThemeResource themeResource = new ThemeResource("icons/processbase.png");
        Embedded logo = new Embedded();
        logo.setSource(themeResource);
        logo.setType(Embedded.TYPE_IMAGE);

        header.addComponent(logo);
        header.setExpandRatio(logo, 1.0f);
        Label helloUser; 
        if(StringUtils.isEmpty(user.getFirstName()) && StringUtils.isEmpty(user.getLastName()))
        	helloUser = new Label(getPbMessages("welcome")+", " + user.getUsername());
        else 
        	helloUser = new Label(getPbMessages("welcome")+", " + user.getFirstName() + " " + user.getLastName());
//        helloUser.setStyleName(Runo.LABEL_H2);
        header.addComponent(helloUser);
        header.setComponentAlignment(helloUser, Alignment.MIDDLE_RIGHT);
        header.setExpandRatio(helloUser, 1.0f);
        String userName = ProcessbaseApplication.getCurrent().getUserName();
        if(!userName.equals(BPMModule.USER_GUEST)){
	        Button profile = new Button(getPbMessages("btnProfile"), new Button.ClickListener() {
	
	            public void buttonClick(ClickEvent event) {
	                openProfileWindow();
	            }
	        });
	        profile.setStyleName(Runo.BUTTON_LINK);
	        header.addComponent(profile);
	        header.setComponentAlignment(profile, Alignment.MIDDLE_RIGHT);
        }
        Button logout = new Button(getPbMessages("btnLogout"), new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                openLogoutWindow();
            }
        });
        logout.setStyleName(Runo.BUTTON_LINK);
        header.addComponent(logout);
        header.setComponentAlignment(logout, Alignment.MIDDLE_RIGHT);

        return header;
    }

    void openLogoutWindow() {
        Window logout = new Window(getPbMessages("btnLogout"));
        logout.setModal(true);
//        logout.setStyleName(Reindeer.WINDOW_BLACK);
        logout.setWidth("260px");
        logout.setResizable(false);
        logout.setClosable(false);
        logout.setDraggable(false);
        logout.setCloseShortcut(KeyCode.ESCAPE, null);

        Label helpText = new Label(
                "Are you sure you want to log out?",
                Label.CONTENT_XHTML);
        logout.addComponent(helpText);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        Button yes = new Button(getPbMessages("btnLogout"), new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
            	
            	PbApplication app=(PbApplication)getApplication();
            	Cookie cookie=null;
            	for (Cookie c : app.getHttpServletRequest().getCookies()) {
					if("username".equals(c.getName())){
						cookie=c;
						break;
					}
				}
            	if(cookie!=null){
            			Cookie del=new Cookie("username", "");
            			cookie.setMaxAge(0); // Delete
            			app.getHttpServletResponse().addCookie(del);
            	}
                WebApplicationContext applicationContext = (WebApplicationContext) getApplication().getContext();
                getApplication().close();
                applicationContext.getHttpSession().invalidate();
                
            }
        });
        yes.setStyleName(Reindeer.BUTTON_DEFAULT);
        yes.focus();
        buttons.addComponent(yes);
        Button no = new Button(getPbMessages("btnCancel"), new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                removeWindow(event.getButton().getWindow());
            }
        });
        buttons.addComponent(no);

        logout.addComponent(buttons);
        ((VerticalLayout) logout.getContent()).setComponentAlignment(buttons, Alignment.MIDDLE_CENTER);
        ((VerticalLayout) logout.getContent()).setSpacing(true);

        addWindow(logout);
    }

    public void selectedTabChange(SelectedTabChangeEvent event) {
        if (event.getTabSheet().getSelectedTab() instanceof PbPanel
                && !((PbPanel) event.getTabSheet().getSelectedTab()).isInitialized()) {
            ((PbPanel) event.getTabSheet().getSelectedTab()).initUI();
            ((PbPanel) event.getTabSheet().getSelectedTab()).setInitialized(true);
            ((PbPanel) event.getTabSheet().getSelectedTab()).setSizeFull();
        }

    }

    private void openProfileWindow() {
        ProfileWindow nuw = new ProfileWindow(user);
        getWindow().addWindow(nuw);
        nuw.initUI();
        nuw.setProfileView();
    }

    private void defineAccess() throws Exception {
        accessSet = new ArrayList<String>();
        String userName = ProcessbaseApplication.getCurrent().getUserName();
        BPMModule bpmModule = ProcessbaseApplication.getCurrent().getBpmModule();
        
        user = bpmModule.findUserByUserName(userName);
        if(userName.equals(BPMModule.USER_GUEST)){
        	accessSet.add("tasklist");
        	return;
        }
        for (Membership membership : user.getMemberships()) {
        	
            if (membership.getRole().getName().equals(IdentityAPI.ADMIN_ROLE_NAME)) {
                if (membership.getGroup().getName().equalsIgnoreCase("bpm")) {
                    accessSet.add("bpm");
                } else if (membership.getGroup().getName().equalsIgnoreCase("bam")) {
                    accessSet.add("bam");
                } else if (membership.getGroup().getName().equalsIgnoreCase("identity")) {
                    accessSet.add("identity");
                } else if (membership.getGroup().getName().equalsIgnoreCase("monitoring")) {
                    accessSet.add("monitoring");
                } else if (membership.getGroup().getName().equalsIgnoreCase("development")) {
                    accessSet.add("development");
                }
            }                
        }
        if (bpmModule.isUserAdmin() || "admin".equals(userName)) {
        	accessSet.add("admin");
            /*accessSet.add("bpm");
            accessSet.add("bam");
            accessSet.add("identity");
            accessSet.add("monitoring");
            accessSet.add("raport");
            accessSet.add("development");*/            
        }
        accessSet.add("tasklist");
    }

    private void prepareTabs() throws Exception {
        Locale locale = getApplication().getLocale();
        GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        Gson gson = gb.create();
        Type collectionType = new TypeToken<LinkedHashMap<Integer, String>>(){}.getType();
        TreeMap<Integer, String> tabList = new TreeMap<Integer, String>();
        String metaDataString = ProcessbaseApplication.getCurrent().getBpmModule().getMetaData("PROCESSBASE_TABSHEETS_LIST");
        if (metaDataString != null) {
                LinkedHashMap<Integer, String> tabs2 = gson.fromJson(metaDataString, collectionType);
            if (!tabs2.isEmpty()) {
                tabList.putAll(tabs2);
            }
        }
        PbPanelModuleService pms = ((PbApplication) getApplication()).getPanelModuleService();
        for (Entry<String, PbPanelModule> pm : pms.getModules().entrySet()) {
            System.out.println("moduleName = " + pm.getKey());
            
            if (pm != null) {
                try {
	                PbPanelModule panel = pm.getValue();
	                if(CollectionUtils.containsAny(Arrays.asList(panel.getRoles()), accessSet)){
	                	tabs.addTab(panel, panel.getTitle(locale), null);
	                }
	                else{
	                	System.out.println("No rights for module = " + pm.getKey());
	                }
                } catch (Exception ex){
                    System.out.println("Exception with pm = " + pm.getKey());
                    ex.printStackTrace();
                    throw new RuntimeException(ex);
                }
            }
        }
        if (tabs.getSelectedTab() != null && tabs.getSelectedTab() instanceof PbPanel) {
            PbPanel first = (PbPanel) tabs.getSelectedTab();
            first.initUI();
            first.setInitialized(true);
            first.setSizeFull();
        }
    }



	public void setUserGroups(List<Group> userGroups) {
		this.userGroups = userGroups;
	}



	public List<Group> getUserGroups() {
		return userGroups;
	}



	public void setActiveGroup(Group activeGroup) {
		this.activeGroup = activeGroup;
	}



	public Group getActiveGroup() {
		return activeGroup;
	}



	public void initProcess(String processDefinitionUUID) {
		 mainLayout = (VerticalLayout) getContent();
         mainLayout.removeAllComponents();
         mainLayout.setMargin(true);
         mainLayout.setSizeFull();
         
		processController = new ProcessController();
		ProcessDefinition processDefinition;
		try {
			processDefinition = PbApplication.getCurrent().getBpmModule().getProcessDefinition(new ProcessDefinitionUUID(processDefinitionUUID));
			processController.initProcess(processDefinition);
			processController.setWindow(this);
			processController.initUI();
		} catch (ProcessNotFoundException e) {
			showError("Protsessi "+processDefinitionUUID+" ei leitud");
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public ProcessManager getProcessManager(){
		return processController.getProcessManager();
	}



	public void initTask(String taskUUID) {
		// TODO Auto-generated method stub
		
	}
}
