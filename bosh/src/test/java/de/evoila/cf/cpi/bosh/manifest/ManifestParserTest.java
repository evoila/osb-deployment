package de.evoila.cf.cpi.bosh.manifest;

import de.evoila.cf.broker.bean.BoshProperties;
import de.evoila.cf.cpi.bosh.deployment.DeploymentManager;
import de.evoila.cf.cpi.bosh.deployment.manifest.Compilation;
import de.evoila.cf.cpi.bosh.deployment.manifest.Manifest;

import de.evoila.cf.cpi.bosh.deployment.manifest.ResourcePool;
import de.evoila.cf.cpi.bosh.deployment.manifest.Update;
import de.evoila.cf.cpi.bosh.deployment.manifest.job.Job;
import de.evoila.cf.cpi.bosh.deployment.manifest.job.Template;
import de.evoila.cf.cpi.bosh.deployment.manifest.network.Network;
import de.evoila.cf.cpi.bosh.deployment.manifest.network.Subnet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {BoshProperties.class, DeploymentManager.class})
class ManifestParserTest extends ManifestTest {

    @Autowired
    DeploymentManager deploymentManager;

    Manifest manifest;

    @BeforeEach
    void before() throws IOException, URISyntaxException {
        manifest = deploymentManager.readTemplate("/manifest.yml");
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
    void testResourcePoolParameter(){
        ResourcePool pool = manifest.getResourcePools().get(0);
        assertEquals(R_POOL_NAME, pool.getName());
        assertEquals(R_NETWORK_NAME, pool.getNetwork());
        assertEquals(STEMCELL_NAME, pool.getStemcell().getName());
        assertEquals(STEMCELL_VERSION, pool.getStemcell().getVersion());
        assertEquals(4, pool.getCloud_properties().get("cpu"));
        assertEquals(11111, pool.getCloud_properties().get("disk"));
        assertEquals(4096, pool.getCloud_properties().get("ram"));
    }

    @Test
    void testJobParameter(){
        Job job = manifest.getJobs().get(0);
        assertEquals(JOB_NAME, job.getName());
        assertEquals(INSTANCES, job.getInstances());
        assertEquals(JOB_PERSISTENT_DISK, job.getPersistentDisk());
        assertEquals(JOB_RESOURCE_POOL_NAME, job.getResourcePool());
        assertEquals(R_NETWORK_NAME, job.getNetworks().get(0).getName());
        assertTrue(job.getProperties().isEmpty());
    }

    @Test
    void testTemplates(){
        Job job = manifest.getJobs().get(0);
        Template t = job.getTemplates().get(0);
        assertEquals(TEMPLATE1, t.getName());
        assertEquals(TEMPLATE1, t.getRelease());
        t = job.getTemplates().get(1);
        assertEquals(TEMPLATE2, t.getName());
        assertEquals(TEMPLATE2, t.getRelease());
    }

    @Test
    void testNetworks(){
        Network net = manifest.getNetworks().get(0);
        assertEquals(NETWORK_NAME, net.getName());

        Subnet snet = net.getSubnets().get(0);
        assertEquals(GATEWAY, snet.getGateway());
        assertEquals(DNS, snet.getDns().get(0));
        assertEquals(RANGE, snet.getRange());
        assertEquals(STATIC_IP, snet.getStaticIps().get(0));
        assertEquals(N_CP_NAME, snet.getCloudProperties().get("name"));
    }

    @Test
    void properties(){
        Map<String,Object> objectMap = manifest.getProperties();
        assertFalse(objectMap.isEmpty());

        Object custom = objectMap.get("custom");
        if( custom instanceof Map map){
            assertEquals("TEST",map.get("name"));
        } else {
            assertFalse(true);
        }
    }

    @Test
    void testReleases(){
        assertEquals(RELEASE_NAME, manifest.getReleases().get(0).getName());
        assertEquals(RELEASE_VERSION, manifest.getReleases().get(0).getVersion());
    }

    @Test
    void testCompilation(){
        Compilation comp = manifest.getCompilation();

        assertEquals(COMP_NETWORK, comp.getNetwork());
        assertEquals(COMP_WORKERS,comp.getWorkers());

    }
}
