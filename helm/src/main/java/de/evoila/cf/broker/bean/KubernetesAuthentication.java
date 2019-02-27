package de.evoila.cf.broker.bean;

import de.evoila.cf.broker.bean.auth.KubernetesAuthMode;

/**
 * @author Johannes Hiemer.
 */
public class KubernetesAuthentication {

    private KubernetesAuthMode mode;

    private KubernetesBasicAuhtentication basic;

    private String token;

    public KubernetesAuthMode getMode() {
        return mode;
    }

    public void setMode(KubernetesAuthMode mode) {
        this.mode = mode;
    }

    public KubernetesBasicAuhtentication getBasic() {
        return basic;
    }

    public void setBasic(KubernetesBasicAuhtentication basic) {
        this.basic = basic;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
