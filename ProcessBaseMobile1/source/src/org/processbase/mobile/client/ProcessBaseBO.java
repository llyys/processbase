package org.processbase.mobile.client;

import de.enough.polish.io.Serializable;

/**
 *
 * @author mgubaidullin
 */
public class ProcessBaseBO implements Serializable {

    private String username;
    private String url;
    private String password;

    public ProcessBaseBO(String username, String url, String password) {
        this.username = username;
        this.url = url;
        this.password = password;
    }

    public ProcessBaseBO() {
        this.username = "admin";
        this.url = "172.25.8.169:8080";
        this.password = "admin";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
   
}
