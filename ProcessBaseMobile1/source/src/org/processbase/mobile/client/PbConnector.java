package org.processbase.mobile.client;

import de.enough.polish.xml.XmlDomNode;
import de.enough.polish.xml.XmlDomParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.Connector;

/**
 *
 * @author mgubaidullin
 */
public class PbConnector {

    private static final int MAX_REDIRECTS = 1;

    public XmlDomNode get(String url) throws IOException, Exception {
        HttpConnection con = null;
        InputStream is = null;
        XmlDomNode result = null;
        try {
            for (int tries = 0; tries < MAX_REDIRECTS; tries++) {
                con = (HttpConnection) Connector.open(url, Connector.READ_WRITE);
                con.setRequestMethod(HttpConnection.GET);
                con.setRequestProperty("Connection", "close");
                con.setRequestProperty("Accept", "application/xml");
                con.setRequestProperty("Content-Language", "ru-RU");
                con.setRequestProperty("Accept-Charset", "UTF-8");
                con.setRequestProperty("Connection", "close");
                if (con.getResponseCode() == HttpConnection.HTTP_OK) {
                    is = con.openInputStream();
                    result = XmlDomParser.parseTree(is, "UTF-8");
                } else {
                    throw new Exception(con.getResponseMessage());
                }
            }
        } finally {
            if (is != null) {
                is.close();
            }
            if (con != null) {
                con.close();
            }
            return result;
        }
    }

    public XmlDomNode post(String url, String data) throws IOException {
        HttpConnection con = null;
        InputStream is = null;
        OutputStream os = null;
        XmlDomNode result = null;
        try {
            for (int tries = 0; tries < MAX_REDIRECTS; tries++) {
                con = (HttpConnection) Connector.open(url, Connector.READ_WRITE);
                con.setRequestMethod(HttpConnection.POST);
                con.setRequestProperty("Accept", "application/xml");
                con.setRequestProperty("Content-Language", "ru-RU");
                con.setRequestProperty("Accept-Charset", "UTF-8");
                con.setRequestProperty("Connection", "close");
                con.setRequestProperty("Content-Type", "binary/octet-stream");
                con.setRequestProperty("Content-Length", "" + data.length());
                os = con.openDataOutputStream();
                os.write(data.getBytes("UTF-8"));
                os.flush();
            }
            if (con.getResponseCode() == HttpConnection.HTTP_OK) {
                is = con.openInputStream();
                result = XmlDomParser.parseTree(is, "UTF-8");
            } else {
                throw new Exception(con.getResponseMessage());
            }
        } finally {
            if (os != null) {
                os.close();
            }
            if (is != null) {
                is.close();
            }
            if (con != null) {
                con.close();
            }
            return result;
        }
    }
}
