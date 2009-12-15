package org.processbase.ui.help;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import java.io.UnsupportedEncodingException;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.ProcessBase;
import org.processbase.util.db.HibernateUtil;
import org.processbase.util.db.PbHelp;

/**
 *
 * @author mgubaidullin
 */
public class HelpPanel extends Panel implements Button.ClickListener {

    protected ResourceBundle messages = ResourceBundle.getBundle("resources/MessagesBundle", ((ProcessBase) getApplication()).getCurrent().getLocale());
    private VerticalLayout layout = new VerticalLayout();
    private Label topLabel = null;
    private Label blueLabel = null;
    private Label blackLabel = null;
    private String topText = null;
    private String blueText = null;
    private String blackText = null;
    private Button btnEdit = new Button(messages.getString("btnEdit"), this);
    private Button btnClose = new Button(messages.getString("btnClose"), this);
    private PbHelp pbHelp = null;

    public HelpPanel() {
        super();
    }

    public void setHelp(String uniqueUUID) {
        HibernateUtil hutil = new HibernateUtil();
        pbHelp = hutil.findPbHelp(uniqueUUID);
        if (pbHelp == null) {
            pbHelp = new PbHelp();
            pbHelp.setBlackText("".getBytes());
            pbHelp.setBlueText("".getBytes());
            pbHelp.setTopText("".getBytes());
            pbHelp.setUniqueUuid(uniqueUUID);
        }
        setHelp(pbHelp);
        setVisible(true);
    }

    public void setHelp(PbHelp pbHelp) {
        try {
            if (pbHelp.getTopText() != null) {
                topText = new String(pbHelp.getTopText(), "UTF-8");
            }
            if (pbHelp.getBlueText() != null) {
                blueText = new String(pbHelp.getBlueText(), "UTF-8");
            }
            if (pbHelp.getBlackText() != null) {
                blackText = new String(pbHelp.getBlackText(), "UTF-8");
            }
            initUI();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HelpPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    public void initUI() {
        setContent(layout);
        layout.setMargin(true, false, false, false);
        setStyleName(Panel.STYLE_LIGHT);
        setStyleName("sample-view");
        addStyleName("feature-info");
        setWidth("369px");
        setCaption(messages.getString("captionHelp"));
        removeAllComponents();
        if (topText != null) {
            topLabel = new Label("<span>" + topText + "</span>", Label.CONTENT_XHTML);
            addComponent(topLabel);
        }
        if (blueText != null) {
            blueLabel = new Label("<div class=\"outer-deco\"><div class=\"deco\"><span class=\"deco\"></span>" + blueText + "</div></div>", Label.CONTENT_XHTML);
            blueLabel.setStyleName("sample-description");
            addComponent(blueLabel);
        }
        if (blackText != null) {
            blackLabel = new Label("<div class=\"outer-deco\"><div class=\"deco\"><span class=\"deco\"></span>" + blackText + "</div></div>", Label.CONTENT_XHTML);
//            blackLabel.setStyleName("description");
            addComponent(blackLabel);
        }
//        btnEdit.setStyleName(Button.STYLE_LINK);
        if (((ProcessBase) getApplication()).getCurrent().getUser().isHelpAdmin()) {
            addComponent(btnEdit);
        }
        addComponent(btnClose);
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton().equals(btnEdit)) {
            HelpEditorWindow helpEditorWindow = new HelpEditorWindow(pbHelp);
            helpEditorWindow.exec();
            getApplication().getMainWindow().addWindow(helpEditorWindow);
        } else if (event.getButton().equals(btnClose)) {
            setVisible(false);
        }
    }
}
