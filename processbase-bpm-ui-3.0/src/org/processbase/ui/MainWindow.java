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
import com.vaadin.ui.themes.Runo;
import org.processbase.ui.panel.BPMConfigurationPanel;
import org.processbase.ui.panel.BAMConfigurationPanel;
import org.processbase.ui.panel.ConsolePanel;
import org.processbase.ui.panel.IdentityPanel;
import org.processbase.ui.panel.BPMMonitoringPanel;
import org.processbase.ui.template.PbPanel;
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

    public void initLogin() {
        mainLayout = (VerticalLayout) getContent();
        LoginPanel loginPanel = new LoginPanel();
        mainLayout.addComponent(loginPanel);
        loginPanel.initUI();

    }

    public void initUI() {
        mainLayout = (VerticalLayout) getContent();
        mainLayout.removeAllComponents();
        mainLayout.setMargin(true);

        consolePanel = new ConsolePanel();
        bpmConfigurationPanel = new BPMConfigurationPanel();
        identityPanel = new IdentityPanel();
        bamConfigurationPanel = new BAMConfigurationPanel();
        bpmMonitoringPanel = new BPMMonitoringPanel();

        mainLayout.setSizeFull();
        mainLayout.addComponent(getHeader());
        tabs = new TabSheet();
        tabs.setSizeFull();
        mainLayout.addComponent(tabs);
        mainLayout.addComponent(tabs);
        mainLayout.setExpandRatio(tabs, 1);
        tabs.addTab(consolePanel, ((Processbase) getApplication()).getMessages().getString("bpmConsole"), null);

        tabs.addTab(bpmConfigurationPanel, ((Processbase) getApplication()).getMessages().getString("bpmAdmin"), null);
        tabs.addTab(identityPanel, ((Processbase) getApplication()).getMessages().getString("bpmIdentity"), null);
        tabs.addTab(bamConfigurationPanel, ((Processbase) getApplication()).getMessages().getString("bamAdmin"), null);
        tabs.addTab(bpmMonitoringPanel, ((Processbase) getApplication()).getMessages().getString("bpmMonitoring"), null);

        consolePanel.initUI();
        consolePanel.setInitialized(true);
        consolePanel.setSizeFull();

        tabs.addListener((SelectedTabChangeListener) this);
        tabs.setImmediate(true);
    }

    Layout getHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidth("100%");
        header.setMargin(false);
        header.setSpacing(false);

        ThemeResource themeResource = new ThemeResource("icons/processbase.png");
        Embedded logo = new Embedded();
        logo.setSource(themeResource);
        logo.setType(Embedded.TYPE_IMAGE);

        header.addComponent(logo);

        Button logout = new Button("Logout", new Button.ClickListener() {

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
}
