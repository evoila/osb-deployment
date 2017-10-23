import de.evoila.Application;
import de.evoila.cf.broker.bean.BoshProperties;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.VolumeUnit;
import de.evoila.cf.broker.service.availability.ServicePortAvailabilityVerifier;
import de.evoila.cf.cpi.bosh.BoshPlatformService;
import de.evoila.cf.cpi.bosh.connection.BoshConnection;
import io.bosh.client.DirectorClient;
import io.bosh.client.deployments.DeploymentSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class})
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles("BoshLite")
public class TestBoshPlatformService {

    @Autowired
    ServicePortAvailabilityVerifier availabilityVerifier;
    @Autowired
    BoshPlatformService platformService;
    @Autowired
    ApplicationContext context;
    @Autowired
    BoshProperties properties;

    private DirectorClient connection;
    ServiceInstance instance;
    Plan plan;

    @Before
    public void before(){
        connection = new BoshConnection(properties.getUsername(),
                                        properties.getPassword(),
                                        properties.getHost()).authenticate().connection();

        plan = new Plan("planId", "Plan", "Test Plan", Platform.BOSH, 20, VolumeUnit.G, "", false,200);
        instance = new ServiceInstance("serviceId", "serviceDefId", "planId","none","none",null,"");
    }
    @Test
    public void testCreate() throws PlatformException {
        platformService.createInstance(instance,plan, new HashMap<>());
        connection.deployments().list().toBlocking();
        connection.deployments().get(instance.getId());
    }

}
