package org.processbase.openesb.monitor;

import com.sun.enterprise.tools.admingui.util.AMXUtil;
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
import java.io.IOException;
import org.processbase.openesb.monitor.ui.BpelProcessesPanel;
import org.processbase.openesb.monitor.ui.ServiceAssembliesPanel;
import org.processbase.openesb.monitor.ui.TopologyPanel;
import org.processbase.openesb.monitor.ui.template.H2;

/**
 *
 * @author marat
 */
public class MainWindow extends Window implements SelectedTabChangeListener {

    private VerticalLayout mainLayout;
    private TabSheet tabs;
    private TopologyPanel topologyPanel;
    private ServiceAssembliesPanel serviceAssembliesPanel;
    private BpelProcessesPanel bpelPanel;

    public MainWindow() {
        super("POEM");
        mainLayout = (VerticalLayout) getContent();
        mainLayout.setMargin(false);
        mainLayout.setStyleName(Reindeer.LAYOUT_WHITE);

        topologyPanel = new TopologyPanel();
        serviceAssembliesPanel = new ServiceAssembliesPanel();
        bpelPanel = new BpelProcessesPanel();
        buildMainView();
    }

    void buildMainView() {
        mainLayout.setSizeFull();
        mainLayout.addComponent(getHeader());
        CssLayout margin = new CssLayout();
        margin.setMargin(false, true, true, true);
        margin.setSizeFull();
        tabs = new TabSheet();
        tabs.setSizeFull();
        tabs.setStyleName(Reindeer.TABSHEET_MINIMAL);
        margin.addComponent(tabs);
        mainLayout.addComponent(margin);
        mainLayout.setExpandRatio(margin, 1);
        tabs.addComponent(topologyPanel);
        topologyPanel.refreshTopologyData();
        tabs.addComponent(serviceAssembliesPanel);
        tabs.addComponent(bpelPanel);
        tabs.addListener((SelectedTabChangeListener) this);
        tabs.setImmediate(true);
//        tabs.addComponent(new TabPanel("Message Queues"));
    }

    Layout getHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidth("100%");
        header.setMargin(false, true, false, true);
        header.setSpacing(true);
//        header.setStyleName(Reindeer.LAYOUT_BLUE);

        ThemeResource themeResource = new ThemeResource("icons/ProcessBase_OpenESB_Monitor3.png");
        Embedded logo = new Embedded("", themeResource);
        logo.setType(Embedded.TYPE_IMAGE);

        header.addComponent(logo);

        H2 topologyType = new H2(POEM.getCurrent().isClusterSupported ? "CLUSTER" : "STANDALONE");
        header.addComponent(topologyType);
        header.setComponentAlignment(topologyType, Alignment.MIDDLE_CENTER);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.setMargin(false);
//        buttons.setStyleName(Reindeer.LAYOUT_BLACK);
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
                try {
                    POEM.getCurrent().jmxConnector.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                WebApplicationContext applicationContext = (WebApplicationContext) POEM.getCurrent().getContext();
                POEM.getCurrent().close();
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
        if (event.getTabSheet().getSelectedTab().equals(topologyPanel)) {
            topologyPanel.refreshTopologyData();
        } else if (event.getTabSheet().getSelectedTab().equals(serviceAssembliesPanel)) {
            serviceAssembliesPanel.refreshServiceAssembliesData();
        } else if (event.getTabSheet().getSelectedTab().equals(bpelPanel)) {
            
        }
    }
}
