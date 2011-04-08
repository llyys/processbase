package org.processbase.ui.servlet;

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
import java.util.HashSet;
import org.ow2.bonita.facade.IdentityAPI;
import org.ow2.bonita.facade.identity.Membership;
import org.ow2.bonita.facade.identity.User;
import org.processbase.ui.bam.panel.BAMConfigurationPanel;
import org.processbase.ui.bam.panel.BPMMonitoringPanel;
import org.processbase.ui.bpm.identity.UserWindow;
import org.processbase.ui.bpm.panel.BPMConfigurationPanel;
import org.processbase.ui.bpm.panel.ConsolePanel;
import org.processbase.ui.bpm.panel.IdentityPanel;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.Processbase;
import org.processbase.ui.core.template.PbPanel;
import org.processbase.ui.core.template.PbWindow;

/**
 *
 * @author marat
 */
public class MainWindow extends PbWindow implements SelectedTabChangeListener {

    private VerticalLayout mainLayout;
    private TabSheet tabs;
    private ConsolePanel consolePanel;
    private BPMConfigurationPanel bpmConfigurationPanel;
    private IdentityPanel identityPanel;
    private BAMConfigurationPanel bamConfigurationPanel;
    private BPMMonitoringPanel bpmMonitoringPanel;
    private User user;
    private HashSet<String> accessSet;

    public MainWindow() {
        super("PROCESSBASE BPMS");
    }

    public void initLogin() {
        mainLayout = (VerticalLayout) getContent();
        LoginPanel loginPanel = new LoginPanel();
        mainLayout.addComponent(loginPanel);
        loginPanel.initUI();

    }

    public void initUI() {
        try {
            mainLayout = (VerticalLayout) getContent();
            mainLayout.removeAllComponents();
            mainLayout.setMargin(true);
            mainLayout.setSizeFull();
            mainLayout.addComponent(getHeader());
            tabs = new TabSheet();
            tabs.setSizeFull();
            mainLayout.addComponent(tabs);
            mainLayout.addComponent(tabs);
            mainLayout.setExpandRatio(tabs, 1);

            // prepare tabs
            defineAccess();
            if (accessSet.contains("tasklist")) {
                consolePanel = new ConsolePanel();
                tabs.addTab(consolePanel, ((Processbase) getApplication()).getMessages().getString("bpmTasklist"), null);
            }
            if (accessSet.contains("bpm")) {
                bpmConfigurationPanel = new BPMConfigurationPanel();
                tabs.addTab(bpmConfigurationPanel, ((Processbase) getApplication()).getMessages().getString("bpmAdmin"), null);
            }
            if (accessSet.contains("identity")) {
                identityPanel = new IdentityPanel();
                tabs.addTab(identityPanel, ((Processbase) getApplication()).getMessages().getString("bpmIdentity"), null);
            }
            if (accessSet.contains("bam")) {
                bamConfigurationPanel = new BAMConfigurationPanel();
                tabs.addTab(bamConfigurationPanel, ((Processbase) getApplication()).getMessages().getString("bamAdmin"), null);
            }
            if (accessSet.contains("monitoring")) {
                bpmMonitoringPanel = new BPMMonitoringPanel();
                tabs.addTab(bpmMonitoringPanel, ((Processbase) getApplication()).getMessages().getString("bpmMonitoring"), null);
            }

            if (tabs.getSelectedTab() != null && tabs.getSelectedTab() instanceof PbPanel) {
                PbPanel first = (PbPanel) tabs.getSelectedTab();
                first.initUI();
                first.setInitialized(true);
                first.setSizeFull();
            }

            tabs.addListener((SelectedTabChangeListener) this);
            tabs.setImmediate(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

        Button profile = new Button(((PbApplication) getApplication()).getMessages().getString("btnProfile"), new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                openProfileWindow();
            }
        });
        profile.setStyleName(Reindeer.BUTTON_LINK);
        header.addComponent(profile);
        header.setComponentAlignment(profile, Alignment.MIDDLE_RIGHT);

        Button logout = new Button(((PbApplication) getApplication()).getMessages().getString("btnLogout"), new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                openLogoutWindow();
            }
        });
        logout.setStyleName(Reindeer.BUTTON_LINK);
        header.addComponent(logout);
        header.setComponentAlignment(logout, Alignment.MIDDLE_RIGHT);

        return header;
    }

    void openLogoutWindow() {
        Window logout = new Window(((PbApplication) getApplication()).getMessages().getString("btnLogout"));
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
        Button yes = new Button(((PbApplication) getApplication()).getMessages().getString("btnLogout"), new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                WebApplicationContext applicationContext = (WebApplicationContext) getApplication().getContext();
                getApplication().close();
                applicationContext.getHttpSession().invalidate();
            }
        });
        yes.setStyleName(Reindeer.BUTTON_DEFAULT);
        yes.focus();
        buttons.addComponent(yes);
        Button no = new Button(((PbApplication) getApplication()).getMessages().getString("btnCancel"), new Button.ClickListener() {

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
        UserWindow nuw = new UserWindow(user);
        getWindow().addWindow(nuw);
        nuw.initUI();
        nuw.setProfileView();
    }

    private void defineAccess() throws Exception {
        accessSet = new HashSet<String>();
        String userName = ((Processbase) getApplication()).getUserName();
        BPMModule bpmModule = ((Processbase) getApplication()).getBpmModule();
        user = bpmModule.findUserByUserName(userName);
        for (Membership membership : user.getMemberships()) {
            if (membership.getGroup().getParentGroup() != null && membership.getGroup().getParentGroup().getName().equals(IdentityAPI.DEFAULT_GROUP_NAME)) {
                if (membership.getRole().getName().equals(IdentityAPI.ADMIN_ROLE_NAME)) {
                    if (membership.getGroup().getName().equalsIgnoreCase("bpm")) {
                        accessSet.add("bpm");
                    } else if (membership.getGroup().getName().equalsIgnoreCase("bam")) {
                        accessSet.add("bam");
                    } else if (membership.getGroup().getName().equalsIgnoreCase("identity")) {
                        accessSet.add("identity");
                    } else if (membership.getGroup().getName().equalsIgnoreCase("monitoring")) {
                        accessSet.add("monitoring");
                    }
                } else if (membership.getRole().getName().equals(IdentityAPI.USER_ROLE_NAME)) {
                    if (membership.getGroup().getName().equalsIgnoreCase("tasklist")) {
                        accessSet.add("tasklist");
                    }
                }
            }
        }
    }
}
