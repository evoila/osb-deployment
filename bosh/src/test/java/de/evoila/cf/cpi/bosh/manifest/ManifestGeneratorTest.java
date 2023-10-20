package de.evoila.cf.cpi.bosh.manifest;

import de.evoila.cf.broker.bean.BoshProperties;
import de.evoila.cf.cpi.bosh.deployment.DeploymentManager;
import de.evoila.cf.cpi.bosh.deployment.manifest.*;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {BoshProperties.class, DeploymentManager.class})
class ManifestGeneratorTest extends ManifestTest {

    @Autowired
    DeploymentManager deploymentManager;

    Manifest manifest;

    @BeforeEach
    void before() throws IOException, URISyntaxException {
        manifest = new Manifest();
        manifest.setName(DEPLOYMENT_NAME);
        manifest.getProperties().put("TEST", "TEST");
        Release release = new Release();
        release.setName(RELEASE_NAME);
        release.setVersion(null);
        manifest.getReleases().add(release);

        Update u = new Update();
        u.setCanaries(CANARIES);
        u.setCanaryWatchTime(CANARY_WATCH_TIME);
        u.setMaxInFlight(MAX_IN_FLIGHT);
        u.setUpdateWatchTime(UPDATE_WATCH_TIME);
        manifest.setUpdate(u);

        Stemcell stemcell = new Stemcell();
        stemcell.setVersion(STEMCELL_VERSION);
        stemcell.setName(STEMCELL_NAME);

        ResourcePool r = new ResourcePool();
        r.setName(R_POOL_NAME);
        r.setName(R_NETWORK_NAME);
        r.setStemcell(stemcell);
        r.getCloud_properties().put("TEST", "TEST");
        r.setNetwork(R_NETWORK_NAME);
        r.setName(R_POOL_NAME);

        Subnet subnet = new Subnet();
        subnet.getDns().add(DNS);
        subnet.setRange(RANGE);
        subnet.getStaticIps().add(STATIC_IP);
        subnet.getReserved().add(RESERVED);

        Network network = new Network();
        network.setName(NETWORK_NAME);
        network.getSubnets().add(subnet);

        Template t = new Template();
        t.setName(TEMPLATE1);
        t.setRelease(TEMPLATE2);
        Network jobNetwork = new Network();
        jobNetwork.setName(NETWORK_NAME);

        Job job = new Job();
        job.setInstances(INSTANCES);
        job.setName(JOB_NAME);
        job.setPersistentDisk(JOB_PERSISTENT_DISK);
        job.setResourcePool(R_POOL_NAME);
        job.getTemplates().add(t);
        job.getNetworks().add(jobNetwork);
        job.getProperties().put("TEST","TEST");

        Compilation compilation = new Compilation();
        compilation.setNetwork(COMP_NETWORK);
        compilation.setWorkers(COMP_WORKERS);

        manifest.setCompilation(compilation);
        manifest.getNetworks().add(network);
        manifest.getResourcePools().add(r);
        manifest.getJobs().add(job);
    }

    @Test
    void testManifestGeneration() throws IOException, URISyntaxException {
        String manifest = deploymentManager.generateManifest(this.manifest);
        String cmp_manifest = readFile("cmp_manifest.yml");
        assertEquals(cmp_manifest, manifest);


    }


}
