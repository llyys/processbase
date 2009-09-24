package org.naxitrale.processbase.ui.portal;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import org.naxitrale.processbase.ui.portal.ActiveLink.LinkActivatedListener;

/**
 *
 * @author mgubaidullin
 */
public class HelpPanel extends Panel {

    private VerticalLayout layout = new VerticalLayout();
    private Link link = null;
    private Label topLabel = null;
    private Label blueLabel = null;
    private Label blackLabel = null;
    private String topText = null;
    private String blueText = null;
    private String blackText = null;
    private String linkText = null;
    private LinkActivatedListener listener = null;

    public HelpPanel(String topText, String blueText, String blackText, String linkText, LinkActivatedListener listener) {
        super();
        this.topText = topText;
        this.blueText = blueText;
        this.blackText = blackText;
        this.linkText = linkText;
        this.listener = listener;
        initUI();
    }

    public void initUI() {
        setContent(layout);
        layout.setMargin(true, false, false, false);
        setStyleName(Panel.STYLE_LIGHT);
        setStyleName("sample-view");
        addStyleName("feature-info");
        setWidth("369px");
        setCaption("Справка");
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
            blackLabel.setStyleName("description");
            addComponent(blackLabel);
        }
        if (linkText != null) {
            link = new Link();
            link.setCaption(linkText);
            link.setStyleName(Button.STYLE_LINK);
            addComponent(link);
        }
    }
}
