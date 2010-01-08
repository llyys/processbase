/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.processbase.mobile.server.template;

import de.enough.polish.xml.XmlDomNode;

/**
 *
 * @author mgubaidullin
 */
public class TestForm extends MobileTask {

    @Override
    public String getForm() {
        StringBuffer sb = new StringBuffer();
        sb.append("<form>");
        sb.append("<text text=\"Необходимо назначить исполнителя на заявку на сумму " + this.processVars.get("amount") + ". Заемщик:" + this.processVars.get("customID") + "\">");
        sb.append("</text>");
        sb.append("<choiceGroup label=\"Исполнитель\">");
        sb.append("<item>Губайдуллин Марат (marat)</item>");
        sb.append("<item>Иванов Иван (ivan)</item>");
        sb.append("<item>Бахытов Бахыт(bakhyt)</item>");
        sb.append("</choiceGroup>");
        sb.append("</form>");
        return sb.substring(0);
    }

    @Override
    public String completeForm(XmlDomNode form) {
        StringBuffer sb = new StringBuffer();
        sb.append("<res>");
        try {
            for (int i = 0; i < form.getChildCount(); i++) {
                XmlDomNode item = form.getChild(i);
                System.out.println(item.toXmlString());
            }
            sb.append("OK");
        } catch (Exception ex) {
            sb.append(ex.getMessage());
        }
        sb.append("</res>");
        return sb.substring(0);
    }
}
