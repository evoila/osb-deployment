package de.evoila.cf.cpi.bosh.manifest;

import de.evoila.cf.broker.bean.BoshProperties;
import de.evoila.cf.cpi.bosh.deployment.DeploymentManager;
import de.evoila.cf.cpi.bosh.deployment.manifest.InstanceGroup;
import de.evoila.cf.cpi.bosh.deployment.manifest.Manifest;
import de.evoila.cf.cpi.bosh.deployment.manifest.Update;
import de.evoila.cf.cpi.bosh.deployment.manifest.instanceGroup.JobV2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = { BoshProperties.class, DeploymentManager.class})
public class ManifestV2ParserTest extends ManifestTest {

    public static final String AZ1 = "z1";
    public static final String VMTYPE = "default";
    public static final String NETWORK_NAME = R_NETWORK_NAME;
    public static final String STEMCELL = "default";
    public static final String PERSISTENT_DISK_TYPE = "default";
    public static final String INST_GRP_NAME = "mongodb3";

    private static final String STEMCELL_ALIAS = "default";
    public static final String STEMCELL_OS = "ubuntu-trusty";
    
    @Autowired
    DeploymentManager deploymentManager;

    Manifest manifest;

    @BeforeEach
    void before() throws IOException, URISyntaxException {
        manifest = deploymentManager.readTemplate("/manifestV2.yml");
    }

    @Test
    void testManifestParameter() throws IOException, URISyntaxException {
        assertEquals(DEPLOYMENT_NAME, manifest.getName());
    }

    @Test
    void testUpdateParameter() throws IOException, URISyntaxException {
        Update update = manifest.getUpdate();
        assertEquals(CANARIES, update.getCanaries());
        assertEquals(CANARY_WATCH_TIME, update.getCanaryWatchTime());
        assertEquals(UPDATE_WATCH_TIME, update.getUpdateWatchTime());
        assertEquals(MAX_IN_FLIGHT, update.getMaxInFlight());
    }

    @Test
    void testInstanceGroup(){
        InstanceGroup job = manifest.getInstanceGroups().get(0);
        assertEquals(INST_GRP_NAME, job.getName());
        assertEquals(AZ1, job.getAzs().get(0));
        assertEquals(VMTYPE, job.getVmType());
        assertEquals(NETWORK_NAME, job.getNetworks().get(0).getName());
        assertEquals(STEMCELL, job.getStemcell());
        assertEquals(PERSISTENT_DISK_TYPE, job.getPersistentDiskType());
    }

    @Test
    void testJobs(){
        InstanceGroup group = manifest.getInstanceGroups().get(0);
        JobV2 t = group.getJobs().get(0);
        assertEquals("mongodb3", t.getName());
        assertEquals("mongodb3", t.getRelease());
        t = group.getJobs().get(1);
        assertEquals("node-exporter", t.getName());
    }

    @Test
    void testReleases(){
        assertEquals(RELEASE_NAME, manifest.getReleases().get(0).getName());
        assertEquals(RELEASE_VERSION, manifest.getReleases().get(0).getVersion());
    }

    @Test
    void testStemCell(){
        assertEquals(STEMCELL_ALIAS, manifest.getStemcells().get(0).getAlias());
        assertEquals(STEMCELL_OS, manifest.getStemcells().get(0).getOs());
    }

}
