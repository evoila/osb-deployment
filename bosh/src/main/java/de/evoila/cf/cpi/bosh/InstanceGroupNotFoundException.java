package de.evoila.cf.cpi.bosh;

import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.cpi.bosh.deployment.manifest.Manifest;

import java.util.stream.Collectors;

public class InstanceGroupNotFoundException extends Exception{
    public InstanceGroupNotFoundException(ServiceInstance instance,Manifest manifest,String instanceGroup) {
        super(getMessage(instance,manifest,instanceGroup));
    }

    private static String getMessage(ServiceInstance instance,Manifest manifest,String instanceGroup) {
        String groups = manifest.getInstanceGroups().stream().map(i -> i.getName()).collect(Collectors.joining(", "));
        return String.format("Could not find instance group %s in manifest for service instance %s. It only contains %s",
                      instanceGroup,
                      instance.getId(),
                      groups
        );
    }
}
