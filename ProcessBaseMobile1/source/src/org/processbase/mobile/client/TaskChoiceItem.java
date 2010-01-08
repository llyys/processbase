package org.processbase.mobile.client;

import de.enough.polish.ui.CustomItem;
import de.enough.polish.ui.IconItem;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author mgubaidullin
 */
public class TaskChoiceItem extends CustomItem {

    private final StringItem captionItem;
    private final IconItem descItem;
    private final Task task;

    public TaskChoiceItem(Task task) {
        this(task, null);
    }

    public TaskChoiceItem(Task task, Style style) {
        super(null, style);
        this.task = task;
        //#style tciCaption
        this.captionItem = new StringItem(task.getCaption(), task.getCreatedDate());
        //#style tciDesc
        this.descItem = new IconItem(task.getUuid(), null);
        try {
            this.descItem.setImage(task.isMobile() ? Image.createImage("/email_red.png") : null);
        } catch (IOException ex) {
        }
    }

    protected int getMinContentWidth() {
        return 100;
    }

    protected int getMinContentHeight() {
        return 30;
    }

    protected int getPrefContentWidth(int height) {
        //#if polish.ScreenWidth:defined
        //#= int width = ${polish.ScreenWidth};
        //#else
        int width = 220;
        //#endif
        this.captionItem.background = null;
        this.captionItem.setBorder(null);
        int headlineWidth = this.captionItem.getItemWidth(width, width, height);
        this.descItem.background = null;
        this.descItem.setBorder(null);
        int textWidth = this.descItem.getItemWidth(width, width, height);
        return Math.max(headlineWidth, textWidth);
    }

    protected int getPrefContentHeight(int width) {
        this.captionItem.background = null;
        this.captionItem.setBorder(null);
        int availHeight = 200;
        int height = this.captionItem.getItemHeight(width, width, availHeight);
        this.descItem.background = null;
        this.descItem.setBorder(null);
        height += this.descItem.getItemHeight(width, width, availHeight);
        return height;
    }

    public void setImage(Image image) {
        this.descItem.setImage(image);
    }

    protected void paint(Graphics g, int w, int h) {
        this.captionItem.paint(0, 0, 0, 0, g);
        this.captionItem.itemWidth = 80;
        int x = this.captionItem.itemWidth;
//        this.createdDateItem.paint(x, 0, 0, w, g);
        int y = this.captionItem.itemHeight;
        this.descItem.paint(0, y, 0, w, g);

    }

    protected void defocus(Style originalStyle) {
        this.setBackground(this.style.background);
        this.descItem.setTextColor(this.style.getFontColor());
        this.descItem.background = null;
        this.captionItem.setTextColor(this.style.getFontColor());
        this.captionItem.background = null;
        this.captionItem.getLabelItem().setStyle(this.style);
        this.captionItem.getLabelItem().setBorder(null);
        this.captionItem.getLabelItem().background = null;
    }

    protected Style focus(Style focusStyle, int direction) {
        this.descItem.setTextColor(focusStyle.getFontColor());
        this.descItem.background = null;
        this.captionItem.setTextColor(focusStyle.getFontColor());
        this.captionItem.background = null;
        this.captionItem.getLabelItem().setStyle(focusStyle);
        this.captionItem.getLabelItem().setBorder(null);
        this.setBackground(focusStyle.background);
        return focusStyle;
    }

    public Task getTask() {
        return task;
    }
}


