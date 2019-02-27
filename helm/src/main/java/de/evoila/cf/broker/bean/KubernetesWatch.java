package de.evoila.cf.broker.bean;

/**
 * @author Johannes Hiemer.
 */
public class KubernetesWatch {

    private Integer reconnectInternal = 1000;

    private Integer reconnectLimit = -1;

    public Integer getReconnectInternal() {
        return reconnectInternal;
    }

    public void setReconnectInternal(Integer reconnectInternal) {
        this.reconnectInternal = reconnectInternal;
    }

    public Integer getReconnectLimit() {
        return reconnectLimit;
    }

    public void setReconnectLimit(Integer reconnectLimit) {
        this.reconnectLimit = reconnectLimit;
    }
}
