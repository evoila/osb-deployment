package de.evoila.cf.cpi.bosh;

import de.evoila.cf.broker.bean.BoshProperties;
import de.evoila.cf.broker.bean.MongoDBSecurityKeyBean;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.DashboardClient;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServerAddress;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.broker.service.CatalogService;
import de.evoila.cf.broker.service.availability.ServicePortAvailabilityVerifier;
import de.evoila.cf.cpi.bosh.deployment.DeploymentManager;
import io.bosh.client.deployments.Deployment;
import io.bosh.client.errands.ErrandSummary;
import io.bosh.client.tasks.Task;
import io.bosh.client.vms.Vm;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@ConditionalOnBean(BoshProperties.class)
public class MongoDBBoshPlatformService extends BoshPlatformService {
    private static final int defaultPort = 27017;

    MongoDBBoshPlatformService (PlatformRepository repository, CatalogService catalogService, ServicePortAvailabilityVerifier availabilityVerifier, BoshProperties boshProperties, Optional<DashboardClient> dashboardClient) {
        super(repository, catalogService, availabilityVerifier, boshProperties, dashboardClient, new MongoDBDeploymentManager());
    }

    public void runCreateErrands (ServiceInstance instance, Plan plan, Deployment deployment, Observable<List<ErrandSummary>> errands) throws PlatformException {
        Task task = super.connection.connection().errands().runErrand(deployment.getName(), "create-replset").toBlocking().first();
        super.waitForTaskCompletion(task);

    }

    protected void runUpdateErrands (ServiceInstance instance, Plan plan, Deployment deployment, Observable<List<ErrandSummary>> errands) throws PlatformException {
        Task task = super.connection.connection().errands().runErrand(deployment.getName(), "reconfigure-replset").toBlocking().first();
        super.waitForTaskCompletion(task);
    }

    protected void runDeleteErrands (ServiceInstance instance, Deployment deployment, Observable<List<ErrandSummary>> errands) { }

    @Override
    protected void updateHosts (ServiceInstance in, Plan plan, Deployment deployment) {
        final int port;
        if (plan.getMetadata().containsKey(MongoDBDeploymentManager.PORT)) {
            port = (int) plan.getMetadata().get(MongoDBDeploymentManager.PORT);
        } else{
            port = defaultPort;
        }

        List<Vm> vms = connection.connection().vms().listDetails(in.getId()).toBlocking().first();
        if(in.getHosts() == null)
            in.setHosts(new ArrayList<>());

        in.getHosts().clear();

        vms.forEach(vm -> {
            in.getHosts().add(new ServerAddress("Host-" + vm.getIndex(), vm.getIps().get(0), port));
        });
    }
}
