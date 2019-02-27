package de.evoila.cf.broker.bean;

/**
 * @author Johannes Hiemer.
 */
public class KubernetesBasicAuhtentication {

    private String username;

    private String password;

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
