package de.evoila.cf.cpi.bosh;

import de.evoila.cf.broker.bean.MongoDBSecurityKeyBean;
import de.evoila.cf.broker.custom.mongodb.RandomString;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.cpi.bosh.deployment.DeploymentManager;
import de.evoila.cf.cpi.bosh.deployment.manifest.Manifest;

import java.util.HashMap;
import java.util.Map;


public class MongoDBDeploymentManager extends DeploymentManager {
    public static final String NODES = "nodes";
    public static final String DATA_PATH = "data_path";
    public static final String REPLICA_SET_NAME = "replica-set-name";
    public static final String VM_TYPE = "vm_type";
    public static final String DISK_TYPE = "disk_type";

    private RandomString randomString;

    MongoDBDeploymentManager(){
        this.randomString = new RandomString(1024);
    }
    protected void replaceParameters (ServiceInstance instance, Manifest manifest, Plan plan, Map<String, String> customParameters) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.putAll(plan.getMetadata());
        properties.putAll(customParameters);

        HashMap<String, Object> manifestProperties = (HashMap<String, Object>) manifest.getProperties();
        HashMap<String, Object> mongodb = (HashMap<String, Object>) manifestProperties.get("mongodb");
        HashMap<String, Object> auth = (HashMap<String, Object>) mongodb.get("auth");
        HashMap<String, Object> replset = (HashMap<String, Object>) auth.get("replica-set");
        if(replset == null){
            replset = new HashMap<>();
            auth.put("replica-set", replset);
        }

        auth.put("password", instance.getId());
        if(!replset.containsKey("keyfile")) {
            replset.put("keyfile", randomString.nextString());
        }

        if(properties.containsKey(REPLICA_SET_NAME)){
            replset.put("name", customParameters.get(REPLICA_SET_NAME));
        }
        if(properties.containsKey(DATA_PATH)){
            mongodb.put(DATA_PATH, customParameters.get(DATA_PATH));
        }

        if(properties.containsKey(NODES)){
            manifest.getInstance_groups().get(0).setInstances((Integer) properties.get(NODES));
        }

        if(properties.containsKey(VM_TYPE)){
            manifest.getInstance_groups().get(0).setVm_type((String) properties.get(VM_TYPE));
        }

        if(properties.containsKey(DISK_TYPE)){
            manifest.getInstance_groups().get(0).setVm_type((String) properties.get(DISK_TYPE));
        }
    }


}
