package de.evoila.cf.broker.bean;

/**
 * @author Johannes Hiemer.
 */
public class KubernetesStore {

    private String file;

    private String passphrase;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }
}
