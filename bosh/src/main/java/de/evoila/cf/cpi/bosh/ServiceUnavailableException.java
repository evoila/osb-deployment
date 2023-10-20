package de.evoila.cf.cpi.bosh;

public class ServiceUnavailableException extends Exception {
    public ServiceUnavailableException(String deploymentName) {
        super(getMessage(deploymentName));
    }

    private static String getMessage(String deploymentName) {
        return "Service for deployment %s is not available. Are the VMs running?".formatted(
                deploymentName);
    }
}
