package de.evoila.cf.broker.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Johannes Hiemer.
 */
@Configuration
@ConfigurationProperties(prefix = "kubernetes")
public class KubernetesProperties {

    private String master;

    private KubernetesApi api;

    private KubernetesCertificates certs;

    private KubernetesAuthentication auth;

    private KubernetesWatch watch;

    private Integer connectionTimeout = 10 * 1000;

    private Integer requestTimeout = 10 * 1000;

    private KubernetesStore truststore;

    private KubernetesStore keystore;

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public KubernetesApi getApi() {
        return api;
    }

    public void setApi(KubernetesApi api) {
        this.api = api;
    }

    public KubernetesCertificates getCerts() {
        return certs;
    }

    public void setCerts(KubernetesCertificates certs) {
        this.certs = certs;
    }

    public KubernetesAuthentication getAuth() {
        return auth;
    }

    public void setAuth(KubernetesAuthentication auth) {
        this.auth = auth;
    }

    public KubernetesWatch getWatch() {
        return watch;
    }

    public void setWatch(KubernetesWatch watch) {
        this.watch = watch;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public KubernetesStore getTruststore() {
        return truststore;
    }

    public void setTruststore(KubernetesStore truststore) {
        this.truststore = truststore;
    }

    public KubernetesStore getKeystore() {
        return keystore;
    }

    public void setKeystore(KubernetesStore keystore) {
        this.keystore = keystore;
    }

}
