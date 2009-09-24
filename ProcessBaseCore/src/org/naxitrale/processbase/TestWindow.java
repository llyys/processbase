package org.naxitrale.processbase;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;

/**
 *
 * @author mgubaidullin
 */
public class TestWindow extends Window {

    private GridLayout grid = new GridLayout(2, 2);
    private Panel panel1 = new Panel();
    private Panel panel2 = new Panel();
    private Panel panel3 = new Panel();
    private Panel panel4 = new Panel();

    public TestWindow() {
        super("ProcessBase");
        initUI();
    }

    public void initUI() {
        setTheme("processbase");
        setStyleName("blue");
        grid.setSizeFull();
        grid.setMargin(true);
        grid.setSpacing(true);
        panel1.addComponent(new Label("1111"));
        panel2.addComponent(new Label("2222"));
        panel3.addComponent(new Label("3333"));
        panel4.addComponent(new Label("4444"));
        panel1.setSizeFull();
        panel2.setSizeFull();
        panel3.setSizeFull();
        panel4.setSizeFull();
        grid.addComponent(panel1, 0, 0);
        grid.addComponent(panel2, 1, 0);
        grid.addComponent(panel3, 0, 1);
        grid.addComponent(panel4, 1, 1);
        setContent(grid);
    }
}
