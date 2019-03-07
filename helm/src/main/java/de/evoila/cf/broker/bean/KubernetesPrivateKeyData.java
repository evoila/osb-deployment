package de.evoila.cf.broker.bean;

/**
 * @author Johannes Hiemer.
 */
public class KubernetesPrivateKeyData {

    private String data;

    private String passphrase;

    private String algo;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public String getAlgo() {
        return algo;
    }

    public void setAlgo(String algo) {
        this.algo = algo;
    }
}
