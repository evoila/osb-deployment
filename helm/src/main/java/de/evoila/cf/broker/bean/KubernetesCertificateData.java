package de.evoila.cf.broker.bean;

/**
 * @author Johannes Hiemer.
 */
public class KubernetesCertificateData {

    private String data;

    private KubernetesPrivateKeyData key;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public KubernetesPrivateKeyData getKey() {
        return key;
    }

    public void setKey(KubernetesPrivateKeyData key) {
        this.key = key;
    }

}
