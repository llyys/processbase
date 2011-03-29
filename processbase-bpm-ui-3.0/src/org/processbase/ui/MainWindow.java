package org.processbase.ui;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
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
import org.processbase.ui.panel.BPMConfigurationPanel;
import org.processbase.ui.panel.BAMConfigurationPanel;
import org.processbase.ui.panel.ConsolePanel;
import org.processbase.ui.panel.IdentityPanel;
import org.processbase.ui.panel.BPMMonitoringPanel;
import org.processbase.ui.template.PbWindow;

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

    public MainWindow() {
        super("PROCESSBASE BPMS");
    }

    public void initLogin(){
        mainLayout = (VerticalLayout) getContent();
        LoginPanel loginPanel = new LoginPanel();
        mainLayout.addComponent(loginPanel);
        loginPanel.initUI();
    }

    public void initUI(){
        mainLayout = (VerticalLayout) getContent();
        mainLayout.removeAllComponents();
        mainLayout.setMargin(false);
        mainLayout.setStyleName(Reindeer.LAYOUT_WHITE);

        consolePanel = new ConsolePanel();
        bpmConfigurationPanel = new BPMConfigurationPanel();
        identityPanel = new IdentityPanel();
        bamConfigurationPanel = new BAMConfigurationPanel();
        bpmMonitoringPanel = new BPMMonitoringPanel();

        mainLayout.setSizeFull();
        mainLayout.addComponent(getHeader());
        CssLayout margin = new CssLayout();
        margin.setMargin(false, true, true, true);
        margin.setSizeFull();
        tabs = new TabSheet();
        tabs.setSizeFull();
//        tabs.setStyleName(Reindeer.TABSHEET_MINIMAL);
        margin.addComponent(tabs);
        mainLayout.addComponent(margin);
        mainLayout.setExpandRatio(margin, 1);
        tabs.addTab(consolePanel, ((Processbase)getApplication()).getMessages().getString("bpmConsole"), null);
        tabs.addTab(bpmConfigurationPanel, ((Processbase)getApplication()).getMessages().getString("bpmAdmin"), null);
//        tabs.addTab(identityPanel, ((Processbase)getApplication()).getMessages().getString("bpmIdentity"), null);
//        tabs.addTab(bamConfigurationPanel, ((Processbase)getApplication()).getMessages().getString("bamAdmin"), null);
//        tabs.addTab(bpmMonitoringPanel, ((Processbase)getApplication()).getMessages().getString("bpmMonitoring"), null);

        consolePanel.initUI();
        consolePanel.setSizeFull();
        bpmConfigurationPanel.initUI();
        bpmConfigurationPanel.setSizeFull();
        identityPanel.initUI();
        identityPanel.setSizeFull();
        bamConfigurationPanel.initUI();
        bamConfigurationPanel.setSizeFull();
        bpmMonitoringPanel.initUI();
        bpmMonitoringPanel.setSizeFull();

        tabs.addListener((SelectedTabChangeListener) this);
        tabs.setImmediate(true);
    }

    Layout getHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidth("100%");
        header.setMargin(false, true, false, true);
        header.setSpacing(true);
//        header.setStyleName(Reindeer.LAYOUT_BLUE);

        ThemeResource themeResource = new ThemeResource("icons/processbase.png");
        Embedded logo = new Embedded("", themeResource);
        logo.setType(Embedded.TYPE_IMAGE);

        header.addComponent(logo);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.setMargin(false);
        Button help = new Button("Help", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                openHelpWindow();
            }
        });
        help.setStyleName(Reindeer.BUTTON_LINK);
        buttons.addComponent(help);
        buttons.setComponentAlignment(help, Alignment.MIDDLE_RIGHT);

        Button logout = new Button("Logout", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                openLogoutWindow();
            }
        });
        logout.setStyleName(Reindeer.BUTTON_LINK);
        buttons.addComponent(logout);
        header.addComponent(buttons);
        header.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);

        return header;
    }
    Window help = new Window("Help");

    void openHelpWindow() {
        if (!"initialized".equals(help.getData())) {
            help.setData("initialized");
            help.setCloseShortcut(KeyCode.ESCAPE, null);

            help.center();
            // help.setStyleName(Reindeer.WINDOW_LIGHT);
            help.setWidth("400px");
            help.setResizable(false);

            Label helpText = new Label(
                    "<strong>How To Use This Application</strong><p>Click around, explore. The purpose of this app is to show you what is possible to achieve with the Reindeer theme and its different styles.</p><p>Most of the UI controls that are visible in this application don't actually do anything. They are purely for show, like the menu items and the components that demostrate the different style names assosiated with the components.</p><strong>So, What Then?</strong><p>Go and use the styles you see here in your own application and make them beautiful!",
                    Label.CONTENT_XHTML);
            help.addComponent(helpText);

        }
        if (!getChildWindows().contains(help)) {
            addWindow(help);
        }
    }

    void openLogoutWindow() {
        Window logout = new Window("Logout");
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
        Button yes = new Button("Logout", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                WebApplicationContext applicationContext = (WebApplicationContext) getApplication().getContext();
                getApplication().close();
                applicationContext.getHttpSession().invalidate();
            }
        });
        yes.setStyleName(Reindeer.BUTTON_DEFAULT);
        yes.focus();
        buttons.addComponent(yes);
        Button no = new Button("Cancel", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                removeWindow(event.getButton().getWindow());
            }
        });
        buttons.addComponent(no);

        logout.addComponent(buttons);
        ((VerticalLayout) logout.getContent()).setComponentAlignment(buttons,
                "center");
        ((VerticalLayout) logout.getContent()).setSpacing(true);

        addWindow(logout);
    }

    public void selectedTabChange(SelectedTabChangeEvent event) {
//        if (event.getTabSheet().getSelectedTab().equals(consolePanel)) {
//            consolePanel.refreshTopologyData();
//        } else if (event.getTabSheet().getSelectedTab().equals(adminPanel)) {
//            adminPanel.refreshServiceAssembliesData();
//        } else if (event.getTabSheet().getSelectedTab().equals(identityPanel)) {
//        }
    }
}
