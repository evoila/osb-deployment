package de.evoila.cf.broker.bean.auth;

/**
 * @author Johannes Hiemer.
 */
public enum KubernetesAuthMode {

    BASIC_AUTH("basic-auth"),
    TOKEN("token"),
    X509("x509");

    private final String name;

    KubernetesAuthMode(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
