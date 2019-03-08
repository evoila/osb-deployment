package de.evoila.cf.broker.bean;

/**
 * @author Johannes Hiemer.
 */
public class KubernetesCertificates {

    private KubernetesCertificateData ca;

    private KubernetesCertificateData client;

    public KubernetesCertificateData getCa() {
        return ca;
    }

    public void setCa(KubernetesCertificateData ca) {
        this.ca = ca;
    }

    public KubernetesCertificateData getClient() {
        return client;
    }

    public void setClient(KubernetesCertificateData client) {
        this.client = client;
    }
}
