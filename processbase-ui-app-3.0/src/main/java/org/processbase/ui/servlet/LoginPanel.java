package org.processbase.ui.servlet;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Runo;
import org.ow2.bonita.util.BonitaConstants;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.PbUser;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ButtonBar;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author mgubaidullin
 */
public class LoginPanel extends GridLayout implements Handler {

    private Panel panel = new Panel();
    public FormLayout form = new FormLayout();
    private VerticalLayout vlayout = new VerticalLayout();
    private Button btnLogin = null;
    private Action action_ok = null;
    private TextField username = new TextField("Username", "");
    private PasswordField password = new PasswordField("Password", "");
    private Label labelLeft = new Label("");
    private Label labelRight = new Label("");
    private Locale locale = null;
    private Embedded logo = null;
    private CheckBox cbRememberMe=null;
	private ComboBox comboDomain;
    private Label label;

    public LoginPanel() {
        super(3, 2);
    }

    public void initUI(String message) {
        setWidth("100%");
        setHeight("100%");
        int row=0;

        addComponent(labelLeft, 0, 0);
        addComponent(labelRight, 2, 0);
        addComponent(panel, 1, 1);
        setComponentAlignment(panel, Alignment.MIDDLE_CENTER);

        panel.setWidth("330px");

        final PbApplication application = (PbApplication) getApplication();
        ResourceBundle messages = application.getPbMessages();
        username.setCaption(messages.getString("userName"));
        form.addComponent(username);
        username.setWidth("100%");
        username.focus();

        password.setCaption(messages.getString("password"));
        password.setWidth("100%");
        form.addComponent(password);
        List<String> domains=new ArrayList<String>();
        File serverDir=new File(BonitaConstants.getBonitaHomeFolder(), "server");
        for (File f : serverDir.listFiles()) {
			if(f.isDirectory()){
				domains.add(f.getName());
			}
		}
        if(domains.size()>1){
	        comboDomain = new ComboBox("Domeen", domains);
	        comboDomain.setNullSelectionAllowed(false);
	        comboDomain.setValue(Constants.BONITA_DOMAIN);
	        form.addComponent(comboDomain);
        }
        cbRememberMe=new CheckBox(application.getString("rememberMe", "Remember me"));
        form.addComponent(cbRememberMe);
        
        btnLogin = new Button(messages.getString("login"), this, "okHandler");
        btnLogin.setStyleName(Runo.BUTTON_DEFAULT);
        action_ok = new ShortcutAction("Default key", ShortcutAction.KeyCode.ENTER, null);
        
        ButtonBar buttons=new ButtonBar();
        buttons.addButton(btnLogin);
        
        Button idButton=new Button("ID-kaart");
        buttons.addButton(idButton);
        idButton.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {

                ProcessbaseApplication current = ProcessbaseApplication.getCurrent();
                HttpServletRequest httpServletRequest = current.getHttpServletRequest();
                HttpServletRequest req = httpServletRequest;

                //we create redirect url to the same path, but we will do redirect to current application.
                //this is needed to use id-card width authentication
                String url=MessageFormat.format("https://{0}:{1}{2}/auth?domain={4}&sid={5}&loc=https://{0}:{3}/SmartBPM",
                        req.getServerName(), //0
                        Constants.getSetting("HTTPS_PORT", "8443"),//1
                        req.getContextPath(), //2
                        Constants.getSetting("HTTPS_PORT", "8443"),//3
                        (String)comboDomain.getValue()//4
                        ,req.getSession().getId()
                );

                current.getServletContext().setAttribute(req.getSession().getId(), req.getSession());
				ExternalResource res=new ExternalResource(url);
				event.getButton().getWindow().open(res);

			}
		});
        
        Button mobidButton=new Button("Anonymous"); 
        buttons.addButton(mobidButton);       
        mobidButton.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
                    PbUser user=new PbUser();
                    user.username=BPMModule.USER_GUEST;
                    user.password=BPMModule.USER_GUEST;
                    user.rememberMe=false;
                    user.domain=getDomain();
					if(application.authenticate(user))
                    {
                        application.showMainWindow();
                    }
                    else {
                        application.showNotification("INVALID USER");
                    }
				} catch (Exception e) {
					// TODO Auto-generated catch block
                    application.showError(e.getMessage());
				}
			}
		});

        createLogo();
        vlayout.addComponent(logo);
        vlayout.setComponentAlignment(logo, Alignment.MIDDLE_CENTER);

        label = new Label(message==null?"":message);
        label.setWidth("100%");
        vlayout.addComponent(label);
        vlayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);

        vlayout.addComponent(form);
        vlayout.setComponentAlignment(form, Alignment.MIDDLE_CENTER);

        vlayout.setMargin(false, true, false, true);
        vlayout.setSpacing(true);
        vlayout.addComponent(buttons);
        
        Label version=new Label(org.processbase.ui.core.Version.VERSION);
        version.setWidth("100%");
        vlayout.addComponent(version);
        vlayout.setComponentAlignment(version, Alignment.MIDDLE_RIGHT);
        
        panel.setContent(vlayout);
        panel.addActionHandler(this);
    }

    public void setStatus(String message){
        label.setValue(message);
    }

    private void createLogo() {
        ThemeResource themeResource = new ThemeResource("icons/processbase.png");
        logo = new Embedded("", themeResource);
        logo.setType(Embedded.TYPE_IMAGE);
    }

    public Action[] getActions(Object target, Object sender) {
        return new Action[]{action_ok};
    }

    public void handleAction(Action action, Object sender, Object target) {
        if (action == action_ok) {
            okHandler();
        }
    }

    public void okHandler() {
        PbApplication pbApplication = (PbApplication)getApplication();
        try {
            username.commit();
            password.commit();
            PbUser user=new PbUser();
            user.username=username.getValue().toString();
            user.password=password.getValue().toString();
            user.rememberMe=cbRememberMe.booleanValue();
            user.domain=getDomain();
            user.authMethod= PbUser.AuthMethod.regular;

			if(pbApplication.authenticate(user))
            {
                pbApplication.showMainWindow();
                return;
            }
            pbApplication.showError("Invalid user or password");
           
        } catch (Exception ex) {
             ex.printStackTrace();
            pbApplication.showError(ex.getMessage());
            initUI("");
            //throw new RuntimeException(ex);
        }
    }

	private String getDomain() {
		return comboDomain!=null?(String)comboDomain.getValue():Constants.BONITA_DOMAIN;
	}
}
